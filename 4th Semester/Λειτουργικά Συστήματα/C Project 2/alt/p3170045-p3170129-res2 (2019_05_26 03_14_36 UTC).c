#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include <zconf.h>
#include "p3170045-p3170129-p3170049-res1.h"

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
int transaction_number;
double bank_sum;
double avg_wait_time;
double avg_service_time;
//each cell in the table corresponds to a seat . the value of the cell is the customer's id 
int seats[SEATS] = {[0 ... SEATS-1] = FREE} ; //array of all seats in the theatre .Initially all seats are empty (-1)
int nextFree_A;  //index of next free seat in zone A
int nextFree_B = 50; //index of next free seat in zone B
int nextFree_C = 150; //index of next free seats in zone C
int seed;

/* binds cust_seats in given zone of seats table and calculates nextFree seat in said zone or
returns -1 if any error occured while processing (seats aren't in the same row or zone is full) */
int bindTable(int *nextFree ,int *total_seats ,int seats_number ,int *thread ,int cust_seats){
	if((*nextFree % 10)+ cust_seats >= 10){
		printf("row is full!\n");
		return -1;
	}

	*total_seats = *nextFree + cust_seats;

	printf("*****************\ncustomer seats = %d\ntotal seats %d\n", cust_seats ,*total_seats);
		if(*total_seats <= seats_number){
			//place customer's id in corresponding seat number
			for(int i = *nextFree ; i< *total_seats ; i++){
				seats[i] = *thread;
				printf("seats[%d] = %d\n" ,i ,seats[i]);
                (*nextFree)++;
			}
		}else{
			printf("Not enough seats!\n");
			return -1;
		}
	return 1;
}

