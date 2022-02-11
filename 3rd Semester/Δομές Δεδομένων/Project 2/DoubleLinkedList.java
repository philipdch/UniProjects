import java.util.*;
import java.io.*;

public interface DoubleLinkedList<T extends Comparable<T>>{
	
	/* declares the methods for a doubly linked list
	*  NOTE : extends Comparable<T> was added in order to avoid warnings concerning type T 
	*/
	
	/** inserts an element at the front of the list
	* @param element is the item to be inserted
	*/
	public void insertAtFront(T element);
	
	/** inserts an item to the back of the list 
	* @param element the item to be inserted
	*/
	public void insertAtBack(T element);
	
	/** removes an item from the front ,only if list isn't empty
	* or throws NoSuchElementException if list is empty
	* @return the data of the Node which was removed
	*/
	public T removeFromFront() throws NoSuchElementException;
	
	/** removes an item from the back if list isn't empty
	* or throws NoSuchElementException if list is empty
	* @return data of Node removed
	*/
	public T removeFromBack() throws NoSuchElementException;
	
	/** get the data of first Node without removing it
	* @return data of firt Node
	*/
	public T peekFirst() throws NoSuchElementException;
	
	/** return data of last Node without removing it
	* @return data of last Node
	*/
	public T peekLast() throws NoSuchElementException;
	
	/** gets the first Node
	* @return first Node
	*/
	public Node<T> getFirst();
	
	/** gets the last Node
	* @return last Node
	*/
	public Node<T> getLast();
	
	/** returns the total number of items in the list
	* @return numbers of items
	*/
	public int getSize();
	
	/** checks if list is empty of not
	* @returns true if list is empty
	*/
	public boolean isEmpty();
	
	/** prints contents of list from first to last
	*/
	public void print();
}