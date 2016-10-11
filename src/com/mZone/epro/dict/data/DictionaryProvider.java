package com.mZone.epro.dict.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class DictionaryProvider extends ContentProvider {

	public static final String AUTHORITY = "com.mZone.epro.dict.data.DictionaryProvider";
	private DictionarySQliteOpenHelper mOpenHelper = null;
	private SQLiteDatabase mDb = null;
	
	private static final int URI_TABLE = 1;
	private static final int URI_SIGNLE_ROW = 2;
	
	private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static {
		sURIMatcher.addURI(AUTHORITY, DictionaryItem.TABLE_NAME, URI_TABLE);
		sURIMatcher.addURI(AUTHORITY, DictionaryItem.TABLE_NAME + "/#", URI_SIGNLE_ROW);
	}
	
	@Override
	public boolean onCreate() {
		boolean success = false;
		mOpenHelper = new DictionarySQliteOpenHelper(getContext());
		mOpenHelper.getWritableDatabase();
		success = (mOpenHelper != null);
		return success;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (sURIMatcher.match(uri)) {
			case URI_TABLE:
				return DictionaryItem.CONTENT_TYPE;
			case URI_SIGNLE_ROW:
				return DictionaryItem.CONTENT_TYPE;
			default:
				throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor = null;
		
		switch (sURIMatcher.match(uri)) {
			case URI_TABLE:
				cursor = queryDictionaryTable(uri, projection, selection, selectionArgs, sortOrder);
				break;
			case URI_SIGNLE_ROW:
				cursor = queryDictionaryItem(uri, projection, selection, selectionArgs, sortOrder);
				break;
			default:
				break;
		}

		if (cursor != null) {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
	}
	
	private Cursor queryDictionaryTable(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		mDb = mOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DictionaryItem.TABLE_NAME);
		if (TextUtils.isEmpty(sortOrder)){
			sortOrder = DictionaryItem.DEFAULT_SORT_ORDER;
		}
		Cursor cursor = queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}
	
	private Cursor queryDictionaryItem(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
		mDb = mOpenHelper.getWritableDatabase();
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(DictionaryItem.TABLE_NAME);
		String rowID = uri.getLastPathSegment();
		queryBuilder.appendWhere(String.format(" %s = %s ", BaseColumns._ID, rowID));
		Cursor cursor = queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri result = null;
		switch (sURIMatcher.match(uri)) {
			case URI_TABLE:
				result = insertDictionaryItem(uri, values);
				break;
			default:
				throw new IllegalArgumentException();
		}
		if (result != null) {
//			getContext().getContentResolver().notifyChange(uri, null);
		}
		return result;
	}
	
	private Uri insertDictionaryItem(Uri uri, ContentValues values){
		Uri result = null;
		mDb = mOpenHelper.getWritableDatabase();
		Cursor countCursor = mDb.rawQuery(String.format("select max(dict_order) from %s", DictionaryItem.TABLE_NAME), null);
		countCursor.moveToFirst();
		int max = countCursor.getInt(0);
		max++;
		values.put(DictionaryItem.Columns.ORDER, max);
		long id = mDb.insert(DictionaryItem.TABLE_NAME, null, values);
		if (id >= 0) {
			result = ContentUris.withAppendedId(uri, id);
		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int updateCount = 0;
		
		switch (sURIMatcher.match(uri)) {
			case URI_TABLE:
				updateCount = updateDictionaryTable(uri, values, selection, selectionArgs);
				break;
			case URI_SIGNLE_ROW:
				updateCount = updateDictionaryItem(uri, values, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException();
		}

		if (updateCount > 0) {
			Long statusValue = values.getAsLong(DictionaryItem.Columns.STATUS);
			Integer order = values.getAsInteger(DictionaryItem.Columns.ORDER);
			if (statusValue != null && (statusValue == DictionaryItem.STATUS_UNZIP_SUCESSFULL || statusValue > 0)){
				getContext().getContentResolver().notifyChange(uri, null);
			}
			else if (order != null){
				getContext().getContentResolver().notifyChange(uri, null);
			}
		}
		return updateCount;
	}
	
	private int updateDictionaryTable(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		int updateCount = 0;
		mDb = mOpenHelper.getWritableDatabase();
		updateCount = mDb.update(DictionaryItem.TABLE_NAME, values, selection, selectionArgs);
		return updateCount;
	}
	
	private int updateDictionaryItem(Uri uri, ContentValues values, String selection, String[] selectionArgs){
		int updateCount = 0;
		mDb = mOpenHelper.getWritableDatabase();
		String rowID = uri.getLastPathSegment();
		selection = BaseColumns._ID + "=" + rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
		updateCount = mDb.update(DictionaryItem.TABLE_NAME, values, selection, selectionArgs);
		return updateCount;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int deleteCount = 0;
		
		switch (sURIMatcher.match(uri)) {
			case URI_TABLE:
				deleteCount = deleteDictionaryItemNormal(uri, selection, selectionArgs);
				break;
			case URI_SIGNLE_ROW:
				deleteCount = deleteDictionaryItemWithRowID(uri, selection, selectionArgs);
				break;
			default:
				throw new IllegalArgumentException();
		}
		
		if (deleteCount > 0){
			getContext().getContentResolver().notifyChange(uri, null);
		}
		
		return deleteCount;

	}
	
	
	private int deleteDictionaryItemNormal(Uri uri, String selection, String[] selectionArgs){
		int deleteCount = 0;
		mDb = mOpenHelper.getWritableDatabase();
		deleteCount = mDb.delete(DictionaryItem.TABLE_NAME, selection, selectionArgs);
		return deleteCount;
	}
	
	private int deleteDictionaryItemWithRowID(Uri uri, String selection, String[] selectionArgs){
		int deleteCount = 0;
		mDb = mOpenHelper.getWritableDatabase();
		String rowID = uri.getLastPathSegment();
		selection = BaseColumns._ID + "=" + rowID
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : "");
		deleteCount = mDb.delete(DictionaryItem.TABLE_NAME, selection, selectionArgs);
		return deleteCount;
	}
	
	public static class DictionarySQliteOpenHelper extends SQLiteOpenHelper{
		public static final String DATABASE_NAME = "dictionary.db";
		public static final int DATABASE_VERSION = 2;
//		private WeakReference<Context> mContext;
		
		public DictionarySQliteOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
//			mContext = new WeakReference<Context>(context);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuilder createSQL = new StringBuilder();
			createSQL.append("CREATE TABLE IF NOT EXISTS ").append(DictionaryItem.TABLE_NAME).append(" ( ");
			createSQL.append(BaseColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
			createSQL.append(DictionaryItem.Columns.DICT_ID).append(" TEXT NOT NULL, ");
			createSQL.append(DictionaryItem.Columns.TITLE).append(" TEXT NOT NULL, ");
			createSQL.append(DictionaryItem.Columns.STATUS).append(" INTEGER NOT NULL, ");
			createSQL.append(DictionaryItem.Columns.LANG).append(" TEXT NOT NULL, ");
			createSQL.append(DictionaryItem.Columns.ORDER).append(" INTEGER, ");
			createSQL.append(DictionaryItem.Columns.ACTIVE).append(" INTEGER NOT NULL);");
			db.execSQL(createSQL.toString());
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (newVersion != oldVersion){
				onCreate(db);
			}
		}

	}

}
