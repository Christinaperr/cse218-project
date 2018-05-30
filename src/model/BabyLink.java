package model;

public class BabyLink {
	private String keyWord;
	private BabyLink next;

	public BabyLink(String word) {
		this.keyWord = word;
	}

	public String getWord() {
		return keyWord;
	}
	
	public boolean hasNext() {
		return next != null;
	}

	public void setWord(String keyWord) {
		this.keyWord = keyWord;
	}

	public BabyLink getNext() {
		return next;
	}

	public void setNext(BabyLink next) {
		this.next = next;
	}

	public void displayLink() {
		System.out.print(keyWord + " ");
	}
}
