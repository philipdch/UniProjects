import java.util.*;

public class SongImpl implements Song{

    private int likes;
    private String title;
    private int ID;

    public SongImpl(){}

    public SongImpl(int likes , String title ,int ID){
        this.likes = likes;
        this.title = (title.length() >=80 )? title.trim().substring(0,81) :title.trim();
        this.ID = (ID >0 && ID <=9999)? ID : 0;
    }

    public int compareTo(Song songB){
        if(songB == null){
            return 0;
        }
        if(this.likes == songB.getLikes()){
            return this.title.compareTo(songB.getTitle());
        }else if(this.likes > songB.getLikes()){
            return 1;
        }
        return -1;
    }
    public int getLikes(){
        return likes;
    }

    public String getTitle(){
        return title;
    }

    public int getID(){
        return ID;
    }

    public void setID(int newID){
        this.ID = (newID>0 && newID <= 9999)? ID :0;
    }

    public void setTitle(String newTitle){
        this.title = (newTitle.length() >=80 )? newTitle.trim().substring(0,81) : newTitle.trim();
    }

    public void setLikes(int newLikes){
        this.likes = newLikes;
    }
	
	public String toString(){
		return title;
	}
}
