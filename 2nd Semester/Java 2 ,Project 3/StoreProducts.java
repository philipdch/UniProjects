import java.io.*;
import java.util.*;

public class StoreProducts {
     
	 private AvailableProducts productsList = new AvailableProducts(); //create Products List
	 
	public void loadFile(String data){
	    int count=0;
		 
		File f=null ;
		BufferedReader reader= null ;
		String line = "";
		String fileLine;
		Item product = null; //stores the product found in each entity
		 
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
			String modelName;
			float price;
			String readProduct; //stores specific type of Item found after the ITEM_TYPE tag
			boolean foundTypeTag; //checks if entity contains an ITEM_TYPE tag
			boolean foundModelTag; //checks if entity contains a MODEL tag
			boolean foundPriceTag; // checks if entity contains a PRICE tag
			boolean allTagsFound; //checks if all of the previous tags are contained in the entity
			int productQuantity; //stores the number of pieces of a product ,if found
			ArrayList<String> linesList = new ArrayList<String>(); //stores each entity's lines for easier access . List is cleared each time a new entity is found
			String tempLine; //holds a modified version of Line 
			fileLine = reader.readLine();
			if (fileLine.trim().equalsIgnoreCase("ITEM_LIST")){ //start of Item list file
				fileLine = reader.readLine();
				count++;
				if(fileLine.trim().startsWith("{")){
					while(fileLine!=null){
						modelName = "";
						price = 0;
						fileLine= reader.readLine();
						product = null;
						foundModelTag = false;
						foundPriceTag = false;
						foundTypeTag = false;
						productQuantity =0;
						count++;
						readProduct = "";
						if(fileLine!=null){
						if(fileLine.trim().equalsIgnoreCase("ITEM")){
							fileLine = reader.readLine();
							count++;
							if(fileLine.trim().equals("{")){
								fileLine = reader.readLine();
								count++;
								while(fileLine != null && !(fileLine.trim().equals("}"))){
									linesList.add(fileLine);
									tempLine = fileLine.toUpperCase();				
									if(tempLine.trim().startsWith("ITEM_TYPE")){
										foundTypeTag = true;
										readProduct = fileLine.trim().substring(10).trim();
									}else if(tempLine.trim().startsWith("MODEL_NAME")){
										foundModelTag = true;
										modelName = fileLine.trim().substring(11).trim();	
									}else if(tempLine.trim().startsWith("PRICE")){
										foundPriceTag = true;
										price  = Float.parseFloat(fileLine.trim().substring(6).trim());
									}
									fileLine = reader.readLine();
									count++;
								}
								allTagsFound = foundModelTag && foundPriceTag && foundTypeTag ;
								if(allTagsFound){ //all of the above tags must be found in order to initiate search for other product characteristics
									if(readProduct.equalsIgnoreCase("CPU")){
										product = new CPU();
									}else if(readProduct.equalsIgnoreCase("GPU")){
										product = new GPU();
									}else if(readProduct.equalsIgnoreCase("Motherboard")){
										product = new Motherboard();
									}else if(readProduct.equalsIgnoreCase("RAM")){
										product = new RAM();
									}else if(readProduct.equalsIgnoreCase("HardDrive")){
										product = new HardDrive();
									}else if(readProduct.equalsIgnoreCase("Monitor")){
										product = new Monitor();
									}else if(readProduct.equalsIgnoreCase("Printer")){
										product = new Printer();
									}else if(readProduct.equalsIgnoreCase("Mouse")){
										product = new Mouse();
									}else if(readProduct.equalsIgnoreCase("Keyboard")){
										product = new Keyboard();
									}
									for(int i=0 ; i<linesList.size() ;i++){
										line = linesList.get(i);
										tempLine = line.toUpperCase();
										//PARTS
										if(tempLine.trim().startsWith("MODEL_YEAR")){ 
												product.setModelYear(line.trim().substring(11).trim());
										}else if(tempLine.trim().startsWith("MANUFACTURER")){
											product.setManufacturer(line.trim().substring(13).trim());
										}else if(tempLine.trim().startsWith("PIECES")){ 
											productQuantity = Integer.parseInt(line.trim().substring(7).trim());
										}					
										
										if(product instanceof CPU){ //if for CPU product specifications
											if(tempLine.trim().startsWith("CORE_SPEED")){
												((CPU)product).setCoreSpeed(line.trim().substring(11).trim());
											}else if (tempLine.trim().startsWith("CORES")){
												((CPU)product).setCoreNumber(Integer.parseInt(line.trim().substring(6).trim()));
											}else if(tempLine.trim().startsWith("INTEGRATED_GRAPHICS")){
												((CPU)product).setGraphics(Boolean.parseBoolean(line.trim().substring(20).trim()));
											}
										}else if(product instanceof GPU){ //if for GPU specifications
											if(tempLine.trim().startsWith("TYPE")){
												((GPU)product).setType(line.trim().substring(4).trim());
											}else if(tempLine.trim().startsWith("MEMORY")){
												((GPU)product).setCapacity(line.trim().substring(7).trim());
											}
										}else if(product instanceof HardDrive ){ //if for HardDrive specifications
											if(tempLine.trim().startsWith("TYPE")){
												((HardDrive)product).setType(line.trim().substring(4).trim());
											}else if(tempLine.trim().startsWith("MEMORY")){
												((HardDrive)product).setCapacity(line.trim().substring(6).trim());
											}else if(tempLine.trim().startsWith("SIZE")){
												((HardDrive)product).setSize(line.trim().substring(4).trim());
											}
										}else if(product instanceof RAM){ //if for RAM specifications
											if(tempLine.trim().startsWith("TYPE")){
												((RAM)product).setType(line.trim().substring(5).trim());
											}else if(tempLine.trim().startsWith("FREQUENCY")){
												((RAM)product).setFrequency(line.trim().substring(10).trim());
											}else if(tempLine.trim().startsWith("CAPACITY")){
												((RAM)product).setCapacity(Integer.parseInt(line.trim().substring(9).trim()));
											}
										}else if(product instanceof Motherboard){// if for motherboard specifications
											if(tempLine.trim().startsWith("CPU_TYPE")){
												((Motherboard)product).setCpu(line.trim().substring(9).trim());
											}else if(tempLine.trim().startsWith("PORTS")){
												((Motherboard)product).setPortNumber(Integer.parseInt(line.trim().substring(5).trim()));
											}else if(tempLine.trim().startsWith("MEMORY")){
												((Motherboard)product).setCapacity(line.trim().substring(7).trim());
											}
											
										//PERIPHERALS
										}else if(product instanceof Monitor){ //if for Monitor specifications
											if(tempLine.trim().startsWith("DISPLAY")){
												((Monitor)product).setDisplay(line.trim().substring(7).trim());
											}else if(tempLine.trim().startsWith("SIZE")){
												((Monitor)product).setSize(line.trim().substring(4).trim());
											}else if(tempLine.trim().startsWith("RESOLUTION")){
												((Monitor)product).setResolution(line.trim().substring(10).trim());
											}else if(tempLine.trim().startsWith("PORTS")){
												((Monitor)product).setPorts(line.trim().substring(6).trim());
											}
										}else if(product instanceof Mouse){ //if for Mouse specifications
											if(tempLine.trim().startsWith("TECHNOLOGY")){
												((Mouse)product).setTechnology(line.trim().substring(10).trim());
											}else if(tempLine.trim().startsWith("TYPE")){
												((Mouse)product).setType(line.trim().substring(4).trim());
											}
										}else if(product instanceof Keyboard){ //if for Keyboard specifications
											if(tempLine.trim().startsWith("TYPE")){
												((Keyboard)product).setType(line.trim().substring(4).trim());
											}
										}else if(product instanceof Printer){ //if for Printer specifications
											if(tempLine.trim().startsWith("TYPE")){
												((Printer)product).setType(line.trim().substring(4).trim());
											}else if(tempLine.trim().startsWith("TECHNOLOGY")){
												((Printer)product).setTechnology(line.trim().substring(10).trim());
											}
										}
									}
									if(product!=null){
										product.setModelName(modelName);
										product.setPrice(price);
										productsList.addProduct(product ,productQuantity);
									}
								}
							}
						}
						}
						linesList.clear();
					}
				}
			}
		}catch (IOException ex){
			System.out.println("Error reading line "+count);
		}
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing file.");
        }
	}
	
	public Item get(int i){
		return productsList.getProduct(i);
	}
	
	public int getQuantity(Item product){
		return productsList.getProductQuantity(product);
	}
	
	public int size(){
		return productsList.listSize();
	}
	
	public AvailableProducts getProductsList(){
		return this.productsList;
	}
}