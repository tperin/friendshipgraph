package structures;

/**
 * Generic node, holds any type of object
 * @author seshv
 *
 */
public class Node<T> {
	public T data;
	public Node<T> next;
	// constructor name does not get the <T>
	public Node(T data, Node<T> next) {
		this.data = data;
		this.next = next;
	}
}

