package com.mZone.epro.client.utility;

/*
 * @author HUY
 * @Created Date: 2013/12/23
 * Get Emei device
 */

import java.lang.reflect.Method;
import android.telephony.TelephonyManager;

public class EmeiDevice {
	
	public String getDeviceID(TelephonyManager phonyManager){
		 String mtemp = "";
		 String id = phonyManager.getDeviceId();
		 if (id == null){
			 mtemp = "not available";
		 }
		 
		 int phoneType = phonyManager.getPhoneType();
		 
		 switch(phoneType){
			 case TelephonyManager.PHONE_TYPE_NONE:
				 mtemp = "NONE: " + id;
				 break;
			 case TelephonyManager.PHONE_TYPE_GSM:
				 mtemp = id;
				 break;
			 case TelephonyManager.PHONE_TYPE_CDMA:
				 mtemp = id;
				 break;
			 /*
			  *  for API Level 11 or above
			  *  case TelephonyManager.PHONE_TYPE_SIP:
			  *   return "SIP";
			  */
			 default:
				 mtemp = "UNKNOWN: ID=" + id;
				 break;
		 }
		 
		String serialnum = "";
		try {                                                                               
			Class<?> c = Class.forName("android.os.SystemProperties");        	 
			Method get = c.getMethod("get", String.class, String.class );                     
			serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );              
		}                                                                                 
		catch (Exception ignored)                                                         
		{                  
			ignored.printStackTrace();
		}
		
		if (mtemp.contains("not available") || mtemp.contains("UNKNOWN") || mtemp.contains("NONE")) {
			mtemp = serialnum;
		}
		 
		//check 24bytes for deviceId
		int len = mtemp.length();
		if (mtemp.length() > 24) {
			mtemp = mtemp.substring(0,24);
		} else if (mtemp.length() < 24) {
			for (int i=0; i < 24-len; i++) {
				mtemp = mtemp + i;
			}
		}
		
		return mtemp;
	}

}
