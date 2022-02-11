import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import com.sun.source.tree.Tree;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class Peer {

    private static final int CHUNK_SIZE = 512000; //size of each individual file chunk

    private int port;
    private InetAddress ip;
    private int tokenId;
    private Socket clientSocket;
    private ServerSocket serversocket;
    private Scanner input = new Scanner(System.in);
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Map<String, ChunksWrapper> directoryListing = new HashMap<>(); //stores the chunks of any file stored in the shared directory. TreeSet guarantees that chunks are sorted
    private Map<String, Integer> fileChunks = new HashMap<>(); //stores for each file the number of chunks
    private List<String> trackerFiles = new ArrayList<>();
    String trackerIp;
    int trackerPort;
    private String directory = ""; //peers username. All methods correlating to "directory" actually refer to this username. Not to be confused with directoryListing
    private boolean waitingForClients = false;
    List<PeerDetails> servingClients = new ArrayList<>(); //clients that are currently being served for the specific file
    String fileToDownload;
    int failedAttempts = 0;
    private ConcurrentHashMap<String, List<Integer>> chunksReceived = new ConcurrentHashMap<>(); //For each peer which chunks are received by said peer

    public boolean isInitSeeder(String file) {
        file = getBaseName(file);
        return directoryListing.get(file).isInitSeeder() == 1;
    }

    public boolean hasFile(String file) {
        return directoryListing.containsKey(file);
    }

    public ChunksWrapper getChunks(String file) {
        return directoryListing.get(file);
    }

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

    /* Scan SharedResources folder for available files and chunks */
    private void searchDirectory(final File directory) {
        File[] fileList = directory.listFiles(); //get list of files in directory
        if (fileList == null) {
            System.out.println("Directory empty");
            return;
        }
        for (File file : fileList) {
            String artist = null;
            if (file.isDirectory()) {
                searchDirectory(file); //search additional folders
            } else if (file.isFile()) {
                String ext = FilenameUtils.getExtension(file.getPath()); //get file extension
                String fileName = FilenameUtils.getName(file.getPath()); //get filename with extension
                int seeder;
                String baseName = getBaseName(fileName); //get filename without chunk number or extension
                System.out.println("BASE NAME = " + baseName);
                ChunksWrapper chunks = directoryListing.get(baseName);
                //basename doesn't contain a chunk number - must be whole file
                if (!fileName.matches("(-[0-9]*)")) {
                    seeder = 1; //given file is not chunk, therefore this peer must be its initial seeder (Reconstructed received files are placed in another directory)
                    if (chunks == null) { //first time coming across this file
                        chunks = new ChunksWrapper(new TreeSet<String>(), seeder); //Make new Set to add chunks of this file(if any)
                        directoryListing.put(baseName + "." + ext, chunks); //files with different extensions are allowed to have the same basename
                    } else { //file (in the form of a chunk) has been seen before. Just set seeder-bit to 1 since this is a whole file
                        chunks.setInitSeeder(1);
                    }
                    //file is a chunk
                } else {
                    seeder = 0;
                    if (chunks == null) { //first time seeing this file in the form of a chunk
                        chunks = new ChunksWrapper(new TreeSet<String>(), seeder);
                    }
                    chunks.getChunks().add(fileName);
                    directoryListing.put(baseName + "." + ext, chunks);
                }
            }
        }
        if (directoryListing.isEmpty()) {
            System.out.println("There are not any files to share yet!");
        } else {
            System.out.println("SharedResources contains:");
            for (String key : directoryListing.keySet()) {
                System.out.println(key + " " + directoryListing.get(key));
            }
        }
    }

    public boolean register() {
        try {
            out.writeInt(Functions.REGISTER);
            out.flush();
            boolean invalidUsername;
            String requestedUsername;
            do {
                System.out.println("Please choose a username:");
                requestedUsername = input.nextLine();
                out.writeUTF(requestedUsername); //sent username
                out.flush();
                invalidUsername = in.readBoolean(); //read server answer
                if (invalidUsername) {
                    System.out.println("Username already exists!");
                    System.out.println("If you are already registered, you could try logging in!\nDo you want to quit? [Y/N]");
                    Scanner input = new Scanner(System.in);
                    String answer = input.nextLine();
                    if (answer.toLowerCase().equals("y")) {
                        out.writeUTF("<exit>");
                        return invalidUsername;
                    }
                }
            } while (invalidUsername);
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
            return false;
        }
        return true;
    }

    private boolean login() {
        try {
            out.writeInt(Functions.LOGIN);
            out.flush();
            String username;
            boolean validUsername;
            do {
                if (directory.equals("")) {
                    System.out.println("Please enter your username:");
                    username = input.nextLine();
                } else {
                    username = directory;
                }
                out.writeUTF(username);
                out.flush();
                validUsername = in.readBoolean();
                if (!validUsername) {
                    directory = "";
                    System.out.println("Username doesn't exist!");
                    System.out.println("If you haven't already registered, be sure to do that first!\nDo you want to quit? [Y/N]");
                    Scanner input = new Scanner(System.in);
                    String answer = input.nextLine();
                    if (answer.toLowerCase().equals("y")) {
                        out.writeUTF("<exit>");
                        return false;
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
            return false;
        }
        return true;
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

    //Send peer's file map to the tracker
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

    //Informs tracker that this peer his initial seeder for sent file(s)
    private void seederInform() {
        try {
            List<String> wholeFiles = new ArrayList<>();
            for (String file : directoryListing.keySet()) {
                //if Peer is initial seeder for this file, add it to the list
                if (directoryListing.get(file).isInitSeeder() == 1) {
                    wholeFiles.add(file);
                }
            }
            out.writeObject(wholeFiles); //send list of files for which the peer is initial seeder to tracker
            out.flush();
            inform();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Requests a list of all the files (not chunks) in the P2P System */
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
            out.writeInt(Functions.DOWNLOAD);
            out.flush();
            System.out.println("Requesting file");
            out.writeUTF(requestedFile);
            out.flush();
            boolean fileExists = in.readBoolean();
            System.out.println("File exists: " + fileExists);
            if (fileExists) {
                List<PeerDetails> details = (List<PeerDetails>) in.readObject();
                System.out.println("These peers have the file:");
                for (PeerDetails peer : details) {
                    System.out.println(peer);
                    if (peer.getChunkWrapper(requestedFile).isInitSeeder() == 1) {
                        fileChunks.put(requestedFile, peer.getChunkWrapper(requestedFile).getChunks().size());
                    }
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

    //Used by peer's server thread to select one of the connected peers.
    public PeerDetails selectPeer() {
        Random random = new Random();
        double probability = random.nextDouble();
        List<PeerDetails> clients = new ArrayList<>(servingClients); //get current copy of List. If the list is altered, changes won't break this method
        PeerDetails selectedClient;
        //randomly select a peer
        if (probability < 0.2) {
            selectedClient = clients.get(random.nextInt(clients.size()));
            //select fastest peer
        } else if (probability < 0.6) {
            TreeMap<Double, PeerDetails> scoreMap = new TreeMap<>();
            for (PeerDetails peer : clients) {
                double rtt = checkActive(peer);
                double score = rtt * Math.pow(0.9, peer.getCountDownloads()) * Math.pow(1.2, peer.getCountFailures());
                scoreMap.put(score, peer);
            }
            selectedClient = scoreMap.pollFirstEntry().getValue();
            //select peer with most transactions
        } else {
            int max = -1;
            selectedClient = clients.get(0);
            for (PeerDetails client : clients) {
                if (chunksReceived.get(client.getUsername()).size() > max) {
                    max = chunksReceived.get(client.getUsername()).size();
                    selectedClient = client;
                }
            }
        }
        System.out.println("SELECTED PEER: " + selectedClient);
        return selectedClient;
    }

    /*Downloads a file from another peer. This method:
        1) Informs the tracker about the file they want to download and receives a list of all the peers that have it.
        2) Pings each peer 3 times, calculates the average RTT as well as its overall score
        3) Connects to the fastest peer and requests to download the file
        4) Saves the file in the peer's SharedResources folder
        5) Informs the tracker about the success or not of the transaction
    */
    boolean collaborativeDownload(String filename) {
        //find missing chunks for requested file
        OutputStream fout = null;
        List<PeerDetails> peers = details(filename);
        if (peers == null) {
            System.out.println("no peers found!");
            return false;
        }
        for (PeerDetails peer : peers) {
            System.out.println(peer);
            TreeSet<String> peerChunks = peer.getChunkWrapper(filename).getChunks();
            for (String chunk : peerChunks) {
                System.out.println(chunk);
            }
            System.out.println();
        }
        int totalFileChunks = fileChunks.get(filename);
        System.out.println("Number of chunks for this file: " + totalFileChunks);
        List<String> missingChunks = new ArrayList<>();
        for (int i = 0; i < totalFileChunks; i++) {
            String missingChunk = Peer.getBaseName(filename) + "-" + (i + 1) + "." + FilenameUtils.getExtension(filename);
            //if chunk is not contained in peer's directory, add it to request list
            System.out.println(missingChunk);
            if (directoryListing.get(filename) == null) {
                missingChunks.add(missingChunk);
                System.out.println("ADDING" + missingChunk);
            } else if (!directoryListing.get(filename).getChunks().contains(missingChunk)) {
                missingChunks.add(missingChunk);
                System.out.println("ADDING" + missingChunk);
            }
        }
        //find 4 (or less if there are less than 4) peers to connect to in order to request the chunks
        List<PeerDetails> peersToConnect = new ArrayList<>();
        PeerDetails highestPeer1 = null;
        PeerDetails highestPeer2 = null;
        int max1 = -1;
        int max2 = -1;
        for (PeerDetails peer : peers) {
            if (chunksReceived.get(peer.getUsername()) == null)
                continue;
            if (chunksReceived.get(peer.getUsername()).size() > max1 && peer.getChunkWrapper(filename).isInitSeeder() == 0) {
                max2 = max1;
                max1 = chunksReceived.get(peer.getUsername()).size();
                highestPeer2 = highestPeer1;
                highestPeer1 = peer;
            } else if (chunksReceived.get(peer.getUsername()).size() > max2 && peer.getChunkWrapper(filename).isInitSeeder() == 0) {
                max2 = chunksReceived.get(peer.getUsername()).size();
                highestPeer2 = peer;
            }
        }
        if (highestPeer1 != null)
            peersToConnect.add(highestPeer1);
        if (highestPeer2 != null)
            peersToConnect.add(highestPeer2);
        int count = 0;
        Random random = new Random();
        while (count < 2 && count < peers.size()) {
            System.out.println("choosing random peer");
            int randomPeer = random.nextInt(peers.size());
            if (!peersToConnect.contains(peers.get(randomPeer))) {
                System.out.println("ADDING PEER: " + peers.get(randomPeer));
                peersToConnect.add(peers.get(randomPeer));
                count++;
            }
        }
        int timeouts = 0;
        //connect to peers and request the chunks
        for (PeerDetails peerToConnect : peersToConnect) {
            System.out.println(peerToConnect);
            String chunkToReceive = "";
            boolean success = false;
            try {
                clientSocket.setSoTimeout(2000); //set amount of time before timeout for this peer
                System.out.println("Downloading file, please wait...");
                connect(peerToConnect.getIp(), peerToConnect.getPort());
                out.writeInt(Functions.DOWNLOAD);
                out.flush();
                out.writeUTF(directory);
                out.flush();
                out.writeObject(missingChunks);
                out.flush();
                try {
                    chunkToReceive = in.readUTF(); //Notifies the peer about the chunk the server is about to send. Empty if server has no chunk for this file
                    if (chunkToReceive.equals("")) { //Equals negative answer - peer doesn't have any of the requested chunks
                        System.out.println("Peer refused our request - no such file available");
                        continue;
                    }
                    try {
                        fout = new FileOutputStream(directory + "/SharedResources/" + chunkToReceive);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }
                    //catches Timeout
                } catch (SocketTimeoutException e) {
                    timeouts++;
                    System.out.println("Timeout for peer: " + timeouts);
                    continue;
                }
                clientSocket.setSoTimeout(0);
                byte[] bytes = new byte[4096]; //buffer size
                int byteCount;
                while ((byteCount = in.read(bytes)) > 0) {
                    fout.write(bytes, 0, byteCount);
                }
                System.out.println("*****Download complete!*****");
                success = true;
                fout.close();
                out.close();
                in.close();
                missingChunks.remove(chunkToReceive);
                ChunksWrapper wrapper = directoryListing.get(filename);
                if (wrapper == null) {
                    TreeSet<String> wrapperSet = new TreeSet<>();
                    wrapperSet.add(chunkToReceive);
                    directoryListing.put(filename, new ChunksWrapper(wrapperSet, 0));
                } else {
                    wrapper.getChunks().add(chunkToReceive);
                }
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
                System.out.println("Server refused to cooperate");
            }
            notify(peerToConnect, success, chunkToReceive);
        }
        return true;
    }

    private void notify(PeerDetails peer, boolean success, String receivedChunk) {
        try {
            connect(trackerIp, trackerPort);
            out.writeInt(Functions.NOTIFY);
            out.flush();
            out.writeUTF(peer.getUsername());
            out.flush();
            out.writeBoolean(success);
            out.flush();
            out.writeUTF(this.directory);
            out.flush();
            out.writeUTF(receivedChunk);
        } catch (IOException e) {
            System.out.println("Something went wrong while informing tracker");
            e.printStackTrace();
            //TODO reset transaction OR attempt to inform the tracker as soon as possible
        }
    }

    private String select() {
        Random random = new Random();
        int randomFilePos = random.nextInt(trackerFiles.size());
        String selectedFile = trackerFiles.get(randomFilePos);
        List<String> uniqueFiles = new ArrayList<>(trackerFiles);
        while (isSeeder(selectedFile) && !uniqueFiles.isEmpty()) {
            uniqueFiles.remove(selectedFile);
            randomFilePos = random.nextInt(trackerFiles.size());
            selectedFile = trackerFiles.get(randomFilePos);
        }
        if (!uniqueFiles.isEmpty()) {
            fileToDownload = selectedFile;
            return selectedFile;
        }
        return "";
    }

    /* Split file into 0.5MB chunks */
    private void partition(String filename) {
        List<ChunkFile> chunks = new ArrayList<>();
        File file = new File(directory + "/SharedResources/" + filename);
        if(!file.exists() || file.isDirectory()){
            return;
        }
        byte[] bytesArray = new byte[(int) file.length()]; //get file as byte array
        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("there are " + bytesArray.length + " bytes in file");
        String ext = FilenameUtils.getExtension(file.getPath()); //get file extension
        //file is mp3 file special treatment is needed while splitting it to preserve information
        try {
            if (ext.equals("mp3")) {
                //preserve mp3 tag values (genre, artist, album)
                int genre = 0;
                String artist = null;
                String album = null;
                Mp3File mp3File = new Mp3File(file.getPath());
                String song = FilenameUtils.getBaseName(file.getPath()); //get mp3 filename without extension
                //Mp3 file tags follow one of two possible formats: ID3v1 or ID3v2
                if (mp3File.hasId3v1Tag()) {
                    ID3v1 id3v1tag = mp3File.getId3v1Tag();
                    artist = id3v1tag.getArtist();
                    album = id3v1tag.getAlbum();
                    genre = id3v1tag.getGenre();
                }
                if (mp3File.hasId3v2Tag()) {
                    ID3v1 id3v2tag = mp3File.getId3v2Tag();
                    artist = id3v2tag.getArtist();
                    album = id3v2tag.getAlbum();
                    genre = id3v2tag.getGenre();
                }
                System.out.println("TOTAL BYTES = " + bytesArray.length);
                int counter = 1; //current chunk number
                int numberOfChunks = bytesArray.length / CHUNK_SIZE; //total number of output chunks
                int lastChunk = bytesArray.length - (numberOfChunks * CHUNK_SIZE); //last chunk can be less than CHUNK_SIZE
                //if file is less than CHUNK_SIZE it can fit into one single chunk
                if (bytesArray.length < CHUNK_SIZE) {
                    chunks.add(new MusicFile(song, artist, album, genre, bytesArray));
                } else {
                    //split file and add each chunk into a list
                    System.out.println("Splitting into " + numberOfChunks + " chunks");
                    byte[] array_byte;
                    for (int i = 0; i < bytesArray.length - CHUNK_SIZE; i += CHUNK_SIZE) {
                        array_byte = Arrays.copyOfRange(bytesArray, i, i + CHUNK_SIZE);
                        chunks.add(new MusicFile(song + "-" + counter++, artist, album, genre, array_byte));
                        System.out.println(i); //byte offset
                    }
                    System.out.println(bytesArray.length - CHUNK_SIZE);
                    array_byte = Arrays.copyOfRange(bytesArray, bytesArray.length - lastChunk, bytesArray.length);
                    chunks.add(new MusicFile(song + "-" + counter, artist, album, genre, array_byte));
                }
            } else if (ext.equals("txt")) {
                int counter = 1; //current chunk number
                int numberOfChunks = bytesArray.length / CHUNK_SIZE; //total number of output chunks
                int lastChunk = bytesArray.length - (numberOfChunks * CHUNK_SIZE); //last chunk can be less than CHUNK_SIZE
                //if file is less than CHUNK_SIZE it can fit into one single chunk
                if (bytesArray.length < CHUNK_SIZE) {
                    chunks.add(new ChunkFile(bytesArray, FilenameUtils.getBaseName(file.getPath())));
                } else {
                    //split file and add each chunk into a list
                    System.out.println("Splitting into " + numberOfChunks + " chunks");
                    byte[] array_byte;
                    for (int i = 0; i < bytesArray.length - CHUNK_SIZE; i += CHUNK_SIZE) {
                        array_byte = Arrays.copyOfRange(bytesArray, i, i + CHUNK_SIZE);
                        chunks.add(new ChunkFile(array_byte, FilenameUtils.getBaseName(file.getPath()) + "-" + counter++));
                        System.out.println(i); //byte offset
                    }
                    System.out.println(bytesArray.length - CHUNK_SIZE);
                    array_byte = Arrays.copyOfRange(bytesArray, bytesArray.length - lastChunk, bytesArray.length);
                    chunks.add(new ChunkFile(array_byte, FilenameUtils.getBaseName(file.getPath()) + "-" + counter));
                }
            }
            fileChunks.put(filename, chunks.size());
            //write each chunk to directory
            for (ChunkFile chunk : chunks) {
                ChunksWrapper fileChunks = directoryListing.get(filename);
                if (fileChunks == null) {
                    fileChunks = new ChunksWrapper(new TreeSet<>(), 1);
                    fileChunks.getChunks().add(chunk.getName() + "." + ext);
                    directoryListing.put(filename, fileChunks);
                } else {
                    fileChunks.getChunks().add(chunk.getName() + "." + ext);
                }
                File chunkFile = new File(directory + "/SharedResources/" + chunk.getName() + "." + ext);
                FileUtils.writeByteArrayToFile(chunkFile, chunk.getFileExtract());
            }
        } catch (IOException | InvalidDataException | UnsupportedTagException e) {
            System.err.println("Error while reading mp3");
        }
    }

    //removes the chunk number (_x) and the extension from a file if they exist
    public static String getBaseName(String fullFileName) {
        String baseName = FilenameUtils.getBaseName(fullFileName);
        baseName = baseName.replaceAll("(-[0-9]*)$", "");
        return baseName;
    }

    public boolean isSeeder(String file) {
        if (directoryListing == null)
            System.out.println("DIRECTORY NULL");
        if (directoryListing.get(file) == null)
            return false;
        int totalChunks = directoryListing.get(file).getChunks().size();
        return fileChunks.get(file) == totalChunks;
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
        peer.trackerIp = "192.168.1.165";
        peer.trackerPort = 2300;
        peer.init(peer.trackerIp, peer.trackerPort);
        boolean successfulLogIn = false;
        while (!successfulLogIn) {
            System.out.println("Do you want to register or login (Type <E> to exit)?");
            String str = scanner.nextLine();
            switch (str.toLowerCase().trim()) {
                case "register":
                    successfulLogIn = peer.register();
                    break;
                case "login":
                    successfulLogIn = peer.login();
                    break;
                case "<e>":
                    return;
            }
        }
        //After logging in, the peer has access to their unique SharedResources folder.
        //Scan folder to get all available files
        peer.searchDirectory(new File("./" + peer.directory + "/SharedResources"));
        for (String key : peer.directoryListing.keySet()) {
            if (peer.directoryListing.get(key).isInitSeeder() == 1) {
                peer.partition(key);
                peer.fileChunks.put(key, peer.getChunks(key).getChunks().size());
            }
        }
        //Inform the tracker about this peer's ip, port and shared content
        peer.seederInform(); //first send files for which the peer is initial seeder
        //start listening for other peer connections
        peer.startServer(peer);
        String str;
        System.out.println("Do you want to download a file? [Y/N]");
        String answer = scanner.nextLine();
        while (true) {
            if (!answer.toLowerCase().equals("y")) {
                System.out.println("Logout? [Y/N]");
                answer = scanner.nextLine();
                if (answer.toLowerCase().equals("y")) break;
            }
            peer.list();
            String selectedFile = peer.select();
            System.out.println("File to download: " + selectedFile);
            if (!selectedFile.equals("")) {
                while (!peer.isSeeder(selectedFile)) {
                    System.out.println("Downloading chunk");
                    boolean success = peer.collaborativeDownload(selectedFile);
                    if (!success) {
                        peer.failedAttempts++;
                    }
                    if (peer.failedAttempts > 20) {
                        System.out.println("Too many failed attemts to download file. Consider checking your network connection or restarting the program");
                        break;
                    }
                }
                if (peer.isSeeder(selectedFile)) {
                    System.out.println("Assembling downloaded file");
                    peer.assemble(selectedFile);
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500); //wait before sending requests
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Re-connect to tracker
                peer.connect(peer.trackerIp, peer.trackerPort);
            }else{
                System.out.println("You have downloaded all files currently available in the system! No new files to download!");
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500); //wait before sending next request
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Do you want to download another file? [Y/N]");
            answer = scanner.nextLine();
        }
        peer.connect(peer.trackerIp, peer.trackerPort);
        peer.logout();
    }

    public void assemble(String selectedFile) {
        List<String> files = new ArrayList<>();
        for (String chunk : directoryListing.get(selectedFile).getChunks()) {
            files.add("./" + directory + "/SharedResources/" + chunk);
        }
        try {
            File folder = new File(directory + "/Downloads");
            folder.mkdirs();
            File wholeFile = new File(directory + "/Downloads/" + selectedFile);
            OutputStream out = new FileOutputStream(wholeFile);
            byte[] buf = new byte[4096];
            for (String file : files) {
                InputStream in = new FileInputStream(file);
                int b = 0;
                while ((b = in.read(buf)) >= 0)
                    out.write(buf, 0, b);
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDirectory() {
        return directory;
    }
}
