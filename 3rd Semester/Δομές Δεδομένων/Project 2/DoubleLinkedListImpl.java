import java.util.*;
import java.io.*;

public class DoubleLinkedListImpl<T extends Comparable<T>> implements DoubleLinkedList<T>   {
	
	private Node<T> first;
	private Node<T> last;
	private int count;
	
	public DoubleLinkedListImpl(){}
	
	public DoubleLinkedListImpl(Node<T> firstNode){
		this.first = this.last = firstNode;
		count++;
	}
	
	public void insertAtFront(T element){
		if(isEmpty()){
			first = last = new Node<T>(element);
		}else{
			Node<T> newNode = new Node<T>(element, first);
			first.setPrevious(newNode);
			first = newNode;
		}
		count++;
	}
	
	public void insertAtBack(T element){
		if(isEmpty()){
			first = last = new Node<T>(element);
		}else{
			Node<T> newNode = new Node<T>(element,null,last);
			last.setNext(newNode);
			last = newNode;
		}
		count++;
	}
	
	public T removeFromFront() throws NoSuchElementException{
		T removedItem;
		if(isEmpty()){
			throw new NoSuchElementException();
		}else if(first == last){
			removedItem = first.getData();
			first = last = null;
		}else{
			removedItem = first.getData();
			first = first.getNext();
			first.setPrevious(null);
		}
		count--;
		return removedItem;
	}
	
	public T removeFromBack() throws NoSuchElementException{
		T removedItem;
		if(isEmpty()){
			throw new NoSuchElementException();
		}else if(first == last){
			removedItem = first.getData();
			first = last = null;
		}else{
			removedItem = last.getData();
			last = last.getPrevious();
			last.setNext(null);
		}
		count--;
		return removedItem;
	}	
	
	public T peekFirst() throws NoSuchElementException{
		if(isEmpty()){
			throw new NoSuchElementException();
		}
		T removed = first.getData();
		return removed;
	}
	
	public T peekLast() throws NoSuchElementException{
		if(isEmpty())
			throw new NoSuchElementException();
		T removed = last.getData();
		return removed;
	}
	
	public Node<T> getFirst(){
		return first;
	}
	
	public Node<T> getLast(){
		return last;
	}
	
	public int getSize(){
		return count;
	}
	
	public boolean isEmpty(){
		return (first == null && last == null);
	}
	
	public void print(){
		Node<T> current = getFirst();
		for(int i =0 ; i<count ; i++){
			System.out.println(current.getData());
			current = current.getNext();
		}
	}
}