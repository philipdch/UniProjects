package sharedResources;

import java.io.Serializable;

public class Value implements Serializable{
    private sharedResources.MusicFile musicFile;

    public Value(sharedResources.MusicFile musicFile){
        this.musicFile = musicFile;
    }



    public sharedResources.MusicFile getMusicFile() {
        return musicFile;
    }

    public void setMusicFile(sharedResources.MusicFile musicFile) {
        this.musicFile = musicFile;
    }

}
