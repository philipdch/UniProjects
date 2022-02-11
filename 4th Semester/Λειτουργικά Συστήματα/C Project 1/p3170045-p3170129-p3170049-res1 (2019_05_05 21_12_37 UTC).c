#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include "p3170045-p3170129-p3170049-res1.h"

#define FREE -1

//arguments to be given to "services" function
typedef struct arguments{ 	
	int threadId;
	int seed;
}ARGUMENTS;

//holds the values returned by gettime
struct timespec start ,stop;

pthread_cond_t cond = PTHREAD_COND_INITIALIZER;
int tel_count = TEL; //number of operators
pthread_mutex_t mutex;  //mutex for operators
pthread_mutex_t transaction_mutex;  //mutex for transactions (locks bank sum and transaction_number)
pthread_mutex_t table_mutex;  // mutex for seats table (locks whenever changes in the table take place)
pthread_mutex_t time_mutex;  //mutex for time variables 
int transaction_number = 0;
double bank_sum = 0;   	 
double avg_wait_time  =0 ;
double avg_service_time = 0;
//each cell in the table corresponds to a seat . the value of the cell is the customer's id 
int seats[SEATS] = {FREE} ; //array of all seats in the theatre .Initially all seats are empty (-1) 
int nextFree;  //index of next free seat in seats array

int service(void *args){
	ARGUMENTS *arguments ;
	arguments = (ARGUMENTS*) args;
	
	int cust_nseats;
	double seat_cost;
	int rc;
	
	printf("Hello from : %d\n",arguments->threadId);
	
	//copies of global variable for each individual thread
	double bank_sum_copy;
	int seats_copy[SEATS] = {FREE};
	int nextFree_copy;
	
	pthread_mutex_lock(&time_mutex);
	clock_gettime(CLOCK_REALTIME ,&start);//calculates the time that the client appeared
	avg_service_time -= start.tv_sec;
	avg_wait_time -= start.tv_sec;
	pthread_mutex_unlock(&time_mutex);
	
	//wait until an operator becomes available
	rc= pthread_mutex_lock(&mutex);
	while (tel_count == 0) {
		printf("Thread %d, is waiting \n", arguments->threadId);
		rc= pthread_cond_wait(&cond, &mutex);
	}
	printf("Thread %d, unblocked.\n",arguments->threadId);    
	tel_count--;
	
	rc= pthread_mutex_unlock(&mutex);
	
	cust_nseats = (rand_r(&arguments->seed)%(N_SEATHIGH - N_SEATLOW +1) +1 ); //returns a random number between 1 and 5 (number of seats selected by customer)
	
	pthread_mutex_lock(&time_mutex);
	clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client found a free operator
	avg_wait_time +=stop.tv_sec;
	pthread_mutex_unlock(&time_mutex);
	
	sleep(rand_r(&arguments->seed) %(T_SEATHIGH - T_SEATLOW +1) + (T_SEATHIGH - T_SEATLOW) ); //returns a number between 5-10 (time required for operator to search seats)
	//check if theatre is full
	
	pthread_mutex_lock(&table_mutex);
	if(nextFree > SEATS){ 
		printf("Reservation cancelled beacuse the thetre is full");
		pthread_mutex_unlock(&table_mutex);
		pthread_exit(&arguments->threadId);
	}
	int total_seats = nextFree + cust_nseats; // seats after purchase
	//check if seats after purchase don't exceed number of seats in theatre
	if(total_seats <= 250){
		//place customer's id in corresponding seat number
		for(int i = nextFree ; i< total_seats ; i++){
			seats[i] = arguments->threadId;
			nextFree++;
		}
	}
	else{
		printf("Not enough seats!\n");
		pthread_mutex_unlock(&table_mutex);
		pthread_exit(&arguments->threadId);
	}
	pthread_mutex_unlock(&table_mutex);
	
	pthread_mutex_lock(&transaction_mutex);
	double card_approved = ((double)rand_r(&arguments->seed))/ RAND_MAX ; //generate a number between 0  and 1 . Every number is equally possible
	
	//check if card was approved ( since every number is equally probable to be returned ,number 0-0.9 appear 90% of the time)
	if( card_approved <= CARD_SUCCESS ){
		bank_sum_copy += SEAT_COST *cust_nseats; 
		printf("Reservation completed . Your transaction number is %d . Your seats are < ",transaction_number);
		for(int i =nextFree - cust_nseats ; i< nextFree ; i++){
			printf(" %d , " , seats[i]);
		}
		printf(">\nThe cost of the transaction is %d euros" ,  SEAT_COST * cust_nseats);
		transaction_number++;
	}else{
		pthread_mutex_lock(&table_mutex);
		printf("Error while processing payment method\n");
		for( int i =0 ; i> nextFree - cust_nseats ; i--){
			seats[i] = FREE;
			nextFree--;
		}
		pthread_mutex_unlock(&table_mutex);
		pthread_mutex_unlock(&transaction_mutex);
		pthread_exit(arguments->threadId);
	}
	pthread_mutex_unlock(&transaction_mutex);
	
	pthread_mutex_lock(&time_mutex);
	clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client has been serviced
	avg_service_time +=stop.tv_sec;
	pthread_mutex_unlock(&time_mutex);
	
	//thread finished ,increase number of available operators
	pthread_mutex_lock(&mutex);
	tel_count++;
	pthread_cond_signal(&cond);
	pthread_mutex_unlock(&mutex);
	exit(arguments->threadId);
}
	
int main(int argc , char* argv[]){
	ARGUMENTS *serviceArgs;
	int customer_count = atoi(argv[1]); //number of customers / threads in the program
	serviceArgs->seed = atoi(argv[2]);
	pthread_t threads[customer_count];
	int threadIds[customer_count];
	
	//initialise mutex locks
	int rc;
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
	
	//create threads	
	for(int i=1 ; i<customer_count ; i++){
		threads[i]++;
		threadIds[i]++;
		serviceArgs->threadId = &threadIds[i];
		//create individual thread
		rc = pthread_create(&threads[i] , NULL , service ,serviceArgs);
		
    	if (rc != 0) {
    		printf("ERROR: return code from pthread_create() is %d\n", rc);
       		exit(-1);
       	}
	}
	void* status;
	
	for(int i=1 ; i< customer_count ; i++){
		rc = pthread_join(&threads[i] , &status);
		if(rc != 0){
			printf("ERROR: return code from pthread_join is %d\n", rc);
      		exit(-1);
		}
	}
	
	for(int i =0 ; i<SEATS ;i++){
		printf("Seat %d / Customer %d" ,i+1 ,seats[i]);
	}
	if(customer_count != 0){
		avg_wait_time /= customer_count;
		avg_service_time /= customer_count;
	}
	printf("Total sum = %d" ,bank_sum);
	printf("Average waiting time = %d" ,avg_wait_time);
	printf("Average service time = %d",avg_service_time);
	
	pthread_mutex_destroy(&mutex);
	pthread_mutex_destroy(&time_mutex);
	pthread_mutex_destroy(&transaction_mutex);
	pthread_mutex_destroy(&table_mutex);
	pthread_cond_destroy(&cond); 
	
	return 1;
}
	
