package com.mZone.epro.client.utility;

/**
 * 
 * @author HUY
 * @Created Date: 2013/12/23
 *
 */

import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class Utils {
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
        	
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              //Read byte from input stream
            	
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              
              //Write byte from output stream
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public static String getDeviceImei(Context mContext){
    	TelephonyManager telephonyManager = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = new EmeiDevice().getDeviceID(telephonyManager);
		return imei;
    }
    
    public static boolean isNetworkAvailable(Context mContext) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}