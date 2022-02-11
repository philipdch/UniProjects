/*    ONOMA             EXAMHNO FOITHSHS      A.M.
DOURACHALIS FILIPPOS         2o             3170045
KOTTAS IOANNIS               2o             3170220
ZACHAROPOULOU GEORGIA        2o             3170049
*/

import java.util.*;
public class mainApp{
	
	//Options to be used in the menu
	private static final String[] OPTIONS = {"Show Products", "Show Orders","Show Sales" ,"Update Orders file","Update Sales file","Exit program"};
	private static final String[] PART_OPTIONS = {"CPUs ","GPUs" ,"Motherboards" ,"RAM" ,"Hard drives"};
	private static final String[] PERIPHERAL_OPTIONS = {"Monitors" , "Keyboard" ,"Mouse" ,"Printer"};
	private static final String productsFile = "products.txt";
	private static final String salesFile = "Sales.txt";
	private static final String ordersFile = "Orders.txt";
	static  Scanner input = new Scanner(System.in);
	
	public static void main(String[] args){
		//Initialise product lists
		AvailableProducts cpuList = new AvailableProducts("CPU");
		AvailableProducts gpuList = new AvailableProducts("GPU");
		AvailableProducts motherboardList = new AvailableProducts("Motherboard");
		AvailableProducts ramList = new AvailableProducts("RAM");
		AvailableProducts hdList = new AvailableProducts("Hard Drive");
		AvailableProducts monitorList = new AvailableProducts("Monitor");
		AvailableProducts keyboardList = new AvailableProducts("Keyboard");
		AvailableProducts printerList = new AvailableProducts("Printer");
		AvailableProducts mouseList = new AvailableProducts("Mouse");
		AvailableProducts productsList = new AvailableProducts(); // List which contains all products in products.txt file
		OrdersList orderList = new OrdersList(); //List which contains every order in order file
		SoldList salesList = new SoldList(); //List which contains every sale in sales.txt file
		StoreProducts storeProducts = new StoreProducts(); // read/write object for Products
		storeProducts.loadFile(productsFile); //Read products file
		StoreSales storeSales = new StoreSales(); // read/write object for sales
		productsList = storeProducts.getProductsList() ; //gets list of all products stored in file
		storeSales.addList(productsList);
		storeSales.loadFile(salesFile);
		salesList = storeSales.getSalesList(); //gets the list of all sales stored in file
		StoreOrder storeOrder = new StoreOrder();
		storeOrder.addList(productsList);
		storeOrder.loadFile(ordersFile);
		orderList = storeOrder.getOrderList(); //gets list of all orders stored in file
		Item product;
		int quantity;
		
		for(int i =0 ; i<storeProducts.size() ;i++){ //Place each product in their respective list
			product = storeProducts.get(i);
			quantity = storeProducts.getQuantity(product);
			if(product instanceof CPU){
				cpuList.addProduct(product,quantity);
			}else if(product instanceof GPU){
				gpuList.addProduct(product,quantity);
			}else if(product instanceof Motherboard){
				motherboardList.addProduct(product,quantity);
			}else if (product instanceof RAM){
				ramList.addProduct(product,quantity);
			}else if(product instanceof HardDrive){
				hdList.addProduct(product,quantity);
			}else if(product instanceof Monitor){
				monitorList.addProduct(product,quantity);
			}else if(product instanceof Keyboard){
				keyboardList.addProduct(product,quantity);
			}else if(product instanceof Printer){
				printerList.addProduct(product,quantity);
			}else if(product instanceof Mouse){
				mouseList.addProduct(product,quantity);
			}
		}
	
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
			}else if(answer.equals("3")){
				System.out.println("Updating "+ordersFile);
				storeOrder.writeFile(ordersFile);
				System.out.println("**********");
			}else if(answer.equals("4")){
				System.out.println("Updating "+salesFile);
				storeSales.writeFile(salesFile);
				System.out.println("**********");
			}else{ 
				System.out.println("Updating sale and order files");
				storeOrder.writeFile(ordersFile);
				storeSales.writeFile(salesFile);
				System.out.println("Exiting program"); flag=true;
			}
		}
	}
			
	
	
	private static void showMenu(String[] menuList){ //method to show the menu inside while
		for(int i = 0 ;i<menuList.length ; i++){
			System.out.println(i+ ". "+menuList[i]);
		}
	}
	
	private static void printProduct(AvailableProducts array){ //method to display all products of a specific type ,after it is selected
		if(!(array.isEmpty())){
			System.out.println("Choose a "+array.getProductType()+".");
			array.showProducts();
			System.out.print("> ");
		}else {
			System.out.println(array.getProductType()+" list does not contain any products.");
			System.out.print("Press 0 to return \n>");
		}
	}
		
		
		
}