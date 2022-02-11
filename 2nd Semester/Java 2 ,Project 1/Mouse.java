public class Mouse extends Peripheral {
	
	private String technology;
	private String connectionType;
	public static final String WIRELESS = "Wireless";
	public static final String WIRED = "Wired - USB";
	
	public Mouse( String modelName , String modelYear ,String manufacturer ,double price, String technology ,String connectionType ){
		super(modelName , modelYear ,manufacturer ,price);
		this.technology = technology;
		this.connectionType = connectionType;
	}
	
	public String getTechnology(){
		return this.technology;
	}
	
	public String getType(){
		return this.connectionType;
	}
	
	public void setTechnology( String newTechnology ){
		this.technology = newTechnology;
	}
	
	public void setType(String newType){
		this.connectionType = newType;
	}
	
	public String toString(){
		return super.toString()+"Mouse technology : "+getTechnology()+"\nConnection Type : "+getType()+"\n";
	}
}