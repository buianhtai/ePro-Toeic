package com.mZone.epro.launch.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.mZone.epro.EproBaseActivity;
import com.mZone.epro.EproBaseSectionListFragment;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.utility.AppLog;
import com.mZone.epro.common.view.AmazingAdapter;
import com.mZone.epro.common.view.AmazingListView;
import com.mZone.epro.common.view.CoverPhotoImageView;

public class LaunchHomeFragmentTabHostPartTestFragment extends EproBaseSectionListFragment implements LoaderCallbacks<Cursor>{

	private static final String LOG_TAG = "LaunchHomeFragmentTabHostPartTestFragment";

	private int mCursorLoaderId = 0;
	
	public static LaunchHomeFragmentTabHostPartTestFragment newInstance(){
		AppLog.in(LOG_TAG, "newInstance()");
		LaunchHomeFragmentTabHostPartTestFragment fragment = new LaunchHomeFragmentTabHostPartTestFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		AppLog.out(LOG_TAG, "newInstance() fragment=%s, args=%s", fragment, args);
		return fragment;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		AppLog.in(LOG_TAG, "onItemClick() position=%d, id=%d viewTag=%s", position, id, view.getTag());
		AppLog.out(LOG_TAG, "onItemClick()");
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		AppLog.in(LOG_TAG, "onCreateView() inflater=%s, container=%s, savedInstanceState=%s", inflater, container, savedInstanceState);
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		// ローダーを取得
		final EproBaseActivity activity = (EproBaseActivity) getActivity();
		final LoaderManager loaderManager = getLoaderManager();
		
		final AmazingListView amazingListView = (AmazingListView) getListView();
		amazingListView.setPinnedHeaderView(LayoutInflater.from(activity).inflate(R.layout.launch_home_fragment_tabhost_parttest_fragment_list_section_item, amazingListView, false));
		
		mCursorLoaderId = activity.getNewLoaderId();
		AppLog.debug(LOG_TAG, "onCreateView() mCursorLoaderId=%d", mCursorLoaderId);
		
		loaderManager.initLoader(mCursorLoaderId, null, this);
		
		AppLog.out(LOG_TAG, "onCreateView() view=%s", view);
		return view;
	}
	
	@Override
	public void onStop() {
		AppLog.in(LOG_TAG, "onStop()");

		super.onStop();

		AppLog.out(LOG_TAG, "onStop()");
	}
	
	@Override
	public void onDestroyView() {
		AppLog.in(LOG_TAG, "onDestroyView()");
		
		final ListAdapter adapter = getListAdapter();
		if (adapter instanceof PartTestAmazingAdapter) {
			final PartTestAmazingAdapter amazingAdapter = (PartTestAmazingAdapter) adapter;
		}
		setListAdapter(null);

		// ローダーを破棄
		final LoaderManager loaderManager = getLoaderManager();
		loaderManager.destroyLoader(mCursorLoaderId);

		super.onDestroyView();
		
		AppLog.out(LOG_TAG, "onDestroyView()");
	}
	
	private class PartTestAmazingAdapter extends AmazingAdapter
	{
		SparseArray<ArrayList<BookItemLocal>> adapterData = new SparseArray<ArrayList<BookItemLocal>>();
		private Context contextActivity;

		public PartTestAmazingAdapter(Context context){
			contextActivity = context;
		}
		
		@Override
		public int getCount() {
			int allRowCount = 0;
			for (int i = 0; i < adapterData.size(); i++){
				int key = adapterData.keyAt(i);
				ArrayList<BookItemLocal> array = adapterData.get(key);
				allRowCount += array.size();
			}
			return allRowCount;
		}

