// added a Node<T> previousNode field (compared to the first project) in order to use in Doubly Linked Lists
// T now extends Comparable<T> to avoid warnings in class Top_k
public class Node<T extends Comparable<T>>{
	
	private T data;
	private Node<T> nextNode;
	private Node<T> previousNode;
	
	Node(T data){
		this(data ,null ,null);
	}

	Node(T data ,Node<T> nextNode){
		this(data , nextNode ,null);
	}
	
	Node(T data , Node<T> next ,Node<T> previous){
		this.data = data;
		this.previousNode = previous;
		this.nextNode = next;
	}
	
	public int compareTo(Node<T> node2){
		return this.data.compareTo(node2.getData());
	}
	
	public T getData(){
		return this.data;
	}
	
	public void setData( T newData ){
		this.data = newData;
	}
	
	public Node<T> getNext(){
		return this.nextNode;
	}
	
	public Node<T> getPrevious(){
		return this.previousNode;
	}
	
	public Node<T> setPrevious(Node<T> previous){
		return this.previousNode = previous;
	}
	
	public void setNext( Node<T> next ){ 
		this.nextNode = next;
	}
	
	public void swap(Node<T> nodeA){
		T temp = this.getData();
		this.setData(nodeA.getData());
		nodeA.setData(temp);
	}
}