import sharedResources.ArtistName;
import sharedResources.BrokerInfo;
import sharedResources.Range;

import java.io.*;
import java.net.*;
import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Broker extends  Node {

    private List <Consumer> registedUsers;
    private static final List<PublisherInfo> registeredPublishers = new ArrayList<>();
    private ServerSocket serverConsumer; //accepts Consumer's requests
    private Socket s;  //socket for handling a connection(accept/close)
    private HashMap<String, PublisherInfo> keyToPublisher = new HashMap<String, PublisherInfo>(); //ka8e publisher mporei na bre8ei apey8eias mesw tou artist pou zhthse o consumer
    private BrokerInfo info; //the sharedResources.BrokerInfo that is created in init
    private List<Integer> hashList=new ArrayList<>(); //list of the hashes of all brokers



    //constructor
    public Broker(){
    }

    //getters
    public ServerSocket getServerConsumer(){
        return this.serverConsumer;
    }

    public BrokerInfo getInfo(){
        return info;
    }

    public List<PublisherInfo> getRegisteredPublishers(){
        return registeredPublishers;
    }

    // arxikopoiei tous servers , pairnei ws orisma->port tou server gia syndeseis me toys clients
    public  void init(int i){
        try {
            serverConsumer = new ServerSocket(i);
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        //takes ipadress and port from serverConsumer and executes the hash
        String socketIp = serverConsumer.getInetAddress().getHostAddress();
        String port = String.valueOf(serverConsumer.getLocalPort());
        int hash = computeHash(socketIp + port);
        hashList.add(hash);
        System.out.println(hash);

        /*
        creates a sharedResources.BrokerInfo object with ip,port the ip,port of serverConsumer
        */
        info=new BrokerInfo(socketIp, Integer.parseInt(port));
        info.setHash(hash);
        setBroker(info);
    }

    /*
    hash for broker
     */
    public static int computeHash(String value){
        BigInteger hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] digest = md.digest();
            hash = new BigInteger(1, digest);
        }catch(NoSuchAlgorithmException e){
            System.err.println("Error generating hash for broker");
        }
        return (hash.mod(BigInteger.valueOf(HASH_MAX))).intValue();
    }

    @Override
    public void connect(String ip, int i) throws IOException {

    }


    //closes connection as client at a Publisher
    public void disconnect() throws  IOException{
        s.close();
    }

    public void acceptConnection() throws IOException{
        s = serverConsumer.accept();
        System.out.println("A new client is connected : " + s);

        // obtaining input and out streams
        ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());
        output.flush();
        ObjectInputStream input = new ObjectInputStream(s.getInputStream());

        //Assign a new thread to this client: actionsClient
        System.out.println("Assigning new thread for this client");
        Thread actionsClient=new ActionsForClients(s, input, output, this);

        //Thread invoke start method
        actionsClient.start();
    }


    public void registerPublisher(PublisherInfo publisher) {
        if(!registeredPublishers.contains(publisher)){
            registeredPublishers.add(publisher);
        }
    }


        /*
        Iterates list :"listArtist" in order to compare the hashes in the sorted list hashList with the
        hash(sharedResources.ArtistName) and add at the appropriate sharedResources.BrokerInfo object artistList the artistName
        */

    public  void lookForArtists(List <ArtistName> listArtist, PublisherInfo pub) {
        System.out.println("\n----\nPublisher " + pub.getPort() + " sends these keys:\n----\n");
        for(BrokerInfo broker: getBrokers()) {
            for (ArtistName artist : listArtist) {
                int artistHash = computeHash(artist.getArtistName());
                if (broker.getRange().contains(artistHash) || hashList.get(0) == broker.getHash() && artistHash > hashList.get(hashList.size() - 1)) {
                    broker.setArtistList(artist);
                    if(info.getHash() == broker.getHash()) {
                        keyToPublisher.put(artist.getArtistName(), pub);
                        System.out.println(artist + "with hash = " + artistHash);
                    }
                }
            }
        }
    }

    public void calculateKeys(PublisherInfo publisher) {
        List<ArtistName> listArtist = publisher.getKeys();
        System.out.println("Publisher sends these artists:");

        lookForArtists(listArtist, publisher);

        for (BrokerInfo brL : getBrokers()) {
            System.out.println(brL);
            brL.displayArtists();
            if (brL.getArtistList().size() == 0) {
                System.out.println("List is empty!");
            }
            System.out.println("ArtistList has : " + brL.getArtistList().size() + " artistNames.");
            System.out.println("-----------");
        }

        for (String i : keyToPublisher.keySet()) {
            System.out.println("{" + i + ": " + keyToPublisher.get(i) + "}");
        }
    }


    public PublisherInfo retreivePublisher(String key) {
        return keyToPublisher.get(key);
    }

    /*Each Broker reads the txt file (init.txt) in which are written the ipAddresse's and
         ports of all the 3 serverConsumer servers.If  it finds ipAddress or port same as its own
         it ignores them and continues searching for the other 2(if it hasn't find them yet).If the
         ipAddress or port that reads are different from its own it creates a sharedResources.BrokerInfo object,
         calculates the hashValue of the ip+port and adds in hashList the hashValue.It also calls method
         setBroker with argument the sharedResources.BrokerInfo object in order to add it in this Broker broker list.
         */
    public void readInitFile(String data) {
        BufferedReader reader;
        String line;

        try {
            reader = new BufferedReader(new FileReader(new File(data)));
            line = reader.readLine();

            for (int i = 0; i < 3; i++) {
                String[] l = line.split(" ");
                if (info.getPort() != Integer.parseInt(l[1]) || !info.getIpAddress().equals(l[0])) {

                    System.out.println(l[0] + " " + l[1]);
                    BrokerInfo bInfo = new BrokerInfo(l[0], Integer.parseInt(l[1]));
                    int hashVal = computeHash(l[0] + l[1]);
                    bInfo.setHash(hashVal);
                    setBroker(bInfo);
                    hashList.add(hashVal);

                }
                line = reader.readLine();
            }
            Collections.sort(hashList);
            System.out.println("Sorted HashList: ");
            for(BrokerInfo broker: getBrokers()) {
                for (int i = 0; i < hashList.size(); i++) {
                    if (hashList.get(i) == broker.getHash()) {
                        if (i == 0)
                            broker.setRange(Range.closed(0, hashList.get(i)));
                        else
                            broker.setRange(Range.closed(hashList.get(i - 1) + 1, hashList.get(i)));
                        System.out.println("Broker " + broker.getIpAddress()+":"+broker.getPort() +" with hash = " +broker.getHash());
                        System.out.println("sharedResources.Range = " +broker.getRange());
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading file !");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Broker broker = new Broker();

        broker.init(Integer.parseInt(args[1]));


        System.out.println(broker.getServerConsumer().getLocalPort());
        System.out.println(broker.getServerConsumer().getInetAddress().getHostAddress());

        broker.readInitFile(args[0]);

        while (true) {
            try{
                broker.acceptConnection();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void updateNode(PublisherInfo receivedInfo) {
        //remove all the entries for the Publisher who disconnected
        keyToPublisher.entrySet().removeIf(entry -> entry.getValue().equals(receivedInfo));
        System.out.println("\nmap after:");
        for(Map.Entry<String, PublisherInfo> entry : keyToPublisher.entrySet()){
            System.out.println(entry);
        }
        //remove all the keys from the list sent to consumer
        for(ArtistName toRemove: receivedInfo.getKeys()){
            info.removeArtist(toRemove);
        }
        System.out.println("Publisher disconnected successfully");
    }
}