		@Override
		public BookItemLocal getItem(int position) {
			int c = 0;
			for (int i = 0; i < adapterData.size(); i++) {
				int key = adapterData.keyAt(i);
				ArrayList<BookItemLocal> array = adapterData.get(key);
				if (position >= c && position < c + array.size()) {
					return array.get(position - c);
				}
				c += array.size();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		protected void onNextPageRequested(int page) {
			//Do nothing, its belong to page load listview
		}

		@Override
		protected void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			if (displaySectionHeader) {
				view.findViewById(R.id.header).setVisibility(View.VISIBLE);
				TextView lSectionTitle = (TextView) view.findViewById(R.id.header);
				lSectionTitle.setText(getSections()[getSectionForPosition(position)]);
			} else {
				view.findViewById(R.id.header).setVisibility(View.GONE);
			}			
		}

		@Override
		public View getAmazingView(int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) view = ((Activity) contextActivity).getLayoutInflater().inflate(R.layout.launch_home_fragment_tabhost_parttest_fragment_list_row, null);
			
			CoverPhotoImageView imgvCover = (CoverPhotoImageView)view.findViewById(R.id.launch_home_fragment_tabhost_parttest_fragment_list_row_item_imgv_cover);
			TextView tvTitle = (TextView)view.findViewById(R.id.launch_home_fragment_tabhost_parttest_fragment_list_row_item_tv_title);
			TextView tvSubtitle = (TextView)view.findViewById(R.id.launch_home_fragment_tabhost_parttest_fragment_list_row_item_tv_subtitle);
			ImageButton btnDelete = (ImageButton)view.findViewById(R.id.launch_home_fragment_tabhost_parttest_fragment_list_row_item_btn_delete);

			BookItemLocal item = getItem(position);
			final long id = item.getLocalID();
			String bookName = item.getBookName();
			String[] bookNameSplit = bookName.split("-");
			String title = bookNameSplit[0];
			String subtitle = bookNameSplit[1];
			final Uri cursorURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
			final String cursorURIString = cursorURI.toString();
			
			tvTitle.setText(title);
			tvSubtitle.setText(subtitle);
			btnDelete.setTag(cursorURI);
			
			imgvCover.coverPhotoUri = cursorURIString;
			imgvCover.setImageResource(R.drawable.loading_temp_image);
			
			String isHaveImage = item.getImageStatus();
			if (!TextUtils.isEmpty(isHaveImage) && isHaveImage.equals(BookItemLocal.IMAGE_HAVE)){
				final EproBaseActivity eproActivity = (EproBaseActivity) getActivity();
				int loaderID = eproActivity.getNewLoaderId();
				// 引数を作成
				final Bundle loaderArgs = new Bundle();
				loaderArgs.putString(CoverPhotoImageView.LocalCoverPhotoLoader.ARGS_KEY_COVER_PHOTO_URI, cursorURIString);

				// 読み込み開始
				final LoaderManager lm = getLoaderManager();
				lm.restartLoader(loaderID, loaderArgs, imgvCover);
			}
			
			view.setTag(cursorURIString);
			
			return view;
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			TextView lSectionHeader = (TextView)header;
			lSectionHeader.setText(getSections()[getSectionForPosition(position)]);
			lSectionHeader.setBackgroundColor(alpha << 24 | (0xbbffbb));
			lSectionHeader.setTextColor(alpha << 24 | (0x000000));			
		}

