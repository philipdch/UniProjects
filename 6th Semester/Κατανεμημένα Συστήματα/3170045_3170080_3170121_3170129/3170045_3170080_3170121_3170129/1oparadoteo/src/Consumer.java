import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.io.FileUtils;
import sharedResources.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class Consumer extends Node {

    private boolean offline = false;
    private boolean terminated = false;
    private Socket socketClient ;
    private int p;
    private String ip;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String name;
    private ArrayList <Value> toPlay = new ArrayList<Value>();



    private void terminate(){
        this.terminated = true;
    }

    private void activateOffline(){
        this.offline = true;
    }

    /*
    creates the first ever connections for consumer to a default Broker
     */
    public void init(int i, String ip){
        try {
            System.out.println("Attempting Connection...");
            socketClient = new Socket(InetAddress.getByName(ip), i);
            System.out.println("Connected to:"+ ip + " " + i);
            //streams
            output = new ObjectOutputStream(socketClient.getOutputStream());
            output.flush();
            input = new ObjectInputStream(socketClient.getInputStream());
            System.out.println("Got I/O streams");
        }
        catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    /*
    connects to next server-Broker
     */
    public void connect(String ip, int i) throws IOException{
        System.out.println("Attempting Connection...");
        socketClient = new Socket(InetAddress.getByName(ip), i);
        System.out.println("Connected to next server:"+ ip);
        //streams
        output = new ObjectOutputStream(socketClient.getOutputStream());
        output.flush();
        input = new ObjectInputStream(socketClient.getInputStream());
        System.out.println("Got I/O streams");
    }


    /*
    Ypotithetai oti einai tis morfis
    public void disconnect(Broker b, sharedResources.ArtistName artistName)throws  IOException
     */
    public void disconnect() throws  IOException {
        socketClient.close();
        System.out.println("Connection closed");
    }

    /*
    the following method performs the exchange of
    information between client and client handler class ActionsforCLients
     */
    private boolean register(Scanner scn) throws IOException {
        output.writeObject("Consumer");
        boolean wrongBroker = false;
        while(true){
            //System.out.println("in consumer");

            try {
                String received = (String) input.readObject();
                System.out.println(received);//print type an artist's name
                String tosend = scn.nextLine();
                output.writeObject(tosend);

                if (tosend.equals("exit")) {
                    System.out.println("Closing this connection : " + socketClient);
                    disconnect();
                    System.out.println("Are you sure you want to exit the app? <Yes,No>");
                    String exitApp = scn.nextLine();
                    if(exitApp.equalsIgnoreCase("Yes")) terminate();
                    break;
                }

                received = (String) input.readObject();

                if(received.equals("Incorrect broker!")){
                    //receive last searched artist
                    name = (String) input.readObject();
                    //receive Broker list from broker
                    this.setBrokers((ArrayList<BrokerInfo>)input.readObject());
                    System.out.println("Look in another broker!");
                    for(BrokerInfo b: this.getBrokers()){
                        System.out.println(b.getIpAddress()+ " " + b.getPort());
                    }
                    System.out.println("Closing this connection : " + socketClient);
                    disconnect();
                    wrongBroker=true;
                    break;
                } else {
                    System.out.println(received);//Print Searching for songs from artist:
                    System.out.println("Waiting for response");
                    List <String> songsToChoose = ((List<String>)input.readObject());

                    for (String song: songsToChoose){
                        System.out.println(song);
                    }

                    System.out.println("Type in song: ");
                    output.writeObject(scn.nextLine());
                    boolean answer = input.readBoolean();
                    received = (String) input.readObject();

                    if(!answer){
                        System.out.println(received);
                    }
                    else{
                        System.out.println(received);

                        int chunksNo = (int) input.readObject();
                        for (int i=0; i<chunksNo; i++) {
                            toPlay.add((Value) input.readObject());
                        }

                        if(!offline){
                            playData(chunksNo);
                            toPlay.clear();
                        }

                        else{
                            storeOfflineData();
                            toPlay.clear();
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }//end while
        return wrongBroker;
    }

    /*
    play data
     */
    public void playData(int chunksNo){
        //an i leitoyrgia einai na ta pairnei apo ti domi kai na ta emfanizei
        for (Value val: toPlay){
            MusicFile musicFile = val.getMusicFile();
            val.getMusicFile().setTrackName(val.getMusicFile().getTrackName()+ "_"+chunksNo);
            try {
                try {
                    File file = new File("./src/temp/"+musicFile.getTrackName()+".mp3");
                    FileUtils.writeByteArrayToFile(file, musicFile.getMusicFileExtract());
                    Mp3File tempMp3 = new Mp3File(file);
                }catch(UnsupportedTagException | InvalidDataException e){
                    System.err.println("Error creating mp3 File");
                }

                System.out.println(val.getMusicFile());
            }catch(IOException e){
                System.err.println("Error creating file");
            }
        }
    }

    public void storeOfflineData(){

        //count each chunk's byte array elements and add to fileSize
        int fileSize=0;
        for (Value val: toPlay){
            System.out.println("****Chunk " + val.getMusicFile().getTrackName() + " has " + val.getMusicFile().getMusicFileExtract().length +" bytes.");
            fileSize = fileSize + val.getMusicFile().getMusicFileExtract().length;
        }

        //create byte array for final file
        byte [] toSave = new byte[fileSize];
        int j=0; //pointer to toSave array
        for (Value v: toPlay){
            for (int i=0; i<v.getMusicFile().getMusicFileExtract().length; i++){
                toSave[j] = v.getMusicFile().getMusicFileExtract()[i];
                j++;
            }
        }
        System.out.println("****Total size of song " + toSave.length + " bytes.");

        //write byte array in mp3File
        try {
            File file = new File("./src/temp/"+toPlay.get(0).getMusicFile().getTrackName()+".mp3");
            FileUtils.writeByteArrayToFile(file, toSave);

        }catch(IOException e){
            System.err.println("Error creating file");
        }
    }

    public void readInitFile(String path){
        BufferedReader reader=null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(new File(path)));
            line = reader.readLine();
            String[] l = line.split(" ");
            ip = l[0];
            p = Integer.parseInt(l[1]);
        } catch(IOException  e){
            System.out.println("Error reading file !");
            e.printStackTrace();
        }

    }


    public static void main(String[] args){
        try{
            Scanner scn=new Scanner(System.in);

            Consumer consumer=new Consumer();

            consumer.readInitFile(args[0]);
            /*
            Initiate first connection with server-Broker
             */
            consumer.init(consumer.p, consumer.ip);

            System.out.println("Offline mode? <Yes,No>");
            if (scn.nextLine().equalsIgnoreCase("Yes")) consumer.activateOffline();


            while(true) {
            /*
            If connection was terminated due to typing in an artist name not found in the specified Broker,
            we have to establish a new connection: Either by connecting to the right broker (s=true, artist's name is found in another broker)
            or by connecting to the same broker again (s=false, artist's name not available in any broker you have to try again)
            */
                if (consumer.register(scn)) {
                    String newIp = "";
                    int newPort = 0;
                    System.out.println(consumer.name);
                    for (BrokerInfo b : consumer.getBrokers()) {
                        for (ArtistName n : b.getArtistList()) {
                            if (n.getArtistName().equals(consumer.name)) {
                                System.out.println(b.getPort());
                                newIp = b.getIpAddress();
                                newPort = b.getPort();
                                break;
                            }
                        }
                    }
                    if (newIp.equals("")) {
                        System.out.println("Unavailable Artist. Try Again :)");
                        consumer.connect(consumer.ip, consumer.p);
                    } else {
                        consumer.connect(newIp, newPort);
                    }
                }
                if (consumer.terminated) break;
            }
            scn.close();
            consumer.output.close();
            consumer.input.close();

        }//end try
        catch (IOException e){
            e.printStackTrace();
        }//end catch
    }//end main


}//end Consumer