package com.mZone.epro.client.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.data.BookProviderMetaData.TestHistoryTableMetaData;
import com.mZone.epro.client.utility.ExternalStorage;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

public class BookProvider extends ContentProvider {

	private static final String TAG = "BookProvider";
	public static final String IMAGE_FOLDER = "book image";

	//local item projection map
	public static HashMap<String, String> localBooksProjectionMap;	
	static{
		localBooksProjectionMap = new HashMap<String, String>();
		localBooksProjectionMap.put(LocalBookTableMetaData._ID, LocalBookTableMetaData._ID);
		localBooksProjectionMap.put(LocalBookTableMetaData.BOOK_ID, LocalBookTableMetaData.BOOK_ID);
		localBooksProjectionMap.put(LocalBookTableMetaData.BOOK_NAME, LocalBookTableMetaData.BOOK_NAME);
		localBooksProjectionMap.put(LocalBookTableMetaData.BOOK_AUTHOR, LocalBookTableMetaData.BOOK_AUTHOR);
		localBooksProjectionMap.put(LocalBookTableMetaData.BOOK_TYPE, LocalBookTableMetaData.BOOK_TYPE);
		localBooksProjectionMap.put(LocalBookTableMetaData.STATUS, LocalBookTableMetaData.STATUS);
		localBooksProjectionMap.put(LocalBookTableMetaData.DOWNLOAD_URI, LocalBookTableMetaData.DOWNLOAD_URI);
		localBooksProjectionMap.put(LocalBookTableMetaData.SECURITY_NAME, LocalBookTableMetaData.SECURITY_NAME);
		localBooksProjectionMap.put(LocalBookTableMetaData.COVER_IMAGE, LocalBookTableMetaData.COVER_IMAGE);
		localBooksProjectionMap.put(LocalBookTableMetaData.CREATE_DATE, LocalBookTableMetaData.CREATE_DATE);
		localBooksProjectionMap.put(LocalBookTableMetaData.MODIFIED_DATE, LocalBookTableMetaData.MODIFIED_DATE);
		localBooksProjectionMap.put(LocalBookTableMetaData.IS_NEW_BOOK, LocalBookTableMetaData.IS_NEW_BOOK);
	}
	
