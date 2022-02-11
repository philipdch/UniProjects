
public class GPU extends Part{
	
	private String gpuType;
	private String memoryCapacity;
	
	public static final String NVIDIA = "Nvidia";
	public static final String AMD = "AMD";
	
	public GPU(String modelName ,String modelYear ,String manufacturer ,double price ,String gpuType ,String memoryCapacity){
		super(modelName, modelYear, manufacturer ,price);
		this.gpuType = gpuType;
		this.memoryCapacity = memoryCapacity;
	}
	
	public GPU(){
		super();
	}
	
	public String getType(){
		return this.gpuType;
	}
	
	public String getMemoryCapacity(){
		return this.memoryCapacity;
	}
	
	public void setType(String newType){
		this.gpuType= newType;
	}
	
	public void setCapacity( String newCap){
		this.memoryCapacity = newCap;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"GPU Manufacturer : "+getType()+"\nMemory : "+getMemoryCapacity()+"\n";
	}
}