#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include "p3170045-p3170129-res2.h"

#define FREE -1

//holds the values returned by gettime
struct timespec start ,stop;

pthread_cond_t cond;
pthread_cond_t cond_cash; 
int tel_count = TEL; //number of operators
int cashier_count = CASHIERS;
pthread_mutex_t mutex;  //mutex for operators
pthread_mutex_t transaction_mutex;  //mutex for transactions (locks bank sum and transaction_number)
pthread_mutex_t table_mutex;  // mutex for seats table (locks whenever changes in the table take place)
pthread_mutex_t time_mutex;  //mutex for time variables 
pthread_mutex_t cashier_mutex; //mutex for locking cashiers
pthread_mutex_t print_mutex;
int transaction_number = 1;
double bank_sum;
int incomplete_seats;
int incomplete_payment;
int incomplete_full;
int completed;
double avg_wait_time;
double avg_service_time;
//each cell in the table corresponds to a seat . the value of the cell is the customer's id 
int seats[SEATS] = {[0 ... SEATS-1] = FREE} ; //array of all seats in the theatre .Initially all seats are empty (-1)
int seed;

zone_array array_A = {
        .cost = C_ZONE_A,
       .size = 50,
       .free_seats = 50
};

zone_array array_B = {
        .cost = C_ZONE_B,
        .size = 100,
        .free_seats = 100
};

zone_array array_C = {
        .cost = C_ZONE_C,
        .size =100,
        .free_seats = 100
};

/* binds cust_seats in given zone of seats table and calculates nextFree seat in said zone or
returns -1 if any error occured while processing (seats aren't in the same row or zone is full) */
int bindTable(zone_array *array_struct,int *total_seats ,int *thread ,int cust_seats){

    if( array_struct->free_seats == 0){
        pthread_mutex_lock(&print_mutex);
        printf("Zone is full\n");
        pthread_mutex_unlock(&print_mutex);
        incomplete_seats++;
        return -1;
    }

    int cons_seats = 0; //checks if consecutive seats equal to the amount specified are available in same row
    int i = 0;
    for(i ; i< array_struct->size ; i++){
        if(array_struct->array[i] == FREE){
            cons_seats++;
        }else{
            cons_seats = 0;
        }
        pthread_mutex_lock(&print_mutex);
        pthread_mutex_unlock(&print_mutex);
        //if next seat of zone is in another row AND specified number of seats hasn't been reached ,
        // begin to search for seats in next row
        if(cons_seats < cust_seats && (i / 10) != (i+1)/10){
            cons_seats = 0;
        }
        //if consecutive seats in row have been found ,place customer id in array
        if(cons_seats == cust_seats) {
            for (int j = i; j > i - cust_seats; j--) {
                array_struct->array[j] = *thread;
            }
            array_struct->free_seats -= cust_seats; //reduce number of free seats in zone
            *total_seats = i; //marks the last seat index occupied by customer
            return 1;
        }
    }
    printf("There are no consecutive seats in zone\n");
    incomplete_seats++;
	return -1;
}

