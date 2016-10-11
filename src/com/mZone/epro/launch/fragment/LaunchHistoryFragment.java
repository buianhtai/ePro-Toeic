package com.mZone.epro.launch.fragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import com.mZone.epro.LaunchActivity;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookProviderMetaData.TestHistoryTableMetaData;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.testhisotry.data.TestHistoryItem;
import com.mZone.epro.toeic.activity.ToeicTestActivity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LaunchHistoryFragment extends ListFragment implements LoaderCallbacks<Cursor>{

	private ListView listview;
	private HistoryListViewAdapter adapter;
	private LoaderManager loadermanager;
	
	private Typeface myTypeface;
		
	@Override
	public void onAttach(Activity activity){
		 super.onAttach(activity);		 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		myTypeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HPHelven.ttf");
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		listview = getListView();
		adapter = new HistoryListViewAdapter(getActivity());
		listview.setAdapter(adapter);
		listview.setBackgroundColor(getActivity().getResources().getColor(R.color.main_background_color));
		listview.setDivider(null);
		setEmptyText(getString(R.string.launch_history_empty_history_text));
		loadermanager = getLoaderManager();
		loadermanager.initLoader(1, null, this);
	}
	
	@Override
	public void onResume (){
		super.onResume();
	}
	@Override
	public void onPause (){
		super.onPause();
	}
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} 
		catch (NoSuchFieldException e) {
		}
		sChildFragmentManagerField = f;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} 
			catch (Exception e) {
			}
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		setListShownNoAnimation(false);
		CursorLoader cursor = new CursorLoader(getActivity(), TestHistoryTableMetaData.CONTENT_URI_EXTENSION, null, null, null, TestHistoryTableMetaData.DEFAULT_SORT_ORDER);
		return cursor;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor != null && cursor.moveToFirst()){
			ArrayList<TestHistoryItem> historyItems = new ArrayList<TestHistoryItem>();
			do{
				int _id = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData._ID));
				int bookID = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.BOOK_ID));
				int status = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.STATUS));
				Date createdDate  = new Date(cursor.getLong(cursor.getColumnIndex(TestHistoryTableMetaData.CREATE_DATE)));
				Date modifiedDate  = new Date(cursor.getLong(cursor.getColumnIndex(TestHistoryTableMetaData.MODIFIED_DATE)));
				String filePath = cursor.getString(cursor.getColumnIndex(TestHistoryTableMetaData.FILE_PATH));
				int mode = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.MODE));
				int scorePart1 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART1));
				int scorePart2 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART2));
				int scorePart3 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART3));
				int scorePart4 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART4));
				int scorePart5 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART5));
				int scorePart6 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART6));
				int scorePart7 = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.SCORE_PART7));
				int currentTime = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.CURRENT_TIME));
				int currentReadingTime = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.CURRENT_READING_TIME));
				int activePart = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.ACTIVE_PART));
				long mediaCurrentTime  = cursor.getLong(cursor.getColumnIndex(TestHistoryTableMetaData.MEDIA_CURRENT_TIME));
				
				int bookRowID = cursor.getInt(cursor.getColumnIndex(TestHistoryTableMetaData.EXTENSION_BOOK_ROW_ID));
				String bookName = cursor.getString(cursor.getColumnIndex(TestHistoryTableMetaData.EXTENSION_BOOK_NAME));
				String isHavingImage = cursor.getString(cursor.getColumnIndex(TestHistoryTableMetaData.EXTENSION_COVER_IMAGE));
				TestHistoryItem item = new TestHistoryItem(_id, bookID, status, createdDate, modifiedDate, filePath, mode, scorePart1, scorePart2, scorePart3, scorePart4, scorePart5, scorePart6, scorePart7, currentTime, currentReadingTime, activePart, mediaCurrentTime, bookRowID, bookName, isHavingImage);
				historyItems.add(item);
				
			}
			while (cursor.moveToNext());
			adapter.swapCursor(historyItems);
		}
		setListShownNoAnimation(true);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	
	private class HistoryListViewAdapter extends BaseAdapter{

		private Context mContext;
		private LayoutInflater mLayoutInflater;
		private ArrayList<TestHistoryItem> historyItems;
		
		public HistoryListViewAdapter(Context c){
			mContext = c;
			mLayoutInflater = LayoutInflater.from(mContext);
			historyItems = new ArrayList<TestHistoryItem>();
		}
		public class Holder{
			ImageView imgv;
			TextView tvTitle;
			TextView tvMode;
			TextView tvStatus;
			TextView tvScore;
			Button btnAction;
		}
		@Override
		public int getCount() {
			return historyItems.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null){
				convertView = mLayoutInflater.inflate(R.layout.launch_history_fragment_listview_item, parent, false);
				holder = new Holder();
				holder.imgv = (ImageView)convertView.findViewById(R.id.imgv);
				holder.tvTitle = (TextView)convertView.findViewById(R.id.titleTv);
				holder.tvMode = (TextView)convertView.findViewById(R.id.modeTv);
				holder.tvStatus = (TextView)convertView.findViewById(R.id.statusTv);
				holder.tvScore = (TextView)convertView.findViewById(R.id.scoreTv);
				holder.btnAction = (Button)convertView.findViewById(R.id.actionBtn);
				convertView.setTag(holder);
			}
			else{
				holder = (Holder)convertView.getTag();
			}
			TestHistoryItem item = this.historyItems.get(position);
			if (item.getIsHavingImage().equals(BookItemLocal.IMAGE_HAVE)){
				Uri myRowUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, item.getBookRowID());
				try {
				      InputStream inStream = mContext.getContentResolver().openInputStream(myRowUri);
				      Bitmap bitmap = BitmapFactory.decodeStream(inStream);
				      holder.imgv.setImageBitmap(bitmap);
				 }
				 catch (FileNotFoundException e) { 
				 } 
			}
			else{
				holder.imgv.setImageResource(R.drawable.corgi);
			}
			if (item.getMode() == ToeicTestActivity.TEST_MODE_PRACTICE){
				holder.tvMode.setText(R.string.launch_history_string_mode_practice);
			}
			else{
				holder.tvMode.setText(R.string.launch_history_string_mode_test);
			}
			if (item.getStatus() == TestHistoryItem.STATUS_DOING){
				holder.tvScore.setVisibility(View.INVISIBLE);
				holder.tvStatus.setText(R.string.launch_history_string_status_doing);
				holder.btnAction.setBackgroundResource(R.drawable.launch_history_continue_button_selector);
				try {
				    XmlResourceParser parser = getResources().getXml(R.drawable.launch_history_continue_button_text_color_selector);
				    ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
				    holder.btnAction.setTextColor(colors);
				} catch (Exception e) {
				    // handle exceptions
				}
				holder.btnAction.setText(R.string.launch_history_string_actionbtn_status_doing);
				holder.btnAction.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((LaunchActivity)mContext).openTest(historyItems.get(position));
					}
				});
			}
			else{
				holder.tvScore.setVisibility(View.VISIBLE);
				holder.tvScore.setText(getScoreStringAtPosition(position));
				holder.tvStatus.setText(R.string.launch_history_string_status_finish);
				holder.btnAction.setBackgroundResource(R.drawable.launch_history_review_button_selector);
				try {
				    XmlResourceParser parser = getResources().getXml(R.drawable.launch_history_review_button_text_color_selector);
				    ColorStateList colors = ColorStateList.createFromXml(getResources(), parser);
				    holder.btnAction.setTextColor(colors);
				} catch (Exception e) {
				    // handle exceptions
				}
				holder.btnAction.setText(R.string.launch_history_string_actionbtn_status_finish);
				holder.btnAction.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						((LaunchActivity)mContext).openTest(historyItems.get(position));
					}
				});
				//set score here
			}
			String[] parsingBookName = item.getBookName().split("-");
			String rebuildBookName = parsingBookName[0] + "\n" + parsingBookName[1];
			holder.tvTitle.setText(rebuildBookName);
