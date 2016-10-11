package com.mZone.epro.client.utility;


public class AESHelper {

    	public static String URLEnCode (String URL) {
    		String seedValue = "m0bf2vm0bfrvm0b12rvm0b4r";
    		String mEncode = "";
    		DesSecurity des = new DesSecurity();
    		try {
    			mEncode = des.Encrypt(URL, seedValue, "DESede");

    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    		return mEncode;
    	}
    	
    	//This method is used to descript URL
    	public static String URLDescript (String URLEnscript) {
    		String seedValue = "m0bf2vm0bfrvm0b12rvm0b4r";
    		String mDecode = "";
    		DesSecurity des = new DesSecurity();
    		try {
    			mDecode = des.Decrypt(URLEnscript, seedValue, "DESede");

    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    		return mDecode;
    	}

        
}