int service(int *id){
    zone_array *temp_array;
    int zone_start;
	int *thread = id;

	int cust_nseats;
	int rc;
	pthread_mutex_lock(&time_mutex);
	clock_gettime(CLOCK_REALTIME ,&start);//calculates the time that the client appeared
	avg_service_time -= start.tv_sec;
	avg_wait_time -= start.tv_sec;
	pthread_mutex_unlock(&time_mutex);
	
	//wait until an operator becomes available
	rc= pthread_mutex_lock(&mutex);
	while (tel_count == 0) {
		rc= pthread_cond_wait(&cond, &mutex);
	}

	tel_count--;
	rc= pthread_mutex_unlock(&mutex);

    pthread_mutex_lock(&time_mutex);
    clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client found a free operator
    avg_wait_time +=stop.tv_sec;
    pthread_mutex_unlock(&time_mutex);
	//Choose zone ,seats and bind seats in table
	double zone_selection = ((double)rand_r(&seed))/ RAND_MAX ; //returns a number between 0-1
	cust_nseats = (rand_r(&seed)%(N_SEATHIGH - N_SEATLOW +1) +1 ); //returns a random number between 1 and 5 (number of seats selected by customer)

	int sleep_time = rand_r(&seed) %(T_SEATHIGH - T_SEATLOW +1) + (T_SEATHIGH - T_SEATLOW);

	sleep(sleep_time ); //returns a number between 5-10 (time required for operator to search seats)
	//check if theatre is full
	
	pthread_mutex_lock(&table_mutex);
    //checks if every zone in theatre is full
	if(array_A.free_seats == 0 && array_B.free_seats == 0 && array_C.free_seats == 0){
	    pthread_mutex_lock(&print_mutex);
		printf("Reservation cancelled beacuse the theatre is full\n");
		pthread_mutex_unlock(&print_mutex);
		incomplete_full++;
		pthread_mutex_unlock(&table_mutex);
		tel_count++;
		pthread_cond_signal(&cond);
		printf("customer %d completed the call\n" ,*thread);
		pthread_exit(thread);
	}

	int total_seats;
	int result;
	/*  zone_A is selected if zone_selection is between 0 - 0.2 (20% chance)
		zone_B is selected if zone_selection is between 0.2 - 0.6 (40% chance) and
		zone_C is selected if zone_selection is between 0.6 - 1 (40% chance)
	*/
	if(zone_selection < ((double)P_ZONE_A) /100){
        temp_array = &array_A;
        zone_start = 0;
	}else if(zone_selection < ((double)P_ZONE_B) /100){
        temp_array = &array_B;
        zone_start = 50;
	}else{
        temp_array = &array_C;
        zone_start = 150;
	}
    result = bindTable(temp_array, &total_seats ,thread ,cust_nseats );

	//if function result = -1 ,the thread must exit
	if(result == -1){
		tel_count++;
		pthread_cond_signal(&cond);
        pthread_mutex_unlock(&table_mutex);
		printf("customer %d completed the call\n" ,*thread);
		pthread_exit(thread);
	}
	pthread_mutex_unlock(&table_mutex);
	
	//thread disconnects from operator
	pthread_mutex_lock(&mutex);
	tel_count++;
	pthread_cond_signal(&cond);
	pthread_mutex_unlock(&mutex);
	
	//wait until a cashier becomes available in order to connect
	rc= pthread_mutex_lock(&cashier_mutex);
    pthread_mutex_lock(&time_mutex);
    clock_gettime(CLOCK_REALTIME ,&start);
    avg_wait_time -= start.tv_sec;
    pthread_mutex_unlock(&time_mutex);
	while (cashier_count == 0) {
		rc= pthread_cond_wait(&cond_cash, &cashier_mutex);
	}
	cashier_count--;
	pthread_mutex_unlock(&cashier_mutex);

    pthread_mutex_lock(&time_mutex);
    clock_gettime(CLOCK_REALTIME,&stop);
    avg_wait_time +=stop.tv_sec;
    pthread_mutex_unlock(&time_mutex);
	
	pthread_mutex_lock(&transaction_mutex);
	sleep_time = rand_r(&seed) %(T_CASHHIGH - T_CASHLOW +1) + (T_CASHHIGH - T_CASHLOW);
	sleep(sleep_time ); //returns a number between 2-4 (time required for cashier to process payment)
	double card_approved = ((double)rand_r(&seed))/ RAND_MAX ; //generate a number between 0  and 1 . Every number is equally possible
	//check if card was approved ( since every number is equally probable to be returned ,number 0-0.9 appear 90% of the time)
	if( card_approved <= CARD_SUCCESS ){
		bank_sum += (double)(temp_array->cost) * cust_nseats;
		completed++;
        pthread_mutex_lock(&print_mutex);
		printf("\n-Reservation completed for customer %d\n -Your transaction number is %d\n -Your seats are < ",*thread, transaction_number);
		for(int i = total_seats + 1 - cust_nseats ; i< total_seats  ; i++){
			printf(" %d , " , i+ zone_start + 1);
		}
        printf(" %d >\n " , total_seats + zone_start +1 );
		printf("-The cost of the transaction is %d euros\n" , (temp_array->cost) * cust_nseats);

		pthread_mutex_unlock(&print_mutex);
		transaction_number++;
	}else{
		pthread_mutex_lock(&table_mutex);
		printf("Error while processing payment method for customer %d\n", *thread);
		incomplete_payment++;
		temp_array->free_seats += cust_nseats;
		for( int i =total_seats ; i> total_seats - cust_nseats ; i--){

			temp_array->array[i] = FREE;
		}
		pthread_mutex_unlock(&table_mutex);
		pthread_mutex_unlock(&transaction_mutex);
		cashier_count++;
		pthread_cond_signal(&cond_cash);
		printf("customer %d completed the call\n" ,*thread);
		pthread_exit(thread);
	}
	pthread_mutex_unlock(&transaction_mutex);
	
	pthread_mutex_lock(&time_mutex);
	clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client has been serviced
	avg_service_time +=stop.tv_sec;
	pthread_mutex_unlock(&time_mutex);
	
	//thread finished ,increase number of available cashiers
	pthread_mutex_lock(&cashier_mutex);
	cashier_count++;
	pthread_cond_signal(&cond_cash);
	pthread_mutex_unlock(&cashier_mutex);
	printf("customer %d completed the call\n" ,*thread);
	return *thread;
}
	
