package com.mZone.epro.client.data;

public class BookItemServer extends BookItem {

	public static final int FREE = 0;
	public static final int PAY_REQUIRED = 1;
	
	private int localID;
	private float bookPrice;
	private String urlImage;
	private String urlDownload;
	private int paymentRequire;
	private String updateDate;
	
	private float scale;
	
	public BookItemServer() {
		// TODO Auto-generated constructor stub
	}

	public BookItemServer(int bookID, String bookName,
			String bookAuthor, int bookType) {
		super(bookID, BookItem.SERVER, bookName, bookAuthor, bookType);
		// TODO Auto-generated constructor stub
	}
	
	public BookItemServer(int bookID, String bookName,
			String bookAuthor, int bookType, float bookPrice, String urlImage, 
			String urlDownload, int paymentRequire) {
		super(bookID, BookItem.SERVER, bookName, bookAuthor, bookType);
		// TODO Auto-generated constructor stub
		this.bookPrice = bookPrice;
		this.urlImage = urlImage;
		this.urlDownload = urlDownload;
		this.paymentRequire = paymentRequire;
		scale = 1.0f;
	}

	public float getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(float bookPrice) {
		this.bookPrice = bookPrice;
	}

	public String getUrlImage() {
		return urlImage;
	}

	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}

	public String getUrlDownload() {
		return urlDownload;
	}

	public void setUrlDownload(String urlDownload) {
		this.urlDownload = urlDownload;
	}

	public int getPaymentRequire() {
		return paymentRequire;
	}

	public void setPaymentRequire(int paymentRequire) {
		this.paymentRequire = paymentRequire;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public int getLocalID() {
		return localID;
	}

	public void setLocalID(int localID) {
		this.localID = localID;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		if (this.scale == 1){
			this.scale = scale;
		}
		else{
			if(scale > this.scale) 
				this.scale = scale;
		}
	}
	
	public void increaseScale(){
		scale = scale + (1-scale)*0.05f;
		if (scale > 1) scale = 1;
	}

	@Override
	public String toString() {
		return "BookItemServer [localID=" + localID + ", bookPrice="
				+ bookPrice + ", urlImage=" + urlImage + ", urlDownload="
				+ urlDownload + ", paymentRequire=" + paymentRequire
				+ ", updateDate=" + updateDate + ", scale=" + scale
				+ ", bookID=" + bookID + ", locationType=" + locationType
				+ ", bookName=" + bookName + ", bookAuthor=" + bookAuthor
				+ ", bookType=" + bookType + "]";
	}
}
