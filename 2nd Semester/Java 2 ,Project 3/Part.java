public class Part extends Item{
	
	private static final double PARTS_DISCOUNT =0.25; //Discount for all Part objects 
	
	public Part(String modelName ,String modelYear ,String manufacturer, double price){
		super(modelName, modelYear ,manufacturer, price);
	}
	
	public Part(){
		super();
	}
	
	public double getDiscount(){ //define getDiscount method
		return PARTS_DISCOUNT;
	}
	
	public double applyDiscount(double price){ //define applyDiscount for all Part objects
		double newPrice = price*(1- PARTS_DISCOUNT);
		return newPrice;
	}
	
	public String toString(){ //override toString() to show item's full specifications
		return super.toString()+"Discount : "+getDiscount()*100+" % \n";
	}
}
