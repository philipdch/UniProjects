import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PublisherUser {
    private Socket client;
    private String ipToConnect;
    private int portToConnect;
    private Scanner input = new Scanner(System.in);
    private Map<String, String> id = new HashMap<>();

    public PublisherUser(String pubIp, int pubPort){
        this.ipToConnect = pubIp;
        this.portToConnect = pubPort;
    }

    public Map<String, String> getId(){
        return this.id;
    }

    public void setId(String name, String password){
        id.put(name, password);
    }

    public static void main(String[] args) {
        PublisherUser user = new PublisherUser("0.0.0.0", 5560);
        System.out.println("Enter your credentials");
        System.out.print("Username: ");
        String name = user.input.nextLine();
        System.out.print("Password: ");
        String password = user.input.nextLine();
        //TODO check id
        user.setId(name, password);
        int action = -1;
        while(action!=2){
            System.out.println("What would you like to do?\n1)Close publisher\n2)Exit application");
            action = Integer.parseInt(user.input.nextLine());
            switch(action){
                case 1:
                    user.closePublisher();
                    break;
                case 2:
                    System.out.println("Exiting application");
                    break;
                default:
                    System.out.println("No action was specified");
            }
        }
    }

    private void closePublisher() {
        try {
            client = new Socket(ipToConnect, portToConnect);
        }catch(IOException e){
            System.err.println("Server is closed. Please try again later");
            return;
        }
        try {
            ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            try {
                Object received;
                out.writeObject(this.id); //send id to Server for verification
                received = in.readObject(); //read Server's answer
                System.out.println(received);
                //TODO send identification (Map<String, String> for name ans pass (?))
                out.writeObject(input.nextLine());
            } catch (ClassNotFoundException e) {
                System.err.println("Unknown class received");
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
