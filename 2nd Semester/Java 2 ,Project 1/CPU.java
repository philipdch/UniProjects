public class CPU extends Part{
	
	public static final String INTEL = "Intel";
	public static final String AMD = "AMD";
	
	private String coreSpeed ;
	private int coreNumber;
	private boolean integratedGraphics ;
	
	public CPU(String modelName, String modelYear ,String manufacturer , double price , String coreSpeed , int coreNumber , boolean integratedGraphics){
		super(modelName ,modelYear ,manufacturer ,price);
		this.coreSpeed = coreSpeed;
		this.coreNumber = coreNumber;
		this.integratedGraphics = integratedGraphics;
	}
	
	public String getCorespeed(){
		return this.coreSpeed;
	}
	
	public int getCoreNumber(){
		return this.coreNumber;
	}
	
	public boolean hasIntegratedGraphics(){ //boolean form of next method
		return this.integratedGraphics;
	}
	
	public String getIntegratedGraphics(){ //returns whether a cpu has integrated graphics or not
		return this.integratedGraphics ? "yes" : "no";
	}
	
	public void setCoreSpeed( String newSpeed ){
		this.coreSpeed = newSpeed;
	}
	
	public void setCoreNumber( int newNumber){
		this.coreNumber = newNumber;
	}
	
	public void setGraphics( boolean newIG){
		this.integratedGraphics = newIG;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"Cores' speed : "+getCorespeed()+"\nNumber of cores : "+getCoreNumber()+"\nIntegrated Graphics : "+getIntegratedGraphics()+"\n";
	}
}