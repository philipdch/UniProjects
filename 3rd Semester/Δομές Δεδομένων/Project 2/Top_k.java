import java.util.*;
import java.io.*;

// Filippos Dourachalis p3170045 
// used a Doubly Linked List instead of a table in order to add items right after reading them 

/*this program gets one int k and one .txt with songs as input 
	* and prints the top k songs based on likes 
	*/
public class Top_k{
	
    private static DoubleLinkedListImpl<Song> songList = new DoubleLinkedListImpl<Song>();
    private static StringTokenizer tokenizer;
    
    public static void main(String[] args){
		int k = 0;
        try{
            k = Integer.parseInt(args[0]);
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println(" number of items to be sorted not specified ");
        }
		try{
            loadFile(args[1]);
        }catch(ArrayIndexOutOfBoundsException e){
            System.err.println(" no file specified ");
        }
		quicksort(songList , songList.getFirst() , songList.getLast());
		if( k > songList.getSize()){
			System.out.println("List contains less than "+k+" songs");
		}else{
			System.out.println("The top "+ k +" songs are :");
			for(int i = 0 ; i< k ; i++)
				System.out.println(songList.removeFromFront());
		}
    }
	
	private static void loadFile(String fileName){
		
		String fileLine = "";
		String token = "";
		int indexOfLikes = 0; //stores the positio of the likes field in each line
        int line = 0; 		  //stores number of line
        String songTitle;	
        int songID;
        int songLikes;
		File file = null;
		BufferedReader reader = null;
		
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
					// 2nd field is title . Since likes were removed ,read additional words until end of line is reached
					token = tokenizer.nextToken();
					while(tokenizer.hasMoreTokens()){
						songTitle += token + " ";
						token = tokenizer.nextToken();
					}
                    songList.insertAtFront( new SongImpl(songLikes ,songTitle ,songID)); //create a song item and place it in list
					
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
	
	private static <T extends Comparable<T>> void quicksort(DoubleLinkedList<T> list ,Node<T> left , Node<T> right){
		if(list.getSize() <= 1)
			return;
		Node<T> partElement = partition(list ,left ,right); //partition Element
		if(partElement!=null){
			if(partElement != left)
				quicksort(list, list.getFirst() ,partElement.getPrevious());
			if(partElement != right)
				quicksort(list, partElement.getNext() , list.getLast());
		}
	}
	
	//T extends Comparable<T> since both Node<T> and DoubleLinkedList<T> extend it in order to be able to compare any object
	private static <T extends Comparable<T>> Node<T> partition(DoubleLinkedList<T> list , Node<T> left , Node<T> right){
		Node<T> i = null;
		Node<T> j = left;
		T partElement = right.getData();
		
		/* while(less(a[++i] ,v);
		*  while(less(v , a[--j])) 
			if(j==l) break;
		*  if(i>= j) break;
		*  exch(a , i , j);
		*/
		for(; j != right ; j = j.getNext()){
			if((j.getData()).compareTo(partElement) == 1){ //sort based on more likes
				i = (i == null) ? left : i.getNext();
				i.swap(j);
			}
		}
		//exch(a , i , r);
		i = ( i == null) ?left : i.getNext();
		i.swap(right);
		return i;
	}
}