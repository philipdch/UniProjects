import java.io.*;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Tracker {

    private ArrayList<Account> accounts = new ArrayList<>();
    private ConcurrentMap<Integer, PeerDetails> peerDetails = new ConcurrentHashMap<>();
    public ArrayList<String> data = new ArrayList<>();
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    public ConcurrentMap<String, ArrayList<Integer>> peerFileMap = new ConcurrentHashMap<>();
    private int port = 2300;

    public ConcurrentMap<Integer, PeerDetails> getPeerDetails() {
        return peerDetails;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public ConcurrentMap<String, ArrayList<Integer>> getPeerFileMap() {
        return peerFileMap;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void init(int i) throws IOException {
        BufferedReader br = null;
        try {
            File accountsFile = new File("./accounts.txt");
            br = new BufferedReader(new FileReader(accountsFile));
            String line = br.readLine();
            while(line!=null){
                System.out.println(line);
                StringTokenizer tokenizer = new StringTokenizer(line, ",");
                accounts.add(new Account(tokenizer.nextToken().trim(), tokenizer.nextToken().trim(), Integer.parseInt(tokenizer.nextToken().trim()), Integer.parseInt(tokenizer.nextToken().trim())));
                line = br.readLine();
            }
            serverSocket = new ServerSocket(i);
            System.out.println("Waiting for the client request");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public PeerDetails findPeerByName(String username){
        PeerDetails peer = null;
        System.out.println("LOOKING FOR PEER: " + username);
        for(Map.Entry<Integer, PeerDetails> entry: peerDetails.entrySet()){
            peer = entry.getValue();
            System.out.println("Checking peer: " + peer.getUsername());
            if(peer.getUsername().equals(username))
                return peer;
        }
        return peer;
    }

    //closes connection as client at a Publisher
    public void disconnect() throws IOException {
        input.close();
        output.close();
        socket.close();
    }

    public void acceptConnection() throws IOException {
        System.out.println("Awaiting client connection...");
        socket = serverSocket.accept();
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        Thread actionsClient = new Threads(socket, input, output, this);
        actionsClient.start();
        System.out.println("Start");
    }


    public static void main(String args[]) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Tracker tracker = new Tracker();
        tracker.init(tracker.port);
        while (true) {
            tracker.acceptConnection();
        }
    }
}

