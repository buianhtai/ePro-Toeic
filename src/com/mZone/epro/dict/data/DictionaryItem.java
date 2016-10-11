package com.mZone.epro.dict.data;

import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

public class DictionaryItem {

	public static final String TABLE_NAME = "dictionary_table";
	public static final String DICT_FOLDER = "dictionary";
	
	public static class Columns implements BaseColumns{
		public static final String DICT_ID = "dict_id";
		public static final String TITLE = "title";
		public static final String STATUS = "status";
		public static final String LANG = "language";
		public static final String ORDER = "dict_order";
		public static final String ACTIVE = "active";
	}
	
	public static final Uri CONTENT_URI;
	static {
		final StringBuilder uri = new StringBuilder();
		uri.append("content://").append(DictionaryProvider.AUTHORITY).append("/").append(TABLE_NAME);
		CONTENT_URI = Uri.parse(uri.toString());
	}
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.epro.dictionaryitem";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.epro.dictionaryitem";
	
	public static final String[] PROJECTION = new String[] {
		Columns._ID,
		Columns.DICT_ID,
		Columns.TITLE,
		Columns.STATUS,
		Columns.LANG,
		Columns.ORDER,
		Columns.ACTIVE,
	};
	
	public static final String DEFAULT_SORT_ORDER = Columns.ORDER + " ASC";
	
	public static final int STATUS_UNSTABLE = -1;
	public static final int STATUS_DOWNLOADING = -2;
	public static final int STATUS_DOWNLOAD_SUCESSFULL = -3;
	public static final int STATUS_UNZIP_SUCESSFULL = -4;
	
	public long _id;
	public String dictID;
	public String title;
	public long status;
	public String url;
	public String lang;
	public int order;
	public int isActive;
	
	public DictionaryItem(String dictID, String title, long status, String url,
			int order, String lang) {
		super();
		this._id = -1L;
		this.dictID = dictID;
		this.title = title;
		this.status = status;
		this.url = url;
		this.order = -1;
		this.isActive = 1;
		this.lang = lang;
	}

	public DictionaryItem(long _id, String dictID, String title, long status,
			String url, int order, int isActive) {
		super();
		this._id = _id;
		this.dictID = dictID;
		this.title = title;
		this.status = status;
		this.url = url;
		this.order = order;
		this.isActive = isActive;
	}
	
	public ContentValues convertToContentValues() {
		final ContentValues values = new ContentValues();
		values.put(Columns.DICT_ID, dictID);
		values.put(Columns.TITLE, title);
		values.put(Columns.STATUS, status);
		values.put(Columns.LANG, lang);
		values.put(Columns.ORDER, order);
		values.put(Columns.ACTIVE, isActive);
		return values;
	}
}
