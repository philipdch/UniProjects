import java.io.*;
import java.util.*;
import java.util.regex.*;
/* note : all auxiliary (private) methods are written at the end of this class' body after all the public methods
*		  these methods appear in the same order as the corresponding public methods which call them
*/
public class ST{
	private class TreeNode{
	
		protected WordFreq item;
		TreeNode left;
		TreeNode right;
		TreeNode parent;
		int subtreeNodes;
		private Comparator  cmp;
	
		public TreeNode( WordFreq item){
			this(item , new WordComparator());
		}
		
		public TreeNode(WordFreq item ,Comparator cmp){
			this.item = item;
			this.cmp = cmp;
		}
		
		public TreeNode getParent(){
			return parent;
		}
		
		public int compare(TreeNode node){
			return cmp.compare(this.item ,node.item);
		}
		
		public void increment(){
			TreeNode current = this.parent;
			while(current != null){
				current.subtreeNodes += 1;
				current = current.parent;
			}
		}
		
		public void decrement(){
			TreeNode current = this.parent;
			while(current != null){
				current.subtreeNodes -= 1;
				current = current.parent;
			}
		}
		
		public String toString(){
			return "\n"+item +" has "+subtreeNodes+" nodes"+"*** LEFT node : "+left+"*** RIGHT node : "+right;
		}
	}
	
	private TreeNode head;
	private DoubleLinkedListImpl<String> stopwords;
	private int distinctWords;
	
	public ST(){
		stopwords = new DoubleLinkedListImpl<String>();
		addStopWord("."); addStopWord(","); addStopWord("("); addStopWord(")"); 
		addStopWord("\\"); addStopWord("?"); addStopWord("!"); addStopWord("-"); addStopWord(":");
		addStopWord("\"");
	}
	
	/** inserts a node as a leaf ,starting from TreeNode head 
	*  @param item is the word to insert
	*/
	public void insert(WordFreq item){
		if(item == null)
			throw new IllegalStateException();
		this.head = insert(this.head , null , new TreeNode(item));
		distinctWords++;
	}
	
	/** searches for a word in the tree and increments its frequency by 1 if found
	*  @param word the string for which to search 
	*/
	public void update(String word){
		TreeNode node = null;
		node = find(word);
		if(node != null)
			node.item.increment();
		else
			insert(new WordFreq(word));
	}
	
	/**  searches for a specific word in the tree and sets it as head if it is found 
	*	@param word is the word to look for in the tree
	*	@returns the word or null if it doesn't exist
	*/
	public WordFreq search(String word){
		word = word.toLowerCase();
		TreeNode node = find(word);
		WordFreq key = null;
		if(node != null){
			key = node.item;
			if(node.item.getFrequency() > getMeanFrequency()){
				remove(node);
				head = insertR(head, null,node);
			}
		}
		return key;
	}
	
	/** removes a word from the tree
	*  @param word is the word to be removed
	*/
	public void remove(String word){
		word = word.toLowerCase();
		TreeNode removed = find(word);
		remove(removed);
	}
	
	public void load(String filename){
		String delims = createString(stopwords);
		String line ="";
		String token;
		int lineCount = 0;
		File file = null;
		BufferedReader reader = null;
		StringTokenizer tokenizer = null;
		try{
			file = new File(filename);
		}catch(NullPointerException e){
			System.out.println("File not found");
		}try{
			reader = new BufferedReader(new FileReader(file));
		}catch(FileNotFoundException e){
			System.out.println("Error opening file");
		}try{
			line = reader.readLine();
			while(line != null){
				tokenizer = new StringTokenizer(line , delims);
				token = tokenizer.nextToken();
				while(tokenizer.hasMoreTokens()){
					if(!hasDigits(token))
						update(token);
					token = tokenizer.nextToken();
				}
				line = reader.readLine();
				lineCount++;
			}
		}catch(IOException e){
			System.err.println("Error reading line "+lineCount);
		}
	}
	/** returns the frequency sum of all the words in the tree
	*  @returns total number of words read
	*/
	public int getTotalWords(){
		return count(head);
	}
	
	/**	returns the number of distinct words in the tree
	*  @returns distinct words 
	*/
	public int getDistictWords(){
		return distinctWords;
	}

	/** returns the frequency of the word given
	*  @param word is the word to look for
	*  @returns frequency of specific word or 0 if this word is not found
	*/
	public int getFrequency(String word){
		TreeNode node = null;
		node = find(word);
		if(node != null)
			return node.item.getFrequency();
		return 0;
	}
	
	/** calculates the average appearance of each word in the text 
	*  @returns the average 
	*/
	public double getMeanFrequency(){
		int totalWords = getTotalWords();
		if(distinctWords != 0){
			return (double)totalWords / distinctWords;
		}
		return 0;
	}
	
