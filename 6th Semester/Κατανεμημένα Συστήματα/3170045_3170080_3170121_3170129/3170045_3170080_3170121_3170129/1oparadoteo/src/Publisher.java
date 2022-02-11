import sharedResources.ArtistName;
import sharedResources.BrokerInfo;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Publisher extends Node{

    private PublisherInfo info;
    private ServerSocket serverBroker;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outStream;
    private File searchDirectory;

    public Publisher(String range){
        this(range, 5555);
    }

    public Publisher(String range, int port){
        info = new PublisherInfo(range, port);
        try {
            serverBroker = new ServerSocket(port);
        }catch(IOException e){
            System.err.println("Error initialising server");
        }
    }

    public PublisherInfo getInfo(){
        return this.info;
    }

    public File getSearchDirectory(){
        return searchDirectory;
    }

    public void init(int port){
        for(BrokerInfo broker: getBrokers()){
            connectToBroker(broker.getIpAddress(), broker.getPort());
        }
    }

    public void connect(String ip, int port) throws IOException { //connects to a broker
        System.out.println("Initialising connection");
        socket = new Socket(InetAddress.getByName(ip), port);
        System.out.println("Connected to: " + socket.getInetAddress().getHostAddress());
    }

    public void disconnect(){
        try{
            if(inputStream != null) {
                inputStream.close();
                outStream.close();
                socket.close();
            }
        }catch(IOException e){
            System.err.println("The server has already closed this connection");
            e.printStackTrace();
        }
    }

    public void connectToBroker(String ip, int port) {
        try {
            connect(ip, port);
            outStream = new ObjectOutputStream(socket.getOutputStream());
            outStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
            outStream.writeObject("Publisher");
            try {
                Object received = inputStream.readObject();
                System.out.println((String) received);
                System.out.println("Sending Publisher info");
                outStream.writeObject(info);
                received = inputStream.readObject();
                System.out.println(received);
                System.out.println("Ending the connection");
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found");
            }
            inputStream.close();
            outStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptConnection() {
        ObjectOutputStream out;
        ObjectInputStream in = null;
        try {
            while (info.isRunning()) {
                socket = serverBroker.accept();
                System.out.println(socket.getInetAddress().getHostName() + " connected to server");
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                //Assign a new thread to this client: handleClients
                Thread actionForBroker = new HandleClients(out, in, socket, this);

                //Thread invoke start method
                actionForBroker.start();
            }
        } catch (IOException e) {
            System.err.println("Error accepting connection");
        }
    }

    public void readInitFile(String data){
        BufferedReader reader=null;
        String line;

        try {
            reader = new BufferedReader(new FileReader(new File(data)));
            line = reader.readLine();

            for(int i=0; i<3; i++){
                String[] l = line.split(" ");

                System.out.println(l[0] + " " + l[1]);
                BrokerInfo bInfo=new BrokerInfo(l[0], Integer.parseInt(l[1]));
                setBroker(bInfo);

                line = reader.readLine();
            }
            reader.close();
        } catch(IOException  e){
            System.out.println("Error reading file !");
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        for(String string: args){
            System.out.println(string);
        }
        Publisher publisher = null;
        if(args.length <= 0) {
            publisher = new Publisher("[A-D]");
        }else if(args.length == 1){
            publisher = new Publisher(args[0]);
        }else{
            publisher = new Publisher(args[0], Integer.parseInt(args[1]));
        }
        if(args.length == 4){
            System.out.println(publisher.info);
            List<String> list = new ArrayList<>();
            publisher.searchDirectory = new File(args[2]);
            publisher.getInfo().findArtists(publisher.searchDirectory);
            for(ArtistName name: publisher.getInfo().getKeys()){
                System.out.println(name);
            }
            Scanner scanner = new Scanner(System.in);
            publisher.readInitFile(args[3]);
            publisher.init(5);
            scanner.close();
            //for receiving connections from brokers
            publisher.acceptConnection();
            publisher.shutdown();
        }else{
            System.err.println("Incorrect number of arguments");
        }
    }

    public void closeServer(){ //allows remote closure of server socket
        if (serverBroker != null && !serverBroker.isClosed()) {
            try {
                serverBroker.close();
                System.out.println("Server shut down successfully");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        //initiate shutdown
        info.stop();
        System.out.println("Initiating publisher shutdown");
        //connect to every broker
        init(5);
    }

}
