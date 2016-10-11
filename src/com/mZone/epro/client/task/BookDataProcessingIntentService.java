package com.mZone.epro.client.task;

import java.io.File;

import com.mZone.epro.BuildConfig;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.utility.DecompressZip;
import com.mZone.epro.client.utility.ExternalStorage;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.DownloadManager.Request;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class BookDataProcessingIntentService extends IntentService {

	public static final String INTENT_KEY = "PROCESS DATA";
	public static final String INTENT_KEY_PROCESS_DOWNLOADED_DATA = "PROCESS DOWNLOADED DATA";
	public static final String INTENT_KEY_PROCESS_DOWNLOADING_DATA = "PROCESS DOWNLOADING DATA";
	public static final String INTENT_KEY_PROCESS_UNKNOWN_DATA = "PROCESS UNKNOWN DATA";
	
	public static final String UNZIP_FOLDER = "unzipped";
	
	private static final long DEFAULT_STATUS_LONG = -1000;
	
	public BookDataProcessingIntentService() {
		super("BookDataProcessingIntentService");
		
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		String intentKey = intent.getStringExtra(INTENT_KEY);
		int id = intent.getIntExtra(LocalBookTableMetaData._ID, -1);
		int bookID = intent.getIntExtra(LocalBookTableMetaData.BOOK_ID, -1);
		String bookName = intent.getStringExtra(LocalBookTableMetaData.BOOK_NAME);
		String downloadURIString = intent.getStringExtra(LocalBookTableMetaData.DOWNLOAD_URI);
		if (intentKey.equals(INTENT_KEY_PROCESS_DOWNLOADED_DATA)){
			String[] projection = new String[]{
    				String.valueOf(LocalBookTableMetaData.STATUS),
    			};
			Uri checkUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
		    ContentResolver cr = getApplicationContext().getContentResolver();
		    Cursor checkCursor = cr.query(checkUri, projection, null, null, null);
		    if (checkCursor.moveToNext()){
		    	long status = checkCursor.getLong(checkCursor.getColumnIndex(LocalBookTableMetaData.STATUS));
			    if (status == BookItemLocal.STATUS_UNZIP_SUCESSFULL){
			    	return;
			    }
		    }
			if (unzipFile(bookID)){
				if (BuildConfig.DEBUG){
					Log.e("debug", "INTENT_KEY_PROCESS_DOWNLOADED_DATA unzip successfull bookID=" + bookID);
				}
				Uri updateURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
				ContentValues updatedValues = new ContentValues();
			    updatedValues.put(LocalBookTableMetaData.STATUS, 
			                      String.valueOf(BookItemLocal.STATUS_UNZIP_SUCESSFULL));
			    String where = null;
			    String whereArgs[] = null;
			    cr.update(updateURI, updatedValues, where, whereArgs);
			}
			else{
				if (BuildConfig.DEBUG){
					Log.e("debug", "INTENT_KEY_PROCESS_DOWNLOADED_DATA processUnzipFail bookID=" + bookID);
				}
				processUnzipFail(id, downloadURIString, bookName, bookID);
			}
		}
		else if (intentKey.equals(INTENT_KEY_PROCESS_DOWNLOADING_DATA)){
			long status = intent.getLongExtra(LocalBookTableMetaData.STATUS, DEFAULT_STATUS_LONG);
			if (status > 0){
				long downloadID = status;
				DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
				DownloadManager.Query query = new DownloadManager.Query();
		        query.setFilterById(downloadID);
		        Cursor cur = mgr.query(query);
		        int index = cur.getColumnIndex(
		            DownloadManager.COLUMN_STATUS);
		        if(cur.moveToFirst()) {
		        	/*
		            if(cur.getInt(index) == DownloadManager.STATUS_SUCCESSFUL){
		            	if (unzipFile(bookID)){
		    				if (BuildConfig.DEBUG){
		    					Log.e("debug", "INTENT_KEY_PROCESS_DOWNLOADING_DATA unzip successfull bookID=" + bookID);
		    				}
		    				Uri updateURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
		    				ContentValues updatedValues = new ContentValues();
		    			    updatedValues.put(LocalBookTableMetaData.STATUS, 
		    			                      String.valueOf(BookItemLocal.STATUS_UNZIP_SUCESSFULL));
		    			    String where = null;
		    			    String whereArgs[] = null;
		    			    ContentResolver cr = getApplicationContext().getContentResolver();
		    			    cr.update(updateURI, updatedValues, where, whereArgs);
		    			}
		            	else{
		    				if (BuildConfig.DEBUG){
		    					Log.e("debug", "INTENT_KEY_PROCESS_DOWNLOADING_DATA processUnzipFail bookID=" + bookID);
		    				}
		            		processUnzipFail(id, downloadURIString, bookName, bookID);
		            	}
		            }
		            */
		            if (cur.getInt(index) == DownloadManager.STATUS_FAILED && cur.getInt(cur.getColumnIndex(DownloadManager.COLUMN_REASON)) == DownloadManager.ERROR_CANNOT_RESUME){
		            	ContentValues updatedValues = new ContentValues();
	    			    updatedValues.put(LocalBookTableMetaData.STATUS, 
	    			                      String.valueOf(BookItemLocal.STATUS_DOWNLOADING));
	    			    String where = null;
	    			    String whereArgs[] = null;
	    			    ContentResolver cr = getApplicationContext().getContentResolver();
	    			    Uri updateURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
	    			    cr.update(updateURI, updatedValues, where, whereArgs);
	    			    if (isNetworkAvailable()){
			            	Uri downloadURI = Uri.parse(downloadURIString);
			            	DownloadManager.Request request = new Request(downloadURI);
			            	request.setDescription(bookName);
						    final String destination = ExternalStorage.getSDCacheDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + bookID + ".zip";
							File file = new File(destination);
							if (file.exists()){
								file.delete();
							}
							DownloadManager downloadmanager = (DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
							final long redownloadID =  downloadmanager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE)
					                .setAllowedOverRoaming(false)
					                .setTitle(bookName)
					                .setDescription(bookName)
					                .setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, bookID + ".zip"));
							updatedValues = new ContentValues();
		    			    updatedValues.put(LocalBookTableMetaData.STATUS, 
		    			                      String.valueOf(redownloadID));
		    			    cr.update(updateURI, updatedValues, where, whereArgs);
	    			    }
		            }
		            else if (cur.getInt(index) == DownloadManager.STATUS_RUNNING || cur.getInt(index) == DownloadManager.STATUS_PENDING){
		            	
		            }
		            else{
		            }
		            cur.close();
		        }
			}
			else if (status == BookItemLocal.STATUS_DOWNLOADING){
				if (isNetworkAvailable()){
					Uri downloadURI = Uri.parse(downloadURIString);
	            	DownloadManager.Request request = new Request(downloadURI);
	            	request.setDescription(bookName);
				    final String destination = ExternalStorage.getSDCacheDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + bookID + ".zip";
					File file = new File(destination);
					if (file.exists()){
						file.delete();
					}
					DownloadManager downloadmanager = (DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
					final long redownloadID =  downloadmanager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE)
			                .setAllowedOverRoaming(false)
			                .setTitle(bookName)
			                .setDescription(bookName)
			                .setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, bookID + ".zip"));
					ContentValues updatedValues = new ContentValues();
    			    updatedValues.put(LocalBookTableMetaData.STATUS, 
    			                      String.valueOf(redownloadID));
    			    Uri updateURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
    			    ContentResolver cr = getApplicationContext().getContentResolver();
    			    cr.update(updateURI, updatedValues, null, null);
				}
			}
		}
	}
	
	private void processUnzipFail(int id, String downloadURIString, String bookName, int bookID){
		Uri updateURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
	    ContentResolver cr = getApplicationContext().getContentResolver();
	    cr.delete(updateURI, null, null);
	    String toastMessage = bookName + " " + getResources().getString(R.string.book_download_error_toast_message);
	    Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
	    
	    /*
		ContentValues updatedValues = new ContentValues();
	    updatedValues.put(LocalBookTableMetaData.STATUS, 
	                      String.valueOf(BookItemLocal.STATUS_DOWNLOADING));
	    String where = null;
	    String whereArgs[] = null;
	    cr.update(updateURI, updatedValues, where, whereArgs);
	    
	    if (isNetworkAvailable()){
			Uri downloadURI = Uri.parse(downloadURIString);
        	DownloadManager.Request request = new Request(downloadURI);
        	request.setDescription(bookName);
		    final String destination = ExternalStorage.getSDCacheDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + bookID + ".zip";
			File file = new File(destination);
			if (file.exists()){
				file.delete();
			}
			DownloadManager downloadmanager = (DownloadManager)getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
			final long redownloadID =  downloadmanager.enqueue(request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |DownloadManager.Request.NETWORK_MOBILE)
	                .setAllowedOverRoaming(false)
	                .setTitle(bookName)
	                .setDescription(bookName)
	                .setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, bookID + ".zip"));
			updatedValues = new ContentValues();
		    updatedValues.put(LocalBookTableMetaData.STATUS, 
		                      String.valueOf(redownloadID));
		    updateURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
		    cr = getApplicationContext().getContentResolver();
		    cr.update(updateURI, updatedValues, null, null);
		}
		*/
	}
	
	private boolean unzipFile( int bookID ) {
		final String source = ExternalStorage.getSDCacheDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + bookID + ".zip";
		File sourceZip = new File(source);
		if (sourceZip.exists()){
			File outputDir = ExternalStorage.getSDCacheDir( getApplicationContext(), "unzipped" );
			DecompressZip decomp = new DecompressZip( sourceZip.getPath(), outputDir.getPath() + File.separator );
			if (decomp.unzip()){
				sourceZip.delete();
				return true;
			}
			else{
				if (BuildConfig.DEBUG){
					Log.e("debug", "unzipFile fail and start to delete file");
				}
				sourceZip.delete();
				deleteRecursive(outputDir);
			}
		}
		return false;
	}
	
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory()){
	    	for (File child : fileOrDirectory.listFiles()){
	        	 deleteRecursive(child);
	        }
	    }
	    if (fileOrDirectory.exists())
	    	fileOrDirectory.delete();
	}
}
