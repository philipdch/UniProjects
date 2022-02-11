import java.io.*;
import java.util.*;

public class Exercise_4 {

    private Map<String, List<String>> philosopherGraph;
    private List<String> solution;

    public Exercise_4(){
        this.philosopherGraph = new HashMap<>();
    }

    public Exercise_4(HashMap<String, List<String>> graph){
        this.philosopherGraph = graph;
    }

    public Exercise_4(String path){
        this(new File(path));
    }

    public Exercise_4(File input){
        philosopherGraph = createGraph(input);
    }

    public Map<String, List<String>> getGraph(){
        return this.philosopherGraph;
    }

    /* Creates a graph represented by a Map (Adjacency lists) using an input file of the following format:
    *   vertex neighbourVertex
    */
    private static Map<String, List<String>> createGraph(File file){
        Map<String, List<String>> graph = new HashMap<>();
        String line;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        try {
            line = reader.readLine();
            while(line!= null){
                String[] values = line.split("\\s+");
                if(!graph.containsKey(values[0])){
                    graph.put(values[0], new ArrayList<String>());
                }
                if(!graph.containsKey(values[1])){
                    graph.put(values[1], new ArrayList<String>());
                }
                if(!graph.get(values[0]).contains(values[1])){
                    graph.get(values[0]).add(values[1]);
                }
                if(!graph.get(values[1]).contains(values[0])){
                    graph.get(values[1]).add(values[0]);
                }
                line = reader.readLine();
            }
        }catch(IOException e){
            System.err.println("Error reading file");
        }
        return graph;
    }

    /* Classic BFS algorithm with the addition of 2 loops to find the maximum δ(s,u) from the starting vertex s */
    private List<String> diameterBFS(Map<String, List<String>> G, String s){
        LinkedList<String> queue = new LinkedList<>();
        //stores every vertex in order by which it is found, so that we can check if we have already encountered it.
        //Also used as a way to store each vertex's index to store it's predecessor and distance from it
        List<String> foundVertexes = new ArrayList<>();
        int[] vertexDistance = new int[G.size()]; //stores for every vertex it's distance from s. First cell represents s itself
        int[] predecessorIndex = new int[G.size()];
        foundVertexes.add(s); //index 0 represents starting vertex s
        queue.add(s);
        while(!queue.isEmpty()){
            String u = queue.removeFirst();
            for(String v: G.get(u)){ //gets all vertices associated with u. If u isn't associated with any vertex or all vertices have already been found, loop ends
                if(!foundVertexes.contains(v)){ //Vertex v hasn't been discovered already and it's associated with u (and therefore with s).
                    foundVertexes.add(v);
                    //use vertex's index to update distance and predecessor
                    vertexDistance[foundVertexes.indexOf(v)] = vertexDistance[foundVertexes.indexOf(u)] +1;
                    predecessorIndex[foundVertexes.indexOf(v)] = foundVertexes.indexOf(u);
                    queue.add(v);
                }
            }
        }
        //find max distance among all vertices associated with s
        int maxDistance = vertexDistance[0];
        int maxIndex = 0;
        for(int i =1; i<vertexDistance.length; i++){
            if(vertexDistance[i] > maxDistance){
                maxDistance = vertexDistance[i];
                maxIndex = i;
            }
        }
        //recreate the path from the furthest vertex from s by visiting each predecessor up to s
        LinkedList<String> path = new LinkedList<>();
        while(maxIndex != 0) {
            path.addFirst(foundVertexes.get(maxIndex));
            maxIndex = predecessorIndex[maxIndex];
        }
        path.addFirst(foundVertexes.get(0));
        return path;
    }

    /*Finds the maximum diameter of a given graph. Since diameterBFS returns the maximum δ(s, u)
      for a given starting vertex, we need to calculate the diameter of every tree produced by selecting
      every possible starting vertex and then pick their max
     */
    public void maximumDiameter(){
        List<List<String>> diameters = new ArrayList<>();
        for(String vertex: philosopherGraph.keySet()){
            diameters.add(diameterBFS(philosopherGraph, vertex));
        }
        /*since the diameter of a tree is the biggest δ(u,v) and each list contains all the vertices from u to v, we simply need
          to compare the lists' size and pick the largest
        */
        int maxIndex = 0;
        int maxDiameter = diameters.get(0).size();
        for(int i =1; i< diameters.size(); i++ ){
            if (diameters.get(i).size() > maxDiameter){
                maxDiameter = diameters.get(i).size();
                maxIndex = i;
            }
        }
        solution = diameters.get(maxIndex);
    }

    public void printInputData()
    {
        if( philosopherGraph !=null )
        {
            System.out.println( "Size of Adjacency-List: "+philosopherGraph.size() );
            for (String key   : philosopherGraph.keySet())
            {
                List<String> values = philosopherGraph.get(key);
                System.out.println( "-Node "+key+" has neighbors: "+Arrays.toString(values.toArray()) );
            }
            System.out.println();
        }
        else
            System.out.println("Input table is null.");
    }

    public void printSolution(){
        System.out.print("{");
        for(int i =0; i< solution.size() -1; i++){
            System.out.print(solution.get(i)+", ");
        }
        System.out.println(solution.get(solution.size()-1)+"}");
    }
}
