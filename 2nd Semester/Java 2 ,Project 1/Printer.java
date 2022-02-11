public class Printer extends Peripheral{
	
	private String technology;
	private String printerType;
	
	public Printer(String modelName ,String modelYear ,String manufacturer ,double price ,String technology , String printerType){
		super(modelName , modelYear, manufacturer ,price);
		this.technology =technology;
		this.printerType = printerType;
	}
	
	public String getTechnology(){
		return this.technology;
	}
	
	public String getType(){
		return this.printerType;
	}
	
	public void setTechnology(String newTechnology){
		this.technology = newTechnology;
	}
	
	public void setType(String newType){
		this.printerType = newType;
	}
	
	public String toString(){
		return super.toString()+"Printer technology : "+getTechnology()+"\nPrinter type : "+getType()+"\n";
	}
}