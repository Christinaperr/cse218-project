package model;

public class BabyLinkedList {
private BabyLink first;

	public BabyLinkedList() {

	}

	public BabyLink getFirst() {
		return first;
	}

	public void setFirst(BabyLink first) {
		this.first = first;
	}
	public void insertFirst(String keyWord) {
		BabyLink newLink = new BabyLink(keyWord);
		newLink.setNext(first);
		first = newLink;
	}

	public BabyLink deleteFirst() {
		BabyLink temp = first;
		first = first.getNext();
		return temp;
	}

	public BabyLink findLink(String key) {
		BabyLink current = first;
		while(!current.getWord().equals(key)) {
			if(current.getNext() == (null)) {
				return null;
			} else {
				current = current.getNext();
			}
		}
		return current;
	}

	public BabyLink delete(int key) {
		BabyLink current = first;
		BabyLink previous = first;
		while(!current.getWord().equals(key)) {
			if(current.getNext().equals(null)) {
				return null;
			} else {
				previous = current;
				current = current.getNext();
			}
		}
		if(current == first){
			first = first.getNext();
		} else {
			previous.setNext(current.getNext());
		}
		return current;
	}

	public void displayList() {
		BabyLink current = first;
		while(!(current==(null))) {
			current.displayLink();
			current = current.getNext();
		}
		System.out.println();
	}
}
