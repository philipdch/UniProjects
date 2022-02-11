import java.util.*;

public class State implements  Comparable<State> {

    private static int N;
    private State father;
    private int totalTime;
    private int score;
    private static int timeUpperBound;
    private List<Person> leftShore = new ArrayList<>();
    private List<Person> rightShore;
    private int[] crossing = new int[2];
    private Person returning = null;


    /** Generates an initial state containing a predefined number of people and assigns a random, unique crossing time to each one of them
     *  Maximum time is automatically set to -1 (unspecified)
     @param n The total number of people in the problem
     */
    public State(int n){
        /* WARNING: if n (number of people) exceeds the maxRandom number, while loop never terminates as there are no unique values left to assign to the people remaining */
        rightShore = new ArrayList<>();
        int maxRandom = 70;
        Random random = new Random();
        timeUpperBound = -1;
        int i = 0;
        do{
            int time = random.nextInt(maxRandom) + 1; // generates a number between 1 and maxRandom
            Person person = new Person(time);
            //Check whether another person with the same time exists in the list (rightShore)
            boolean contained = false;
            for(Person p: rightShore){
                if(p.compareTo(person) == 0){
                    contained = true;
                    break;
                }
            }
            if(!contained){
                this.rightShore.add(person);
                i++;
            }
        }while(i < n);
        N = n;
        this.father = null;
        for(Person person: rightShore){
            this.score+=person.getDuration();
        }
    }

    /**
     Constructor
     Initializes State object given
     @param people: the people on the right shore
     timeUpperBound=-1, stands for not specified
     */
    public State(List<Person> people){
        this(people, -1);
    }

    /**
     Constructor
     Initializes State object given
     @param people: the people on the right shore
     @param maxTime: maximum time to get across, the time before the light goes off
     */
    public State(List<Person> people, int maxTime){
        this.rightShore = people;
        timeUpperBound = maxTime;
        N = rightShore.size();
        this.father = null;
        for(Person person: rightShore){
            this.score+=person.getDuration();
        }
    }

    /**
     Constructor
     Initializes a State object pointing to its father
     @param father: father's State
     Left and right shore structures are passed down to the child and get manipulated there
     Used in getChildren method
     */
    public State(State father){
        this.leftShore = new ArrayList<>(father.leftShore);
        this.rightShore = new ArrayList<>(father.rightShore);
        this.totalTime = father.totalTime;
        this.crossing[0] = father.crossing[0]; this.crossing[1] = father.crossing[1];
        this.father = father;
    }

