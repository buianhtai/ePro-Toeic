package com.mZone.epro.client.data;

public abstract class BookItem {
	
	public static final int LOCAL = 0;
	public static final int SERVER = 1;
	
	public static final int BOOK_TYPE_TOEIC = 1;
	
	protected int bookID;
	protected int locationType;
	protected String bookName;
	protected String bookAuthor;
	protected int bookType;	//toeic ielts toefl
	
	public BookItem() {
		// TODO Auto-generated constructor stub
	}

	public BookItem(int bookID, int locationType, String bookName, String bookAuthor, int bookType) {
		// TODO Auto-generated constructor stub
		this.bookID = bookID;
		this.locationType = locationType;
		this.bookName = bookName;
		this.bookAuthor = bookAuthor;
		this.bookType = bookType;
	}
	
	public int getBookID() {
		return bookID;
	}

	public void setBookID(int bookID) {
		this.bookID = bookID;
	}

	public int getLocationType() {
		return locationType;
	}

	public void setLocationType(int locationType) {
		this.locationType = locationType;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public int getBookType() {
		return bookType;
	}

	public void setBookType(int bookType) {
		this.bookType = bookType;
	}

	@Override
	public String toString() {
		return "BookItem [bookID=" + bookID + ", locationType=" + locationType
				+ ", bookName=" + bookName + ", bookAuthor=" + bookAuthor
				+ ", bookType=" + bookType + "]";
	}
	
	
}
