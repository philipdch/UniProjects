package sharedResources;

import java.io.Serializable;

public class MusicFile implements Serializable {
    private String trackName;
    private String artistName;
    private String albumInfo;
    private int genre;
    private byte[] musicFileExtract;

    public MusicFile(){}

    public MusicFile(String track, String artist, String album, int genre, byte[] musicFileExtract){
        this.trackName = track;
        this.artistName = artist;
        this.albumInfo = album;
        this.genre = genre;
        this.musicFileExtract = new byte[musicFileExtract.length];
        for(int i = 0; i<musicFileExtract.length; i++) {
            this.musicFileExtract[i] = musicFileExtract[i];
        }
    }

    public String getTrackName(){
        return this.trackName;
    }

    public String getArtistName(){
        return this.artistName;
    }

    public String getAlbumInfo(){
        return this.albumInfo;
    }

    public int getGenre() {
        return genre;
    }

    public byte[] getMusicFileExtract() {
        return musicFileExtract;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setAlbumInfo(String albumInfo) {
        this.albumInfo = albumInfo;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public void setMusicFileExtract(byte[] musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    public String toString(){
        return "Track: " + this.trackName + "\n" + "Artist: " + this.artistName + "\n" + "Album Info: " + this.albumInfo + "\n" + "Genre: " + this.genre;
    }


}
