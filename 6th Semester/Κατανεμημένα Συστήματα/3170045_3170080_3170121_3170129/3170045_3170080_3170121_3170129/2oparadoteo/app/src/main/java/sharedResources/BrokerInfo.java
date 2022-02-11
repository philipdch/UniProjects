package sharedResources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BrokerInfo implements Serializable {
    private String ipAddress;
    private int port;
    private int hash;//the result of the hash(ip+port) in init
    private List<ArtistName> artistList = new ArrayList<>();
    private Range range;

    public BrokerInfo(){}

    public BrokerInfo(String ipAddress, int port){
        this.ipAddress=ipAddress;
        this.port=port;
    }

    public String getIpAddress(){
        return this.ipAddress;
    }

    public int getPort(){
        return this.port;
    }

    public void setIpAddress(String ipAddress){
        this.ipAddress=ipAddress;
    }

    public void setPort (int port){
        this.port=port;
    }

    public String toString(){
        return "Ip: " + this.ipAddress + "\n" + "Port: " + this.port;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public List<ArtistName> getArtistList() {
        return artistList;
    }

    public void setArtistList(ArtistName newName) {
        for(ArtistName name: artistList){
            if(name.getArtistName() == newName.getArtistName())
                return;
        }
        artistList.add(newName);
    }

    public void displayArtists(){
        for(ArtistName a:artistList){
            System.out.println(a );
        }
    }

    public void removeArtist(ArtistName toRemove){
        if(toRemove == null)
            return;
        Iterator<ArtistName> iter = artistList.listIterator();
        while(iter.hasNext()){
            if(iter.next().getArtistName().equals(toRemove.getArtistName())){
                iter.remove();
                return;
            }
        }
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }
}
