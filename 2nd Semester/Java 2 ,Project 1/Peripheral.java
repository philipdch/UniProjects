public class Peripheral extends Item{
	
	private static final double PERIPHERALS_DISCOUNT = 0.1; //Discount for all Peripheral objects
	
	public Peripheral(String modelName ,String modelYear ,String manufacturer, double price){
		super(modelName, modelYear, manufacturer, price);
	}
	
	public double getDiscount(){ //define getDiscount method for Peripheral
		return PERIPHERALS_DISCOUNT;
	}
	
	public double applyDiscount(double price){ //define applyDiscount for all Peripheral objects
		double newPrice = price*(1- PERIPHERALS_DISCOUNT);
		return newPrice;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"Discount : "+getDiscount()*100+" % \n";
	}
}
