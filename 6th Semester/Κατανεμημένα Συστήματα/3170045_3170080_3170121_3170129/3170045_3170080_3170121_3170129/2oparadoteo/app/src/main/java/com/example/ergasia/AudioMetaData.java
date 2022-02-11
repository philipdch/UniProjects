package com.example.ergasia;

public class AudioMetaData{

    private String trackName;
    private String artistName;
    private String albumArtistName;
    private String albumInfo;
    private String genre;
    private String composer;
    private String trackDuration;
    private String year;
    private String trackNumber;
    private String seconds;
    private String minutes;
    private byte[] image;

    public AudioMetaData(){}

    public void setTrackName(String trackName){
        if(trackName==null){
            this.trackName="Unknown";
            return;
        }
        this.trackName=trackName;
    }
    public void setArtistName(String artistName){
        if(artistName==null){
            this.artistName="Unknown";
            return;
        }
        this.artistName=artistName;
    }
    public void setAlbumArtistName(String albumArtistName){
        if(albumArtistName==null){
            this.albumArtistName="Unknown";
            return;
        }
        this.albumArtistName=albumArtistName;
    }
    public void setAlbumInfo(String albumInfo){
        if(albumInfo==null){
            this.albumInfo="Unknown";
            return;
        }
        this.albumInfo=albumInfo;
    }
    public void setGenre(String genre){
        if(genre==null){
            this.genre="Unknown";
            return;
        }
        this.genre=genre;
    }
    public void setComposer(String composer){
        if(composer==null){
            this.composer="Unknown";
            return;
        }
        this.composer=composer;
    }
    public void setTrackDuration(String trackDuration){
        this.trackDuration=trackDuration;
    }
    public void setYear(String year){
        if(year==null){
            this.year="Unknown";
            return;
        }
        this.year=year;
    }
    public void setTrackNumber(String trackNumber){
        if(trackNumber==null){
            this.trackNumber="Unknown";
            return;
        }
        this.trackNumber=trackNumber;
    }
    public void setSeconds(String seconds){
        this.seconds=seconds;
    }
    public void setMinutes(String minutes){
        this.minutes=minutes;
    }
    public void setImage(byte[] image){
        this.image=image;
    }

    public String getTrackName() {
        return this.trackName;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public String getAlbumArtistName() {
        return this.albumArtistName;
    }

    public String getAlbumInfo() {
        return this.albumInfo;
    }

    public String getGenre() {
        return this.genre;
    }

    public String getComposer() {
        return this.composer;
    }

    public String getTrackDuration() {
        return this.trackDuration;
    }

    public String getYear() {
        return this.year;
    }

    public String getTrackNumber() {
        return this.trackNumber;
    }

    public String getSeconds() {
        return this.seconds;
    }

    public String getMinutes() {
        return this.minutes;
    }

    public byte[] getImage() {
        return this.image;
    }
}