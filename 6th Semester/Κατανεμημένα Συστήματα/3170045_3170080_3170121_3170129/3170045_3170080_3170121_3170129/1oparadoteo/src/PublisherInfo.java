import com.mpatric.mp3agic.*;
import org.apache.commons.io.FilenameUtils;
import sharedResources.ArtistName;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublisherInfo implements Serializable {
    private List<ArtistName> keys = new ArrayList<>(); //stores every key (artist) that belongs to this publisher
    private Map<String, List<String>> mp3List = new HashMap<>(); //stores for every artist, a list with all their songs
    private String range;
    private String pubIpAddress;
    private int pubPort;
    private boolean running;

    public PublisherInfo(){
        this("[A-Z]");
    }

    public PublisherInfo(String range){
        this(range, 5555);
    }

    public PublisherInfo(String range, int port){
        this.range = range;
        this.pubIpAddress = InetAddress.getLoopbackAddress().getHostAddress();
        this.pubPort = port;
        this.running = true;
    }

    public boolean isRunning(){
        return this.running;
    }

    public void stop(){
        this.running = false;
    }
    /*
    Returns all the artists that this publisher oversees
     */
    public List<ArtistName> getKeys(){
        return this.keys;
    }

    /*
    Given an artist name, returns a list with all their songs
    */
    public List<String> getSongs(ArtistName artist){
        String name = artist.getArtistName();
        return mp3List.get(name);
    }

    /* Given an artist(Key) and a song(sharedResources.Value), this method
        1) Checks if the map mp3List already has this key
        2) a. If not, creates for this key a new List to store the artist's songList
           b. If the key already exists, checks if the songList already contains this song and adds it
     */
    public void addSong(ArtistName artist, String newSong){
        List<String> songList = mp3List.get(artist.getArtistName());
        if(songList == null){
            songList = new ArrayList<>();
            songList.add(newSong);
            mp3List.put(artist.getArtistName(), songList);
        }else{
            if(!songList.contains(newSong)){
                songList.add(newSong);
            }
        }
    }

    public String getRange(){
        return this.range;
    }

    public String getIpAddress(){
        return this.pubIpAddress;
    }

    public int getPort(){
        return this.pubPort;
    }

    public void setKeys(ArrayList<ArtistName> artists){
        this.keys = artists;
    }

    public void setRange(String newRange){
        this.range = newRange;
    }

    public void setPubIpAddress(String newAddress){
        this.pubIpAddress = newAddress;
    }

    public void setPubPort(int newPort){
        this.pubPort = newPort;
    }

    public boolean equals(PublisherInfo object){
        if(pubPort == object.pubPort && pubIpAddress.equals(object.pubIpAddress))
            return true;
        return false;
    }

    /* Returns true if the artist List already contains this key
    */
    public boolean hasArtist(ArtistName artist){
        for(ArtistName key: keys){
            if(artist.getArtistName().equals(key.getArtistName()))
                return true;
        }
        return false;
    }

    /* Adds this artist key to the list, provided it doesn't already exist
     */
    public void addArtist(ArtistName artist){
        if(!hasArtist(artist))
            keys.add(artist);
    }

    /* Scans a directory recursively in order to find every mp3 file in it, then gets it's artist and and title.
        If the artist(key) belongs to this publisher's range then checks to see if it has not already been added
        and adds it to the publisher's key list. Also maps the song's name to this artist key
     */
    public void findArtists(final File directory) { //searches an entire directory for mp3 files, gets the artist associated with each one and checks if they are in Publisher's range
        //initialise regex
        Pattern pattern = Pattern.compile(range);
        Matcher matcher = null;
        File[] fileList = directory.listFiles(); //get list of files in directory
        for (int i = 0; i < fileList.length; i++) {
            File file = fileList[i];
            String artist = null;
            if (file.isDirectory()) {
                findArtists(file); //search additional folders
            } else if (file.isFile()) {
                String ext = FilenameUtils.getExtension(file.getPath()); //get file extension
                String songName = FilenameUtils.getBaseName(file.getPath());
                if (!ext.equals("mp3") || file.getName().contains("._")) {
                    continue;
                }
                try {
                    Mp3File mp3File = new Mp3File(file.getPath());
                    if (mp3File.hasId3v1Tag()) {
                        ID3v1 id3v1tag = mp3File.getId3v1Tag();
                        artist = id3v1tag.getArtist();
                    }
                    if (mp3File.hasId3v2Tag()) {
                        ID3v2 id3v2tag = mp3File.getId3v2Tag();
                        if (artist == null)
                            artist = (id3v2tag.getArtist() != null) ? id3v2tag.getArtist() : id3v2tag.getComposer();
                    }
                } catch (IOException | InvalidDataException | UnsupportedTagException e) {
                    System.err.println("Error reading mp3 file " + file.getPath());
                }
                if (artist != null) {
                    if (artist.equals(""))
                        continue;
                    String firstLetter = "" + artist.trim().toUpperCase().charAt(0);
                    matcher = pattern.matcher(firstLetter);
                    if (matcher.find()) {
                        ArtistName newArtist = new ArtistName(artist);
                        addSong(new ArtistName(artist), songName);
                        addArtist(newArtist);
                    }
                }
            }
        }
    }

    public String toString(){
        return "Publisher info: \nsharedResources.Range: "+this.range+"\nIP Address: "+ this.pubIpAddress+"\nListening on port: "+this.pubPort;
    }


}
