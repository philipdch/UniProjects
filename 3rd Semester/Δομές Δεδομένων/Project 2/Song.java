/** defines the methods for a song object */
public interface Song extends Comparable<Song>{

    /**
     * compares two songs based on likes . If # of likes is the same for both objects
     * then compares them alphabetically
     * 
     * @param songB is the song which will be compared to this one
     * @return 0 if songB == this.song or if number of likes is the same ,1 if number of likes of songB is lesser than this song's likes and -1 otherwise
     */
    public int compareTo(Song songB);

    /**
     * returns this song's ID number
     * @return song ID
     */
    public int getID();
    
    /** 
     * returns the song's title
     * @return song title
     */
    public String getTitle();
    
    /** 
     * returns how many likes this song has
     * @return song likes
     */
    public int getLikes();
    
    /** 
     * sets a different ID for this song as long as ID>0 && ID<= 9999 .Else sets the ID to 0 (meaning the song is not to be taken into account)
     * @param newId is the new ID of the song
     */
    public void setID(int newID);
    
    /** 
     * sets the current number of likes for the song 
     * @param newLikes new number of likes 
     */
    public void setLikes(int newLikes);
    
    /** 
     * sets a new title for this song
     * @param newTitle is the new title to be given to this song
     */
	public void setTitle(String newTitle);
}