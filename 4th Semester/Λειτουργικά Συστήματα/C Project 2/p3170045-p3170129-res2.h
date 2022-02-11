#define SEATS 250
#define TEL 8
#define CASHIERS 4
#define ZONE_A 5
#define ZONE_B 10
#define ZONE_C 10
#define P_ZONE_A 20
#define P_ZONE_B 40
#define P_ZONE_C 40
#define C_ZONE_A 30
#define C_ZONE_B 25
#define C_ZONE_C 20
#define T_CASHLOW 2
#define T_CASHHIGH 4
#define N_SEATLOW 1
#define N_SEATHIGH 5
#define T_SEATLOW 5
#define T_SEATHIGH 10
#define CARD_SUCCESS 0.9
#define SEAT_COST 20

typedef struct { //struct which represents the table of a single zone
    int cost;
    int size;
    int free_seats;
    int *array;
}zone_array;

int service(int *id);
int bindTable(zone_array *array , int *total_seats ,int *thread , int cust_seats);
