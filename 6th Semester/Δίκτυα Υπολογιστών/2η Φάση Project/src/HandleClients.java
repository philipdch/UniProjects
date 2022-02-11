import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class HandleClients extends Thread {
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket socket;
    Peer caller;
    Socket trackerSocket;
    ObjectOutputStream trackeroOut;
    ObjectInputStream trackerIn;

    private void connect(String ip, int port) throws IOException {
        System.out.println("Attempting Connection...");
        trackerSocket = new Socket(ip, port);
        System.out.println("Connected to:" + ip + " " + port);
        //streams
        trackeroOut = new ObjectOutputStream(trackerSocket.getOutputStream());
        trackerIn = new ObjectInputStream(trackerSocket.getInputStream());
        System.out.println("Got I/O streams");
    }

    public HandleClients(Socket socket, Peer caller) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.socket = socket;
            this.caller = caller;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            int received = in.readInt();
            if (received == Functions.PING) {
                for (int i = 0; i < 3; i++) {
                    out.writeUTF("Echo");
                    out.flush();
                }
            } else if (received == Functions.DOWNLOAD) {
                String username = in.readUTF();
                //find which peer is connected to this thread by contacting tracker
                //This will be used later to select the right peer
                connect(caller.trackerIp, caller.trackerPort);
                trackeroOut.writeInt(Functions.DETAILS);
                trackeroOut.flush();
                trackeroOut.writeUTF(username); //send connected peer's username
                trackeroOut.flush();
                PeerDetails connectedPeer = (PeerDetails) trackerIn.readObject(); //Tracker's answer
                caller.servingClients.add(connectedPeer); //add peer to the peers that are being serviced by the this peer
                List<String> requestedChunks = (List<String>) in.readObject(); //chunks requested by client
                if (requestedChunks == null) {
                    System.out.println("Client sent null list");
                    caller.servingClients.remove(connectedPeer);
                    return;
                }
                if (requestedChunks.isEmpty()) {
                    System.out.println("Client sent empty list");
                    caller.servingClients.remove(connectedPeer);
                    return;
                }
                String fileBaseName = Peer.getBaseName(requestedChunks.get(0)) + "." + FilenameUtils.getExtension(requestedChunks.get(0));
                PeerDetails selectedClient = null;
                if(caller.servingClients.size() > 1){
                    selectedClient = caller.selectPeer(); // client to serve
                }
                if(selectedClient != null && !selectedClient.getUsername().equals(username)){
                    caller.servingClients.remove(connectedPeer);
                    return;
                }
                //if there is only one client (this client) proceed as usual
                //calculate chunks that could be sent to client
                TreeSet<String> chunksToSend = new TreeSet<>(caller.getChunks(fileBaseName).getChunks());
                chunksToSend.retainAll(requestedChunks);
                if(chunksToSend.isEmpty()){
                    out.writeUTF("");
                    out.flush();
                    caller.servingClients.remove(connectedPeer);
                    return;
                }
                //select random chunk to send
                Random random = new Random();
                List<String> actualChunks = new ArrayList<>(chunksToSend);
                String chunkToSend = actualChunks.get(random.nextInt(actualChunks.size()));
                out.writeUTF(chunkToSend);
                out.flush();
                //if peer is file's seeder, simply send file without asking anything in return
                if(!requestedChunks.isEmpty() && caller.isSeeder(requestedChunks.get(0))){
                    System.out.println("SENDING FILE");
                    seederServe(chunkToSend);
                //if peer isn't seeder ask other client to send one chunk back
                }else{
                    //TODO peer is not seeder. Request chunk in return by calling collaborative download
                }
                System.out.println("FILE SENT");
                caller.servingClients.remove(connectedPeer); //peer request completed, remove them from connected peers list
            }
            out.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            caller.servingClients.clear();
        }
        caller.servingClients.clear();
    }

    //Seeder-Serve simply sends a chunk as usual without asking anything in return
    private void seederServe(String chunkToSend) {
        sendFile(chunkToSend);
    }

    private void sendFile(String file){
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(new File("./" + caller.getDirectory() + "/SharedResources/" + file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            caller.servingClients.clear();
            return;
        }
        //read and send file
        try {
            byte[] bytes = new byte[4096];
            int count;
            while ((count = fin.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }
        }catch (IOException e){
            e.printStackTrace();
            caller.servingClients.clear();
        }
    }
}
