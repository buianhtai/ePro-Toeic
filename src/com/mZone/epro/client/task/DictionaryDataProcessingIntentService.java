package com.mZone.epro.client.task;

import java.io.File;

import com.mZone.epro.client.utility.DecompressZip;
import com.mZone.epro.client.utility.ExternalStorage;
import com.mZone.epro.dict.data.DictionaryItem;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;

public class DictionaryDataProcessingIntentService extends IntentService {

	public static final String INTENT_KEY = "DictionaryDataProcessingIntentService.INTENT_KEY";
	public static final String INTENT_KEY_PROCESS_DOWNLOADED_DATA = "DictionaryDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADED_DATA";
	public static final String INTENT_KEY_PROCESS_DOWNLOADING_DATA = "DictionaryDataProcessingIntentService.INTENT_KEY_PROCESS_DOWNLOADING_DATA";
	public static final String INTENT_KEY_PROCESS_UNKNOWN_DATA = "DictionaryDataProcessingIntentService.INTENT_KEY_PROCESS_UNKNOWN_DATA";

	public DictionaryDataProcessingIntentService(){
		super("DictionaryDataProcessingIntentService");
	}
	
	public DictionaryDataProcessingIntentService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String intentKey = intent.getStringExtra(INTENT_KEY);
		int id = intent.getIntExtra(BaseColumns._ID, -1);
		String dictID = intent.getStringExtra(DictionaryItem.Columns.DICT_ID);
		if (INTENT_KEY_PROCESS_DOWNLOADED_DATA.equals(intentKey)){
			Uri updateURI = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, id);
			ContentValues updatedValues = new ContentValues();
		    updatedValues.put(DictionaryItem.Columns.STATUS, 
		                      String.valueOf(DictionaryItem.STATUS_DOWNLOAD_SUCESSFULL));
		    String where = null;
		    String whereArgs[] = null;
		    ContentResolver cr = getContentResolver();
		    cr.update(updateURI, updatedValues, where, whereArgs);
		    if (unzipFile(dictID)){
		    	updatedValues.put(DictionaryItem.Columns.STATUS, 
	                      String.valueOf(DictionaryItem.STATUS_UNZIP_SUCESSFULL));
		    	cr.update(updateURI, updatedValues, where, whereArgs);
		    }
		}
	}
	
	private boolean unzipFile( String dictID ) {
		final String source = ExternalStorage.getSDCacheDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + dictID + ".zip";
		File sourceZip = new File(source);
		if (sourceZip.exists()){
			File outputDir = ExternalStorage.getSDCacheDir( getApplicationContext(), "dictionary" );
			DecompressZip decomp = new DecompressZip( sourceZip.getPath(), 
					outputDir.getPath() + File.separator );
			if (decomp.unzip()){
				sourceZip.delete();
				return true;
			}
			else{
			}
		}
		return false;
	}

}
