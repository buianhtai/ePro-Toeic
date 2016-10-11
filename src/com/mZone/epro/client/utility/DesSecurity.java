package com.mZone.epro.client.utility;

/**
 * 
 * @author HUY
 * @Created Date: 2013/12/23
 *
 */

import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DesSecurity {
    private static String salt    = "hywebpg5";
    private static String mode    = "ECB";//"CBC";
    private static String padding = "NoPadding";//"PKCS5Padding";
    
    public String Encrypt(String msg, String k, String type) throws Exception
    {
    	if (k.length() > 24){
			k = k.substring(0, 24);
		}
        SecretKeySpec skey = getKey(k, type); 
        
        msg = padding(msg); 
        
        byte[] plainBytes = msg.getBytes(); 
        String encMode = type + "/" + mode + "/" + padding;
        Cipher cipher = Cipher.getInstance(encMode);
        IvParameterSpec ivspec = new IvParameterSpec(salt.getBytes()); 
        if("CBC".equals(mode))
        {
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
        }
        else
        {
            cipher.init(Cipher.ENCRYPT_MODE, skey);
        }
        byte[] cipherText = cipher.doFinal(plainBytes); 
        
        return byte2hex(cipherText); 
    }
    
    public String Decrypt(String msg, String k, String type) throws Exception
    {
    	if (k.length() > 24){
			k = k.substring(0, 24);
		}
        SecretKeySpec skey = getKey(k,type);
        
        byte[] inPut = hex2byte(msg); 
        
        String decMode = type + "/" + mode + "/" + padding;
        Cipher cipher = Cipher.getInstance(decMode);
        IvParameterSpec ivspec = new IvParameterSpec(salt.getBytes()); 
        if("CBC".equals(mode))
        {
            cipher.init(Cipher.DECRYPT_MODE, skey, ivspec);
        }
        else
        {
            cipher.init(Cipher.DECRYPT_MODE, skey);
        }
        
        byte[] output = cipher.doFinal(inPut); 
 
        return new String(removePadding(output));
    }

    public String DESMAC(String msg, String k, String type) throws Exception
    {  
        SecretKeySpec skey = getKey(k,type); 
        
        msg = sha1(msg);
        byte[] mgsByte = macPadding(msg); 
            
        String encMode = type + "/" + mode + "/" + padding;
        Cipher cipher = Cipher.getInstance(encMode);
        IvParameterSpec ivspec = new IvParameterSpec(salt.getBytes()); 
        if("CBC".equals(mode))
        {
            cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
        }
        else
        {
            cipher.init(Cipher.ENCRYPT_MODE, skey);
        }
        byte[] cipherText = cipher.doFinal(mgsByte);
        
        
        return byte2hex(cipherText);
    }
    
    
    public String sha1(String input) throws Exception
    {
    	try
        {
	        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
	        byte[] result = mDigest.digest(input.getBytes());
	        StringBuffer sb = new StringBuffer();
	        
	        for (int i = 0; i < result.length; i++) 
	        {
	            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
	        }
	         
	        return sb.toString();
        }
        catch (Exception ex)
        {
        	throw new Exception("SHA-1: " + ex.toString());
        }      
    }
    
    
    private String byte2hex(byte[] b) 
    {
      String hs = "";
      String stmp = "";
      for (int n = 0; n < b.length; n++)
      {
          stmp = (Integer.toHexString(b[n] & 0XFF));
          
          if (stmp.length() == 1)
          {
              hs = hs + "0" + stmp;
          }
          else 
          {
              hs = hs + stmp;
          }
          
          if (n < (b.length-1))  
          {
              hs = hs + "";             
          }
      }
      
      return hs.toUpperCase();
    }

    private byte[] hex2byte(String hex) throws IllegalArgumentException   
    {   
        if((hex.length() % 2) != 0)   
        {   
            throw new IllegalArgumentException();   
        }   
        char[] arr = hex.toCharArray();   
        byte[] b = new   byte[hex.length()/2]; 
            
        for(int i = 0, j = 0, l = hex.length(); i < l; i++, j++)   
        {   
            String swap = "" + arr[i++] + arr[i];   
            int byteint = Integer.parseInt(swap, 16) & 0xFF;   
            b[j] = new Integer(byteint).byteValue();   
        }   
        return b;   
    }   
        
    private String padding(String str) throws Exception
    { 
    	System.out.println(str.length());
        byte[] oldByteArray = str.getBytes();
        int numberToPad = 8 - oldByteArray.length % 8; 
        byte[] newByteArray = new byte[oldByteArray.length + numberToPad]; 
        System.arraycopy(oldByteArray, 0, newByteArray, 0, oldByteArray.length); 
        for (int i = oldByteArray.length; i < newByteArray.length; ++i) 
        { 
            newByteArray[i] = 0; 
        }
        
        return new String(newByteArray);  
    } 

    public static byte[] hexStringToByteArray(String s) 
    {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) 
        {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        
        return data;
    }

    
    private byte[] macPadding(String str) 
    { 
    	byte[] oldByteArray = hexStringToByteArray(str);
        int numberToPad = 8 - oldByteArray.length % 8; 
        byte[] newByteArray = new byte[oldByteArray.length + numberToPad]; 
        System.arraycopy(oldByteArray, 0, newByteArray, 0, oldByteArray.length); 
        for (int i = oldByteArray.length; i < newByteArray.length; ++i) 
        { 
            newByteArray[i] = 0; 
        }
        
        return newByteArray;  
    } 
    
    private byte[] removePadding(byte[] oldByteArray) 
    { 
        int numberPaded = 0; 
        for (int i = oldByteArray.length; i >= 0; --i) 
        { 
            if (oldByteArray[(i - 1)] != 0) 
            { 
                numberPaded = oldByteArray.length - i; 
                break; 
            } 
        } 

        byte[] newByteArray = new byte[oldByteArray.length - numberPaded]; 
        System.arraycopy(oldByteArray, 0, newByteArray, 0, newByteArray.length); 

        return newByteArray; 
    } 
    
    private SecretKeySpec getKey(String keys, String mode)
    {   
        SecretKeySpec pass = new SecretKeySpec(keys.getBytes(), mode);
        return pass;
    }
    
}
