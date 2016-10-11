package com.mZone.epro.client.data;

/*
 * @author HUY
 * @Created Date: 2013/12/22
 */

public class Payment {
	int id;
	String deviceId;
	String securitykey1;
	String securitykey2;
	String email;
	String product_type;
	String status;
	String barcode;
	String date;

	public Payment (String securitykey1, String securitykey2, String email, String product_type, String status, String barcode, String date) {
		this.securitykey1 = securitykey1;
		this.securitykey2 = securitykey2;
		this.email = email;
		this.product_type =product_type;
		this.status = status;
		this.barcode = barcode;
		this.date = date;
	}

	public String getSecuritykey1() {
		return securitykey1;
	}

	public void setSecuritykey1(String securitykey1) {
		this.securitykey1 = securitykey1;
	}

	public String getSecuritykey2() {
		return securitykey2;
	}

	public void setSecuritykey2(String securitykey2) {
		this.securitykey2 = securitykey2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProduct_type() {
		return product_type;
	}

	public void setProduct_type(String product_type) {
		this.product_type = product_type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
