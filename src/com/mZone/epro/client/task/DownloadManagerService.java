package com.mZone.epro.client.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mZone.epro.BuildConfig;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookItemServer;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.data.TransactionParameter;
import com.mZone.epro.client.utility.AESHelper;
import com.mZone.epro.client.utility.Constants;
import com.mZone.epro.client.utility.DesSecurity;
import com.mZone.epro.client.utility.EnDeCode;
import com.mZone.epro.client.utility.ExternalStorage;
import com.mZone.epro.client.utility.JSONParser;
import com.mZone.epro.client.utility.Utils;
import com.mZone.epro.dict.data.DictionaryItem;
import com.mZone.epro.dict.data.DictionaryItemServer;

import android.app.DownloadManager;
import android.app.Service;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.util.Log;

public class DownloadManagerService extends Service {

	public static final String BookDownloadTitle = "ePro TOEIC book downloading";
	public static final String DictionaryDonwloadTitle = "ePro TOEIC Dictionary downloading";
	
	private Context mContext;
	private final IBinder mBinder = new LocalBinder();
	private boolean isStartSticky = true;
	public class LocalBinder extends Binder {
		public DownloadManagerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DownloadManagerService.this;
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		if (!isStartSticky) {
		}
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return false;
	}

	public DownloadManagerService() {
		mContext = this;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	// We want this service to continue running until it is explicitly
	// stopped, so return sticky.
		registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
		isStartSticky = false;
		return START_STICKY;
	}
	
	DownloadReceiver receiver = new DownloadReceiver();
	@Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        registerReceiver(receiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Do not forget to unregister the receiver!!!
        this.unregisterReceiver(this.receiver);
    }
	
