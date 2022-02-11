public class RAM extends Part{
	
	private String memoryType;
	private int capacity ;
	private String frequency;
	
	public RAM(String modelName ,String modelYear ,String manufacturer ,double price ,String memoryType ,int capacity, String frequency){
		super(modelName ,modelYear ,manufacturer,price);
		this.memoryType = memoryType;
		this.capacity = capacity;
		this.frequency = frequency;
	}
	
	public RAM(){
		super();
	}
	
	public String getType(){
		return this.memoryType;
	}
	
	public int getCapacity(){
		return this.capacity;
	}
	
	public String getFrequency(){
		return this.frequency;
	}
	
	public void setType(String newType){
		this.memoryType = newType;
	}
	
	public void setCapacity(int newCap){
		this.capacity = newCap;
	}
	
	public void setFrequency(String newFrequency){
		this.frequency = newFrequency;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"Memory Type : "+getType()+"\nCapacity : "+getCapacity()+" GB\nFrequence :"+getFrequency()+" MHz\n";
	}
}