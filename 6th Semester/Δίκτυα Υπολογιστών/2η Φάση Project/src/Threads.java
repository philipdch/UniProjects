import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Threads extends Thread {

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket socket;
    private Tracker caller;

    public Threads(Socket connection, ObjectInputStream input, ObjectOutputStream output, Tracker caller) {
        this.socket = connection;
        this.input = input;
        this.output = output;
        this.caller = caller;
    }

    public void run() {
        while (true) {
            try {
                int function = input.readInt();
                System.out.println("Client");
                if (function == Functions.REGISTER) {
                    register();
                } else if (function == Functions.LOGIN) {
                    login();
                } else if (function == Functions.LIST) {
                    replyList();
                } else if (function == Functions.DOWNLOAD) {
                    request();
                } else if (function == Functions.LOGOUT) {
                    logout();
                    updateAccounts();
                    break;
                } else if (function == Functions.NOTIFY) {
                    incrementCounters();
                } else if(function == Functions.DETAILS){
                    findPeer();
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                updateAccounts();
                System.out.println("Client ended the connection abruptly");
                break;
            }
            updateAccounts();
        }
    }//run

    private void findPeer() throws  IOException{
        String username = input.readUTF();
        output.writeObject(caller.findPeerByName(username));
        output.flush();
    }

    private void incrementCounters() throws IOException {
        String peerUsername = input.readUTF();
        boolean success = input.readBoolean();
        String connectedPeerName = input.readUTF();
        String receivedChunk = input.readUTF();
        for (Account account : caller.getAccounts()) {
            if (account.getUsername().equals(peerUsername)) {
                if (success) {
                    account.incrementDownloads();
                    for (HashMap.Entry<Integer, PeerDetails> entry : caller.getPeerDetails().entrySet()) {
                        PeerDetails peer = entry.getValue();
                        if (peerUsername.equals(peer.getUsername()))
                            peer.setCountDownloads(account.getCountDownloads());
                    }
                }
            } else {
                account.incrementFailures();
                for (HashMap.Entry<Integer, PeerDetails> entry : caller.getPeerDetails().entrySet()) {
                    PeerDetails peer = entry.getValue();
                    if (peerUsername.equals(peer.getUsername()))
                        peer.setCountFailures(account.getCountFailures());
                }
            }
        }
        if(success) {
            for (HashMap.Entry<Integer, PeerDetails> entry : caller.getPeerDetails().entrySet()) {
                PeerDetails peer = entry.getValue();
                if (connectedPeerName.equals(peer.getUsername())) {
                    ChunksWrapper wrapper = peer.getChunkWrapper(Peer.getBaseName(receivedChunk) + "." + FilenameUtils.getExtension(receivedChunk));
                    if (wrapper != null) {
                        wrapper.getChunks().add(receivedChunk);
                    }
                }
            }
        }
    }


    public void register() throws IOException, ClassNotFoundException {
        String message;
        message = input.readUTF(); // read requested username
        System.out.println(message);
        //Check if exit same user name
        String username = null;
        boolean exist;
        while (true) {
            exist = false;
            for (Account account : caller.getAccounts()) {
                if (account.getUsername().equals(message)) {
                    exist = true;
                    System.out.println("there are+ " + message);
                    break;
                }
            }
            if (exist) {
                output.writeBoolean(exist);
                output.flush();
                message = input.readUTF();
            }
            if (!exist) {
                output.writeBoolean(exist);
                output.flush();
                username = message;
                //receive password
                message = input.readUTF();
                if(message.equals("<exit>"))
                    return;
                break;
            }
        }
        String password = message;
        System.out.println(password);
        caller.getAccounts().add(new Account(username, password));
        output.writeUTF("Register successful!");
        output.flush();
        int token_id = generateToken();
        //send token_id
        output.writeInt(token_id);
        output.flush();
        getInfo(token_id, username);
    }

    public void login() throws IOException, ClassNotFoundException {
        System.out.println("login");
        String username;
        //receive user name
        String message;
        message = input.readUTF();
        System.out.println("Username: " + message);
        boolean validUsername;
        int i;
        while (true) {
            i = -1;
            validUsername = false;

            for (Account item : caller.getAccounts()) {
                i++;
                if (item.getUsername().equals(message)) {
                    validUsername = true;
                    break;
                }
            }
            if (!validUsername) {
                output.writeBoolean(validUsername);
                output.flush();
                message = input.readUTF();
                if(message.equals("<exit>"))
                    return;
            }
            if (validUsername) {
                output.writeBoolean(validUsername);
                output.flush();
                break;
            }

        }
        username = message;
        //receive password
        message = input.readUTF();
        System.out.println("received password: " + message);
        boolean validPass;
        while (true) {
            validPass = false;
            if (caller.getAccounts().get(i).getPassword().equals(message)) {
                validPass = true;
                output.writeBoolean(validPass);
                output.flush();
                break;
            }
            if (!validPass) {
                output.writeBoolean(validPass);
                output.flush();
                message = input.readUTF();
            }
        }
        System.out.println(message);
        output.writeUTF("You are now logged in.");
        output.flush();
        int token_id = generateToken();
        output.writeInt(token_id);
        output.flush();
        getInfo(token_id, username);
    }


    public void request() throws IOException {
        String filename;
        filename = input.readUTF();
        //return peer details
        reply_details(filename);

    }

    public void logout() throws IOException {
        int token_id;
        ArrayList<Integer> arrayList;
        token_id = input.readInt();
        System.out.println("delete token_id " + token_id);
        Iterator<Map.Entry<String, ArrayList<Integer>>> it = caller.getPeerFileMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ArrayList<Integer>> entry = it.next();
            entry.getValue().remove(((Integer) token_id));
            if (entry.getValue().isEmpty()) {
                it.remove();
            }
        }
        caller.getPeerDetails().remove(token_id);
        output.writeUTF("Successful logout");
        output.flush();
        caller.disconnect();
    }

    public void replyList() throws IOException {
        //send list
        List<String> filesList = new ArrayList<>(caller.peerFileMap.keySet());
        output.writeObject(filesList);
        output.flush();
    }

    public void reply_details(String key) throws IOException {
        ArrayList<PeerDetails> peerDetails = new ArrayList<>();
        ArrayList<Integer> arrayList = caller.getPeerFileMap().get(key);
        if (arrayList != null) {
            for (Integer i : arrayList) {
                if (caller.getPeerDetails().containsKey(i)) {
                    peerDetails.add(caller.getPeerDetails().get(i));
                }
            }
            output.writeBoolean(true);
            output.flush();
            output.writeObject(peerDetails);
            output.flush();
        } else {
            output.writeBoolean(false);
            output.flush();
        }
    }

    private void getInfo(int token_id, String username) throws IOException, ClassNotFoundException {
        List<String> wholeFiles = (ArrayList<String>) input.readObject();
        Map<String, ChunksWrapper> list = (HashMap<String, ChunksWrapper>) input.readObject();
        String ip;
        int port;
        //receive ip
        ip = (String) input.readObject();
        System.out.println("ip: " + ip);
        //receive port
        port = input.readInt();
        System.out.println("port: " + port);
        //create peerDetails
        PeerDetails peer = new PeerDetails(ip, port, token_id, username, list);
        caller.getPeerDetails().put(token_id, peer);
        for (Account account : caller.getAccounts()) {
            if (account.getUsername().equals(username)) {
                peer.setCountDownloads(account.getCountDownloads());
                peer.setCountFailures(account.getCountFailures());
                break;
            }
        }
        //check if any token is already associated with this file
        for (String data : list.keySet()) {
            if (caller.getPeerFileMap().containsKey(data)) {
                caller.getPeerFileMap().get(data).add(token_id);
            } else {
                caller.getPeerFileMap().put(data, new ArrayList<>());
                caller.getPeerFileMap().get(data).add(token_id);
            }
        }
    }

    private int generateToken() {
        Random r = new Random();
        int low = 0;
        int high = 3000;
        int token_id = r.nextInt(high - low) + low;
        boolean exist_token_id = false;
        //check token id
        while (true) {
            for (Map.Entry item : caller.getPeerDetails().entrySet()) {
                if ((Integer) item.getKey() == token_id) {
                    exist_token_id = true;
                    break;
                }
            }
            if (exist_token_id == true) {
                token_id = r.nextInt(high - low) + low;

            } else {
                break;
            }
        }
        return token_id;
    }

    private void updateAccounts(){
        try {
            PrintWriter writer = new PrintWriter("./accounts.txt", StandardCharsets.UTF_8);
            for(Account account: caller.getAccounts()){
                writer.println(account.getUsername()+", "+account.getPassword()+", "+account.getCountDownloads()+", "+account.getCountFailures());
            }
            writer.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
