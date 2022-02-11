import sharedResources.BrokerInfo;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Node {
    private  ArrayList<BrokerInfo> brokers = new ArrayList<>();
    public static final int HASH_MAX = 281;

    public  void init(int i){}
    public void setBrokers(ArrayList<BrokerInfo> brokers){ this.brokers = brokers; }
    public void setBroker(BrokerInfo broker){ this.brokers.add(broker); }
    public  ArrayList<BrokerInfo> getBrokers(){
        return brokers;
    }
    public abstract void connect(String ip, int i) throws IOException;
    public void disconnect()throws  IOException{}


}