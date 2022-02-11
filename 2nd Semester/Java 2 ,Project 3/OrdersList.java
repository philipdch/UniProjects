import java.util.*;

public class OrdersList{
	
	private ArrayList<Order> ordersList = new ArrayList<Order>();
	
	public void showOrders(){
		int count = 0;
		for( Order x : ordersList){
			System.out.println(count+".");
			count++;
			System.out.println(x.getOrderedItem().getModelName());
			System.out.println("Order number : "+x.getOrderNumber());
			System.out.println("Order status : "+x.getOrderStatus());
			System.out.println("");
		}
	}
	
	public void addOrder(Order newOrder){
		this.ordersList.add(newOrder);
	}
	
	public void removeOrder(int index){
		if(index>0 && index<ordersList.size())
			this.ordersList.remove(index);
		else
			System.out.println("Order doesn't exist");
	}
	
	public boolean contains(Order order){
		return ordersList.contains(order);
	}
	
	public boolean isEmpty(){
		if(ordersList.size()==0)
			return true;
		return false;
	}
	
	public int size(){
		return this.ordersList.size();
	}
	
	public Order getOrder(int index){
		if(index>=0 && index <ordersList.size()){
			return ordersList.get(index);
		}
		return null;
	}
	
}	