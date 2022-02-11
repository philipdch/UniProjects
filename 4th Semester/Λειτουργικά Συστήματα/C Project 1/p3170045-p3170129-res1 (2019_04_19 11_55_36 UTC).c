#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <time.h>
#include "p3170045-p3170129-res1.h"

#define FREE -1

//arguments to be given to "services" function
typedef struct arguments{ 	
	int threadId;
	int seed;
}ARGUMENTS;

struct timespec start ,stop;

pthread_cond_t cond = PTHREAD_COND_INITIALIZER;
int tel_count = TEL; //number of operators
pthread_mutex_t mutex;
int transaction_number = 0;
double bank_sum = 0;   	 
double avg_wait_time  =0 ;
double avg_service_time = 0;
int seats[SEATS] = {FREE} ; //array of all seats in the theatre .Initially all seats are empty
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
	
	rc= pthread_mutex_lock(&mutex);
	while (tel_count == 0) {
		printf("Thread %d, is waiting \n", arguments->threadId);
		rc= pthread_cond_wait(&cond, &mutex);
	}
	printf("Thread %d, unblocked.\n",arguments->threadId);    
	tel_count--;
	
	rc= pthread_mutex_unlock(&mutex);
	
	clock_gettime(CLOCK_REALTIME ,&start);//calculates the time that the client appeared
	avg_service_time -= start.tv_sec;
	avg_wait_time -= start.tv_sec;
	
	cust_nseats = (rand_r(&arguments->seed)%(N_SEATHIGH - N_SEATLOW +1) +1 );
	
	
	clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client found a free operator
	avg_wait_time +=stop.tv_sec;
	
	sleep(rand_r(&arguments->seed) %(T_SEATHIGH - T_SEATLOW +1) + (T_SEATHIGH - T_SEATLOW) );
	
	if(nextFree > SEATS){
		printf("Reservation cancelled beacuse the thetre is full");
		pthread_exit(&arguments->threadId);
	}
	
	int total_seats = nextFree + cust_nseats;
	if(total_seats <= 250){
		for(int i = nextFree ; i< total_seats ; i++){
			seats[i] = arguments->threadId;
			nextFree++;
		}
		
		double card_approved = ((double)rand_r(&arguments->seed))/ RAND_MAX ; //generate a number between 0  and 1 
		if( card_approved < CARD_SUCCESS ){
			bank_sum_copy += SEAT_COST *cust_nseats; 
			printf("Reservation completed . Your transaction number is %d . Your seats are < ",transaction_number);
			for(int i =nextFree - cust_nseats ; i< nextFree ; i++){
				printf(" %d , " , seats[i]);
			}
			printf(">\nThe cost of the transaction is %d euros" ,  SEAT_COST * cust_nseats);
		}else{
			printf("Error while processing payment method\n");
			for( int i =0 ; i> nextFree - cust_nseats ; i--){
				seats[i] = FREE;
				nextFree--;
			}
			pthread_exit(arguments->threadId);
		}
	}
	else{
		printf("Not enough seats!\n");
		
		pthread_exit(&arguments->threadId);
	}
	
	clock_gettime(CLOCK_REALTIME,&stop);//calculates the time that the client has been serviced
	avg_service_time +=stop.tv_sec;
}
	
int main(int argc , char* argv[]){
	ARGUMENTS *serviceArgs;
	int customer_count = atoi(argv[1]); //number of customers / threads in the program
	serviceArgs->seed = atoi(argv[2]);
	pthread_t *threads = (int*)malloc(sizeof(pthread_t) * customer_count);
	int *threadIds = (int*)malloc(sizeof(int) * customer_count);
	
	//initialise mutex lock
	int rc;
	rc = pthread_mutex_init(&mutex, NULL);
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
			printf("ERROR: return code from pthread_mutex_destroy() is %d\n", rc);
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
}
	
