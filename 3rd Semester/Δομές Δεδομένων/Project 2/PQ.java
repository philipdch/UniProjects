import java.util.*;

public class PQ<T> {
    /**
     * Array based heap representation
     */
    private T[] heap;
    /**
     * The number of objects in the heap
     */
    private int size;
	private T minElement;
    protected Comparator<T> cmp;
    
    
    /**
     * Creates heap with a given capacity and comparator.
     * @param capacity The capacity of the heap being created.
     * @param cmp The comparator that will be used.
     */
    public PQ(int capacity, Comparator<T> cmp) {
        if (capacity < 1) throw new IllegalArgumentException();
        
        this.heap = (T[])new Object[capacity+1];
        this.size = 0;
        this.cmp = cmp;
    }
	
    /**
     * Inserts an object in this heap.
     * @throws IllegalArgumentException if element is null.
     * @param element is thhe object to be inserted.
     */
    public void insert(T element) {
		if(minElement == null)
			minElement = element;
		else
			minElement = (cmp.compare(minElement , element) == 1) ?element : minElement;
        if (element == null) 
			throw new IllegalArgumentException();
        if (size >= (heap.length-1)*0.75)
			resize();
        heap[++size] = element;
        swim(size);
    }
	
	/** returns object at root without removing it
	*   @throws IllegalStateException if heap is empty
	*   @return max object (object at root)
	*/
	public T max(){
		if(isEmpty())
			throw new IllegalStateException();
		return heap[1];
	}
	
    /**
     * Removes the object at the root of this heap.
     * @throws IllegalStateException if heap is empty.
     * @return The object removed.
     */
    public T getMax() {
        // Ensure not empty
        if (size == 0) 
			throw new IllegalStateException();
        T object = heap[1];
        // Replace root object with the one at rightmost leaf
        if (size > 1) 
			heap[1] = heap[size];
        // Dispose the rightmost leaf
        heap[size--] = null;
        sink(1);
        return object;
    }
	
	/** Removes object with this ID 
	*	@throws NoSuchElementException if such element doesn't exist or heap is empty
	*	@return object associated with this id
	*/
	public T remove(int ID) throws NoSuchElementException ,IllegalStateException{
		if(isEmpty())
			throw new IllegalStateException();
		int index = 0;
		for(int i = 1 ; i<=size ;i++){
			if(((Song)heap[i]).getID() == ID){
				index = i;
				break;
			}
		}
		if (index == 0 )
			throw new NoSuchElementException();
		T removed = heap[index];
		heap[index] = heap[size];
		heap[size--] = null;
		if(cmp.compare(removed, heap[index]) == -1){
			swim(index);
		}else{
			sink(index);
		}
		// in case min element was removed ,find new minimum
		this.minElement = heap[size];
		if(removed == minElement)
			findMin(); 
		return removed;
	}
		
    /**
     * Shift up.
     */
    private void swim(int i) {
        while (i > 1) {  //if i root (i==1) return
            int p = i/2;  //find parent
            int result = cmp.compare(heap[i], heap[p]);  //compare parent with child
            if (result <= 0) 
				return;    //if child <= parent return
            swap(i, p);                 //else swap and i=p
            i = p;
        }
    }
    /**
     * Shift down.
     */
    private void sink(int i){
        int left = 2*i;
		int right = left+1;
		int max = left;
        // If 2*i >= size, node i is a leaf
        while (left <= size) {
            // Determine the largest children of node i
            if (right <= size) {
                max = cmp.compare(heap[left], heap[right]) < 0 ? right : left;
            }
            // If the heap condition holds, stop. Else swap and go on.
            if (cmp.compare(heap[i], heap[max]) >= 0) return;
            swap(i, max);
            i = max; left = 2*i; right = left+1; max = left;
        }
    }
	
	/** finds and sets the minimum element in heap . Since the leaves are smaller than their parent ,only them are checked
	*/
	private void findMin(){
		this.minElement = (this.minElement == null)? max() : minElement; 
		int leafNumber = (int)(Math.log10((double)size)/Math.log10(2)) +1; //gets the position of the leaves 
		for(int i =1 ; i<= (this.size - leafNumber) ; i++){ 
			if(cmp.compare( heap[i + leafNumber] ,minElement) == -1) //compares minimum element with each leaf
				this.minElement = heap[i+ leafNumber];  //sets new min 
		}
	}
	
	/** returns the minimum element in the queue
	*	@return minElement
	*/
	public T getMin(){
		return minElement;
	}
    
    /**
     * Interchanges two array elements.
     */
    private void swap(int i, int j) {
        T tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }
	
    public void print() {
        for (int i=1; i<=size; i++){
            System.out.print(i+" "+heap[i]+ " ");
        }
        System.out.println();
    }
	
	private void resize(){
		T[] newHeap = (T[])new Object[this.heap.length * 2];
		for(int i = 1 ; i<= this.size ; i++){
			newHeap[i] = this.heap[i];
		}
		this.heap = newHeap;
	}
	
	public int size(){
		return this.size;
	}
	
    boolean isEmpty(){
        if(this.size == 0)
			return true;
		return false;
    } 
}
