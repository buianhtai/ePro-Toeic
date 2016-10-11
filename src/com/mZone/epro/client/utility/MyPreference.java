package com.mZone.epro.client.utility;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class MyPreference {
	
	public static class Account implements Parcelable{
		
		public String account;
		public int credits;
		public int numberOfClick;
		
		public Account(String account, int credits, int numberOfClick) {
			super();
			this.account = account;
			this.credits = credits;
			this.numberOfClick = numberOfClick;
		}

		public Account(String account, int credits){
			this.account = account;
			this.credits = credits;
			this.numberOfClick = 0;
		}
		
		public Account(Parcel in){
			readFromParcel(in);
		}

		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeString(account);
			dest.writeInt(credits);
			dest.writeInt(numberOfClick);
		}
		
		private void readFromParcel(Parcel in)
		{
			account = in.readString();
			credits = in.readInt();
			numberOfClick = in.readInt();
		}
		
		@SuppressWarnings("rawtypes")
		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
			@Override
			public Account createFromParcel(Parcel in) {
				 return new Account(in); 
			}   
			@Override
			public Account[] newArray(int size) {
				 return new Account[size]; 
			} 
		}; 
	}
	
	public static final String PREFS_NAME = "com.mZone.epro.client.utility.MyPreference.MyPrefsFile";	
	public static final String ACCOUNT_KEY = "com.mZone.epro.client.utility.MyPreference.account";
	public static final String ACCOUNT_CREDIT = "com.mZone.epro.client.utility.MyPreference.credits";
	public static final String NUMBER_OF_CLICK_KEY = "com.mZone.epro.client.utility.MyPreference.number_of_click";
	
	//Add Click
	public static final String PREVIOUS_AD_CLICK_TIME_NUMBER_OF_CLICK = "com.mZone.epro.client.utility.MyPreference.previous_click_number_click";
	public static final String PREVIOUS_AD_CLICK_TIME_KEY = "com.mZone.epro.client.utility.MyPreference.previous_click";
	public static final int PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_AND_RESET = 2;
	public static final int PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_WITHOUT_RESET  = 1;
	public static final int PREVIOUS_AD_CLICK_STATUS_UNABLE  = 0;
	private static final int MAX_CLICK_IN_HOUR = 4;
	
	//Twitter
	public static final String PREVIOUS_TWITTER_SHARE_TIME = "com.mZone.epro.client.utility.MyPreference.previous_twitter_time";
	private static final int NUMBER_OF_CLICK_PER_SHARE = 5;
	
	//Facebook
	public static final String PREVIOUS_FACEBOOK_TIME = "com.mZone.epro.client.utility.MyPreference.previous_facebook_time";
	
		
	public static void removeCurrentAccount(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(ACCOUNT_KEY);
		editor.remove(ACCOUNT_CREDIT);
		editor.remove(NUMBER_OF_CLICK_KEY);
		editor.commit();
	}
	
	public static Account getAccount(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String imei = Utils.getDeviceImei(mContext);
		String decodedAccount = pref.getString(ACCOUNT_KEY, null);
		if (!TextUtils.isEmpty(decodedAccount)){
			String account = EnDeCode.Decode(pref.getString(ACCOUNT_KEY, null), imei);
			String credits = EnDeCode.Decode(pref.getString(ACCOUNT_CREDIT, null), imei);
			String numberOfClick = EnDeCode.Decode(pref.getString(NUMBER_OF_CLICK_KEY, null), imei);
			return new Account(account, Integer.valueOf(credits), Integer.valueOf(numberOfClick));
		}
		else{
			return null;
		}
	}
	
	public static void setAccount(Context mContext, Account account){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		String imei = Utils.getDeviceImei(mContext);
		String encodedAccount = EnDeCode.EnCode(account.account, imei);
		String encodedCredits = EnDeCode.EnCode(String.valueOf(account.credits), imei);
		String encodedNClick = EnDeCode.EnCode(String.valueOf(account.numberOfClick), imei);
		editor.putString(ACCOUNT_KEY, encodedAccount);
		editor.putString(ACCOUNT_CREDIT, encodedCredits);
		editor.putString(NUMBER_OF_CLICK_KEY, encodedNClick);
		editor.commit();
	}
	
	public static String getCurrentAccount(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String imei = Utils.getDeviceImei(mContext);
		String decodedAccount = pref.getString(ACCOUNT_KEY, null);
		if (!TextUtils.isEmpty(decodedAccount)){
			String account = EnDeCode.Decode(pref.getString(ACCOUNT_KEY, null), imei);
			return account;
		}
		else{
			return null;
		}
	}
	
	public static void setAccountCredit(Context mContext, int credits){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		String imei = Utils.getDeviceImei(mContext);
		String encodedCredits = EnDeCode.EnCode(String.valueOf(credits), imei);
		editor.putString(ACCOUNT_CREDIT, encodedCredits);
		editor.commit();
	}

	/*************************************** Ad Click start ************************************************/
	
	public static int checkIfCanClickAd(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String saveTimeString = pref.getString(PREVIOUS_AD_CLICK_TIME_KEY, null);
		if (TextUtils.isEmpty(saveTimeString)){
			return PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_AND_RESET;
		}
		String imei = Utils.getDeviceImei(mContext);
		long currentTime = System.currentTimeMillis()/1000;
		long saveTime = Long.valueOf(EnDeCode.Decode(saveTimeString, imei))/1000;
		if (currentTime - saveTime < 3600){
			int currentClick = Integer.valueOf(EnDeCode.Decode(pref.getString(PREVIOUS_AD_CLICK_TIME_NUMBER_OF_CLICK, null), imei));
			if (currentClick > MAX_CLICK_IN_HOUR){
				return PREVIOUS_AD_CLICK_STATUS_UNABLE;
			}
			else{
				return PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_WITHOUT_RESET;
			}
		}
		else{
			return PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_AND_RESET;
		}
	}
	
	public static int increaseNumberOfClickNormal(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String imei = Utils.getDeviceImei(mContext);
		String numberOfClick = EnDeCode.Decode(pref.getString(NUMBER_OF_CLICK_KEY, null), imei);
		int nClick = Integer.valueOf(numberOfClick);
		nClick++;
		String encodedNClick = EnDeCode.EnCode(String.valueOf(nClick), imei);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(NUMBER_OF_CLICK_KEY, encodedNClick);
		int currentClick = Integer.valueOf(EnDeCode.Decode(pref.getString(PREVIOUS_AD_CLICK_TIME_NUMBER_OF_CLICK, null), imei));
		currentClick++;
		editor.putString(PREVIOUS_AD_CLICK_TIME_NUMBER_OF_CLICK, EnDeCode.EnCode(String.valueOf(currentClick), imei));
		editor.commit();
		return nClick;
	}
	
	public static int increaseNumberOfClickAndReset(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String imei = Utils.getDeviceImei(mContext);
		String numberOfClick = EnDeCode.Decode(pref.getString(NUMBER_OF_CLICK_KEY, null), imei);
		int nClick = Integer.valueOf(numberOfClick);
		nClick++;
		String encodedNClick = EnDeCode.EnCode(String.valueOf(nClick), imei);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(NUMBER_OF_CLICK_KEY, encodedNClick);
		editor.putString(PREVIOUS_AD_CLICK_TIME_KEY, EnDeCode.EnCode(String.valueOf(System.currentTimeMillis()), imei));
		editor.putString(PREVIOUS_AD_CLICK_TIME_NUMBER_OF_CLICK, EnDeCode.EnCode(String.valueOf(1), imei));
		editor.commit();
		return nClick;
	}
	
	public static void resetNumberOfClick(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		String imei = Utils.getDeviceImei(mContext);
		editor.putString(NUMBER_OF_CLICK_KEY, EnDeCode.EnCode("0", imei));
		editor.commit();
	}
	
	/*************************************** Ad Click End ************************************************/
	
	/*************************************** Twitter Share Start *****************************************/
	
	public static boolean checkIfAbleToShareTwitter(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String saveTimeString = pref.getString(PREVIOUS_TWITTER_SHARE_TIME, null);
		if (TextUtils.isEmpty(saveTimeString)){
			return true;
		}
		else{
			String imei = Utils.getDeviceImei(mContext);
			long currentTime = System.currentTimeMillis()/1000;
			long saveTime = Long.valueOf(EnDeCode.Decode(saveTimeString, imei))/1000;
			if (currentTime - saveTime < 86400){
				return false;
			}
			else{
				return true;
			}
		}
	}
	
	public static int increaseCreditByTwitterShare(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String imei = Utils.getDeviceImei(mContext);
		String numberOfClick = EnDeCode.Decode(pref.getString(NUMBER_OF_CLICK_KEY, null), imei);
		int nClick = Integer.valueOf(numberOfClick);
		nClick += NUMBER_OF_CLICK_PER_SHARE;
		String encodedNClick = EnDeCode.EnCode(String.valueOf(nClick), imei);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(NUMBER_OF_CLICK_KEY, encodedNClick);
		editor.putString(PREVIOUS_TWITTER_SHARE_TIME, EnDeCode.EnCode(String.valueOf(System.currentTimeMillis()), imei));
		editor.commit();
		return nClick;
	}
	
	/*************************************** Twitter Share End *****************************************/
	
    /*************************************** Facebook Share Start *****************************************/
	
	public static boolean checkIfAbleToShareFacebook(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String saveTimeString = pref.getString(PREVIOUS_FACEBOOK_TIME, null);
		if (TextUtils.isEmpty(saveTimeString)){
			return true;
		}
		else{
			String imei = Utils.getDeviceImei(mContext);
			long currentTime = System.currentTimeMillis()/1000;
			long saveTime = Long.valueOf(EnDeCode.Decode(saveTimeString, imei))/1000;
			if (currentTime - saveTime < 86400){
				return false;
			}
			else{
				return true;
			}
		}
	}
	
	public static int increaseCreditByFacebookShare(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		String imei = Utils.getDeviceImei(mContext);
		String numberOfClick = EnDeCode.Decode(pref.getString(NUMBER_OF_CLICK_KEY, null), imei);
		int nClick = Integer.valueOf(numberOfClick);
		nClick += NUMBER_OF_CLICK_PER_SHARE;
		String encodedNClick = EnDeCode.EnCode(String.valueOf(nClick), imei);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(NUMBER_OF_CLICK_KEY, encodedNClick);
		editor.putString(PREVIOUS_FACEBOOK_TIME, EnDeCode.EnCode(String.valueOf(System.currentTimeMillis()), imei));
		editor.commit();
		return nClick;
	}
	
	/*************************************** Twitter Share End *****************************************/
	
	public static final String APP_FIRST_LOAD_PREFS_NAME = "com.mZone.epro.client.utility.MyPreference.AppFirstLoad";
	public static final String APP_FIRST_LOAD_PREFS_TOKEN = "com.mZone.epro.client.utility.MyPreference.AppFirstLoadToken";
	
	public static void setAppFirstLoadToken(Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(APP_FIRST_LOAD_PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(APP_FIRST_LOAD_PREFS_TOKEN, 1);
		editor.commit();
	}
	
	public static int getAppFirstLoadToken(Context mContext){
		int result = 0;
		SharedPreferences pref = mContext.getSharedPreferences(APP_FIRST_LOAD_PREFS_NAME, 0);
		result = pref.getInt(APP_FIRST_LOAD_PREFS_TOKEN, 0);
		return result;
	}
	
}
