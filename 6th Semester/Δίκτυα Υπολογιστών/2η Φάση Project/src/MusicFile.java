public class MusicFile extends ChunkFile {
    private String artistName;
    private String albumInfo;
    private int genre;

    public MusicFile(String track, String artist, String album, int genre, byte[] musicFileExtract) {
        super(musicFileExtract, track);
        this.artistName = artist;
        this.albumInfo = album;
        this.genre = genre;
    }

    public String getArtistName() {
        return this.artistName;
    }

    public String getAlbumInfo() {
        return this.albumInfo;
    }

    public int getGenre() {
        return genre;
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

    public String toString() {
        return "Track: " + getName() + "\n" + "Artist: " + this.artistName + "\n" + "Album Info: " + this.albumInfo + "\n" + "Genre: " + this.genre;
    }
}

