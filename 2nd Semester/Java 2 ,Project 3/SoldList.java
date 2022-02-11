import java.util.*;

public class SoldList {
	
	private ArrayList<Sale> salesList = new ArrayList<>();
	
	public void showSales(){
		int count =0;
		for(Sale y : salesList){
			System.out.println(count+".");
			System.out.println(y.getSoldProduct().getModelName());
			System.out.println("Sale number : "+y.getSaleNumber());
			System.out.println("");
			count++;
		}
	}
	
	public Sale getSale(int index){
		if(index>=0 && index<salesList.size()){
			return salesList.get(index);
		}
		return null;
	}
	
	public int size(){
		return salesList.size();
	}
	
	public boolean isEmpty(){
		if(salesList.size()==0)
			return true;
		return false;
	}
	
	public void addSale(Sale sale){
		this.salesList.add(sale);
	}
	
	public void removeSale(int index){
		if (index>=0 && index<salesList.size()){
			this.salesList.remove(index);
		}else{
			System.out.println("List does not contain this sale");
		}
	}
	
}
	
	
	
	
	
	
	

	