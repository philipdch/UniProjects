import java.io.Serializable;
import java.net.InetAddress;

public class PeerDetails implements Serializable {
    private  String ip;
    private  int port;
    private  String username;
    private int token_id;
    private  int count_download;
    private int count_failures;


    public PeerDetails(){}

    public PeerDetails(String ip, int port, int toke_id, String username){
        this.ip=ip;
        this.port=port;
        this.token_id=toke_id;
        this.username=username;
    }

    public int getToken_id() {
        return token_id;
    }

    public void setToken_id(int token_id) {
        this.token_id = token_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setCountDownloads(int count_download) {
        this.count_download = count_download;
    }

    public void setCountFailures(int count_failures) {
        this.count_failures = count_failures;
    }

    public int getCountDownloads() {
        return count_download;
    }

    public int getCountFailures() {
        return count_failures;
    }

    @Override
    public String toString() {
        return "\nUsername: "+username+"\nIP: "+ip+"\nPort: "+port+"\nSuccessful Downloads: "+count_download+"\nFailed Downloads: "+count_failures+"\n";
    }
}
