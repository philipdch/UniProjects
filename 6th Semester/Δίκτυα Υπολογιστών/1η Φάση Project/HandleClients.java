import java.io.*;
import java.net.Socket;

public class HandleClients extends Thread {
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket socket;
    Peer caller;

    public HandleClients(Socket socket, Peer caller) {
        try {
            this.out = new ObjectOutputStream(socket.getOutputStream());
            this.in = new ObjectInputStream(socket.getInputStream());
            this.socket = socket;
            this.caller = caller;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run() {
        FileInputStream fin = null;
        try {
            int received = in.readInt();
            if (received == Functions.PING) {
                for(int i=0; i<3; i++) {
                    out.writeUTF("Echo");
                    out.flush();
                }
            } else if (received == Functions.DOWNLOAD) {
                String requestedFilename = in.readUTF();
                System.out.println("Requested file: "+requestedFilename);
                try {
                    fin = new FileInputStream(new File("./"+caller.getDirectory()+"/SharedResources/" + requestedFilename));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //read and send file
                byte[] bytes = new byte[4096];
                int count;
                while ((count = fin.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
            }
            out.close();
            fin.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
