import java.util.*;
import java.io.*;

/*this program gets one int k and one .txt with songs as input 
	* and prints the top k songs based on likes 
	*/
public class Top_k_With_PQ{
	
    private static PQ<Song> songQueue;
    private static StringTokenizer tokenizer;
    
    public static void main(String[] args){
		int k = 0;
        try{
            k = Integer.parseInt(args[0]);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println(" number of items to be sorted not specified ");
        }
		try{
			songQueue = new PQ<Song>(2*k , new SongComparator());
            loadFile(args[1] ,k);
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println(" no file specified ");
        }
		System.out.println("The top "+k+" songs are : ");
		for(int i = 0 ; i<k ; i++){
			System.out.println(songQueue.getMax());
		}
    }
	
	private static void loadFile(String fileName , int k){
		
		String fileLine = "";
		String token = "";
		int indexOfLikes = 0; //stores the position of the likes field in each line
        int line = 0; 		  //stores number of line
        String songTitle;	
        int songID;
        int songLikes;
		File file = null;
		BufferedReader reader = null;
		Song newSong;
		Song minSong;
		
        try{
			file = new File(fileName);   
        } catch (NullPointerException e) {
            System.err.println("File not found");
        }
        try{
            reader = new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException e ){
                System.out.println("Error opening file");
        }
        try {
            fileLine = reader.readLine();
            while(fileLine != null){
                tokenizer = new StringTokenizer(fileLine);
				// read likes and remove this field from line
				indexOfLikes = fileLine.lastIndexOf(" ") +1;
				songLikes = Integer.parseInt(fileLine.substring(indexOfLikes));
				fileLine = fileLine.substring(0 , indexOfLikes);
                while(tokenizer.hasMoreTokens()){
					//read first field of line (ID) 
					token  = tokenizer.nextToken();
                    songID = Integer.parseInt(token);
					songTitle = "";
					// 2nd field is title . Since likes were removed ,read additional words until end of line is reached and add them to title
					token = tokenizer.nextToken();
					while(tokenizer.hasMoreTokens()){
						songTitle += token + " ";
						token = tokenizer.nextToken();
					}
					
					minSong = songQueue.getMin();
                    newSong = new SongImpl(songLikes ,songTitle ,songID); //create a song item and place it in list
					if(songQueue.size() < k){
						songQueue.insert(newSong); //insert songs as long as number of songs is less than k
					}else{						   //if maximum capacity of queue is reached (k) then last song must be removed in order for new one to be added
						if(newSong.compareTo(minSong) == 1){   //if song to be inserted has more likes than min song in queue then
							songQueue.remove(minSong.getID()); //remove last song in queue
							songQueue.insert(newSong);		   //insert new song
						}
					}
				}
				fileLine = reader.readLine();
                line++;
			}
        } catch (IOException e) {
            System.err.println("Error reading line : "+ line);
        }
		try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing file.");
        }
	}
}