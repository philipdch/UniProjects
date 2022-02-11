package com.example.projectui;

public class MenuSettings {

    private int power;
    private String time;
    private boolean grillOn;
    private double grillPercent;


    public MenuSettings(int power, String time, boolean grillOn, double grillPercent) {
        this.power = power;
        this.time = time;
        this.grillOn = grillOn;
        this.grillPercent = grillPercent;
    }
    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isGrillOn() {
        return grillOn;
    }

    public void setGrillOn(boolean grillOn) {
        this.grillOn = grillOn;
    }

    public double getGrillPercent() {
        return grillPercent;
    }

    public void setGrillPercent(float grillPercent) {
        this.grillPercent = grillPercent;
    }

    public String toString(){
        String s = "Microwave power level = " + power +"\nCooking time: " + time + " Minutes\n";
        if(grillOn){
            s += "Grill: On\nGrill Power = " + grillPercent;
        }else{
            s+= "Grill: Off\n";
        }
        return s;
    }
}
