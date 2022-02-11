public class Exercise_3 {

    public static boolean subsetExists(int[] W, int C){
        C++;
        boolean[][] f = new boolean[W.length][C];
        //initialise values for first item( when there is only 1 item, the program returns true only if C = item weight)
        for(int i=0; i< C; i++)
            f[0][i] = W[0] == i;
        //initialise first column (when C = 0, the program returns true since it doesn't have to choose any element)
        for(int i=0; i< W.length; i++)
            f[i][0] = true;
        for(int i =1; i< W.length; i++){
            for(int w = 1; w< C; w++){
                if(W[i] > w) //if item weight > subproblem weight then return true only if
                    f[i][w] = f[i-1][w];
                else
                    f[i][w] = f[i-1][w] || f[i-1][w-W[i]];
            }
        }
        return f[W.length-1][C-1];
    }
}

