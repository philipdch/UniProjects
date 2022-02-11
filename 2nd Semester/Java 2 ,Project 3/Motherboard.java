public class Motherboard extends Part{
	
	private String cpuType;
	private String memoryCapacity;
	private int portNumber;
	
	public Motherboard(String modelName ,String modelYear ,String manufacturer ,double price ,String cpuType ,String memoryCapacity ,int portNumber){
		super(modelName ,modelYear ,manufacturer,price);
		this.cpuType= cpuType;
		this.memoryCapacity = memoryCapacity;
		this.portNumber = portNumber;
	}
	
	public Motherboard(){
		super();
	}
	
	public String getType(){
		return this.cpuType;
	}
	
	public String getMemory(){
		return this.memoryCapacity;
	}
	
	public int getPortNumber(){
		return this.portNumber;
	}
	
	public void setCpu(String newCpu){
		this.cpuType = newCpu;
	}
	
	public void setCapacity(String newCap){
		this.memoryCapacity = newCap;
	}
	
	public void setPortNumber(int newNumber){
		this.portNumber = newNumber;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"CPU Type : "+getType()+"\nMemory : "+getMemory()+"\nNumber of ports : "+getPortNumber()+"\n";
	}
}