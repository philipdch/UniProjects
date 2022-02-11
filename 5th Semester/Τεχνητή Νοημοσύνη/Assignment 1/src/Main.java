import java.util.*;

public class Main {
    private static Scanner input = new Scanner(System.in);
    public static void main(String[] args){
        List<Person> people = new ArrayList<>();
        int maxTime = -1; //Time upper bound
        boolean random = false; //Check whether to create initial state at random or based on user input
        int randomPeopleCount = 0; //Random initial state will contain the specified number of people

        /*Arguments: time1 time2 time3... UpperBound (ex 1 2 5 10 20) OR peopleCount
         *If only one argument is given, then the initial state will be constructed at random by creating n people and assigning a unique value (crossing time) to each one, where n = peopleCount given
         *If an upper bound is to be specified, then UpperBound should be a positive number. Else it should be -1
        */
        if(args.length>1) {
            for (int i = 0; i < args.length - 1; i++) {
                Person person = new Person(Integer.parseInt(args[i]));
                people.add(person);
            }
            maxTime = Integer.parseInt(args[args.length - 1]);
        }else if(args.length == 1){
            random = true;
            randomPeopleCount = Integer.parseInt(args[0]);
        }else { // If no arguments are given, then user is prompted to give the crossing time for each person (and thus the total number of people) as well as the upper time bound
            System.out.print("No input arguments were given. Would you like to enter them now?\n[Y]\t[N]\n");
            String answer = input.nextLine();
            if(answer.toLowerCase().equals("y")){
                System.out.print("Specify a time limit for every person to cross?\n[Y]\t[N]\n");
                answer = input.nextLine();
                if(answer.toLowerCase().equals("y")){
                    System.out.println("Enter the time limit:");
                    answer = input.nextLine();
                    maxTime = Integer.parseInt(answer);
                }
                System.out.println("Enter each person's time to cross the bridge in order. Enter 0 to stop");
                int i = 1;
                while(true) {
                    System.out.print(i + ") ");
                    answer = input.nextLine();
                    int givenTime = Integer.parseInt(answer);
                    if (givenTime == 0) break;
                    Person person = new Person(givenTime);
                    people.add(person);
                    System.out.println();
                    i++;
                }
                System.out.println();
            }else{
                System.out.println("Exiting program");
                return;
            }
        }
        State initialState = (random)? new State(randomPeopleCount) : new State(people, maxTime); //If only one argument was given create a random initial state. Else create state based on input
        State terminalState = null;
        AStar alg = new AStar();

        long start = System.currentTimeMillis(); //Begin counting time in milliseconds
        terminalState = alg.AStarAlgorithm(initialState); //Run algorithm
        long finish = System.currentTimeMillis(); //End counting milliseconds

        if(terminalState == null){
            System.out.println("Couldn't find solution");
        }else{
            //Print solution
            List<State> toPrint = new ArrayList<>();
            terminalState.listToPrint(toPrint);
            ListIterator<State> iterator = toPrint.listIterator(toPrint.size());
            while(iterator.hasPrevious()){
                iterator.previous().print();
            }
        }
        System.out.println("The algorithm run in: " + ((finish - start)/1000.0) + " sec");
    }
}
