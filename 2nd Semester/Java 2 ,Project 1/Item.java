public abstract class Item { //abstract class which contains instance variables and methods shared by all items
	
	private String modelName , modelYear , manufacturer;
	private double price;
	
	public Item(String modelName ,String modelYear ,String manufacturer, double price){
		this.modelName = modelName;
		this.modelYear = modelYear;
		this.manufacturer = manufacturer;
		this.price = price;
	}
	
	public String getModelYear(){
		return modelYear;
	}
	
	public String getModelName(){
		return modelName;
	}
	
	public String getManufacturer(){
		return manufacturer;
	}
	
	public double getPrice(){
		return price;
	}
	
	public void setModelName( String newName ){
		this.modelName = newName;
	}
	
	public void setModelYear( String newYear){
			this.modelYear = newYear;
	}
	
	public void setManufacturer( String newManufacturer ){
		this.manufacturer = newManufacturer;
	}
	
	public void setPrice(double newPrice){
		if( newPrice >=0 ) //Check that a valid price is given
			this.price = newPrice;
		else
			System.out.printf("%s%n","Invalid price tag");
	}
	
	public abstract double getDiscount(); //abstract method is defined in subclasses Part and Peripheral
	public abstract double applyDiscount(double price); //applies specified discount to any Part or Peripheral object 
	
	public String toString(){ //override toString() to show item's full specifications
		return "Model name : "+ getModelName() +"\nModel year : "+ getModelYear() +"\nManufacturer : "+ getManufacturer() +"\nPrice : "+ getPrice() +"\n";
	}
}