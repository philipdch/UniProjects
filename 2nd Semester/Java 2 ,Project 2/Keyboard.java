public class Keyboard extends Peripheral {
	
	private String connectionType;
	public static final String WIRELESS = "Wireless";
	public static final String WIRED = "Wired - USB";
	
	public Keyboard( String modelName ,String modelYear ,String manufacturer ,double price ,String connectionType){
		super(modelName , modelYear ,manufacturer ,price);
		this.connectionType = connectionType;
	}
	
	public Keyboard(){
		super();
	}
	
	public String getType(){
		return this.connectionType;
	}
	
	public void setType(String newType){
		this.connectionType = newType;
	}
	
	public String toString(){
		return super.toString()+"Connection Type : "+getType()+"\n";
	}
}