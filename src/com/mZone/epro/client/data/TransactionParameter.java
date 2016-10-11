package com.mZone.epro.client.data;

import android.graphics.Bitmap;

public class TransactionParameter {
	private BookItemServer bookItem;
	private String account;
	private String imei;
	private boolean isPaymentRequired;
	private float accountCredit;
	private Bitmap bitmap;
	public TransactionParameter() {
		// TODO Auto-generated constructor stub
	}
	public TransactionParameter(BookItemServer bookItem, String account, String imei) {
		// TODO Auto-generated constructor stub
		this.bookItem = bookItem;
		this.account = account;
		this.imei = imei;
		bitmap = null;
	}
	public BookItemServer getBookItem() {
		return bookItem;
	}
	public void setBookItem(BookItemServer bookItem) {
		this.bookItem = bookItem;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public boolean isPaymentRequired() {
		return isPaymentRequired;
	}
	public void setPaymentRequired(boolean isPaymentRequired) {
		this.isPaymentRequired = isPaymentRequired;
	}
	public float getAccountCredit() {
		return accountCredit;
	}
	public void setAccountCredit(float accountCredit) {
		this.accountCredit = accountCredit;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	

}