	public WordFreq getMaxFrequency(){
		ST newTree = new ST();
		newTree = copyTree(newTree ,new IntComparator() ,head);
		TreeNode current = newTree.head;
		WordFreq max = current.item;
		while(current != null){
			max = current.item;
			current = current.right;
		}
		return max;
	}
	
	/** prints each word of the tree in alphabetical order
	*  @param stream is the stream from which to output the result
	*/
	public void printAlphabetically(PrintStream stream){
		printInorder(head, stream);
	}
	
	/** adds a stop word to the list 
	*  @param word is the word to add
	*/
	public void addStopWord(String word){
		if(!stopwords.contains(word))
			stopwords.insertAtBack(word);
	}
	
	/** creates a new tree in which words are stored based on frequency and prints it
	*  @param stream is the output stream to use 
	*/
	public void printByFrequency(PrintStream stream){
		ST frequencyTree = new ST();
		frequencyTree = copyTree(frequencyTree ,new IntComparator(), head);
		frequencyTree.printRtoL(frequencyTree.head ,stream);
	}		
	
	/** removes a stop word from the list
	*  @param word ,the word to remove 
	*/
	public void removeStopWord(String word){
		stopwords.remove(word);
		
	}
	
	/** returns the tree's head node */
	public TreeNode getHead(){
		return head;
	}
	
	/** inserts a node as a leaf ,starting from a specific node 
	* @param node is the node from which to starting
	* @param parent is the node to set as parent when a new node is created
	* @param item is the word to be inserted
	*/
	private TreeNode insert(TreeNode node ,TreeNode parent ,TreeNode newNode ){
		if(newNode == null)
			throw new IllegalStateException();
		if(node == null){
			node = newNode;
			node.parent = parent;
			node.increment();
		}else{
			int compareResult = newNode.compare(node); 
			if(compareResult >= 1){
				node.right = insert(node.right , node , newNode);
			}else if(compareResult <= -1){
				node.left = insert(node.left , node ,newNode);
			}
		}
		return node;
	}
	
	private TreeNode insertR(TreeNode node, TreeNode parent ,TreeNode newNode){	
        if (node == null){
            node = newNode;
			node.parent = parent;
			node.increment();
        }
        int compareResult = newNode.compare(node);        
        if (compareResult >= 1){
            node.right = insertR(node.right, node, newNode);
            node = rotateLeft(node);
        }else if(compareResult <=-1){
			node.left = insertR(node.left, node, newNode);
            node = rotateRight(node);
        }
        return node;
    }
	
	/** removes a node from the tree
	*  @param node is the TreeNode to remove 
	*/
	private void remove(TreeNode node) {
        // If node given has two children find its successor, then remove it
        if (node.left != null && node.right != null) {
            TreeNode successor = successor(node);
            node.item = successor.item;
            node = successor;
        }
        TreeNode parent = node.parent;
        TreeNode child = node.left != null ? node.left : node.right;
        // The root is being removed
        if (parent == null){
            head = child;
        }else if (node == parent.left) {
            parent.left = child;
        }else {
            parent.right = child;
        }
        if(child != null){
            child.parent = parent;
        }
		node.decrement(); // reduce the number of subtree nodes from its ancestors
    } 
	
	//prints the tree using in-order traversal
	private void printInorder(TreeNode node ,PrintStream stream){
		if(node == null){
			return;
		}else{
			printInorder(node.left ,stream);
			stream.println(node.item);
			printInorder(node.right, stream);
		}
	}
	
	//prints the tree from right to left (in descending order)
	private void printRtoL(TreeNode node,PrintStream stream){
		if(node == null){
			return;
		}else{
			printRtoL(node.right ,stream);
			stream.println(node.item);
			printRtoL(node.left, stream);
		}
	}
	
	/** searches for a specific word in the tree and returns the node containing it
	*  @param word is the word to look for
	*  @returns the node holding the word
	*/
	private TreeNode find(String word){
		return find(head, word);
	}
	
	/** given a word finds the node containing it
	*  @param word is the specific word to find
	*  @returns the node holding the word or null if it doesn't exist
	*/
	private TreeNode find(TreeNode node ,String word){
		TreeNode result;
		if(node == null)
			return null;
		int compareResult = word.compareToIgnoreCase(node.item.key());
		if(compareResult == 0)
			return node;
		if(compareResult >= 1)
			result = find(node.right ,word);
		else
			result = find(node.left , word);
		return result;
	}
	
	/** returns the frequency sum of all the words in a subtree
	*  @param current is the node to 
	*  @returns total number of words starting from a node
	*/
	private int count(TreeNode current){
		if(current == null)
			return 0;
		else 
			return current.item.getFrequency() + count(current.left) + count(current.right);
	}
	
