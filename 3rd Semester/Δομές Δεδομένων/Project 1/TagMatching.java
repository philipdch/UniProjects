import java.io.*;
import java.util.*;

public class TagMatching{
	
	private static StringStackImpl<String> tagsStack = new StringStackImpl<String>();
	private static StringTokenizer tokenizer;
	private static final String DELIMS = "<> \\ //"; //characters to be used be StringTokenizer to separate words
	
	public static void main(String[] args){
		String popped = ""; //stores the string return from Stack method pop()
	    int count=0;
		File file=null ;
		BufferedReader reader= null ;
		String line = null;
		
		String token = null;
		/* invalidPair : becomes true if an opening tag does not match with a closing tag */
		boolean invalidPair =false;
		
		/* isClosingTag : becomes true if a <\> tag is found
		   isOpeningTag : becomes true is a < > tag is found
		*/
		boolean isOpeningTag ,isClosingTag;
		isOpeningTag = isClosingTag = false;
		String openTag = ""; 		//stores the last tag which was found
		String closeTag = "";		// stores the tag with which the last item of the stack will be compared to
		try{
		    file=new File(args[0]);
		}catch(NullPointerException e){
		    System.err.println("File not found.");
		}
		try{
		    reader=new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
		    System.err.println("Error opening File!");
		}
		try{
			line = reader.readLine();
			while(line != null && !invalidPair){
				tokenizer = new StringTokenizer(line , DELIMS , true);
				while(tokenizer.hasMoreTokens() && !invalidPair){
					isOpeningTag = isClosingTag =false;
					closeTag ="";					
					token = tokenizer.nextToken();
					if(token.equals("<")){ 			//indicates beginning of tag . Proceed to identify it
						token = tokenizer.nextToken();
						if(DELIMS.indexOf(token) == -1 ){ //if token after "<" isn't a a space or "/" ,then it's an opening tag
							openTag = token; 		// possible opening tag ; 
							isOpeningTag = true;
						}else if(token.equals("/")){ 
							token = tokenizer.nextToken();
							closeTag = token; 		//possible closing tag;
							isClosingTag = true;
						}
						token = tokenizer.nextToken();
						if(token.equals(">")){		//valid tag
							if(isOpeningTag){
								tagsStack.push(openTag); //inserts tag in stack
								openTag = "";
							}else if(isClosingTag){
								try{
									popped = tagsStack.pop();
									invalidPair = !(popped.equals(closeTag)); //if latest tag in stack != latest tag found ,end tag-matching process
								}catch(NoSuchElementException e){ 
									invalidPair = true;
								}
							}
						}
					}
				}
				line = reader.readLine();
			}
			if(invalidPair || !tagsStack.isEmpty()){ // any invalid pair was found or the stack is not empty (meaning a tag doesn't have a pair)
				System.out.println("This HTML file contains no matching tags");
			}else{
				System.out.println("HTML file contains matching tags");
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