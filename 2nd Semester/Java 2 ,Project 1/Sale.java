public class Sale {
	
	private static int count=1;
	private int saleNumber ;
	private final Item soldProduct;
	private final String clientName;
	private String clientPhone;
	private String saleDate;
	private double finalCost;
	
	public Sale(Item product ,String name ,String phone, String saleDate ){
		this.saleNumber = count;
		count++;
		this.soldProduct = product;
		this.clientName = name;
		this.clientPhone = phone;
		this.saleDate =saleDate;
		this.finalCost = product.applyDiscount(product.getPrice());
	}
	
	public Sale(Order order){
		this.saleNumber = count;
		count++;
		this.soldProduct = order.getOrderedItem();
		this.clientName = order.getClientName();
		this.clientPhone = order.getClientPhone();
		this.saleDate = order.getOrderDate();
		this.finalCost = order.getCost();
	}

	public int getSaleNumber(){
		return this.saleNumber;
	}
	
	public Item getSoldProduct(){
		return this.soldProduct;
	}
	
	public String getName(){
		return this.clientName;
	}
	
	public String getPhone(){
		return this.clientPhone;
	}
	
	public String getSaleDate(){
		return this.saleDate;
	}
	
	public double getCost(){
		return this.finalCost;
	}
	
	public void setPhone(String newPhone){
		this.clientPhone = newPhone;
	}
	
	public void setCost(double newCost){
		this.finalCost = newCost;
	} 
	
	public void setSaleDate(String newDate){
		this.saleDate = newDate;
	}
	
	public String toString(){
		return "Sale Details "+"\nSold product : "+getSoldProduct()+"\nSale number : "+getSaleNumber()+"\nClient's name : "+getName()+"\nClient's phone : "+getPhone()+"\nDate : "+getSaleDate()+"\nFinal cost : "+getCost()+"\n";
	}
}