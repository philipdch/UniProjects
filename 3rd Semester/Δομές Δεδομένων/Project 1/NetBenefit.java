import java.io.*;
import java.util.*;

public class NetBenefit {
    private static IntQueueImpl<Integer> quantityQueue= new IntQueueImpl<Integer>();
	private static IntQueueImpl<Integer> priceQueue= new IntQueueImpl<Integer>();
	private static StringTokenizer tokenizer;
	
	public static void main(String args[]){
		String line ="";
		File file=null ;
		BufferedReader reader=null;
		int count =0;
		
		int buyQuantity = 0; // shares at queue's head
		int soldShares = 0; // shares to be sold
		int sharePrice = 0; // price at which shares will be sold
		int remShares = 0; // shares missing from queue's head after sale (eg when soldShares = 30 and buyQuantity = 80 , remShares = 50)
		int totalSum = 0;
		boolean buying =false;
		String token = null;
		boolean noMoreShares = false; // tried to sell more shares than what the queue has
		
		try{
			file=new File(args[0]);
		}catch(NullPointerException e){
		    System.err.println("File not found.");
		}	
		try{
			reader=new BufferedReader(new FileReader(args[0]));
		}catch(FileNotFoundException e){
			System.err.println("Error ,could not open file");
		}
		try{ 
			line=reader.readLine();
		    while (line !=null){
				tokenizer = new StringTokenizer(line);
				token = tokenizer.nextToken();
				while (tokenizer.hasMoreTokens()){
					buying = false;
					if (token.equalsIgnoreCase("buy")){ // buying shares 
						token =tokenizer.nextToken();
						quantityQueue.put(Integer.parseInt(token));	 // insert number of shares bought in quantityQueue
						buying = true;
				    }else if(token.equalsIgnoreCase("sell")){ //selling shares
                        token =tokenizer.nextToken();
						soldShares = Integer.parseInt(token);  		// number of shares to sell
					}
					token = tokenizer.nextToken();
					if(buying){ 
						
						token = tokenizer.nextToken();						
						priceQueue.put(Integer.parseInt(token)); // insert buying price to priceQueue
					}else{
						sharePrice = Integer.parseInt(tokenizer.nextToken()); //gets the price of the shares to be sold
						while(soldShares > 0){
							try{
								buyQuantity = quantityQueue.peek();
								if(soldShares >= (buyQuantity - remShares)){ // # of shares in head Node <= # of shares to sell
									soldShares -= buyQuantity - remShares;
									totalSum += (quantityQueue.get() - remShares) * (sharePrice - priceQueue.get()); // benefit = SharesBoughtFirst *( selling price - share price) . Contents of queue's head must be removed
									remShares = 0; //current Node removed . Next head Node is full
								}else{	// there are still more items in head Node
									totalSum += soldShares * (sharePrice - priceQueue.peek()); 
									remShares = soldShares; // shares missing from current head (since shares in head > shares sold)
									soldShares = 0; // no more shares to sell
								}
							}catch(NoSuchElementException e){
								noMoreShares = true;
								totalSum = 0;
								break;
							}
						}
					}
				}
				line = reader.readLine();
			}
			if(noMoreShares){
				System.out.println("There aren't enough shares to sell ,cannot calculate net benefit");
			}else{
				System.out.println( ((totalSum >=0 ) ? "Total profit : " : "Total loss : ")+ totalSum);
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
}