public class HardDrive extends Part {
	
	private String hdType;
	private String hdSize;
	private String hdCapacity;
	
	public static final String HD = "HDD";
	public static final String SD = "SSD";
	
	public HardDrive(String modelName, String modelYear ,String manufacturer ,double price, String hdType ,String hdSize ,String hdCapacity){
		super(modelName, modelYear, manufacturer ,price);
		this.hdType = hdType;
		this.hdSize = hdSize;
		this.hdCapacity = hdCapacity;
	}
	
	public String getType(){
		return this.hdType;
	}
	
	public String getHdSize(){
		return this.hdSize;
	}
	
	public String getCapacity(){
		return this.hdCapacity;
	}
	
	public void setType(String newType){
		this.hdType = newType;
	}
	
	public void setSize(String newSize){
		this.hdSize = newSize;
	}
	
	public void setCapacity(String newCap){
		this.hdCapacity = newCap;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"HardDrive Type : "+getType()+"\nHardDrive size : "+getHdSize()+"\nHardDrive capacity : "+getCapacity()+"\n";
	}
}