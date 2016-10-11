package com.mZone.epro.client.utility;

public class EnDeCode {
	public static String EnCode (String value, String emei) {
		String mEncode = "";
		DesSecurity des = new DesSecurity();
		if (emei.length() > 24){
			emei = emei.substring(0, 24);
		}
		try {
			mEncode = des.Encrypt(value, emei, "DESede");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return mEncode;
	}
	public static String Decode (String value, String emei) {
		String mDecode = "";
		DesSecurity des = new DesSecurity();
		if (emei.length() > 24){
			emei = emei.substring(0, 24);
		}
		try {
			mDecode = des.Decrypt(value, emei, "DESede");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return mDecode;
	}
	
}
