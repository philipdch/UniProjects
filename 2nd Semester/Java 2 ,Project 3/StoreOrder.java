import java.util.*;
import java.io.*;

public class StoreOrder{
	
	private OrdersList orderList = new OrdersList();
	private AvailableProducts productList = new AvailableProducts();
	
	public void loadFile(String data ){
		int count=0;
		 
		File f=null ;
		BufferedReader reader= null ;
		String line = "";
		String fileLine;
		Order order = null; //stores the sale found in each entity
		 
		try{
		     f=new File(data);
		}catch(NullPointerException e){
		     System.err.println("File not found.");
		}
		
		try{
		     reader=new BufferedReader(new FileReader(f));
		}catch(FileNotFoundException e){
		     System.err.println("Error opening File!");
		}
		
		try{
			ArrayList<String> linesList = new ArrayList<String>();
			String tempLine;
			boolean itemAvailable; //checks if product in Order entity exists in AvailableProducts list
			boolean foundModelTag; 
			boolean foundTypeTag;
			boolean foundPriceTag;
			String modelName; //stores the specific model of a product
			String readProduct; //stores the Type of a product
			float price;
			Item product , orderProduct;
			fileLine = reader.readLine();
			count++;
			boolean modelMatch;
			if(fileLine.trim().equalsIgnoreCase("ORDERS_LIST")){
				fileLine = reader.readLine();
				count++;
				if(fileLine.trim().equals("{")){
					while(fileLine != null){
						modelName = "";
						readProduct="";
						orderProduct = null;
						product = null;
						modelMatch = false;
						foundModelTag= false;
						foundTypeTag = false;
						foundPriceTag=false;
						itemAvailable = false;
						tempLine ="";
						fileLine =reader.readLine();
						count++;
						if(fileLine!= null){
							if(fileLine.trim().equalsIgnoreCase("ORDER")){
								fileLine = reader.readLine();
								count++;
								if(fileLine.trim().equals("{")){
									fileLine = reader.readLine();
									count++;
									while(fileLine != null && !(fileLine.trim().equals("}"))){ //Scan ORDER Entity to determine if product is defined
										linesList.add(fileLine); //store Order entity in a list 
										tempLine = fileLine.toUpperCase();				
										if(tempLine.trim().startsWith("ITEM_TYPE")){
											foundTypeTag = true;
											readProduct = fileLine.trim().substring(10).trim();
										}else if(tempLine.trim().startsWith("MODEL")){
											foundModelTag = true;
											modelName = fileLine.trim().substring(6).trim();
										}
										fileLine = reader.readLine();
										count++;
									}
									if(foundModelTag && foundTypeTag ){
										for(int i =0 ;i<productList.listSize() ;i++){ //scan productList to determine if product in ORDER entity exists
											product = productList.getProduct(i);
											modelMatch = product.getModelName().equalsIgnoreCase(modelName);
											//check if product in list and product in Sale entity correspond to the same Item type
											if(((product instanceof CPU && readProduct.equalsIgnoreCase("CPU")) || 
											 (product instanceof GPU && readProduct.equalsIgnoreCase("GPU")) ||
											 (product instanceof Motherboard &&readProduct.equalsIgnoreCase("Motherboard")) ||
											 (product instanceof RAM && readProduct.equalsIgnoreCase("RAM")) ||
											 (product instanceof HardDrive && readProduct.equalsIgnoreCase("HardDrive")) ||
											 (product instanceof Monitor && readProduct.equalsIgnoreCase("Monitor")) ||
											 (product instanceof Mouse && readProduct.equalsIgnoreCase("Mouse")) ||
											 (product instanceof Keyboard && readProduct.equalsIgnoreCase("Keyboard")) ||
											 (product instanceof Printer && readProduct.equalsIgnoreCase("Printer"))) && modelMatch){
												itemAvailable = true;
												orderProduct = product;
												break;
											}
										}
									}
									if(itemAvailable){
										order = new Order();
										order.setCost(orderProduct.applyDiscount(orderProduct.getPrice())); //set final cost based on Product's price										
										for(int i =0 ;i< linesList.size() ;i++){ //find rest of Order characteristics
											line = linesList.get(i);
											tempLine = line.toUpperCase();
											order.setStatus(Order.PENDING);
											if(tempLine.trim().startsWith("NUMBER")){
												order.setNumber(Integer.parseInt(line.trim().substring(7).trim()));
											}else if(tempLine.trim().startsWith("NAME")){
												order.setName(line.trim().substring(5).trim());
											}else if(tempLine.trim().startsWith("PHONE")){
												order.setPhone(line.trim().substring(6).trim());
										    }else if(tempLine.trim().startsWith("ORDER_DATE")){
											     order.setOrderDate(line.trim().substring(11).trim());
											}else if (tempLine.trim().startsWith("FINAL_COST")){
												order.setCost(Float.parseFloat(line.trim().substring(11).trim()));
											}else if(tempLine.trim().startsWith("DELIVERY_DATE")){
											     order.setArrivalDate(line.trim().substring(14).trim());
											}else if(tempLine.trim().startsWith("ORDER_STATUS")){
												order.setStatus(line.trim().substring(13).trim());
											}
										}
										order.setProduct(orderProduct);
										orderList.addOrder(order);
									}else { System.out.println("Product not available"); }
								}
							}
						}
						linesList.clear();
					}
				}
			}
		}catch (IOException e){
			System.out.println("Error reading line : "+count);
		}try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing file.");
        }
	}
	
	public void writeFile(String data){
		File f = null;
		BufferedWriter writer = null;

		try	{
			f = new File(data);
		}
		catch (NullPointerException e) {
			System.out.println ("Can't create file");
		}

		try	{
			writer = new BufferedWriter(new FileWriter(f));
		}
		catch (IOException e){System.out.println("Can't write to file"); 
		}
		try	{
			Item product;
			Order order;
			writer.write("ORDERS_LIST" );
			writer.newLine();
			writer.write("\t "+"{" );
			writer.newLine();
			for (int i =0 ; i<orderList.size() ; i++) {
				order = orderList.getOrder(i);
				product = order.getOrderedItem();
				writer.write("\t"+ "ORDER");
				writer.newLine();
				writer.write("\t"+ "{");
				writer.newLine();
				if(product instanceof CPU){
					writer.write("\t\tITEM_TYPE CPU ");
				}else if(product instanceof GPU){
					writer.write("\t\tITEM_TYPE GPU ");
				}else if(product instanceof RAM){
					writer.write("\t\tITEM_Type RAM ");
				}else if(product instanceof Motherboard){
					writer.write("\t\tITEM_TYPE Motherboard " );
				}else if(product instanceof HardDrive){
					writer.write("\t\tITEM_TYPE HardDrive ");
				}else if(product instanceof Monitor){
					writer.write("\t\tITEM_TYPE Monitor ");
				}else if(product instanceof Mouse){
					writer.write("\t\tItem_TYPE Mouse ");
				}else if(product instanceof Keyboard){
					writer.write("\t\tITEM_TYPE Keyboard ");
				}else if(product instanceof Printer){
					writer.write("ITEM_TYPE Printer ");
				}
				writer.newLine();
				writer.write("\t\t" + "MODEL " + product.getModelName());
				writer.newLine();
				writer.write("\t\t" + "NUMBER "+ order.getOrderNumber());
				writer.newLine();
				writer.write("\t\t" + "NAME "+ order.getClientName());
				writer.newLine();
				writer.write("\t\t" + "PHONE "+order.getClientPhone());
				writer.newLine();
				writer.write("\t\t" + "ORDER_DATE "+order.getOrderDate());
				writer.newLine();
				writer.write("\t\t" + "DELIVERY_DATE "+order.getArrivalDate());
				writer.newLine();
				writer.write("\t\t" + "FINAL_COST "+order.getCost());
				writer.newLine();
				writer.write("\t\t" + "ORDER_STATUS " + order.getOrderStatus());
				writer.newLine();
				writer.write("\t" +"}" +"\n");
				writer.newLine();
			}
			writer.newLine();
			writer.write("}");
		}
		catch (IOException e) {
			System.err.println("Write error!");
		}
		try {
			writer.close();

		}
		catch (IOException e) {
			System.err.println("Error closing file.");
		}
	}
	
	public Order get(int index){
		return this.orderList.getOrder(index);
	}
	
	public void addList(AvailableProducts list){
		this.productList = list;
	}
	
	public void add(Order order){
		this.orderList.addOrder(order);
	}
	
	public int size(){
		return this.orderList.size();
	}
	
	public OrdersList getOrderList(){
		return this.orderList;
	}

}
	
			
								
							
	