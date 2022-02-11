import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Peer {
    private int port;
    private InetAddress ip;
    private int tokenId;
    private Socket clientSocket;
    private ServerSocket serversocket;
    private Scanner input = new Scanner(System.in);
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private List<String> directoryListing = new ArrayList<>();
    private List<String> trackerFiles = new ArrayList<>();
    private String trackerIp;
    private int trackerPort;
    private String directory;

    public void init(String ip, int port) {
        try {
            connect(ip, port);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void connect(String ip, int port) throws IOException {
        System.out.println("Attempting Connection...");
        clientSocket = new Socket(ip, port);
        System.out.println("Connected to:" + ip + " " + port);
        //streams
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        System.out.println("Got I/O streams");
    }

    /* Scan SharedResources folder for available files */
    private void searchDirectory(final File directory) {
        File[] fileList = directory.listFiles(); //get list of files in directory
        if (fileList == null) {
            System.out.println("Directory empty");
            return;
        }
        for (int i = 0; i < fileList.length; i++) {
            File file = fileList[i];
            String artist = null;
            if (file.isDirectory()) {
                searchDirectory(file); //search additional folders
            } else if (file.isFile()) {
                String ext = FilenameUtils.getExtension(file.getPath()); //get file extension
                String fileName = FilenameUtils.getName(file.getPath());
                directoryListing.add(fileName);
            }
        }
        System.out.println("Peer got these files");
        for (String file : directoryListing) {
            System.out.println(file);
        }
    }

    public void register() {
        try {
            out.writeInt(Functions.REGISTER);
            out.flush();
            boolean validUsername = false;
            String requestedUsername;
            do {
                System.out.println("Please choose a username:");
                requestedUsername = input.nextLine();
                out.writeUTF(requestedUsername);
                out.flush();
                validUsername = in.readBoolean();
                if (validUsername) {
                    System.out.println("Username already exists!");
                    System.out.println("If you are already registered, you could try logging in!\nDo you want to quit? [Y/N]");
                    Scanner input = new Scanner(System.in);
                    String answer = input.nextLine();
                    if(answer.toLowerCase().equals("y"))
                        out.writeUTF("<exit>");
                        return;
                }
            } while (validUsername);
            System.out.println("Please select a password");
            String pass = input.nextLine();
            out.writeUTF(pass);
            out.flush();
            String serverAnswer = in.readUTF();
            System.out.println(serverAnswer);
            this.tokenId = in.readInt();
            directory = requestedUsername;
            File folder = new File("./" + directory + "/SharedResources");
            folder.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        try {
            out.writeInt(Functions.LOGIN);
            out.flush();
            String username;
            boolean validUsername;
            do {
                System.out.println("Please enter your username:");
                username = input.nextLine();
                out.writeUTF(username);
                out.flush();
                validUsername = in.readBoolean();
                if (!validUsername) {
                    System.out.println("Username doesn't exist!");
                    System.out.println("If you haven't already registered, be sure to do that first!\nDo you want to quit? [Y/N]");
                    Scanner input = new Scanner(System.in);
                    String answer = input.nextLine();
                    if(answer.toLowerCase().equals("y")) {
                        out.writeUTF("<exit>");
                        return;
                    }
                }
            } while (!validUsername);
            boolean validPass;

            do {
                System.out.println("Please enter your password");
                String pass = input.nextLine();
                out.writeUTF(pass);
                out.flush();
                validPass = in.readBoolean();
                if (!validPass) {
                    System.out.println("The password you entered was incorrect!");
                }
            } while (!validPass);
            String serverAnswer = in.readUTF();
            System.out.println(serverAnswer);
            this.tokenId = in.readInt();
            System.out.println("Your id for this session is: " + this.tokenId);
            out.flush();
            directory = username;
            File folder = new File("./" + directory + "/SharedResources");
            folder.mkdirs();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inform() {
        try {
            out.writeObject(this.directoryListing);
            out.flush();
            out.writeObject(clientSocket.getLocalAddress().getHostAddress());
            out.flush();
            out.writeInt(port);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Informs the tracker that the peer wishes to disconnect */
    private void logout() {
        try {
            out.writeInt(Functions.LOGOUT);
            out.flush();
            out.writeInt(this.tokenId);
            out.flush();
            String serverAnswer = in.readUTF();
            System.out.println(serverAnswer);
            disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Requests a list of all the files in the P2P System */
    private void list() {
        try {
            out.writeInt(Functions.LIST);
            out.flush();
            this.trackerFiles = (List<String>) in.readObject();
            System.out.println("Files currently available in the P2P System: ");
            int i = 1;
            for (String filename : trackerFiles) {
                System.out.println(i++ + ") " + filename);
            }
            System.out.println();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private List<PeerDetails> details(String requestedFile) {
        try {
            out.writeUTF(requestedFile);
            out.flush();
            boolean fileExists = in.readBoolean();
            if (fileExists) {
                List<PeerDetails> details = (List<PeerDetails>) in.readObject();
                System.out.println("These peers have the file:");
                for (PeerDetails peer : details) {
                    System.out.println(peer);
                }
                return details;
            } else {
                System.out.println("The requested file doesn't exist");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*Pings a specific peer and calculates the RTT of the message*/
    private double checkActive(PeerDetails peer) {
        double totalRTT = -1;
        try {
            System.out.println("Sending Ping requests");
            connect(peer.getIp(), peer.getPort());
            out.writeInt(Functions.PING);
            out.flush();
            for (int i = 0; i < 3; i++) {
                //boolean isActive = InetAddress.getByName(peer.getIp()).isReachable(1000);
                //Send and receive Echo message. Count RTT to get reply.
                double startTime = System.currentTimeMillis();
                out.writeUTF("Echo");
                out.flush();
                in.readUTF();
                double endTime = System.currentTimeMillis();
                double rtt = endTime - startTime;
                totalRTT += rtt;
            }
            totalRTT /= 3;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalRTT;
    }

    public void disconnect() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    /*Downloads a file from another peer. This method:
        1) Informs the tracker about the file they want to download and receives a list of all the peers that have it.
        2) Pings each peer 3 times, calculates the average RTT as well as its overall score
        3) Connects to the fastest peer and requests to download the file
        4) Saves the file in the peer's SharedResources folder
        5) Informs the tracker about the success or not of the transaction
    */
    private boolean simpleDownload(String filename) {
        try {
            out.writeInt(Functions.DOWNLOAD);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        OutputStream fout = null;
        try {
            fout = new FileOutputStream("./"+directory+"/SharedResources/"+filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        List<PeerDetails> peers = details(filename);
        if (peers == null) {
            return false;
        }
        TreeMap<Double, PeerDetails> scoreMap = new TreeMap<>();
        for (PeerDetails peer : peers) {
            double rtt = checkActive(peer);
            double score = Math.pow(0.9, peer.getCountDownloads()) * Math.pow(1.2, peer.getCountFailures());
            scoreMap.put(score, peer);
        }
        boolean success = false;
        do {
            PeerDetails fastestPeer = scoreMap.pollFirstEntry().getValue();
            if (fastestPeer == null) return false;
            try {
                System.out.println("Downloading file, please wait...");
                connect(fastestPeer.getIp(), fastestPeer.getPort());
                out.writeInt(Functions.DOWNLOAD);
                out.flush();
                out.writeUTF(filename);
                out.flush();
                byte[] bytes = new byte[4096];
                int count;
                while ((count = in.read(bytes)) > 0) {
                    fout.write(bytes, 0, count);
                }
                System.out.println("*****Download complete!*****");
                success = true;
                fout.close();
                out.close();
                in.close();
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
            notify(fastestPeer, success);
        } while (!success);
        return true;
    }

    private void notify(PeerDetails peer, boolean success) {
        try {
            connect(trackerIp, trackerPort);
            out.writeInt(Functions.NOTIFY);
            out.writeUTF(peer.getUsername());
            out.writeBoolean(success);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*Method to handle server */
    public void startServer(Peer caller) {
        final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(100);

        //Assign new thread to act as the server for this peer
        Runnable serverTask = new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    System.out.println("Waiting for clients to connect...");
                    while (true) {
                        Socket clientSocket = serverSocket.accept();
                        clientProcessingPool.submit(new HandleClients(clientSocket, caller));
                    }
                } catch (IOException e) {
                    System.err.println("Unable to process client request");
                    e.printStackTrace();
                }
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Peer peer = new Peer();
        //No username or port specified, assigning new port
        if (args.length == 0) {
            Random random = new Random();
            peer.port = random.nextInt(5000) + 2000;
        }
        //Port specified
        if (args.length == 1) {
            peer.port = Integer.parseInt(args[0]);
        }
        //Port and username specified
        if (args.length == 2) {
            peer.directory = args[1];
        }
        //set Tracker Ip and port
        peer.trackerIp = "192.168.1.165"; peer.trackerPort = 9876;
        peer.init("192.168.1.165", 9876);
        System.out.println("Do you want to register or login");
        String str = scanner.nextLine();
        if (str.toLowerCase().equals("register")) {
            peer.register();
        } else {
            peer.login();
        }
        //After logging in, the peer has access to their unique SharedResources folder.
        //Scan folder to get all available files
        peer.searchDirectory(new File("./" + peer.directory + "/SharedResources"));
        //Inform the tracker about this peer's ip, port and shared content
        peer.inform();
        //start listening for other peer connections
        peer.startServer(peer);
        while (true) {
            System.out.println("Available actions:\n1: Download\n2: List files\n3: Logout");
            str = scanner.nextLine();
            if (Integer.parseInt(str)==1) {
                System.out.println("Please enter the name of the file");
                String filename = scanner.nextLine();
                peer.simpleDownload(filename);
                //Re-connect to tracker
                peer.connect("192.168.1.165", 9876);
            } else if (Integer.parseInt(str)==2) {
                peer.list();
            } else if (Integer.parseInt(str)==3) {
                peer.logout();
                break;
            }
        }
    }

    public String getDirectory() {
        return directory;
    }
}
