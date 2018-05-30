package model;


public class ListIterator {
	public MasterLink current;
	public MasterLink previous;
	public MasterLinkedList ourList;

	public ListIterator(MasterLinkedList list) {
		ourList = list;
		reset();
	}
	
	public void setPreviousBabyLink(String word){
		previous.setBabyLink(word);
	}

	public void reset() {
		current = ourList.getFirst();
		previous = null;
	}

	public boolean atEnd() {
		return current.next == null;
	}

	public void nextLink() {
		previous = current;
		current = current.next;
	}

	public MasterLink getCurrent() {
		return current;
	}
	public MasterLink getPreviousCurrent() {
		return previous;
	}

	public MasterLink insertAfter(String word) { // insert newLink after current link
		MasterLink newLink = new MasterLink(word);

		if(ourList.isEmpty()) {
			ourList.setFirst(newLink);
			current = newLink;
		} else {
			newLink.next = current.next;
			current.next = newLink;
			nextLink();
		}
		return current;
	}
	public void insertFirst(String word) { // insert as first
		MasterLink newLink = new MasterLink(word);
		newLink.next = ourList.getFirst();
		ourList.setFirst(newLink);
		current = newLink;
	}
	public void insertBefore(String word) { // insert newLink before current link
		MasterLink newLink = new MasterLink(word);

		if(previous == null) { // beginning of list or empty list
			newLink.next = ourList.getFirst();
			ourList.setFirst(newLink);
			reset();
		} else {
			newLink.next = previous.next;
			previous.next = newLink;
			current = newLink;
		}
	}

	public String deleteCurrent() { // delete item at current
		String value = current.keyWord;
		if(previous == null) { // beginning of list
			ourList.setFirst(current.next);
			reset();
		} else {
			previous.next = current.next;
			if(atEnd()) {
				reset();
			}
		}
		return value;
	}

	public MasterLink findLink(String key) {
		current = ourList.getFirst();
		while(current.keyWord != key) {
			if(current.next == null) {
				return null;
			} else {
				current = current.next;
			}
		}
		return current;
	}
}
