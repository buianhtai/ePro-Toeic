package com.mZone.epro.client.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class BookProviderMetaData {
	private BookProviderMetaData(){}
	
	public static final String AUTHORITY = "com.mZone.epro.client.data.BookProvider";
	public static final String DATABASE_NAME = "book.db";
	public static final int DATABASE_VERSION = 3;
	
	public static final String LOCAL_BOOKS_TABLE_NAME = "local_books";
	public static final class LocalBookTableMetaData implements BaseColumns{
		private LocalBookTableMetaData(){}
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + LOCAL_BOOKS_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.epro.booklocalitem";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.epro.booklocalitem";
		
		//integer type
		public static final String _ID = BaseColumns._ID;
		//integer book_id (on server)
		public static final String BOOK_ID = "book_id";
		//string type
		public static final String BOOK_NAME = "book_name";
		//string type
		public static final String BOOK_AUTHOR = "book_author";
		//integer type
		public static final String BOOK_TYPE = "book_type";
		//integer type
		public static final String STATUS = "status";
		//string type
		public static final String DOWNLOAD_URI = "download_uri";
		//string type
		public static final String SECURITY_NAME = "security_name";
		//string type
		public static final String COVER_IMAGE = "image_data";
		//integer type
		public static final String CREATE_DATE = "created";
		//integer type
		public static final String MODIFIED_DATE = "modified";
		//integer type
		public static final String IS_NEW_BOOK = "newbook";
		
		//Sort order
		public static final String DEFAULT_SORT_ORDER = MODIFIED_DATE + " DESC";
		public static final String STATUS_SORT_ORDER = STATUS + " ASC";
		
		//constant string
		public static final String UNKNOWN_AUTHOR = "Unknown Author";
		
	}
	
	public static final String TEST_HISTORY_TABLE_NAME = "test_history";
	public static final String TEST_HISTORY_TABLE_NAME_EXTENSION = "test_history_extension";
	public static final class TestHistoryTableMetaData implements BaseColumns{
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TEST_HISTORY_TABLE_NAME);
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.epro.testhistory";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.epro.testhistory";
		
		public static final String _ID = "_id";
		public static final String BOOK_ID = "book_id";
		public static final String STATUS = "status";
		public static final String CREATE_DATE = "created";
		public static final String MODIFIED_DATE = "modified";
		public static final String FILE_PATH = "file_path";
		public static final String MODE = "mode";
		public static final String SCORE_PART1 = "score_part_1";
		public static final String SCORE_PART2 = "score_part_2";
		public static final String SCORE_PART3 = "score_part_3";
		public static final String SCORE_PART4 = "score_part_4";
		public static final String SCORE_PART5 = "score_part_5";
		public static final String SCORE_PART6 = "score_part_6";
		public static final String SCORE_PART7 = "score_part_7";
		public static final String CURRENT_TIME = "current_time";
		public static final String CURRENT_READING_TIME = "current_reading_time";
		public static final String ACTIVE_PART = "active_part";
		public static final String MEDIA_CURRENT_TIME = "media_current_time";
		
		//using for extension
		public static final Uri CONTENT_URI_EXTENSION = Uri.parse("content://" + AUTHORITY + "/" + TEST_HISTORY_TABLE_NAME_EXTENSION);
		public static final String EXTENSION_BOOK_ROW_ID = "book_row_id";
		public static final String EXTENSION_BOOK_NAME = "book_name";
		public static final String EXTENSION_COVER_IMAGE = "image_data";
		
		
		public static final String DEFAULT_SORT_ORDER = MODIFIED_DATE + " DESC";
		public static final String DEFAULT_SORT_ORDER_EXTENSION = TEST_HISTORY_TABLE_NAME + "." + MODIFIED_DATE + " DESC";
	}

}