int service(int *id){
    int *nextFree;
	int *thread = id;
	printf("Thread %d entered service\n",*thread);
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
		printf("Thread %d, is waiting \n", *thread);
		rc= pthread_cond_wait(&cond, &mutex);
	}
	printf("Thread %d, unblocked.\n",*thread);    
	tel_count--;
	rc= pthread_mutex_unlock(&mutex);
	
	//Choose zone ,seats and bind seats in table
	double zone_selection = ((double)rand_r(&seed))/ RAND_MAX ; //returns a number between 0-1
	cust_nseats = (rand_r(&seed)%(N_SEATHIGH - N_SEATLOW +1) +1 ); //returns a random number between 1 and 5 (number of seats selected by customer)

	
	pthread_mutex_lock(&time_mutex);
	clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client found a free operator
	avg_wait_time +=stop.tv_sec;
	pthread_mutex_unlock(&time_mutex);

	int sleep_time = rand_r(&seed) %(T_SEATHIGH - T_SEATLOW +1) + (T_SEATHIGH - T_SEATLOW);
	printf("operator of thread %d sleeps for %d\n" ,*thread ,sleep_time);
	sleep(sleep_time ); //returns a number between 5-10 (time required for operator to search seats)
	//check if theatre is full
	
	pthread_mutex_lock(&table_mutex);
	printf("\nthread %d locks the table to reserve seats\n" ,*thread);
	if((50 - nextFree_A)== 0 && (150 - nextFree_B) == 0 && (250 - nextFree_C) ==0){ 
		printf("Reservation cancelled beacuse the theatre is full\n");
		pthread_mutex_unlock(&table_mutex);
		tel_count++;
		pthread_cond_signal(&cond);
		pthread_exit(thread);
	}

	int total_seats;
	int result;
	/*  zone_A is selected if zone_selection is between 0 - 0.2 (20% chance)
		zone_B is selected if zone_selection is between 0.2 - 0.6 (40% chance) and
		zone_C is selected if zone_selection is between 0.6 - 1 (40% chance)
	*/
	if(zone_selection < ((double)P_ZONE_A) /100){
	    printf("nextFreeA = %d\n", nextFree_A);
	    nextFree = &nextFree_A;
        result = bindTable(&nextFree_A, &total_seats,50 ,thread ,cust_nseats );
	}else if(zone_selection < ((double)P_ZONE_B) /100){
        printf("nextFreeB = %d\n", nextFree_B);
        nextFree = &nextFree_B;
        result = bindTable(&nextFree_B ,&total_seats ,150 ,thread , cust_nseats);
	}else{
        printf("nextFreeC = %d\n", nextFree_C);
        nextFree = &nextFree_C;
		result = bindTable(&nextFree_C ,&total_seats ,250 ,thread , cust_nseats);
	}
	//if function result = -1 ,the thread must exit
	if(result == -1){
		tel_count++;
		pthread_cond_signal(&cond);
        pthread_mutex_unlock(&table_mutex);
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
	printf("available cashiers %d\n", cashier_count);
	while (cashier_count == 0) {
		printf("Thread %d, is waiting \n", *thread);
		rc= pthread_cond_wait(&cond_cash, &cashier_mutex);
	}
	printf("Thread %d, unblocked.\n",*thread);    
	cashier_count--;
	pthread_mutex_unlock(&cashier_mutex);
	
	pthread_mutex_lock(&transaction_mutex);
	printf("\nthread %d proceeds to payment\n" ,*thread);
	
	sleep_time = rand_r(&seed) %(T_CASHHIGH - T_CASHLOW +1) + (T_CASHHIGH - T_CASHLOW);
	printf("operator of thread %d sleeps for %d\n" ,*thread ,sleep_time);
	sleep(sleep_time ); //returns a number between 2-4 (time required for cashier to process payment)
	double card_approved = ((double)rand_r(&seed))/ RAND_MAX ; //generate a number between 0  and 1 . Every number is equally possible
	//check if card was approved ( since every number is equally probable to be returned ,number 0-0.9 appear 90% of the time)
	if( card_approved <= CARD_SUCCESS ){
		bank_sum += (double)SEAT_COST * cust_nseats;

		printf("Reservation completed for thread %d\n. Your transaction number is %d . Your seats are < ",*thread, transaction_number);
		for(int i = *nextFree - cust_nseats ; i< *nextFree-1 ; i++){
			printf(" %d , " , i+1);
		}
        printf(" %d >\n " , *nextFree );
		printf("The cost of the transaction is %d euros\n" ,  SEAT_COST * cust_nseats);
		printf("\nthread %d served \n", *thread);
		transaction_number++;
	}else{
		pthread_mutex_lock(&table_mutex);
		printf("Error while processing payment method\n");
		for( int i =total_seats ; i> total_seats - cust_nseats ; i--){
			seats[i] = FREE;
            (*nextFree)--;
		}
		pthread_mutex_unlock(&table_mutex);
		pthread_mutex_unlock(&transaction_mutex);
		cashier_count++;
		pthread_cond_signal(&cond_cash);
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
	printf("\nthread %d exits function\n", *thread);
	return *thread;
}
	
int main(int argc , char* argv[]){
    printf("nextFree A = %d \nnextFree B = %d\n nextFree C = %d\n", &nextFree_A ,&nextFree_B ,&nextFree_C);
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
		printf("creating thread %d\n", threadIds[i]);
		rc = pthread_create(&threads[i] , NULL , service ,&threadIds[i]);
		
    		if (rc != 0) {
    			printf("ERROR: return code from pthread_create() is %d\n", rc);
       			exit(-1);
       		}
	}
	void* status;
	printf("for loop create ended\n");
	for(int i=0 ; i< customer_count ; i++){
		rc = pthread_join(threads[i] , &status);
		if(rc != 0){
			printf("ERROR: return code from pthread_join is %d\n", rc);
      		exit(-1);
		}
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
	printf("Total sum = %f\n" ,bank_sum);
	printf("Average waiting time = %f\n" ,avg_wait_time);
	printf("Average service time = %f\n",avg_service_time);
	
	pthread_mutex_destroy(&mutex);
	pthread_mutex_destroy(&time_mutex);
	pthread_mutex_destroy(&transaction_mutex);
	pthread_mutex_destroy(&table_mutex);
	pthread_cond_destroy(&cond); 
	
	return 1;
}
	
