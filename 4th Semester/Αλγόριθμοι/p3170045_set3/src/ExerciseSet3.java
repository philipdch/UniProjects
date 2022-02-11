import java.io.File;

public class ExerciseSet3 {

    public static void main(String[] args){
        System.out.println("\nExercise 1\n");
        ex1();
        System.out.println("\nExercise 3\n");
        ex3();
        System.out.println("\nExercise 4\n");
        ex4();
    }

    public static void ex1()
    {
        int[] intArray = new int[]{ 5,4,3,2,1 };
        Exercise_1 exercise1 = new Exercise_1(intArray);
        exercise1.printSolution();
    }

    public static void ex3()
    {
        int[] S = new int[]{ 1, 5, 34, 9, 2, 10, 3};
        int M = 20;
        System.out.println(Exercise_3.subsetExists(S, M));
    }

    public static void ex4(){
        Exercise_4 ex4 = new Exercise_4( new File("./graphTest" ));
        ex4.printInputData();
        ex4.maximumDiameter();
        ex4.printSolution();
    }
}