    public static void setTimeUpperBound(int timeUpperBound) {
        State.timeUpperBound = timeUpperBound;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public static int getTimeUpperBound() {
        return timeUpperBound;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getScore() {
        return score;
    }

    public Person getReturning() {
        return returning;
    }

    public void setReturning(Person person){
        this.returning = person;
    }

    public State getFather() {
        return father;
    }

    public void setFather(State father) {
        this.father = father;
    }

    /*
    Checks whether a State is terminal
    A State object is terminal only if every person on the right shore has crossed the bridge, the rightShore list is empty and the leftShore is of size N
     */
    public boolean isTerminal(){
        boolean finished = rightShore.isEmpty() && leftShore.size() == N;
        return (timeUpperBound != -1)? finished && totalTime <= timeUpperBound : finished;
    }

    /*
    Creates a list of States produced by the current State (children)
    Each State is created by different combinations of pairs chosen to cross the bridge
    Assumes the person with the shorter duration returns to the right shore
    */
    public ArrayList<State> getChildren(){
        ArrayList<State> children = new ArrayList<>();
        for(int i = 0 ; i<rightShore.size() ; i++){
            for(int j = i+1; j<rightShore.size() ; j++) {
                State child = new State(this);
                Person personA = rightShore.get(i);
                Person personB = rightShore.get(j);
                child.leftShore.add(personA);
                child.leftShore.add(personB);
                child.crossing[0] = personA.getDuration();
                child.crossing[1] = personB.getDuration();
                if (personA.compareTo(personB) <= -1)
                    child.totalTime += personB.getDuration();
                else
                    child.totalTime += personB.getDuration();
                child.rightShore.remove(personA);
                child.rightShore.remove(personB);
                //if the current right shore has more than 2 people, then there are more than one ways to get them across and more children have to be created.
                //Otherwise there is only one child where the last 2 people have crossed together and there are no more people on the child's right shore
                if(rightShore.size() > 2) {
                    //choose a person from the left shore to return
                    for(int k = 0; k< child.leftShore.size(); k++) {
                        Person returningPerson = child.leftShore.get(k);
                        State tempChild = new State(child);
                        tempChild.setFather(this);
                        tempChild.rightShore.add(returningPerson);
                        tempChild.leftShore.remove(returningPerson);
                        tempChild.totalTime += returningPerson.getDuration();
                        tempChild.setReturning(returningPerson);
                        tempChild.heuristic();
                        Collections.sort(tempChild.rightShore);
                        children.add(tempChild);
                    }
                }else{
                    Collections.sort(child.rightShore);
                    children.add(child);
                }
            }
        }
        return children;
    }

    /*
    Heuristic to be used with A*. Calculates the score as the sum on two functions h,g:
        h: Minimum time required for every person left to cross, if they were to pass one by one
        g: Total time taken to get to this state from the root
     */
    private void heuristic() {
        int h = 0;
        for(Person person: rightShore){
            h+=person.getDuration();
        }
        this.score = h + totalTime;
    }

    @Override
    public int compareTo(State o) {
        return Integer.compare(this.score , o.score);
    }

    /*
    Creates a list containing this state and its predecessors
     */
    public void listToPrint(List<State> list){
        list.add(this);
        if(father!=null){
            father.listToPrint(list);
        }
    }

    /*
    Printing current State
    The chosen pair has crossed the bridge together
    Left Shore     Right Shore
    {_,..,_} <----- {_,...,_)
    The fastest on the left goes back with the flashlight
    Left Shore     Right Shore
    {_,..,_} -----> {_,...,_)
    Elapsed Time = minutes from the start
     */
    public void print(){

        StringBuilder left;
        StringBuilder right;
        int paddingCrossing = 4*N + 22;
        int paddingReturning = 4*N - 2;
        String formatCrossing = "{%-" + paddingCrossing + "s} <----- {%s}";
        String formatReturning = "{%-" +paddingReturning+"s} -----> {%s}";

        //print transition to the left
        if(returning!=null) {
            //Begin building string representing left shore
            left = new StringBuilder();
            //show people crossing over to the left shore first
            for(Integer person: crossing){
                left.append("\033[0;1m") //ASCI code - makes following text bold
                        .append(person)
                        .append("\033[0;0m") //illegal ASCI code - ends bold text
                        .append(", ");
            }
            //show every other person already on the left shore
            for (Person person : leftShore) {
                if(person.getDuration() != crossing[0] && person.getDuration() != crossing[1]) {
                    left.append(person)
                            .append(", ");
                }
            }
            //Check if returning person has already been added to the string in a previous loop
            if(!left.toString().contains(String.valueOf(returning.getDuration())))
                left.append(returning).append(", ");
            left.delete(left.length() - 2, left.length() - 1);
            //End of left String

            //Begin building string representing right shore
            right = new StringBuilder();
            for (Person person : rightShore) {
                if (!person.equalsTo(returning))
                    right.append(person).append(", ");
            }
            if(right.length()>3)
                right.delete(right.length() - 2, right.length() - 1);
            //End of right string
            System.out.printf(formatCrossing + "%n", left, right);
        }
        //Print transition to the right
        left = new StringBuilder();
        for(Person person :leftShore){
            left.append(person).append(", ");
        }
        if(left.length()>3)
            left.delete(left.length()-2, left.length()-1);

        right = new StringBuilder();
        for (Person person : rightShore) {
            //make returning person bold
            if(returning != null && person.equalsTo(returning)) {
                right.append("\033[0;1m")
                        .append(person)
                        .append("\033[0;0m")
                        .append(", ");
            }else {
                right.append(person).append(", ");
            }
        }
        if(right.length()>3)
            right.delete(right.length()-2,right.length()-1);
        System.out.printf(formatReturning + "%n", left, right);
        System.out.println("Elapsed Time = " + this.totalTime);
        System.out.println();
    }
}
