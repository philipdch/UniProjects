import sharedResources.ArtistName;
import sharedResources.BrokerInfo;
import sharedResources.Value;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class ActionsForClients extends  Thread{
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private Broker callerBroker;
    private ArrayList<BrokerInfo> brokers;
    private String clientType;

    //for Publisher connection (Handle Consumer)
    private ObjectOutputStream outputPub;
    private ObjectInputStream inputPub;
    private Queue<Value> songsToSend =new LinkedList<>();

    public ActionsForClients(Socket connection, ObjectInputStream input, ObjectOutputStream output, Broker callerBroker) {
            this.connection = connection;
            this.input = input;
            this.output = output;
            this.callerBroker = callerBroker;
            this.brokers = callerBroker.getBrokers();
    }

    public void run() {
        try{
            this.clientType = (String) input.readObject();
            System.out.println(clientType);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //System.out.println("run called");
        switch(clientType.toLowerCase()) {
            case "consumer":
                handleConsumer();
                break;
            case "publisher":
                handlePublisher();
                break;
            default :
                System.out.println("User not specified!");
        }
    }//run

    public void handlePublisher() {
        try {
            output.flush();
            try {
                output.writeObject("Awaiting Publisher input");
                Object received = input.readObject();
                PublisherInfo receivedInfo = (PublisherInfo) received;
                if(receivedInfo.isRunning()) {
                    callerBroker.registerPublisher(receivedInfo);
                    callerBroker.calculateKeys(receivedInfo);
                }else{ //if publisher has been terminated, update broker's data structures
                    System.out.println("\n\n---------------\nPublisher exits\n---------------\n");
                    callerBroker.updateNode(receivedInfo);
                }
                output.writeObject("Publisher information received successfully");
                connection.close();
            } catch (ClassNotFoundException e) {
                System.err.println("Couldn't resolve received Object");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (!connection.isClosed()) {
                    System.out.println("Closing this connection.");
                    connection.close();
                }
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//finally
    }

    public void handleConsumer(){
        String received;
        try {
            while (true) {

                output.writeObject("Awaiting artist name input");
                // receive the answer from client
                received = (String) input.readObject();
                ArtistName artist = new ArtistName(received);
                System.out.println("Artist name received from :" + this.connection + "\n"+artist);
                int i = findArt(artist);

                if (received.equals("exit")) {
                    System.out.println("Client " + this.connection + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.connection.close();
                    System.out.println("Connection closed");
                    break;
                }
                /*
                if this Broker is the right one (has artist)
                 */
                if (callerBroker.getInfo().getHash() == i){

                    output.writeObject("Searching for songs from artist: " +artist.getArtistName());

                    PublisherInfo pubToReach = callerBroker.retreivePublisher(artist.getArtistName());
                    List<String> songsToChoose = pubToReach.getSongs(artist);
                    output.writeObject(songsToChoose);
                    String requestedSong = (String) input.readObject(); //read song entered by client
                    if(pubToReach.getSongs(artist).contains(requestedSong)){

                        //pull
                        boolean pullSuccess = pull(pubToReach, artist, requestedSong);
                        if(!pullSuccess){
                            continue;
                        }
                        for (Value v: songsToSend){
                            System.out.println(v.getMusicFile());
                        }

                    } else {
                        output.writeBoolean(false); //sent broker answer
                        output.writeObject("The song you requested doesn't exist. Please try again");
                    }
                } else {
                    output.writeObject("Incorrect broker!");
                    //sends last searched artist
                    output.writeObject(received);
                    //tou stelnei to arxeio
                    output.writeObject(brokers);
                    break;
                }
                //output.writeObject("eimai edw");
            }//while
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }//catch
        finally{
            try {
                if (this.connection!= null){
                    //System.out.println("Client " + this.connection + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.connection.close();
                }
                input.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//finally
    }

    public void connectToPub(String ip, int i) throws IOException {
        //search for publisher
        System.out.println("Attempting Connection to Publisher...");
        connection = new Socket(InetAddress.getByName(ip), i);
        System.out.println("Connected to Pubisher as client:"+ ip +" " + i);
        //streams
        outputPub = new ObjectOutputStream(connection.getOutputStream());
        outputPub.flush();
        inputPub = new ObjectInputStream(connection.getInputStream());
        System.out.println("Got I/O streams");
    }

    public boolean pull(PublisherInfo pubToReach, ArtistName artist, String requestedSong) throws IOException, ClassNotFoundException {
        //sent broker's answer
        try {
            connectToPub(pubToReach.getIpAddress(), pubToReach.getPort());
        }catch(IOException e){
            System.err.println("Server "+pubToReach.getIpAddress()+":"+pubToReach.getPort()+" is unavailable. Please try again later");
            output.writeBoolean(false);
            output.writeObject("Server is unavailable at the moment. Please try again later");
            return false;
        }
        //in Publisher connection
        output.writeBoolean(true);
        output.writeObject("Reaching for your song");
        System.out.println("Sending song name and artist name");
        outputPub.writeObject(requestedSong);
        outputPub.writeObject(artist.getArtistName());
        int chunks_length=(int)inputPub.readObject();
        output.writeObject(chunks_length);
        for(int j=0; j<chunks_length; j++) {
            songsToSend.add((Value) inputPub.readObject());
            output.writeObject(songsToSend.remove());
        }
        return true;
    }

    public int findArt(ArtistName artistName){
        for(ArtistName n: callerBroker.getInfo().getArtistList()){
            System.out.println(n+" equals "+artistName+ " = " + n.getArtistName().equals(artistName.getArtistName()));
            if (n.getArtistName().equals(artistName.getArtistName())){
                return callerBroker.getInfo().getHash();
            }
        }
        return -1;
    }

}//class