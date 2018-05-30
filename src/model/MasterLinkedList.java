package model;


public class MasterLinkedList {
	private MasterLink first;

	public MasterLinkedList() {

	}

	public MasterLink getFirst() {
		return first;
	}

	public void setFirst(MasterLink first) {
		this.first = first;
	}

	public ListIterator getIterator() {
		return new ListIterator(this);
	}
	
	public MasterLink find(String key) { // assuming list is not empty
		MasterLink current = first;
		while(!current.keyWord.equals(key)) {
			if(current.next == null) {
				return null; // key does not exist
			} else {
				current = current.next;
			}
		}
		return current; // key found
	}
	
	public void insertFirst(String word) { // insert as first
		MasterLink newLink = new MasterLink(word);
		newLink.next = first;
		first = newLink;
	}

	public void displayList() {
		MasterLink current = first;
		while(current!=null) {
			current.displayLink();
			current = current.next;
		}
		System.out.println();
	}

	public boolean isEmpty() {
		return first == null;
	}
}
