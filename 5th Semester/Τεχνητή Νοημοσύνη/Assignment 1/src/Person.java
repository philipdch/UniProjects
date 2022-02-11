public class Person implements Comparable<Person>{
    private int duration;
    String name;



    public Person(){}

    /**
     *
     * @param duration Minutes it takes for the person to cross the bridge
     */
    public Person(int duration) {this.duration = duration;}

    public Person(int duration, String name) {
        this.duration = duration;
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public int compareTo(Person o) {
        if(o.duration > this.duration){
            return -1;
        }else if(o.duration < this.duration){
            return 1;
        }else {
            return 0;
        }
    }

    /*
    Checks whether Person objects are equal (have equal attributes)
     */
    public boolean equalsTo(Person o){
        if(this.name!=null && o.name!=null){
            return this.compareTo(o)==0 && this.name.equals(o.name);
        }
        else {
            return this.compareTo(o)==0; //if this person's duration is equal to o's duration (true) => this.compareTo(o)==0
        }
    }

    public String toString(){
        return ""+this.duration;
    }
}
