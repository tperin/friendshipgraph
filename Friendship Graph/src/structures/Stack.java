package structures;

import java.util.NoSuchElementException;

public class Stack<T> {

	Node<T> front;
	int size;
	
	public Stack() {
		// TO DO
		front = null;
		size = 0;
	}
	
	public void push(T item) {
		// TO DO
		// add to front (front is top)
		front = new Node<T>(item, front);
		size++;
	}
	
	public T pop() 
	throws NoSuchElementException {
		// TO DO
		// front is top, so pop deletes the front
		if (size == 0) {
			throw new NoSuchElementException();
		}
		T hold = front.data;
		front = front.next;
		size--;
		return hold;
	}
	
	public boolean isEmpty() {
		// TO DO
		return size == 0;
	}
	
	public int size() {
		// TO DO
		return size;
	}
	
	public void clear() {
		// TO DO
		front = null;
		size = 0;
	}
	
}
