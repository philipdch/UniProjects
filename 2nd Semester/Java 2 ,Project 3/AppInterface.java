/* H klash den exei teleiwsei , 
	gia auto to logo trexei apeu8eias apo edw
	anti gia thn mainApp */

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

public class AppInterface extends JFrame implements ActionListener {
	CPU cpu = new CPU("i7 4200M" , "2016" , "INTEL" , 160 , "3.2 GHz" , 4 ,true);
	AvailableProducts cpuList = new AvailableProducts("CPU");
	AvailableProducts gpuList = new AvailableProducts("GPU");
	AvailableProducts mbList = new AvailableProducts("Motherboard");
	AvailableProducts ramList = new AvailableProducts("RAM");
	AvailableProducts hdList = new AvailableProducts("Hard Drive");
	AvailableProducts monitorList = new AvailableProducts("Monitor");
	AvailableProducts keyboardList = new AvailableProducts("Keyboard");
	AvailableProducts printerList = new AvailableProducts("Printer");
	AvailableProducts mouseList = new AvailableProducts("Mouse");
	AvailableProducts productList = new AvailableProducts();
	AvailableProducts selectedArray = new AvailableProducts();
	
	JFrame frame ; 
	JMenu  menu  ;
	JMenuItem fileOpen ;
	JMenuBar menubar ;
	JPanel productsTab ; //tab where product categories appear
	JPanel ordersTab; //tab for orders
	JPanel salesTab ; //tab for sales
	JPanel tabPanel;
	JTabbedPane menuTabs;
	SoldList salesList;
	OrdersList ordersList;
	AvailableProducts productsList ;
	JList list;
	JButton backButton;
	DefaultListModel midList; //updates the JList "productList" according to the changes made in order ,sales and products lists
	Object itemClicked; //monitors which item was clicked in JList
	
	StoreOrder orderRW ;
	StoreSales saleRW ;
	StoreProducts productsR ;
	String salesFile;
	String ordersFile;
	String productsFile;
	Item product;
	int quantity;
	
	public AppInterface(){
		setOrdersFile("Orders.txt");
		setSalesFile("Sales.txt");
		setProductsFile("products.txt");
		drawFrame();
	}
	
	public static void main(String[] args){
		new AppInterface();
	}
	
