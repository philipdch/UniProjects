import java.util.*;
import java.io.*;

public class IntQueueImpl<T> implements IntQueue<T> {
	
	private Node<T> head; //oldest/first item in the queue
	private Node<T> tail; //newest/last item in queue
	private int count = 0;
	
	IntQueueImpl(){ //creates an empty queue
		head = tail = null;
	}
	
	IntQueueImpl(Node<T> firstNode){  //creates a queue with one item
		head = tail = firstNode;
		count++;
	}
	
	public boolean isEmpty(){
		return (head == null && tail == null)?true : false;
	}
	
	public void put(T item){
		Node<T> newNode = new Node<T>(item,null);
		if(head == null){
			head =tail = newNode;
		}else{
			tail.setNext(newNode);
			tail = newNode;
		}
		count++;
	}
	
	public T get() throws NoSuchElementException{
		T removedItem;
		if( isEmpty()){
			throw new NoSuchElementException();
		}else if(head.getNext() == null){
			removedItem = head.getData();
			head = tail =null;
		}else{
			removedItem = head.getData();
			head = head.getNext();
		}
		count--;
		return removedItem;
	}
	
	public T peek() throws NoSuchElementException{
		if(isEmpty())
			throw new NoSuchElementException();
		return head.getData();
	}
	
	public void printQueue(PrintStream stream){
		Node<T> currentNode = head;
		if(currentNode == null){
			stream.println("Queue contains no items");
		}else{
			stream.println("Queue contains the following items :");
			while(currentNode != null){
				stream.println(currentNode.getData());
				currentNode = currentNode.getNext();
			}
		}
	}
	
	public int size(){
		return count;
	}
}