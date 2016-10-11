package com.mZone.epro.client.data;

public class BookItemLocal extends BookItem {

	private int localID;
	private int status;
	private String folderPath;
	private String securityName;
	private String imageStatus;
	private int isNewBook;
	
	public static final int STATUS_UNTABLE = -1;
	public static final int STATUS_DOWNLOADING = -2;
	public static final int STATUS_DOWNLOAD_SUCESSFULL = -3;
	public static final int STATUS_UNZIP_SUCESSFULL = -4;
	
	public static final String IMAGE_HAVE = "1111";
	public static final String IMAGE_NO = "0000";
	
	
	public BookItemLocal() {
		// TODO Auto-generated constructor stub
	}

	public BookItemLocal(int bookID, String bookName,
			String bookAuthor, int bookType) {
		super(bookID, BookItem.LOCAL, bookName, bookAuthor, bookType);
		// TODO Auto-generated constructor stub
	}
	
	public BookItemLocal(int bookID, String bookName,
			String bookAuthor, int bookType, int status, String folderPath, String securityName, int localID){
		super(bookID, BookItem.LOCAL, bookName, bookAuthor, bookType);
		this.status = status;
		this.folderPath = folderPath;
		this.securityName = securityName;
		this.localID = localID;
		this.isNewBook = 0;
	}
	
	public BookItemLocal(int bookID, String bookName, String bookAuthor, int localID, String imageStatus, String securityName){
		super(bookID, BookItem.LOCAL, bookName, bookAuthor, BOOK_TYPE_TOEIC);
		this.localID = localID;
		this.imageStatus = imageStatus;
		this.securityName = securityName;
	}
	
	public BookItemLocal(int bookID, String bookName, String bookAuthor, int localID, String imageStatus, String securityName, int bookType){
		super(bookID, BookItem.LOCAL, bookName, bookAuthor, bookType);
		this.localID = localID;
		this.imageStatus = imageStatus;
		this.securityName = securityName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public String getSecurityName() {
		return securityName;
	}

	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	public int getLocalID() {
		return localID;
	}

	public void setLocalID(int localID) {
		this.localID = localID;
	}

	public String getImageStatus() {
		return imageStatus;
	}

	public void setImageStatus(String imageStatus) {
		this.imageStatus = imageStatus;
	}

	public int getIsNewBook() {
		return isNewBook;
	}

	public void setIsNewBook(int isNewBook) {
		this.isNewBook = isNewBook;
	}
}
