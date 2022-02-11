import java.io.Serializable;
import java.util.TreeSet;

/*  Class representing the chunks of a file.
    Also stores whether or not the peer is the initial seeder for this file (and its chunks).
*/
public class ChunksWrapper implements Serializable {
    private TreeSet<String> chunks = new TreeSet<>();
    private int isInitSeeder;

    public ChunksWrapper(){}

    public ChunksWrapper( TreeSet<String> chunks, int isInitSeeder){
        this.chunks = chunks;
        this.isInitSeeder = isInitSeeder;
    }

    public TreeSet<String> getChunks() {
        return chunks;
    }

    public int isInitSeeder(){
        return isInitSeeder;
    }

    public void setChunks(TreeSet<String> chunks) {
        this.chunks = chunks;
    }

    public void setInitSeeder(int initSeeder) {
        isInitSeeder = initSeeder;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("(" + isInitSeeder + "): ");
        for(String chunk: chunks){
            System.out.println("Appending: " + chunk);
            string.append(chunk).append(", ");
        }
        string.deleteCharAt(string.length()-2);
        return string.toString();
    }
}