//			holder.tvTitle.setText(item.getBookName());
			holder.tvTitle.setTypeface(myTypeface);
			holder.tvStatus.setTypeface(myTypeface);
			holder.tvMode.setTypeface(myTypeface);
			holder.tvScore.setTypeface(myTypeface);
			return convertView;
		}
		
		private String getScoreStringAtPosition(int position){
			TestHistoryItem item = historyItems.get(position);
			int[] listeningScores = item.getListeningScore();
			int[] readingScores = item.getReadingScore();
			String label = mContext.getResources().getString(R.string.toeic_result_dialog_total_score);
			int totalScore = mContext.getResources().getInteger(R.integer.toeic_total_score);
			int listeningScore = 0;
			for (int i = 0; i < listeningScores.length; i++){
				listeningScore += listeningScores[i];
			}
			int readingScore = 0;
			for (int i = 0; i < readingScores.length; i++){
				readingScore += readingScores[i];
			}
			int revertTotalScore = mContext.getResources().getIntArray(R.array.listening_score_table)[listeningScore] + 
					mContext.getResources().getIntArray(R.array.reading_score_table)[readingScore];
			return String.format("%s   %d/%d", label, revertTotalScore, totalScore).toString();
		}
		
		public void swapCursor(ArrayList<TestHistoryItem> historyItems){
			this.historyItems = historyItems;
			notifyDataSetChanged();
		}
	}

}