	public static HashMap<String, String> testHistoryProjectionMap;	
	static{
		testHistoryProjectionMap = new HashMap<String, String>();
		testHistoryProjectionMap.put(TestHistoryTableMetaData._ID, TestHistoryTableMetaData._ID);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.BOOK_ID, TestHistoryTableMetaData.BOOK_ID);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.STATUS, TestHistoryTableMetaData.STATUS);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.CREATE_DATE, TestHistoryTableMetaData.CREATE_DATE);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.MODIFIED_DATE, TestHistoryTableMetaData.MODIFIED_DATE);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.FILE_PATH, TestHistoryTableMetaData.FILE_PATH);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.MODE, TestHistoryTableMetaData.MODE);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART1, TestHistoryTableMetaData.SCORE_PART1);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART2, TestHistoryTableMetaData.SCORE_PART2);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART3, TestHistoryTableMetaData.SCORE_PART3);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART4, TestHistoryTableMetaData.SCORE_PART4);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART5, TestHistoryTableMetaData.SCORE_PART5);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART6, TestHistoryTableMetaData.SCORE_PART6);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.SCORE_PART7, TestHistoryTableMetaData.SCORE_PART7);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.CURRENT_TIME, TestHistoryTableMetaData.CURRENT_TIME);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.CURRENT_READING_TIME, TestHistoryTableMetaData.CURRENT_READING_TIME);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.ACTIVE_PART, TestHistoryTableMetaData.ACTIVE_PART);
		testHistoryProjectionMap.put(TestHistoryTableMetaData.MEDIA_CURRENT_TIME, TestHistoryTableMetaData.MEDIA_CURRENT_TIME);
	}
	
	public static HashMap<String, String> testHistoryProjectionMapExtend;
	
	static{
		testHistoryProjectionMapExtend = new HashMap<String, String>();
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData._ID, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData._ID + " AS " + TestHistoryTableMetaData._ID);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.BOOK_ID, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.BOOK_ID + " AS " + TestHistoryTableMetaData.BOOK_ID);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.STATUS, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.STATUS + " AS " + TestHistoryTableMetaData.STATUS);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.CREATE_DATE, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.CREATE_DATE + " AS " + TestHistoryTableMetaData.CREATE_DATE);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.MODIFIED_DATE, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.MODIFIED_DATE + " AS " + TestHistoryTableMetaData.MODIFIED_DATE);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.FILE_PATH, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.FILE_PATH + " AS " + TestHistoryTableMetaData.FILE_PATH);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.MODE, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.MODE + " AS " + TestHistoryTableMetaData.MODE);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART1, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART1 + " AS " + TestHistoryTableMetaData.SCORE_PART1);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART2, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART2 + " AS " + TestHistoryTableMetaData.SCORE_PART2);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART3, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART3 + " AS " + TestHistoryTableMetaData.SCORE_PART3);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART4, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART4 + " AS " + TestHistoryTableMetaData.SCORE_PART4);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART5, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART5 + " AS " + TestHistoryTableMetaData.SCORE_PART5);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART6, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART6 + " AS " + TestHistoryTableMetaData.SCORE_PART6);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.SCORE_PART7, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.SCORE_PART7 + " AS " + TestHistoryTableMetaData.SCORE_PART7);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.CURRENT_TIME, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.CURRENT_TIME + " AS " + TestHistoryTableMetaData.CURRENT_TIME);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.CURRENT_READING_TIME, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.CURRENT_READING_TIME + " AS " + TestHistoryTableMetaData.CURRENT_READING_TIME);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.ACTIVE_PART, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.ACTIVE_PART + " AS " + TestHistoryTableMetaData.ACTIVE_PART);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.MEDIA_CURRENT_TIME, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "." + TestHistoryTableMetaData.MEDIA_CURRENT_TIME + " AS " + TestHistoryTableMetaData.MEDIA_CURRENT_TIME);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.EXTENSION_BOOK_ROW_ID, BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + "." + LocalBookTableMetaData._ID + " AS " + TestHistoryTableMetaData.EXTENSION_BOOK_ROW_ID);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.EXTENSION_BOOK_NAME, BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + "." + LocalBookTableMetaData.BOOK_NAME + " AS " + TestHistoryTableMetaData.EXTENSION_BOOK_NAME);
		testHistoryProjectionMapExtend.put(TestHistoryTableMetaData.EXTENSION_COVER_IMAGE, BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + "." + LocalBookTableMetaData.COVER_IMAGE + " AS " + TestHistoryTableMetaData.EXTENSION_COVER_IMAGE);
	}
	
	private static final UriMatcher mUriMatcher;
	private static final int LOCAL_BOOK_ARRAY_REQUEST = 1;
	private static final int LOCAL_BOOK_ITEM_REQUEST = 2;
	private static final int TEST_HISTORY_ARRAY_REQUEST = 3;
	private static final int TEST_HISTORY_ITEM_REQUEST = 4;
	private static final int TEST_HISTORY_ARRAY_REQUEST_EXTENSION = 5;
	static{
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(BookProviderMetaData.AUTHORITY, BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME, 
				LOCAL_BOOK_ARRAY_REQUEST);
		mUriMatcher.addURI(BookProviderMetaData.AUTHORITY, BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + "/#", 
				LOCAL_BOOK_ITEM_REQUEST);
		mUriMatcher.addURI(BookProviderMetaData.AUTHORITY, BookProviderMetaData.TEST_HISTORY_TABLE_NAME, 
				TEST_HISTORY_ARRAY_REQUEST);
		mUriMatcher.addURI(BookProviderMetaData.AUTHORITY, BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "/#", 
				TEST_HISTORY_ITEM_REQUEST);
		mUriMatcher.addURI(BookProviderMetaData.AUTHORITY, BookProviderMetaData.TEST_HISTORY_TABLE_NAME_EXTENSION, 
				TEST_HISTORY_ARRAY_REQUEST_EXTENSION);
	}
	
	private static class DataBaseHelper extends SQLiteOpenHelper{
		private static final String PRIMARY_KEY = " INTEGER PRIMARY KEY AUTOINCREMENT";
		private static final String INTEGER_NOT_NULL = " INTEGER NOT NULL";
		private static final String TEXT_NOT_NULL = " TEXT NOT NULL";
//		private static final String INTEGER_CAN_NULL = " INTEGER";
		private static final String TEXT_CAN_NULL = " TEXT";
		private static final String LONG_NOT_NULL = " LONG NOT NULL";
		private static final String LONG_CAN_NULL = " LONG";
		public DataBaseHelper(Context context) {
			super(context, BookProviderMetaData.DATABASE_NAME, null, BookProviderMetaData.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String dbExecCmd = "CREATE TABLE IF NOT EXISTS " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + " ( "
								+ LocalBookTableMetaData._ID + PRIMARY_KEY + ", "
								+ LocalBookTableMetaData.BOOK_ID + INTEGER_NOT_NULL + ", "
								+ LocalBookTableMetaData.BOOK_NAME + TEXT_NOT_NULL + ", "
								+ LocalBookTableMetaData.BOOK_AUTHOR + TEXT_CAN_NULL + ", "
								+ LocalBookTableMetaData.BOOK_TYPE + INTEGER_NOT_NULL + ", "
								+ LocalBookTableMetaData.STATUS + LONG_NOT_NULL + ", "
								+ LocalBookTableMetaData.DOWNLOAD_URI + TEXT_CAN_NULL + ", "
								+ LocalBookTableMetaData.SECURITY_NAME + TEXT_NOT_NULL + ", "
								+ LocalBookTableMetaData.COVER_IMAGE + TEXT_CAN_NULL + ", "
								+ LocalBookTableMetaData.CREATE_DATE + LONG_NOT_NULL + ", "
								+ LocalBookTableMetaData.MODIFIED_DATE + LONG_CAN_NULL + ", "
								+ LocalBookTableMetaData.IS_NEW_BOOK + INTEGER_NOT_NULL + ");";  
			db.execSQL(dbExecCmd);
			dbExecCmd = "CREATE TABLE IF NOT EXISTS " + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + " ( "
								+ TestHistoryTableMetaData._ID + PRIMARY_KEY + ", "
								+ TestHistoryTableMetaData.BOOK_ID + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.STATUS + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.CREATE_DATE + LONG_NOT_NULL + ", "
								+ TestHistoryTableMetaData.MODIFIED_DATE + LONG_NOT_NULL + ", "
								+ TestHistoryTableMetaData.FILE_PATH + TEXT_NOT_NULL + ", "
								+ TestHistoryTableMetaData.MODE + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART1 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART2 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART3 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART4 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART5 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART6 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.SCORE_PART7 + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.CURRENT_TIME + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.CURRENT_READING_TIME + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.ACTIVE_PART + INTEGER_NOT_NULL + ", "
								+ TestHistoryTableMetaData.MEDIA_CURRENT_TIME + LONG_NOT_NULL + ");";  
			db.execSQL(dbExecCmd);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion < newVersion){
				db.beginTransaction();
				try {
					String dbExecCmd = "ALTER TABLE " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + " RENAME TO " + "tmp_" + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + ";";
					db.execSQL(dbExecCmd);
					
					dbExecCmd = "CREATE TABLE IF NOT EXISTS " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + " ( "
							+ LocalBookTableMetaData._ID + PRIMARY_KEY + ", "
							+ LocalBookTableMetaData.BOOK_ID + INTEGER_NOT_NULL + ", "
							+ LocalBookTableMetaData.BOOK_NAME + TEXT_NOT_NULL + ", "
							+ LocalBookTableMetaData.BOOK_AUTHOR + TEXT_CAN_NULL + ", "
							+ LocalBookTableMetaData.BOOK_TYPE + INTEGER_NOT_NULL + ", "
							+ LocalBookTableMetaData.STATUS + LONG_NOT_NULL + ", "
							+ LocalBookTableMetaData.DOWNLOAD_URI + TEXT_CAN_NULL + ", "
							+ LocalBookTableMetaData.SECURITY_NAME + TEXT_NOT_NULL + ", "
							+ LocalBookTableMetaData.COVER_IMAGE + TEXT_CAN_NULL + ", "
							+ LocalBookTableMetaData.CREATE_DATE + LONG_NOT_NULL + ", "
							+ LocalBookTableMetaData.MODIFIED_DATE + LONG_CAN_NULL + ", "
							+ LocalBookTableMetaData.IS_NEW_BOOK + INTEGER_NOT_NULL + ");";  

					db.execSQL(dbExecCmd);
					
					dbExecCmd = "INSERT INTO " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + "(" 
							+ LocalBookTableMetaData._ID + ", "
							+ LocalBookTableMetaData.BOOK_ID + ", "
							+ LocalBookTableMetaData.BOOK_NAME + ", "
							+ LocalBookTableMetaData.BOOK_AUTHOR + ", "
							+ LocalBookTableMetaData.BOOK_TYPE + ", "
							+ LocalBookTableMetaData.STATUS + ", "
							+ LocalBookTableMetaData.DOWNLOAD_URI + ", "
							+ LocalBookTableMetaData.SECURITY_NAME + ", "
							+ LocalBookTableMetaData.COVER_IMAGE + ", "
							+ LocalBookTableMetaData.CREATE_DATE + ", "
							+ LocalBookTableMetaData.MODIFIED_DATE + ", "
							+ LocalBookTableMetaData.IS_NEW_BOOK + ") "
							+ "SELECT id, "
							+ LocalBookTableMetaData.BOOK_ID+ ", "
							+ LocalBookTableMetaData.BOOK_NAME + ", "
							+ LocalBookTableMetaData.BOOK_AUTHOR + ", "
							+ LocalBookTableMetaData.BOOK_TYPE + ", "
							+ LocalBookTableMetaData.STATUS + ", "
							+ LocalBookTableMetaData.DOWNLOAD_URI + ", "
							+ LocalBookTableMetaData.SECURITY_NAME + ", "
							+ LocalBookTableMetaData.COVER_IMAGE + ", "
							+ LocalBookTableMetaData.CREATE_DATE + ", "
							+ LocalBookTableMetaData.MODIFIED_DATE + ", "
							+ "0 "
							+ "FROM " + "tmp_" + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + ";";
					db.execSQL(dbExecCmd);
					
					dbExecCmd = "DROP TABLE tmp_" + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + ";";
					db.execSQL(dbExecCmd);
					
					dbExecCmd = "ALTER TABLE " + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + " RENAME TO " + "tmp_" + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + ";";
					db.execSQL(dbExecCmd);
					
					dbExecCmd = "CREATE TABLE IF NOT EXISTS " + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + " ( "
							+ TestHistoryTableMetaData._ID + PRIMARY_KEY + ", "
							+ TestHistoryTableMetaData.BOOK_ID + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.STATUS + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.CREATE_DATE + LONG_NOT_NULL + ", "
							+ TestHistoryTableMetaData.MODIFIED_DATE + LONG_NOT_NULL + ", "
							+ TestHistoryTableMetaData.FILE_PATH + TEXT_NOT_NULL + ", "
							+ TestHistoryTableMetaData.MODE + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART1 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART2 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART3 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART4 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART5 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART6 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.SCORE_PART7 + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.CURRENT_TIME + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.CURRENT_READING_TIME + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.ACTIVE_PART + INTEGER_NOT_NULL + ", "
							+ TestHistoryTableMetaData.MEDIA_CURRENT_TIME + LONG_NOT_NULL + ");";  

					db.execSQL(dbExecCmd);
					
					dbExecCmd = "INSERT INTO " + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + "(" 
							+ TestHistoryTableMetaData._ID + ", "
							+ TestHistoryTableMetaData.BOOK_ID + ", "
							+ TestHistoryTableMetaData.STATUS + ", "
							+ TestHistoryTableMetaData.CREATE_DATE + ", "
							+ TestHistoryTableMetaData.MODIFIED_DATE + ", "
							+ TestHistoryTableMetaData.FILE_PATH + ", "
							+ TestHistoryTableMetaData.MODE + ", "
							+ TestHistoryTableMetaData.SCORE_PART1 + ", "
							+ TestHistoryTableMetaData.SCORE_PART2 + ", "
							+ TestHistoryTableMetaData.SCORE_PART3 + ", "
							+ TestHistoryTableMetaData.SCORE_PART4 + ", "
							+ TestHistoryTableMetaData.SCORE_PART5 +  ", "
							+ TestHistoryTableMetaData.SCORE_PART6 + ", "
							+ TestHistoryTableMetaData.SCORE_PART7 + ", "
							+ TestHistoryTableMetaData.CURRENT_TIME + ", "
							+ TestHistoryTableMetaData.CURRENT_READING_TIME + ", "
							+ TestHistoryTableMetaData.ACTIVE_PART + ", "
							+ TestHistoryTableMetaData.MEDIA_CURRENT_TIME + ") "
							+ "SELECT id, "
							+ TestHistoryTableMetaData.BOOK_ID + ", "
							+ TestHistoryTableMetaData.STATUS + ", "
							+ TestHistoryTableMetaData.CREATE_DATE + ", "
							+ TestHistoryTableMetaData.MODIFIED_DATE + ", "
							+ TestHistoryTableMetaData.FILE_PATH + ", "
							+ TestHistoryTableMetaData.MODE + ", "
							+ TestHistoryTableMetaData.SCORE_PART1 + ", "
							+ TestHistoryTableMetaData.SCORE_PART2 + ", "
							+ TestHistoryTableMetaData.SCORE_PART3 + ", "
							+ TestHistoryTableMetaData.SCORE_PART4 + ", "
							+ TestHistoryTableMetaData.SCORE_PART5 +  ", "
							+ TestHistoryTableMetaData.SCORE_PART6 + ", "
							+ TestHistoryTableMetaData.SCORE_PART7 + ", "
							+ TestHistoryTableMetaData.CURRENT_TIME + ", "
							+ TestHistoryTableMetaData.CURRENT_READING_TIME + ", "
							+ TestHistoryTableMetaData.ACTIVE_PART + ", "
							+ TestHistoryTableMetaData.MEDIA_CURRENT_TIME + " "
							+ "FROM " + "tmp_" + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + ";";
					db.execSQL(dbExecCmd);
					
					dbExecCmd = "DROP TABLE tmp_" + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + ";";
					db.execSQL(dbExecCmd);
					
					db.setTransactionSuccessful();
				} finally {
					db.endTransaction();
				}
			}
		}
	}
	
	private DataBaseHelper dbHelper;
	
	public BookProvider() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		dbHelper = new DataBaseHelper(getContext());
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		switch (mUriMatcher.match(uri)) {
			case LOCAL_BOOK_ARRAY_REQUEST:
				return LocalBookTableMetaData.CONTENT_TYPE;
			case LOCAL_BOOK_ITEM_REQUEST:
				return LocalBookTableMetaData.CONTENT_ITEM_TYPE;
			case TEST_HISTORY_ARRAY_REQUEST:
				return TestHistoryTableMetaData.CONTENT_TYPE;
			case TEST_HISTORY_ITEM_REQUEST:
				return TestHistoryTableMetaData.CONTENT_ITEM_TYPE;
			case TEST_HISTORY_ARRAY_REQUEST_EXTENSION:
				return TestHistoryTableMetaData.CONTENT_ITEM_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	synchronized public Uri insert(Uri uri, ContentValues contentValues) {
		// TODO Auto-generated method stub.
		if (mUriMatcher.match(uri) == LOCAL_BOOK_ARRAY_REQUEST){
			ContentValues values;
			if (contentValues != null){
				values = new ContentValues(contentValues);
			}
			else{
				values = new ContentValues();
			}
			Long now = Long.valueOf(System.currentTimeMillis());
			values.put(LocalBookTableMetaData.CREATE_DATE, now);
			values.put(LocalBookTableMetaData.MODIFIED_DATE, now);
			
			//check contentValues
			if (values.containsKey(LocalBookTableMetaData.BOOK_ID) == false){
				throw new SQLException(LocalBookTableMetaData.BOOK_ID + " is null " + uri);
			}
			if (values.containsKey(LocalBookTableMetaData.BOOK_NAME) == false){
				throw new SQLException(LocalBookTableMetaData.BOOK_NAME + " is null " + uri);
			}
			if (values.containsKey(LocalBookTableMetaData.BOOK_TYPE) == false){
				throw new SQLException(LocalBookTableMetaData.BOOK_TYPE + " is null " + uri);
			}
			if (values.containsKey(LocalBookTableMetaData.STATUS) == false){
				throw new SQLException(LocalBookTableMetaData.STATUS + " is null " + uri);
			}
			if (values.containsKey(LocalBookTableMetaData.SECURITY_NAME) == false){
				throw new SQLException(LocalBookTableMetaData.SECURITY_NAME + " is null " + uri);
			}
			if (values.containsKey(LocalBookTableMetaData.BOOK_AUTHOR) == false){
				values.put(LocalBookTableMetaData.BOOK_AUTHOR, LocalBookTableMetaData.UNKNOWN_AUTHOR);
			}
			
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			long rowID = db.insert(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME, LocalBookTableMetaData.BOOK_ID, values);
			if (rowID > 0){
				Uri insertedBookUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, rowID);
//				getContext().getContentResolver().notifyChange(insertedBookUri, null);
				return insertedBookUri;
			}
			throw new SQLException("FAIL TO INSERT TO " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME + " at uri " + uri);
		}
		else if (mUriMatcher.match(uri) == TEST_HISTORY_ARRAY_REQUEST){
			ContentValues values;
			if (contentValues != null){
				values = new ContentValues(contentValues);
			}
			else{
				values = new ContentValues();
			}
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			long rowID = db.insert(BookProviderMetaData.TEST_HISTORY_TABLE_NAME, null, values);
			if (rowID > 0){
				Uri insertedHistoryUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, rowID);
				return insertedHistoryUri;
			}
			throw new SQLException("FAIL TO INSERT TO " + BookProviderMetaData.TEST_HISTORY_TABLE_NAME + " at uri " + uri);
		}
		return null;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		int count = 0;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (mUriMatcher.match(uri)) {
			case LOCAL_BOOK_ARRAY_REQUEST:
				count = db.delete(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME, where, whereArgs);
				break;
			case LOCAL_BOOK_ITEM_REQUEST:
				String rowID = uri.getPathSegments().get(1);
				count = db.delete(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME, 
						LocalBookTableMetaData._ID + "=" + rowID + 
						(!TextUtils.isEmpty(where)? " AND (" + where + ")" : ""), whereArgs);
				break;
			case TEST_HISTORY_ARRAY_REQUEST:
				count = db.delete(BookProviderMetaData.TEST_HISTORY_TABLE_NAME, where, whereArgs);
				break;
			case TEST_HISTORY_ITEM_REQUEST:
				rowID = uri.getPathSegments().get(1);
				count = db.delete(BookProviderMetaData.TEST_HISTORY_TABLE_NAME, 
						TestHistoryTableMetaData._ID + "=" + rowID + 
						(!TextUtils.isEmpty(where)? " AND (" + where + ")" : ""), whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		// TODO Auto-generated method stub
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		switch (mUriMatcher.match(uri)) {
			case LOCAL_BOOK_ARRAY_REQUEST:
				qb.setTables(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME);
				qb.setProjectionMap(localBooksProjectionMap);
				break;
			case LOCAL_BOOK_ITEM_REQUEST:
				qb.setTables(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME);
				qb.setProjectionMap(localBooksProjectionMap);
				qb.appendWhere(LocalBookTableMetaData._ID + "=" + uri.getPathSegments().get(1));
				break;	
			case TEST_HISTORY_ARRAY_REQUEST:
				qb.setTables(BookProviderMetaData.TEST_HISTORY_TABLE_NAME);
				qb.setProjectionMap(testHistoryProjectionMap);
				break;
			case TEST_HISTORY_ITEM_REQUEST:
				qb.setTables(BookProviderMetaData.TEST_HISTORY_TABLE_NAME);
				qb.setProjectionMap(testHistoryProjectionMap);
				qb.appendWhere(TestHistoryTableMetaData._ID + "=" + uri.getPathSegments().get(1));
				break;
			case TEST_HISTORY_ARRAY_REQUEST_EXTENSION:
				qb.setTables(BookProviderMetaData.TEST_HISTORY_TABLE_NAME + " INNER JOIN " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME
						+ " ON " 
						+ BookProviderMetaData.TEST_HISTORY_TABLE_NAME+"."+TestHistoryTableMetaData.BOOK_ID
						+ " = " + BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME+"."+LocalBookTableMetaData.BOOK_ID);
				qb.setProjectionMap(testHistoryProjectionMapExtend);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)){
			if (mUriMatcher.match(uri) == LOCAL_BOOK_ARRAY_REQUEST || mUriMatcher.match(uri) == LOCAL_BOOK_ITEM_REQUEST){
				orderBy = LocalBookTableMetaData.DEFAULT_SORT_ORDER;
			}
			else if (mUriMatcher.match(uri) == TEST_HISTORY_ARRAY_REQUEST || mUriMatcher.match(uri) == TEST_HISTORY_ITEM_REQUEST){
				orderBy = TestHistoryTableMetaData.DEFAULT_SORT_ORDER;
			}
			else if (mUriMatcher.match(uri) == TEST_HISTORY_ARRAY_REQUEST_EXTENSION){
				orderBy = TestHistoryTableMetaData.DEFAULT_SORT_ORDER;
			}
			else{
				orderBy = null;
			}
		}
		else{
			orderBy = sortOrder;
		}
		String groupBy = null;
	    String having = null;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, groupBy, having, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String where, String[] whereArgs) {
		// TODO Auto-generated method stub
		int count = 0;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		switch (mUriMatcher.match(uri)) {
			case LOCAL_BOOK_ARRAY_REQUEST:
				ContentValues values = new ContentValues(contentValues);
				Long now = Long.valueOf(System.currentTimeMillis());
				values.put(LocalBookTableMetaData.MODIFIED_DATE, now);
				count = db.update(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME, values, where, whereArgs);
				break;
			case LOCAL_BOOK_ITEM_REQUEST:
				values = new ContentValues(contentValues);
				now = Long.valueOf(System.currentTimeMillis());
				values.put(LocalBookTableMetaData.MODIFIED_DATE, now);
				String rowID = uri.getPathSegments().get(1);
				count = db.update(BookProviderMetaData.LOCAL_BOOKS_TABLE_NAME, values, 
						LocalBookTableMetaData._ID + "=" + rowID + 
						(!TextUtils.isEmpty(where)? " AND (" + where + ")" : ""), whereArgs);
				break;
			case TEST_HISTORY_ARRAY_REQUEST:
				count = db.update(BookProviderMetaData.TEST_HISTORY_TABLE_NAME, contentValues, where, whereArgs);
				break;
			case TEST_HISTORY_ITEM_REQUEST:
				rowID = uri.getPathSegments().get(1);
				count = db.update(BookProviderMetaData.TEST_HISTORY_TABLE_NAME, contentValues, 
						LocalBookTableMetaData._ID + "=" + rowID + 
						(!TextUtils.isEmpty(where)? " AND (" + where + ")" : ""), whereArgs);
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		if (mUriMatcher.match(uri) == LOCAL_BOOK_ARRAY_REQUEST || mUriMatcher.match(uri) == LOCAL_BOOK_ITEM_REQUEST){
			long status = contentValues.getAsLong(LocalBookTableMetaData.STATUS);
			if (status == BookItemLocal.STATUS_UNZIP_SUCESSFULL){
				getContext().getContentResolver().notifyChange(uri, null);
			}
			else if (status > 0){
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}
		else if (mUriMatcher.match(uri) == TEST_HISTORY_ARRAY_REQUEST || mUriMatcher.match(uri) == TEST_HISTORY_ITEM_REQUEST){
			getContext().getContentResolver().notifyChange(TestHistoryTableMetaData.CONTENT_URI_EXTENSION, null);
		}
			
		return count;
	}
	
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) 
	    throws FileNotFoundException {
	    // Find the row ID and use it as a filename.
	    String rowID = uri.getPathSegments().get(1);
	    
	    // Create a file object in the application's external 
	    // files directory.
	    String picsDir = ExternalStorage.getSDCacheDir(getContext(), "book image").toString() + "/" + rowID;
	    File file = 
	      new File(picsDir);
	    
	    // If the file doesn't exist, create it now.
	    if (!file.exists()) {
	      try {
	        file.createNewFile();
	      } catch (IOException e) {
	        Log.d(TAG, "File creation failed: " + e.getMessage());
	      }
	    }
	    
	    // Translate the mode parameter to the corresponding Parcel File
	    // Descriptor open mode.
	    int fileMode = 0;  
	    if (mode.contains("w"))
	      fileMode |= ParcelFileDescriptor.MODE_WRITE_ONLY;
	    if (mode.contains("r")) 
	      fileMode |= ParcelFileDescriptor.MODE_READ_ONLY;
	    if (mode.contains("+")) 
	      fileMode |= ParcelFileDescriptor.MODE_APPEND;     

	    // Return a Parcel File Descriptor that represents the file.
	    return ParcelFileDescriptor.open(file, fileMode);
	}
}
