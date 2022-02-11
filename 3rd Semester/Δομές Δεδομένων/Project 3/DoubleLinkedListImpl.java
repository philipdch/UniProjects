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

	public void remove(T item) throws NoSuchElementException{
		Node<T> currentA = first.getNext();
		Node<T> currentB = last.getPrevious();
		if(isEmpty())
			throw new NoSuchElementException();
		if(first.getData() == item){
			removeFromFront();
			count--;
			return;
		}else if(last.getData() == item){
			removeFromBack();
			count--;
			return;
		}
		for(int i =0 ; i< (count-2)/2 ;i++){
			if(currentA.getData() == item){
				currentA.getPrevious().setNext(currentA.getNext());
				currentA.getNext().setPrevious(currentA.getPrevious());
				currentA = null;
				count--;
				return;
			}else if(currentB.getData() ==item){
				currentB.getNext().setPrevious(currentB.getPrevious());
				currentB.getPrevious().setNext(currentB.getNext());
				currentB = null;
				count--;
				return;
			}
			currentA = currentA.getNext();
			currentB = currentB.getPrevious();
		}
		throw new NoSuchElementException();
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
	
	public boolean contains(T element){
		if(isEmpty()) 
			return false;
		Node<T> left = getFirst();
		Node<T> right = getLast();
		if(first == last && first.getData() == element)
			return true;
		else if(first == last && first.getData() != element)
			return false;
		for(int i =0 ; i<= (count-2)/2 ;i++){
			if(left.getData() == element || right.getData() == element)
				return true;
			left = left.getNext();
			right = right.getPrevious();
		}
		return false;
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