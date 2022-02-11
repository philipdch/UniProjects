package sharedResources;

import java.io.Serializable;

public class Range implements Serializable {

   private double low;
    private double high;
    private String intervalType;

    private Range(double low, double high, String intervalType){
        this.low = low;
        this.high = high;
        this.intervalType = intervalType;
    }

    public static Range open(double low, double high){
        return new Range(low, high, "open");
    }

    public static Range closed(double low, double high){
        return new Range(low, high, "closed");
    }

    public static Range leftOpen(double low, double high){
        return new Range(low, high, "leftOpen");
    }

    public static Range rightOpen(double low, double high){
        return new Range(low, high, "rightOpen");
    }

    public boolean contains(double data){
        switch(intervalType) {
            case "open":
                return data > low && data < high;
            case "leftOpen":
                return data > low && data <= high;
            case "rightOpen":
                return data >= low && data < high;
            default:
                return data >= low && data <= high;
        }
    }

    public String toString(){
        return this.low + "-" + this.high;
    }
}