	public class DownloadReceiver extends BroadcastReceiver{
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        long receivedID = intent.getLongExtra(
	            DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
	        DownloadManager mgr = (DownloadManager)
	            context.getSystemService(Context.DOWNLOAD_SERVICE);
	 
	        DownloadManager.Query query = new DownloadManager.Query();
	        query.setFilterById(receivedID);
	        Cursor cur = mgr.query(query);
	        int index = cur.getColumnIndex(
	            DownloadManager.COLUMN_STATUS);
	        if(cur.moveToFirst()) {
	            if(cur.getInt(index) == DownloadManager.STATUS_SUCCESSFUL){
	            	String title = cur.getString(cur.getColumnIndex(DownloadManager.COLUMN_TITLE));
	            	if (BookDownloadTitle.equals(title)){
		            	StringBuilder selection = new StringBuilder();
		    			selection.append(LocalBookTableMetaData.STATUS).append(" = ? ");
		    			String[] selectionArgs = new String[]{
		    				String.valueOf(receivedID),
		    			};
		    			String[] projection = new String[]{
		    				String.valueOf(LocalBookTableMetaData._ID),
		    				String.valueOf(LocalBookTableMetaData.BOOK_ID),
		    				String.valueOf(LocalBookTableMetaData.DOWNLOAD_URI),
		    				String.valueOf(LocalBookTableMetaData.BOOK_NAME),
		    			};
		    			ContentResolver cr = mContext.getContentResolver();
		    			int updateURIIndex = -1;
		    			int bookID = -1;
		    			Cursor cursor = cr.query(LocalBookTableMetaData.CONTENT_URI, projection, 
		    					selection.toString(), selectionArgs, null);
		    			if (cursor.moveToNext()){
		    				updateURIIndex = cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData._ID));
		    				bookID = cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_ID));
		    			}
						if (BuildConfig.DEBUG){
							Log.e("debug", "DownloadReceiver STATUS_SUCCESSFUL bookId=" + bookID);
						}
		    			if (updateURIIndex > 0){
		    				String encryptedURL = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.DOWNLOAD_URI));
		    				String downloadURI = EnDeCode.Decode(encryptedURL, Utils.getDeviceImei(getApplicationContext()));
		    				if (downloadURI == null || downloadURI.isEmpty()){
		    					downloadURI = encryptedURL;
		    				}
		    				String bookName = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_NAME));
		    			    Intent processDataIntent = new Intent(mContext, BookDataProcessingIntentService.class);
			    			processDataIntent.putExtra(BookDataProcessingIntentService.INTENT_KEY, BookDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADED_DATA);
			    			processDataIntent.putExtra(LocalBookTableMetaData._ID, updateURIIndex);
			    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_ID, bookID);
			    			processDataIntent.putExtra(LocalBookTableMetaData.DOWNLOAD_URI, downloadURI);
			    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_NAME, bookName);
			    			startService(processDataIntent);
		    			}
		    			cursor.close();
	            	}
	            	else if (DictionaryDonwloadTitle.equals(title)){
		            	StringBuilder selection = new StringBuilder();
		    			selection.append(DictionaryItem.Columns.STATUS).append(" = ? ");
		    			String[] selectionArgs = new String[]{
		    				String.valueOf(receivedID),
		    			};
		    			String[] projection = new String[]{
		    				String.valueOf(BaseColumns._ID),
		    				String.valueOf(DictionaryItem.Columns.DICT_ID),
		    			};
		    			ContentResolver cr = mContext.getContentResolver();
		    			int updateURIIndex = -1;
		    			String dictID = "";
		    			Cursor cursor = cr.query(DictionaryItem.CONTENT_URI, projection, 
		    					selection.toString(), selectionArgs, null);
		    			if (cursor.moveToNext()){
		    				updateURIIndex = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
		    				dictID = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.DICT_ID));
		    			}
		    			if (updateURIIndex > 0){
		    				Intent processDataIntent = new Intent(mContext, DictionaryDataProcessingIntentService.class);
			    			processDataIntent.putExtra(DictionaryDataProcessingIntentService.INTENT_KEY, DictionaryDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADED_DATA);
			    			processDataIntent.putExtra(BaseColumns._ID, updateURIIndex);
			    			processDataIntent.putExtra(DictionaryItem.Columns.DICT_ID, dictID);
			    			startService(processDataIntent);
		    			}
		    			cursor.close();
	            	}
	            }
	        }
	        cur.close();
	    }
	}
	
	public void startDownloadBookItem(TransactionParameter parameters){
		DownloadProcessingTask thread = new DownloadProcessingTask(getApplicationContext(), parameters);
		thread.start();
	}
	
	public void reprocessAllData()
	{
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				preProcessBookData();
			}
		}).start();
	}
	
	private void preProcessBookData(){
		
		//process book
		StringBuilder selection = new StringBuilder();
		selection.append(LocalBookTableMetaData.STATUS).append(" > ? ");
		selection.append(" OR ").append(LocalBookTableMetaData.STATUS).append(" = ? ");
		selection.append(" OR ").append(LocalBookTableMetaData.STATUS).append(" = ? ");
		
		String[] selectionArgs = new String[]{
			String.valueOf(0),
			String.valueOf(BookItemLocal.STATUS_DOWNLOADING),
			String.valueOf(BookItemLocal.STATUS_DOWNLOAD_SUCESSFULL),
		};
		String[] projection = new String[]{
			String.valueOf(LocalBookTableMetaData._ID),
			String.valueOf(LocalBookTableMetaData.STATUS),
			String.valueOf(LocalBookTableMetaData.BOOK_ID),
			String.valueOf(LocalBookTableMetaData.DOWNLOAD_URI),
			String.valueOf(LocalBookTableMetaData.BOOK_NAME),
		};
		ContentResolver cr = mContext.getContentResolver();
		Cursor cursor = cr.query(LocalBookTableMetaData.CONTENT_URI, projection, 
				selection.toString(), selectionArgs, LocalBookTableMetaData.STATUS_SORT_ORDER);
		while (cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData._ID));
			long status = cursor.getLong(cursor.getColumnIndex(LocalBookTableMetaData.STATUS));
			int bookID = cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_ID));
			String encryptedURL = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.DOWNLOAD_URI));
			DesSecurity des = new DesSecurity();
			String downloadURI = "";
			 try {
				 downloadURI = des.Decrypt(encryptedURL, Utils.getDeviceImei(getApplicationContext()), "DESede");
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
			String bookName = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_NAME));
			if (status == BookItemLocal.STATUS_DOWNLOADING){
				if (BuildConfig.DEBUG){
					Log.e("debug", "preProcessBookData STATUS_DOWNLOADING bookId=" + bookID);
				}
				Intent processDataIntent = new Intent(mContext, BookDataProcessingIntentService.class);
    			processDataIntent.putExtra(BookDataProcessingIntentService.INTENT_KEY, BookDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADING_DATA);
    			processDataIntent.putExtra(LocalBookTableMetaData._ID, id);
    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_ID, bookID);
    			processDataIntent.putExtra(LocalBookTableMetaData.STATUS, status);
    			processDataIntent.putExtra(LocalBookTableMetaData.DOWNLOAD_URI, downloadURI);
    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_NAME, bookName);
    			startService(processDataIntent);
			}
			else if (status == BookItemLocal.STATUS_DOWNLOAD_SUCESSFULL){
				if (BuildConfig.DEBUG){
					Log.e("debug", "preProcessBookData STATUS_DOWNLOAD_SUCESSFULL bookId=" + bookID);
				}
				Intent processDataIntent = new Intent(mContext, BookDataProcessingIntentService.class);
    			processDataIntent.putExtra(BookDataProcessingIntentService.INTENT_KEY, BookDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADED_DATA);
    			processDataIntent.putExtra(LocalBookTableMetaData._ID, id);
    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_ID, bookID);
    			processDataIntent.putExtra(LocalBookTableMetaData.DOWNLOAD_URI, downloadURI);
    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_NAME, bookName);
    			startService(processDataIntent);
			}
			else{
				if (BuildConfig.DEBUG){
					Log.e("debug", "preProcessBookData others bookId=" + bookID);
				}
				Intent processDataIntent = new Intent(mContext, BookDataProcessingIntentService.class);
    			processDataIntent.putExtra(BookDataProcessingIntentService.INTENT_KEY, BookDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADING_DATA);
    			processDataIntent.putExtra(LocalBookTableMetaData._ID, id);
    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_ID, bookID);
    			processDataIntent.putExtra(LocalBookTableMetaData.STATUS, status);
    			processDataIntent.putExtra(LocalBookTableMetaData.DOWNLOAD_URI, downloadURI);
    			processDataIntent.putExtra(LocalBookTableMetaData.BOOK_NAME, bookName);
    			startService(processDataIntent);
			}
		}
		cursor.close();
		
		//process dictionary
		selection = new StringBuilder();
		selection.append(DictionaryItem.Columns.STATUS).append(" <> ?");
		
		selectionArgs = new String[]{
			String.valueOf(DictionaryItem.STATUS_UNZIP_SUCESSFULL),
		};
		projection = new String[]{
			String.valueOf(BaseColumns._ID),
			String.valueOf(DictionaryItem.Columns.STATUS),
			String.valueOf(DictionaryItem.Columns.DICT_ID),
		};
		cr = mContext.getContentResolver();
		cursor = cr.query(DictionaryItem.CONTENT_URI, projection, 
				selection.toString(), selectionArgs, null);
		while (cursor.moveToNext()){
			int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			long status = cursor.getLong(cursor.getColumnIndex(DictionaryItem.Columns.STATUS));
//			String dictID = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.DICT_ID));
			if (status > 0){
				DownloadManager mgr = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
				DownloadManager.Query query = new DownloadManager.Query();
				query.setFilterById(status);
				Cursor cur = mgr.query(query);
				int index = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
				if(cur.moveToFirst()) {
					if(cur.getInt(index) != DownloadManager.STATUS_FAILED){
						continue;
					}
				}
				else{
					continue;
				}
			}
			Uri deleteURI = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, id);
			cr = mContext.getContentResolver();
			cr.delete(deleteURI, null, null);			
		}
		cursor.close();
	}
	
	public class DownloadProcessingTask extends Thread{
		private Context mContext;
		private TransactionParameter parameters;
		
		public DownloadProcessingTask(Context mContext, TransactionParameter parameters){
			this.mContext = mContext;
			this.parameters = parameters;
		}
		
		@Override 
		public void run(){
			JSONParser jsonParser = new JSONParser();		
			List<NameValuePair> paramsSearch = new ArrayList<NameValuePair>();
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_BOOK_ID, String.valueOf(parameters.getBookItem().getBookID())));
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_USER_ID, parameters.getAccount()));
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_KEY_DEVICE, parameters.getImei()));
			
			BookItemServer item = parameters.getBookItem();
			int successSearch = 0;
			JSONObject json = null;
			if (item.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_FREE || item.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_BACKUP){
				successSearch = 1;
			}
			else{
				json = jsonParser.makeHttpRequest(AESHelper.URLDescript(Constants.url_update_history), "POST", paramsSearch);
			}
			try {
		    	if (json != null || successSearch == 1) {
		    		if (json != null){
			        	successSearch = json.getInt(Constants.PARAM_SUCCESS_005);
		    		}
					if (successSearch == 1) {
						String securityName = EnDeCode.EnCode(String.valueOf(item.getBookID()), parameters.getImei());
						if (securityName == null || securityName.isEmpty()){
							securityName = String.valueOf(item.getBookID());
						}

						BookItemLocal insertItem = new BookItemLocal(item.getBookID(), 
								item.getBookName(), item.getBookAuthor(), item.getBookType(), 
								BookItemLocal.STATUS_DOWNLOADING, null, 
								securityName, -1);
						//check for test only
						StringBuilder selection = new StringBuilder();
						selection.append(LocalBookTableMetaData.BOOK_ID).append(" = ? ");
						String[] selectionArgs = new String[]{
							String.valueOf(insertItem.getBookID()),
						};
						String[] projection = new String[]{
							String.valueOf(LocalBookTableMetaData._ID),
						};
						ContentResolver cr = mContext.getContentResolver();
						int rowID = -1;
						Cursor cursor = cr.query(LocalBookTableMetaData.CONTENT_URI, projection, 
								selection.toString(), selectionArgs, null);
						if (cursor.moveToNext()){
							rowID = cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData._ID));
						}
						cursor.close();
						Bitmap bitmap = parameters.getBitmap();
						ContentValues contentValues = new ContentValues();
						contentValues.put(LocalBookTableMetaData.BOOK_ID, insertItem.getBookID());
						contentValues.put(LocalBookTableMetaData.BOOK_NAME, insertItem.getBookName());
						contentValues.put(LocalBookTableMetaData.BOOK_AUTHOR, insertItem.getBookAuthor());
						contentValues.put(LocalBookTableMetaData.BOOK_TYPE, insertItem.getBookType());
						contentValues.put(LocalBookTableMetaData.STATUS, insertItem.getStatus());
						
						String encryptURL = EnDeCode.EnCode(parameters.getBookItem().getUrlDownload(), parameters.getImei());
						if (encryptURL == null || encryptURL.isEmpty()){
							encryptURL = parameters.getBookItem().getUrlDownload();
						}
						contentValues.put(LocalBookTableMetaData.DOWNLOAD_URI, encryptURL);
						contentValues.put(LocalBookTableMetaData.SECURITY_NAME, insertItem.getSecurityName());
						if (bitmap != null){
							contentValues.put(LocalBookTableMetaData.COVER_IMAGE, BookItemLocal.IMAGE_HAVE);
						}
						else{
							contentValues.put(LocalBookTableMetaData.COVER_IMAGE, BookItemLocal.IMAGE_NO);
						}
						Uri processURI;
						if (rowID < 0){	
							contentValues.put(LocalBookTableMetaData.IS_NEW_BOOK, 1);
							cr = mContext.getContentResolver();
							processURI = cr.insert(LocalBookTableMetaData.CONTENT_URI, contentValues);
							rowID = Integer.valueOf(processURI.getLastPathSegment());
							if (bitmap != null){
								try {
								    // Open an output stream using the new row's URI.
									OutputStream outStream = cr.openOutputStream(processURI);
								    // Compress your bitmap and save it into your provider.
								    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
								}
								catch (FileNotFoundException e) { 
								}
							}
						}
						else{
//							Uri.Builder builder = LocalBookTableMetaData.CONTENT_URI.buildUpon();
//							builder.appendEncodedPath(String.valueOf(rowID));
//							processURI = builder.build();
							processURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, rowID);
							if (bitmap != null){
								try {
								    // Open an output stream using the new row's URI.
									OutputStream outStream = cr.openOutputStream(processURI);
								    // Compress your bitmap and save it into your provider.
								    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
								}
								catch (FileNotFoundException e) { 
								}
							}
						}
						if (rowID > 0 && isNetworkAvailable()){
							DownloadManager downloadmanager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
						    Uri uri = Uri.parse(item.getUrlDownload());
						    DownloadManager.Request request = new Request(uri);
						    request.setDescription(item.getBookName());
						    
							final String destination = ExternalStorage.getSDCacheDir(mContext, Environment.DIRECTORY_DOWNLOADS).toString() + "/" + item.getBookID() + ".zip";
							File file = new File(destination);
							if (file.exists()){
								file.delete();
							}
							final long id =  downloadmanager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE)
					                .setAllowedOverRoaming(false)
					                .setTitle(BookDownloadTitle)
					                .setDescription(item.getBookName())
					                .setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, item.getBookID() + ".zip"));
							ContentValues updatedValues = new ContentValues();
						    updatedValues.put(LocalBookTableMetaData.STATUS, 
						                      String.valueOf(id));
						    String where = null;
						    String whereArgs[] = null;
						    cr.update(processURI, updatedValues, where, whereArgs);
						}
					}
		    	}
			}
			catch (JSONException e) {
			       e.printStackTrace();
			}
		}
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public void startDownloadDictionary(DictionaryItemServer dict){
		new DownloadDictionaryThreads(mContext, dict).start();
	}
	
	public class DownloadDictionaryThreads extends Thread{
		
		private Context mContext;
		private DictionaryItemServer dict;
		
		public DownloadDictionaryThreads(Context mContext, DictionaryItemServer dict) {
			super();
			this.mContext = mContext;
			this.dict = dict;
		}
		
		@Override 
		public void run(){
			StringBuilder selection = new StringBuilder();
			selection.append(DictionaryItem.Columns.DICT_ID).append(" = ? ");
			String[] selectionArgs = new String[]{
				String.valueOf(dict.dictID),
			};
			String[] projection = new String[]{
				String.valueOf(BaseColumns._ID),
			};
			ContentResolver cr = mContext.getContentResolver();
			int rowID = -1;
			Cursor cursor = cr.query(DictionaryItem.CONTENT_URI, projection, selection.toString(), selectionArgs, null);
			if (cursor.moveToNext()){
				rowID = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
			}
			cursor.close();
			
			Uri processURI;
			if (rowID <= 0){	//insert new row
				DictionaryItem item = new DictionaryItem(dict.dictID, dict.title, DictionaryItem.STATUS_DOWNLOADING, dict.url, -1, dict.lang);
				ContentValues contentValues = item.convertToContentValues();
				processURI = cr.insert(DictionaryItem.CONTENT_URI, contentValues);
				rowID = Integer.valueOf(processURI.getLastPathSegment());
			}
			else{	
				processURI = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, rowID);
			}
			
			if (rowID > 0 && isNetworkAvailable()){
				DownloadManager downloadmanager = (DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
			    Uri uri = Uri.parse(dict.url);
			    DownloadManager.Request request = new Request(uri);
			    request.setDescription(dict.title);
			    
				final String destination = ExternalStorage.getSDCacheDir(mContext, Environment.DIRECTORY_DOWNLOADS).toString() + "/" + dict.dictID + ".zip";
				File file = new File(destination);
				if (file.exists()){
					file.delete();
				}
				final long id =  downloadmanager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE)
		                .setAllowedOverRoaming(false)
		                .setTitle(DictionaryDonwloadTitle)
		                .setDescription(dict.title)
		                .setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, dict.dictID + ".zip"));
				ContentValues updatedValues = new ContentValues();
			    updatedValues.put(DictionaryItem.Columns.STATUS, 
			                      String.valueOf(id));
			    String where = null;
			    String whereArgs[] = null;
			    cr.update(processURI, updatedValues, where, whereArgs);
			}
		}
	}
}