	public void drawFrame(){
		frame = new JFrame("Menu");
		frame.setBounds( 200 , 200 , 1000 , 550 );
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		menu = new JMenu("File");
		fileOpen = new JMenuItem("Open File");
		fileOpen.addActionListener(this);
		menu.add(fileOpen);
		
		menubar = new JMenuBar();
		menubar.add(menu);
		frame.add(menubar,BorderLayout.PAGE_START);
		
		backButton = new JButton("Back");
		ordersTab = new JPanel();
		salesTab = new JPanel();
		productsTab = new JPanel();
		menuTabs = new JTabbedPane();
		menuTabs.add("Products" , productsTab);
		midList = new DefaultListModel();
		midList.addElement("Parts");
		midList.addElement("Peripherals");
		list = new JList(midList);
		list.setSelectedIndex(0);
		JScrollPane scroller = new JScrollPane(list);
		scroller.setPreferredSize(new Dimension(400 ,400));
		productsTab.add(scroller);
		productsTab.add(backButton);
		backButton.addActionListener(this);
		
		list.addMouseListener(new MouseAdapter() {
		public void mouseClicked(MouseEvent evt) {
			JList list = (JList)evt.getSource();
				if (evt.getClickCount() == 2) { // if for double click
					int index = list.locationToIndex(evt.getPoint());
					itemClicked = list.getModel().getElementAt(index);
					if( ((String)itemClicked).equals("Parts")){
						midList.clear();
						midList.addElement("CPUs");
						midList.addElement("GPUs");
						midList.addElement("Motherboards");
						midList.addElement("Hard Drives");
						midList.addElement("RAMs");
						list.setModel(midList);
					}else if( ((String)itemClicked).equals("Peripherals")){
						midList.clear();
						midList.addElement("Monitors");
						midList.addElement("Keyboards");
						midList.addElement("Mouses");
						midList.addElement("Printers");
						list.setModel(midList);
					}else if(((String)itemClicked).equals("CPUs")){ 
						createProductList(cpuList);
						selectedArray = cpuList;
					}else if(((String)itemClicked).equals("GPUs")){
						createProductList(gpuList);
						selectedArray = gpuList;
					}else if(((String)itemClicked).equals("Motherboards")){
						createProductList(mbList);
						selectedArray = mbList;
					}else if(((String)itemClicked).equals("Hard Drives")){
						createProductList(hdList);
						selectedArray = hdList;
					}else if(((String)itemClicked).equals("RAMs")){
						createProductList(ramList);
						selectedArray = ramList;
					}else if(((String)itemClicked).equals("Monitors")){
						createProductList(monitorList);
						selectedArray = monitorList;
					}else if (((String)itemClicked).equals("Printers")){
						createProductList(printerList);
						selectedArray = printerList;
					}else if(((String)itemClicked).equals("Keyboards")){
						createProductList(keyboardList);
						selectedArray = keyboardList;
					}else if(((String)itemClicked).equals("Mouses")){
						createProductList(mouseList);
						selectedArray = mouseList;
					}else if(selectedArray!= null){
						JFrame itemFrame = new JFrame("Item details");
						itemFrame.setBounds(200,200 ,500,400);
						itemFrame.setLayout(new BorderLayout());
						itemFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
						itemFrame.setVisible(true);
						
					}
				}
			}
		});
		menuTabs.add("Orders" , ordersTab);
		menuTabs.add("Sales" ,salesTab);
		tabPanel = new JPanel();
    	tabPanel.setLayout(new GridLayout(2,1));
    	tabPanel.setPreferredSize(new Dimension(550,550));
    	tabPanel.add(menuTabs);
		frame.add(tabPanel ,BorderLayout.LINE_START);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent event){
		File selectedFile;
		JFileChooser chooser = new JFileChooser();
		int returnValue;
		saleRW = new StoreSales();
		orderRW = new StoreOrder();
		productsR = new StoreProducts();
		
		if( event.getSource() == fileOpen ){ //set action when user clicks on the "Open file" button 
			returnValue = chooser.showOpenDialog(null);
			if(returnValue == JFileChooser.APPROVE_OPTION){
				selectedFile = chooser.getSelectedFile();
				if(selectedFile.getName().equals(salesFile)){ //option in case user selects the "sales" file
					saleRW.loadFile(selectedFile.getName());
					
					midList = new DefaultListModel();
					for (int i =0 ; i<saleRW.size() ; i++)
						midList.addElement(saleRW.get(i));
					list = new JList(midList);
					list.setSelectedIndex(0);
					JScrollPane listScroller = new JScrollPane(list);
					listScroller.setPreferredSize(new Dimension(350, 200));
					salesTab.add(listScroller);
				}else if(selectedFile.getName().equals(ordersFile)){ //option in case user selects the "orders" file
					orderRW.loadFile(selectedFile.getName());
					
					midList.clear();
					for (int i =0 ; i< orderRW.size() ; i++)
						midList.addElement(orderRW.get(i));
					list.setModel(midList);
					list.setSelectedIndex(0);
					JScrollPane listScroller = new JScrollPane(list);
					listScroller.setPreferredSize(new Dimension(350 ,200));
					ordersTab.add(listScroller);
				}else if(selectedFile.getName().equals(productsFile)){ //option in case user selects the "products" file
					productsR.loadFile(selectedFile.getName());
					productList = productsR.getProductsList();
					for(int i =0 ; i<productsR.size() ;i++){ //Place each product in their respective list
						product = productsR.get(i);
						quantity = productsR.getQuantity(product);
						if(product instanceof CPU){
							cpuList.addProduct(product,quantity);
						}else if(product instanceof GPU){
							gpuList.addProduct(product,quantity);
						}else if(product instanceof Motherboard){
							mbList.addProduct(product,quantity);
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
				}
			}
		}else if(event.getSource() == backButton ){
			midList.clear();
			midList.addElement("Parts");
			midList.addElement("Peripherals");
			list.setModel(midList);
		}
	}
	
	public void setProductsFile(String fileName){
		this.productsFile = fileName;
	}
	
	public void setSalesFile(String fileName){
		this.salesFile = fileName;
	}
	
	public void setOrdersFile(String fileName){
		this.ordersFile = fileName;
	}
	
	public void createProductList(AvailableProducts itemList){ // creates the midList which contains every product in the list
		midList.clear();
		if(itemList.isEmpty()){
			midList.addElement("There aren't any available products of this type");
		}else{
			for( int i = 0 ; i<itemList.listSize() ; i++){
				midList.addElement("Model : "+itemList.getProduct(i).getModelName() +"  ,Price : "+itemList.getProduct(i).getPrice()+" ,Pieces : "+itemList.getProductQuantity(itemList.getProduct(i)));
			}
		}
		list.setModel(midList);
	}
}
		
		
		
		
	
