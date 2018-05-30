package model;

public class MasterLink {
	public String keyWord;
	public MasterLink next;
	public BabyLinkedList babyList;
	public int counter;

	public MasterLink(String word) {
		this.keyWord = word;
		babyList = new BabyLinkedList();
	}
	public boolean hasNext() {
		return next != null;
	}
	public void setBabyLink(String babyWord) {
		babyList.insertFirst(babyWord);
		counter++;
	}

	public void displayBabyList() {
		babyList.displayList();
	}

	public void displayLink() {
		System.out.print(keyWord + " ");
	}
	
	public void displayLinkTest() {
		System.out.println(keyWord + " ");
	}

}
