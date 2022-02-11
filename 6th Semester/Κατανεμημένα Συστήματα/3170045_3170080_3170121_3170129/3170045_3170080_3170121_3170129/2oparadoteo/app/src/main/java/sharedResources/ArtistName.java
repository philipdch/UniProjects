package sharedResources;

import java.io.Serializable;

public class ArtistName implements Serializable {
    private String artistName;

    public ArtistName (String artist){
        this.artistName = artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String toString(){
        return artistName;
    }
}
