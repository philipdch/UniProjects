import java.util.*;
public class AvailableProducts{
	
	private ArrayList<Item> productList = new ArrayList<>();
	private ArrayList<Integer> quantityList = new ArrayList<>();
	private String productType;
	
	public AvailableProducts(){
	}
	
	public AvailableProducts(String productType , Item product ,Integer quantity){
		this.productType = productType;
		this.productList.add(product);
		this.quantityList.add(quantity);
	}
	
	public void addProduct(Item product, Integer quantity){
		productList.add(product);
		quantityList.add(quantity);
	}
	
	public void removeProduct(Item product){
		productList.remove(product);
		quantityList.remove(product);
	}
	
	public int listSize(){
		return this.productList.size();
	}
		
	
	public String getProductType(){
		return this.productType;
	}
	
	public Item getProduct(int index){
		if(index>=0 && index<productList.size())
			return productList.get(index);
		return null;
	}
	
	public boolean isAvailable(Item product){
		return getProductQuantity(product)>0;
	}
	
	public Integer getProductQuantity(Item product){
		int index = productList.indexOf(product);
		if(index != -1) 
			return this.quantityList.get(index);
		return -1;
	}
	
	public void setProductQuantity(Item product, Integer newQuantity){
		int index = productList.indexOf(product);
		if(index != -1)
			this.quantityList.set(index ,newQuantity);
	}
	
	public void reduceQuantity(Item product, int reduction){
		int index = productList.indexOf(product);
		int newQuantity = getProductQuantity(product)-reduction;
		if(index != -1){
			if(newQuantity>=0){
			 this.quantityList.set(index , (newQuantity));
			}else{
				System.out.println("Invalid quantity value");
			}
		}
	}
	
	public void showProducts(){
		int count=0 ;
		for (Item x : productList){
			System.out.println(count+".");
			System.out.println(x.getModelName() +"\nPrice : "+x.getPrice()+"\nQuantity : "+getProductQuantity(x)+"\n");
			count++;
		}
	}
}
		
		