int main(int argc , char* argv[]){
    array_A.array = malloc(sizeof(int)*array_A.size);
    array_B.array = malloc(sizeof(int)*array_B.size);
    array_C.array = malloc(sizeof(int)*array_C.size);
    for(int i  = 0 ; i< 100 ;i++){
        if(i<50){
            array_A.array[i] = FREE;
        }
        array_B.array[i] = FREE;
        array_C.array[i] = FREE;
    }

	int customer_count = 0;
	if (argc != 3) {
		printf("Invalid number of arguments (%d instead of 3)\n", argc);
		exit(-1);
	}
	else {
		customer_count = atoi(argv[1]); //number of customers / threads in the program
		seed = atoi(argv[2]);
	}
	pthread_t threads[customer_count];

	int threadIds[customer_count];
	
	//initialise mutex locks
	int rc;
	pthread_cond_init(&cond ,NULL);
	pthread_cond_init(&cond_cash ,NULL);
    rc = pthread_mutex_init(&print_mutex, NULL);
	rc = pthread_mutex_init(&mutex, NULL);
	if (rc != 0) {	
		printf("ERROR: return code from pthread_mutex_init() is %d\n", rc);
		exit(-1);
	} 
	rc = pthread_mutex_init(&transaction_mutex, NULL);
	if (rc != 0) {	
		printf("ERROR: return code from pthread_mutex_init() is %d\n", rc);
		exit(-1);
	} 
	rc = pthread_mutex_init(&table_mutex, NULL);
	if (rc != 0) {	
		printf("ERROR: return code from pthread_mutex_init() is %d\n", rc);
		exit(-1);
	} 
	rc = pthread_mutex_init(&time_mutex, NULL);
	if (rc != 0) {	
		printf("ERROR: return code from pthread_mutex_init() is %d\n", rc);
		exit(-1);
	} 
	rc = pthread_mutex_init(&cashier_mutex ,NULL);
	if(rc != 0 ) {
		printf("ERROR: return code from pthread_mutex_init() is %d\n", rc);
		exit(-1);
	} 
	
	//create threads	
	for(int i=0 ; i<customer_count ; i++){
		threadIds[i] = i +1 ;
		//create individual thread

		rc = pthread_create(&threads[i] , NULL , service ,&threadIds[i]);
		
    		if (rc != 0) {
    			printf("ERROR: return code from pthread_create() is %d\n", rc);
       			exit(-1);
       		}
	}
	void* status;
	for(int i=0 ; i< customer_count ; i++){
		rc = pthread_join(threads[i] , &status);
		if(rc != 0){
			printf("ERROR: return code from pthread_join is %d\n", rc);
      		exit(-1);
		}
	}
	for(int i = 0; i <100 ; i++){
	    if( i < 50) {
            seats[i] = array_A.array[i];
        }
	    seats[i+50] = array_B.array[i];
	    seats[i+150] = array_C.array[i];
	}
	for(int i =0 ; i<SEATS ;i++){
	    if(i < 50) {
            printf("Zone A / Seat %d / Customer %d\n", i + 1, seats[i]);
        }else if(i < 150){
            printf("Zone B / Seat %d / Customer %d\n", i + 1, seats[i]);
	    }else{
            printf("Zone C / Seat %d / Customer %d\n", i + 1, seats[i]);
	    }
	}
	if(customer_count != 0){
		avg_wait_time /= customer_count;
		avg_service_time /= customer_count;
	}
	printf("Total sum = %.02f euros\n" ,bank_sum);
	printf("Reservations cancelled due to payment failures : ");
	if(incomplete_payment != 0){
	    printf("%.02f%%\n" ,(double)incomplete_payment *100.0F /customer_count);
	}else{
	    printf("0%%\n");
	}
    printf("Reservations cancelled because no consecutive seats were found : ");
	if(incomplete_seats != 0){
	    printf("%.02f%%\n", (double)incomplete_seats *100.0F / customer_count);
	}else{
	    printf("0%%\n");
	}
    printf("Reservations cancelled because the theatre was full : ");
	if(incomplete_full != 0){
	    printf("%.02f%%\n" ,(double)incomplete_full *100.0F /customer_count);
	}else{
	    printf("0%%\n");
	}
    printf("Reservations completed successfully : ");
	if(completed != 0){
	    printf("%.02f%%\n" ,(double)completed *100.0F /customer_count);
	}else{
	    printf("0%%\n");
	}
	printf("Average waiting time = %.02f s\n" ,avg_wait_time);
	printf("Average service time = %.02f s\n",avg_service_time);
	
	pthread_mutex_destroy(&mutex);
	pthread_mutex_destroy(&time_mutex);
	pthread_mutex_destroy(&transaction_mutex);
	pthread_mutex_destroy(&table_mutex);
    pthread_mutex_destroy(&cashier_mutex);
    pthread_mutex_destroy(&print_mutex);
	pthread_cond_destroy(&cond);
	free(array_A.array);
	free(array_B.array);
	free(array_C.array);
	
	return 1;
}
	
