import java.util.*;
public class Monitor extends Peripheral{
	
	private String display , size ,resolution;
	private ArrayList<String> ports ; //all ports can be stored in a list
	
	public Monitor(String modelName ,String modelYear ,String manufacturer ,double price ,String display ,String size ,String resolution ,ArrayList<String> ports){
		super(modelName ,modelYear ,manufacturer ,price);
		this.display = display;
		this.size = size;
		this.resolution = resolution;
		this.ports = ports;
	}

	public String getDisplay(){
		return this.display;
	}
	
	public String getSize(){
		return this.size;
	}
	
	public String getResolution(){
		return this.resolution;
	}
	
	public ArrayList<String> getPorts(){
		return this.ports;
	}
	
	public void setDisplay(String newDisplay){
		this.display = newDisplay;
	}
	
	public void setSize(String newSize){
		this.size = newSize;
	}
	
	public void setResolution(String newResolution){
		this.resolution = newResolution;
	}
	
	public void setPorts(ArrayList<String> newPorts){
		this.ports = newPorts;
	}
	
	public String toString(){
		return super.toString()+ "Display : "+getDisplay()+"\nMonitor size : "+getSize()+"\nScreen resolution : "+getResolution()+"\nAvailable ports : "+getPorts()+"\n";
	}
}
		