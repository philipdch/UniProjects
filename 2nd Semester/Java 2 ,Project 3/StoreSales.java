import java.util.*;
import java.io.*;

public class StoreSales{
	
	private SoldList salesList = new SoldList();
	private AvailableProducts productList = new AvailableProducts();
	
	public void loadFile(String data ){
		int count=0;
		 
		File f=null ;
		BufferedReader reader= null ;
		String line = "";
		String fileLine;
		Sale sale = null; //stores the sale found in each entity
		 
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
			boolean itemAvailable; //checks if product in Sale entity exists in AvailableProducts list
			boolean foundModelTag; 
			boolean foundTypeTag;
			String modelName; //stores the specific model of a product
			String readProduct; //stores the Type of a product
			Item product , soldProduct;
			fileLine = reader.readLine();
			count++;
			boolean modelMatch;
			if(fileLine.trim().equalsIgnoreCase("SALES_LIST")){
				fileLine = reader.readLine();
				count++;
				if(fileLine.trim().equals("{")){
					while(fileLine != null){
						modelName = "";
						readProduct="";
						soldProduct = null;
						product = null;
						modelMatch = false;
						foundModelTag= false;
						foundTypeTag = false;
						itemAvailable = false;
						tempLine ="";
						fileLine =reader.readLine();
						count++;
						if(fileLine!= null){
							if(fileLine.trim().equalsIgnoreCase("SALE")){
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
											readProduct = fileLine.trim().substring(9).trim();
										}else if(tempLine.trim().startsWith("MODEL")){
											foundModelTag = true;
											modelName = fileLine.trim().substring(6).trim();
										}	
										fileLine = reader.readLine();
										count++;
									}
									if(foundModelTag && foundTypeTag){
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
												soldProduct = product;
												break;
											}
										}
									}
									if(itemAvailable){
										sale = new Sale();
										sale.setCost(soldProduct.applyDiscount(soldProduct.getPrice())); //set final cost based on Product's price
										for(int i =0 ;i< linesList.size() ;i++){ //find rest of Order characteristics
											line = linesList.get(i);
											tempLine = line.toUpperCase();
											if(tempLine.trim().startsWith("NUMBER")){
												sale.setNumber(Integer.parseInt(line.trim().substring(7).trim()));
											}else if(tempLine.trim().startsWith("NAME")){
												sale.setName(line.trim().substring(5));
											}else if(tempLine.trim().startsWith("SALE_DATE")){
												sale.setDate(line.trim().substring(10).trim());
											}else if(tempLine.trim().startsWith("PHONE")){
												sale.setPhone(line.trim().substring(6));
											}else if(tempLine.trim().startsWith("PRICE")){
												sale.setCost(Float.parseFloat(line.trim().substring(6).trim())); //set final cost based on Price tag
											}
										}
										sale.setProduct(soldProduct);
										salesList.addSale(sale);
									}else { System.out.println(readProduct + " "+modelName+" not available"); }
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
			Sale sale;
			writer.write("SALES_LIST" );
			writer.newLine();
			writer.write("\t "+"{" );
			writer.newLine();
			for (int i =0 ; i<salesList.size() ; i++) {
				sale = salesList.getSale(i);
				product = sale.getSoldProduct();
				writer.write("\t"+ "SALE");
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
				writer.write("\t\t MODEL "+ product.getModelName());
				writer.newLine();
				writer.write("\t\t" + "NUMBER "+ sale.getSaleNumber());
				writer.newLine();
				writer.write("\t\t" + "NAME "+ sale.getName());
				writer.newLine();
				writer.write("\t\t" + "PHONE "+sale.getPhone());
				writer.newLine();
				writer.write("\t\t" + "SALE_DATE "+sale.getSaleDate());
				writer.newLine();
				writer.write("\t\t" + "FINAL_COST "+sale.getCost());
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
	
	public Sale get(int index){
		return this.salesList.getSale(index);
	}
	
	public void addList(AvailableProducts list){
		this.productList = list;
	}
	
	public void add(Sale sale){
		this.salesList.addSale(sale);
	}
	
	public int size(){
		return this.salesList.size();
	}
	
	public SoldList getSalesList(){
		return this.salesList;
	}

}
	
			
								
							
	