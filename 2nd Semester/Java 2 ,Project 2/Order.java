public class Order {
	//Order details 
	private static int count =1 ; //number of orders by the time a new Order object is created
	private int orderNumber; // tracks each object's individual number 
	private Item orderedProduct;
	private String clientName; 
	private String clientPhone;
	private String orderDate;
	private String arrivalDate;
	private double orderCost;
	private String orderStatus;
	public static final String PENDING = "Pending";
	public static final String COMPLETED = "Arrived";
	
	public Order(){
	}
	
	public Order(Item orderedProduct ,String clientName, String clientPhone ,String orderDate ,String arrivalDate ,String orderStatus){ 
		orderNumber = count;
		count++;
		this.orderedProduct = orderedProduct;
		this.clientName = clientName;
		this.clientPhone = clientPhone;
		this.orderDate = orderDate;
		this.arrivalDate = arrivalDate;
		this.orderCost = orderedProduct.applyDiscount(orderedProduct.getPrice());
		this.orderStatus = orderStatus;
	}
	
	public int getOrderNumber(){
		return this.orderNumber;
	}
	
	public Item getOrderedItem(){
		return this.orderedProduct;
	}
	
	public String getClientName(){
		return this.clientName;
	}
	
	public String getClientPhone(){
		return this.clientPhone;
	}
	
	public String getOrderDate(){
		return this.orderDate;
	}
	
	public String getArrivalDate(){
		return this.arrivalDate;
	}
	
	public double getCost(){
		return this.orderCost;
	}
	
	public String getOrderStatus(){
		return this.orderStatus;
	}
	
	public void setProduct(Item newProduct){
		this.orderedProduct = newProduct;
	}
	
	public void setName(String newName){
		this.clientName = newName;
	}
	
	public void setNumber(int newNumber){
		this.orderNumber = newNumber;
		count = newNumber +1;
	}
	
	public void setPhone(String newPhone){
		this.clientPhone = newPhone;
	}
	
	public void setOrderDate(String newDate){
		this.orderDate = newDate;
	}
	
	public void setArrivalDate(String newDate){
		this.arrivalDate = newDate;
	}
	
	public void setCost(double newCost){
		this.orderCost = newCost;
	}
	
	
	public void setStatus(String newStatus){
		this.orderStatus = newStatus;
	}
	
	public String toString(){ //override toString method . Shows product's details along with order information
		return "Order Details"+"\nProduct specifications : "+getOrderedItem()+"\nOrder number : "+getOrderNumber()+"\nCost : "+getCost()+"\nState : "+getOrderStatus()+		  "\nClient's Name : "+getClientName() +"\nClient's phone number : "+getClientPhone()+"\nOrder date : "+getOrderDate()+"\nEstimated arrival : "+getArrivalDate()+"\n";
	}
}