	/** given a comparator copies the contents of the tree in a new tree and returns it
	*  @param tree is the target tree 
	*  @param cmp is the comparator to be used to create the new tree
	*  @param node is the node to copy
	*  @returns the new tree
	*/
	private ST copyTree(ST tree ,Comparator cmp, TreeNode node){
		if(node == null)
			return tree;
		TreeNode temp = new TreeNode(node.item , cmp);
		tree.head = insert(tree.head ,null ,temp);
		tree = copyTree(tree ,cmp ,node.left);
		tree = copyTree(tree ,cmp ,node.right);
		return tree;
	}
	
	/** finds the successor of a given node
	*  the successor is the leftmost leaf of right subtree
	*  @param node is the node whose successor to find
	*  @returns the node's successor
	*/
	private TreeNode successor(TreeNode node) {
        if (node.right != null) {
            TreeNode sc = node.right;
            while (sc.left != null) 
				sc = sc.left;
            return sc;
        }
        else{
            TreeNode ancestor = node.parent;
            TreeNode child = node;
            while (ancestor != null && child == ancestor.right) {
                child = ancestor;
                ancestor = ancestor.parent;
            }
            return ancestor;
        }
    }
	
	private TreeNode rotateLeft(TreeNode pivot) {
        TreeNode rotated = pivot.right; //node to be rotated along with pivot
		if(rotated == null) return pivot;
		pivot.right = rotated.left;  	//set pivot's right subtree as rotated node's left subtree
		if(rotated.left != null)		
			rotated.left.parent = pivot; //if subtree exists sets it's head's parent as pivot 
		if(pivot.parent == null)
			head = rotated;
		else if(pivot.parent.right == pivot)   //make proper changes to pivot's parent node to point to rotated node
			pivot.parent.right = rotated;
		else 
			pivot.parent.left = rotated;
		rotated.parent = pivot.parent;	
		pivot.parent = rotated;		
		rotated.left = pivot;			//finally set rotated node's left child to point to pivot 
		if(pivot.left != null && pivot.right != null)
			pivot.subtreeNodes = pivot.left.subtreeNodes + pivot.right.subtreeNodes +2;  //update pivot node subtree node count
		else if(pivot.left == null && pivot.right != null)
			pivot.subtreeNodes = pivot.right.subtreeNodes +1;
		else if(pivot.left != null && pivot.right == null)
			pivot.subtreeNodes = pivot.left.subtreeNodes +1;
		if(rotated.right != null)
			rotated.subtreeNodes = rotated.right.subtreeNodes +1 +rotated.left.subtreeNodes +1; //update rotated node's subtree node count
		else 
			rotated.subtreeNodes = rotated.left.subtreeNodes +1;
		return rotated;					//return the node that is now the head of the rotated subtree
	}
	
	private TreeNode rotateRight(TreeNode pivot){
		TreeNode rotated = pivot.left; 
		if(rotated == null) return pivot;
		pivot.left = rotated.right;  	//set pivot's left subtree as rotated node's right subtree
		if(rotated.right != null)		
			rotated.right.parent = pivot; //if subtree exists sets it's head's parent as pivot 
		if(pivot.parent == null)
			head = rotated;
		else if(pivot.parent.right == pivot)   //make proper changes to pivot's parent node to point to rotated node
			pivot.parent.right = rotated;
		else 
			pivot.parent.left = rotated;
		rotated.parent = pivot.parent;	
		pivot.parent = rotated;		
		rotated.right = pivot;		//finally set rotated node's right child to point to pivot 
		if(pivot.left != null && pivot.right != null)
			pivot.subtreeNodes = pivot.left.subtreeNodes + pivot.right.subtreeNodes +2;  //update pivot node subtree node count
		else if(pivot.left == null && pivot.right != null)
			pivot.subtreeNodes = pivot.right.subtreeNodes +1;
		else if(pivot.left != null && pivot.right == null)
			pivot.subtreeNodes = pivot.left.subtreeNodes +1;
		if(rotated.left != null)
			rotated.subtreeNodes = rotated.right.subtreeNodes +1 +rotated.left.subtreeNodes +1; //update rotated node's subtree node count
		else 
			rotated.subtreeNodes = rotated.right.subtreeNodes +1;
		return rotated;					//return the node that is now the head of the rotated subtree
	}
	
	private String createString(DoubleLinkedListImpl list){
		String s = " ";
		Node current = list.getFirst();
		for(int i = 0 ; i< list.getSize() ; i++){
			s+= current.getData() +" ";
			current = current.getNext();
		}
		return s;
	}
	
	private boolean hasDigits(String token){
		char c ;
		for(int i =0 ; i<token.length() ;i++){
			c = token.charAt(i);
			if(Character.isDigit(c))
				return true;
		}
		return false;
	}
}		