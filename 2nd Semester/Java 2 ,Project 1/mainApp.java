/*    ONOMA             EXAMHNO FOITHSHS      A.M.
DOURACHALIS FILIPPOS         2o             3170045
KOTTAS IOANNIS               2o             3170220
ZACHAROPOULOU GEORGIA        2o             3170049
*/

import java.util.*;
public class mainApp{
	
	//Options to be used in the menu
	private static final String[] OPTIONS = {"Show Products", "Show Orders","Show Sales" ,"Exit program"};
	private static final String[] PART_OPTIONS = {"CPUs ","GPUs" ,"Motherboards" ,"RAM" ,"Hard drives"};
	private static final String[] PERIPHERAL_OPTIONS = {"Monitors" , "Keyboard" ,"Mouse" ,"Printer"};
	static  Scanner input = new Scanner(System.in);
	
	public static void main(String[] args){
		//Initialise items
		Scanner input = new Scanner(System.in);
		CPU cpu1=new CPU("i5 4200M","2015",CPU.INTEL,119.99,"2.7 Ghz",4,true);
		CPU cpu2=new CPU("ryzen 6700K","2014",CPU.AMD,100,"2.0 Ghz",4,false);
	    GPU gpu1=new GPU("gtx 1080","2016"," NVIDIA",1027.32,GPU.NVIDIA ,"6 GB");
		GPU gpu2 = new GPU("r8 4548mx" ,"2015","AMD",650.50,GPU.AMD ,"4 GB");
		Motherboard mb1 = new Motherboard("MSI Z270" ,"2016" ,"MSI" ,80, CPU.INTEL, "16 GB", 8);
		Motherboard mb2 = new Motherboard("Gigabyte X299" , "2017" ,"Gigabyte" ,225.50, CPU.AMD , "32 GB",10);
		RAM ram1 = new RAM("Vengeance" ,"2015" ,"Crucial" , 45 ,"DDR4" ,4 ,"2400 MHz");
		RAM ram2 = new RAM("Ballistix" ,"2015" ,"Corsair" , 160 ,"DDR5" ,16 ,"1600 MHz");
		HardDrive hd1 = new HardDrive("Blue" ,"2013", "Western Digital" ,100 , HardDrive.HD ,"3.5\"","2 TB");
		HardDrive hd2 = new HardDrive("EVO 860" ,"2016" ,"Samsung" ,96.90 ,HardDrive.SD ,"2.5\"","256 GB");
		ArrayList<String> portList = new ArrayList<>();
		portList.add("HDMI"); portList.add("USB"); portList.add("VGA");
		Monitor monitor1 = new Monitor("BSD322015", " 2014 ", "Sony" ,105, "LED" , "32\"", "1080p" ,portList);
		Monitor monitor2 = new Monitor("PHk9021" , "2013" ,"Philips" ,85, "LCD" ,"27\"", "720p",portList);
		Keyboard key1=new Keyboard("Blackwidow","2017","Razer",200,Keyboard.WIRELESS);
		Keyboard key2=new Keyboard("Logitech","2016","Logitech",150,Keyboard.WIRED);
		Mouse mouse1 = new Mouse("Abyss" ,"2015" ,"Razer" ,90,"laser",Mouse.WIRED);
		Mouse mouse2 = new Mouse("G650" ,"2013" ,"Logitech" ,80,"Optical",Mouse.WIRELESS);
		Printer printer1 = new Printer("HP 4050" ,"2012" ,"HP" ,100,"inkjet","Color");
		Printer printer2 = new Printer("HP 809", "2016" ,"HP" ,150,"laser" ,"RGB");
		
		AvailableProducts cpuList = new AvailableProducts("CPU", cpu1 ,20); //Create CPU list
		cpuList.addProduct(cpu2 ,0);
		AvailableProducts gpuList = new AvailableProducts("GPU", gpu1 ,10); //Create GPU list
		gpuList.addProduct(gpu2 ,25);
		AvailableProducts motherboardList = new AvailableProducts("Motherboard", mb1 ,30); //Create Motherboard list
		motherboardList.addProduct(mb2, 18);
		AvailableProducts ramList = new AvailableProducts("RAM", ram1 , 56); //Create Ram list
		ramList.addProduct(ram2 , 1);
		AvailableProducts hdList = new AvailableProducts("HardDrive", hd1 , 46); //Create HardDrive List
		hdList.addProduct(hd2 , 87);
		AvailableProducts monitorList = new AvailableProducts("Monitor", monitor1 ,16); //Create Monitor list
		monitorList.addProduct(monitor2 , 0);
		AvailableProducts keyboardList = new AvailableProducts("Keyboard", key1 ,28); //Create Keyboard List
		keyboardList.addProduct(key2, 22);
		AvailableProducts mouseList = new AvailableProducts("Mouse", mouse1 , 33); //Create Mouse List
		mouseList.addProduct(mouse2 , 21);
		AvailableProducts printerList = new AvailableProducts("Printer", printer1 , 0); //Create Printer list
		printerList.addProduct(printer2 , 17);
		
		OrdersList orderList = new OrdersList(); //create order list
		SoldList salesList = new SoldList(); //create sales list
		AvailableProducts selectedArray = new AvailableProducts() ; //stores the type of array that is chosen by the user (also used to find selected product's quantity)
		boolean flag = false; 
		String answer; //stores the answer for each options menu
		Item selectedProduct; //stores the type of product chosen by the user
		Order order; //stores an order
		Sale sale;
		String clientName; //stores client's name in an order or sale
		String clientPhone; //stores client's philip in an order or sale
		String date; //stores the sale or order date
		String arDate; //stores the arrival date
		
		while( !flag){
			System.out.println("Choose one of the following options.");
			showMenu(OPTIONS);
			System.out.print("> "); 
			answer = input.nextLine();
			System.out.println("***********");
			if(answer.equals("0")){ //if for category
				System.out.println("Pick a category.");
				System.out.println("0. Parts"+"\n1. Peripherals");
				System.out.print("> ");
				answer = input.nextLine();
				System.out.println("***********");
				if(answer.equals("0")){ //if for parts
					System.out.println("Choose a part.");
					showMenu(PART_OPTIONS);
					System.out.print("> ");
					answer = input.nextLine();
					System.out.println("***********");
					if(answer.equals("0")){ 
						printProduct(cpuList);
						selectedArray = cpuList;
						answer = input.nextLine();
					}else if(answer.equals("1")){
						printProduct(gpuList);
						selectedArray = gpuList;
						answer = input.nextLine();
					}else if(answer.equals("2")){
						printProduct(motherboardList);
						selectedArray = motherboardList;
						answer = input.nextLine();
					}else if(answer.equals("3")){
						printProduct(ramList);
						selectedArray = ramList;
						answer = input.nextLine();
					}else if(answer.equals("4")){
						printProduct(hdList);
						selectedArray = hdList;
						answer = input.nextLine();
					}else{ 
						System.out.println(" Unable to find specified part "); 
						System.out.println("**********");
					}
				}else if(answer.equals("1")){ // if for peripherals
					System.out.println("Choose a Peripheral.");
					showMenu(PERIPHERAL_OPTIONS);
					System.out.println("> ");
					answer = input.nextLine();
					System.out.println("***********");
					if(answer.equals("0")){ //
						printProduct(monitorList);
						selectedArray = monitorList;
						answer = input.nextLine();
					}else if(answer.equals("1")){
						printProduct(keyboardList);
						selectedArray = keyboardList;
						answer = input.nextLine();
					}else if(answer.equals("2")){
						printProduct(mouseList);
						selectedArray = mouseList;
						answer = input.nextLine();
					}else if(answer.equals("3")){
						printProduct(printerList);
						selectedArray = printerList;
						answer = input.nextLine();
					}else{
						System.out.println(" Unable to find specified peripheral ");
						System.out.println("**********");
					}
				}else{
					System.out.println("Unable to find category");
					System.out.println("**********");
				}
				selectedProduct = selectedArray.getProduct(Integer.parseInt(answer));
				if(selectedProduct!=null){ //checks whether a correct selection was made or not 
					System.out.println(selectedProduct);
					if(selectedArray.isAvailable(selectedProduct)){ //checks whether product is available 
						System.out.println("Would you like to buy this product ?");
						System.out.println("1. yes "+"\n2. no");
						System.out.print("> ");
						answer = input.nextLine();
						if(answer.equals("1")){ //proceed to sale product
							System.out.println("Enter the client's name : ");
							clientName=input.nextLine();
							System.out.println("Enter client's phone : ");
							clientPhone = input.nextLine();
							System.out.println("Enter sale date (day / month / year) : ");
							date = input.nextLine();
							System.out.println("");
							sale = new Sale(selectedProduct , clientName ,clientPhone ,date); //create new sale
							salesList.addSale(sale); //add new sale to list
							selectedArray.reduceQuantity(selectedProduct,1); //reduce product quantity by 1
							System.out.println(sale); //review sale
							System.out.println("");
							System.out.println("Purchase completed ,returning to home screen");
							System.out.println("**********");
						}else{
							System.out.println("Returning to home screen");
							System.out.println("**********");
						}
					}else{ //if product is not available ,prompt user to order it
						System.out.println("Would you like to order this product ?");
						System.out.println("1. yes "+"\n2. no");
						System.out.print("> ");
						answer = input.nextLine();
						if(answer.equals("1")){ //proceed to complete order
							System.out.println("Enter the client's name : ");
							clientName=input.nextLine();
							System.out.println("Enter client's phone : ");
							clientPhone = input.nextLine();
							System.out.println("Enter sale date (day / month / year) : ");
							date = input.nextLine();
							System.out.println("Enter arrival date (day / month / year) : ");
							arDate = input.nextLine();
							System.out.println("");
							order = new Order(selectedProduct , clientName , clientPhone ,date , arDate ,Order.PENDING); //create new order object
							orderList.addOrder(order); //add new order to list
							System.out.println(order); //review new order
							System.out.println("");
							System.out.println("Order completed ,returning to home screen");
							System.out.println("**********");
						}else{
							System.out.println("Returning to home screen");
							System.out.println("**********");
						}
					}
				}
			}else if(answer.equals("1")){ //if for option "Show orders"
				if(orderList.isEmpty()){ //checks if the order list is empty or not
					System.out.println("There are no orders in the list");
				}else{
					System.out.println("Pick an order : ");
					orderList.showOrders(); //shows all orders stored in the list
					System.out.print("> ");
					answer = input.nextLine();
					System.out.println("");
					if(Integer.parseInt(answer)>=0 && Integer.parseInt(answer)<orderList.size()){
						order = orderList.getOrder(Integer.parseInt(answer));
						System.out.println(order); //shows selected order
						if(order.getOrderStatus().equals(Order.PENDING)){ //option to change order status only if that doesn't equal "Completed"
							System.out.println("Complete order? ");
							System.out.println("1. yes "+"\n2. no");
							answer = input.nextLine();
							switch(Integer.parseInt(answer)){
								case 1 : order.setStatus(Order.COMPLETED); //changes order status
									sale = new Sale(order); 
									salesList.addSale(sale); //adds order to sales list
									System.out.println("Order status changed to \"Completed\" and added to sales list.");
								default :
									System.out.println("returning to home screen");
							}
						}
					}else{ System.out.println("Cannot find order"); 
					}
				}
				System.out.println("**********");
			}else if(answer.equals("2")){
				if(salesList.isEmpty()){ //checks if the sales list is empty or not
					System.out.println("There are no sales in the list");
				}else{
					System.out.println("Pick a sale : ");
					salesList.showSales(); //shows all sales stored in the list
					System.out.print("> ");
					answer = input.nextLine();
					System.out.println("");
					if(Integer.parseInt(answer)>=0 && Integer.parseInt(answer)<salesList.size()){
						sale = salesList.getSale(Integer.parseInt(answer));
						System.out.println(sale);
					}else{
						System.out.println("Cannot find specified sale");
					}
				}
				System.out.println("**********");
			}else{ System.out.println("Exiting program"); flag=true;}
		}
	}
			
	
	
	private static void showMenu(String[] menuList){ //method to show the menu inside while
		for(int i = 0 ;i<menuList.length ; i++){
			System.out.println(i+ ". "+menuList[i]);
		}
	}
	
	private static void printProduct(AvailableProducts array){ //method to display all products of a specific type ,after it is selected
		System.out.println("Choose a "+array.getProductType()+".");
		array.showProducts();
		System.out.print("> ");
	}
		
		
		
}