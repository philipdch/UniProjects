public class Node<T>{
	
	private T data;
	private Node<T> nextNode;
	
	Node(T data){
		this(data ,null);
	}

	Node(T data ,Node<T> nextNode){
		this.data = data;
		this.nextNode = nextNode;
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
	
	public void setNext( Node<T> next ){ 
		this.nextNode = next;
	}
}