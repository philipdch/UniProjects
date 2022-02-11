import java.util.*; 
import java.io.*;
public class StringStackImpl<T> implements StringStack<T>{
	
	private Node<T> head;
	private int size = 0;
	
	StringStackImpl(){  //creates an empty stack
		this(null);
	}
	
	StringStackImpl(Node<T> firstNode){  //creates a stack with one item
		head = firstNode;
		size++;
	}
	
	public boolean isEmpty(){
		return (head == null ? true : false);
	}
	
	public void push(T item){
		if(isEmpty()){
			head = new Node<T>(item);
		}else{
			head = new Node<T>(item ,head);
		}
		size++;
	}
	
	public T pop() throws NoSuchElementException {
		T removedItem ;
		if(isEmpty()){
			throw new NoSuchElementException();
		}else if(head.getNext() == null){
			removedItem = head.getData();
			head = null;
		}else{
			removedItem = head.getData();
			head = head.getNext();
		}
		size--;
		return removedItem;
	}
	
	public T peek() throws NoSuchElementException{
		if(isEmpty())
			throw new NoSuchElementException();
		return head.getData();
	}
	
	public void printStack(PrintStream stream){
		Node<T> current = head;
		if(isEmpty()){
			stream.println("Stack has no items");
		}else{
			stream.println("Stack contains the following items :");
			while(current != null){
				stream.println(current.getData());
				current = current.getNext();
			}
		}
	}
	
	public int size(){
		return size;
	}
}