		@Override
		public int getPositionForSection(int section) {
			if (section < 0) section = 0;
			if (section >= adapterData.size()) section = adapterData.size() - 1;
			int c = 0;
			for (int i = 0; i < adapterData.size(); i++) {
				int key = adapterData.keyAt(i);
				ArrayList<BookItemLocal> array = adapterData.get(key);
				if (section == i) { 
					return c;
				}
				c += array.size();
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			int c = 0;
			for (int i = 0; i < adapterData.size(); i++) {
				int key = adapterData.keyAt(i);
				ArrayList<BookItemLocal> array = adapterData.get(key);
				if (position >= c && position < c + array.size()) {
					return i;
				}
				c += array.size();
			}
			return -1;
		}

		@Override
		public String[] getSections() {
			// TODO Auto-generated method stub
			String[] res = new String[adapterData.size()];
			for (int i = 0; i < adapterData.size(); i++) {
				int key = adapterData.keyAt(i);
				res[i] = String.format("Part %d", key);
			}
			return res;
		}
		
		public void setBookData(List<BookItemLocal> bookData){
			adapterData = new SparseArray<ArrayList<BookItemLocal>>();
			for (int i = 0; i < bookData.size(); i++){
				int index = i%3;
				ArrayList<BookItemLocal> array = adapterData.get(index);
				if (array == null){
					array = new ArrayList<BookItemLocal>();
					adapterData.append(index, array);
				}
				array.add(bookData.get(i));
			}
		}
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		AppLog.in(LOG_TAG, "onCreateLoader() id=%d, args=%s", loaderID, bundle);
		
		String[] projection = new String[]{
				String.valueOf(LocalBookTableMetaData._ID),
				String.valueOf(LocalBookTableMetaData.BOOK_ID),
				String.valueOf(LocalBookTableMetaData.BOOK_NAME),
				String.valueOf(LocalBookTableMetaData.BOOK_AUTHOR),
				String.valueOf(LocalBookTableMetaData.SECURITY_NAME),
				String.valueOf(LocalBookTableMetaData.COVER_IMAGE),
				String.valueOf(LocalBookTableMetaData.BOOK_TYPE),
			};
		StringBuilder selection = new StringBuilder();
		selection.append(LocalBookTableMetaData.STATUS).append(" = ? ");
		
		String[] selectionArgs = new String[]{
			String.valueOf(BookItemLocal.STATUS_UNZIP_SUCESSFULL),
		};
		CursorLoader cursorLoader = new CursorLoader(getActivity(), LocalBookTableMetaData.CONTENT_URI, projection, selection.toString(), selectionArgs, LocalBookTableMetaData.DEFAULT_SORT_ORDER);
		
		AppLog.out(LOG_TAG, "onCreateLoader() loader=%s", cursorLoader);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
		AppLog.in(LOG_TAG, "onLoadFinished() loader=%s, newCursor=%s", loader, cursor);
		
		List<BookItemLocal> bookData = new ArrayList<BookItemLocal>();
		if (cursor != null){
			if (cursor.moveToFirst()){
				do{
					BookItemLocal item = new BookItemLocal(cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_ID)), 
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_NAME)), 
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_AUTHOR)), 
							cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData._ID)),
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.COVER_IMAGE)),
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.SECURITY_NAME)),
							cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_TYPE)));
					bookData.add(item);
				}
				while (cursor.moveToNext());
			}
		}
		cursor.close();

		final ListAdapter listAdapter = getListAdapter();
		if (listAdapter instanceof PartTestAmazingAdapter){
			AppLog.debug(LOG_TAG, "onLoadFinished() Swap cursor.");
			final PartTestAmazingAdapter amazingAdapter = (PartTestAmazingAdapter)listAdapter;
			amazingAdapter.setBookData(bookData);
			amazingAdapter.notifyDataSetChanged();
		}
		else{
			AppLog.debug(LOG_TAG, "onLoadFinished() Create adapter.");
			final FragmentActivity activity = getActivity();
			final PartTestAmazingAdapter amazingAdapter = new PartTestAmazingAdapter(activity);
			amazingAdapter.setBookData(bookData);
			setListAdapter(amazingAdapter);
		}

		// リストを表示
		setListShown(true);

		AppLog.out(LOG_TAG, "onLoadFinished()");
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		AppLog.in(LOG_TAG, "onLoaderReset() loader=%s", loader);

		// リストビューからアダプターを破棄
		final ListAdapter adapter = getListAdapter();
		if (adapter instanceof PartTestAmazingAdapter) {
			final PartTestAmazingAdapter amazingAdapter = (PartTestAmazingAdapter) adapter;
		}
		setListAdapter(null);

		AppLog.out(LOG_TAG, "onLoaderReset()");		
	}
}
