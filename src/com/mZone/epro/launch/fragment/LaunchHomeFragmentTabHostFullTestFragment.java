package com.mZone.epro.launch.fragment;

import com.mZone.epro.EproBaseActivity;
import com.mZone.epro.EproBaseListFragment;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.utility.AppLog;
import com.mZone.epro.common.view.CoverPhotoImageView;
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
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

public class LaunchHomeFragmentTabHostFullTestFragment extends EproBaseListFragment implements LoaderCallbacks<Cursor>{

	private static final String LOG_TAG = "LaunchHomeFragmentTabHostFullTestFragment";
	
	private int mCursorLoaderId = 0;
	
	public static LaunchHomeFragmentTabHostFullTestFragment newInstance(){
		AppLog.in(LOG_TAG, "newInstance()");
		LaunchHomeFragmentTabHostFullTestFragment fragment = new LaunchHomeFragmentTabHostFullTestFragment();
		final Bundle args = new Bundle();
		fragment.setArguments(args);
		AppLog.out(LOG_TAG, "newInstance() fragment=%s, args=%s", fragment, args);
		return fragment;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		AppLog.in(LOG_TAG, "onCreateView() inflater=%s, container=%s, savedInstanceState=%s", inflater, container, savedInstanceState);
		final View view = super.onCreateView(inflater, container, savedInstanceState);
		
		// ローダーを取得
		final EproBaseActivity activity = (EproBaseActivity) getActivity();
		final LoaderManager loaderManager = getLoaderManager();
		
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
		if (adapter instanceof FullTestCursorAdapter) {
			final FullTestCursorAdapter cursorAdapter = (FullTestCursorAdapter) adapter;
			cursorAdapter.changeCursor(null);
		}
		setListAdapter(null);

		// ローダーを破棄
		final LoaderManager loaderManager = getLoaderManager();
		loaderManager.destroyLoader(mCursorLoaderId);

		super.onDestroyView();
		
		AppLog.out(LOG_TAG, "onDestroyView()");
	}
	
	private class FullTestCursorAdapter extends CursorAdapter{
		
		private static final String LOG_TAG = "FullTestCursorAdapter";

		public FullTestCursorAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			AppLog.in(LOG_TAG, "bindView() view=%s, context=%s, cursor=%s", view, context, cursor);
			
			final ViewHolder holder = (ViewHolder) view.getTag();
			
			final long id = cursor.getLong(cursor.getColumnIndex(LocalBookTableMetaData._ID));
			String bookName = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_NAME));
			String[] bookNameSplit = bookName.split("-");
			String title = bookNameSplit[0];
			String subtitle = bookNameSplit[1];
			final Uri cursorURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, id);
			final String cursorURIString = cursorURI.toString();
			
			holder.tvTitle.setText(title);
			holder.tvSubtitle.setText(subtitle);
			holder.btnDelete.setTag(cursorURI);
			
			holder.imgvCover.coverPhotoUri = cursorURIString;
			holder.imgvCover.setImageResource(R.drawable.loading_temp_image);
			
			String isHaveImage = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.COVER_IMAGE));
			if (!TextUtils.isEmpty(isHaveImage) && isHaveImage.equals(BookItemLocal.IMAGE_HAVE)){
				if (holder.loaderId == -1){
					final EproBaseActivity eproActivity = (EproBaseActivity) getActivity();
					holder.loaderId = eproActivity.getNewLoaderId();
				}
				// 引数を作成
				final Bundle loaderArgs = new Bundle();
				loaderArgs.putString(CoverPhotoImageView.LocalCoverPhotoLoader.ARGS_KEY_COVER_PHOTO_URI, cursorURIString);

				// 読み込み開始
				final LoaderManager lm = getLoaderManager();
				lm.restartLoader(holder.loaderId, loaderArgs, holder.imgvCover);
			}
			
			AppLog.out(LOG_TAG, "bindView()");
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			AppLog.in(LOG_TAG, "newView() context=%s, cursor=%s, parent=%s", context, cursor, parent);

			// ビューを作成
			final View view = View.inflate(context, R.layout.launch_home_fragment_tabhost_fulltest_fragment_list_row, null);
			
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.imgvCover = (CoverPhotoImageView)view.findViewById(R.id.launch_home_fragment_tabhost_fulltest_fragment_list_row_item_imgv_cover);
			viewHolder.tvTitle = (TextView)view.findViewById(R.id.launch_home_fragment_tabhost_fulltest_fragment_list_row_item_tv_title);
			viewHolder.tvSubtitle = (TextView)view.findViewById(R.id.launch_home_fragment_tabhost_fulltest_fragment_list_row_item_tv_subtitle);
			viewHolder.btnDelete = (ImageButton)view.findViewById(R.id.launch_home_fragment_tabhost_fulltest_fragment_list_row_item_btn_delete);
			view.setTag(viewHolder);
			
			AppLog.out(LOG_TAG, "newView()");
			return view;
		}
	}
	
	/**
	 *
	 */
	private static class ViewHolder {
		/**  */
		public int loaderId = -1;
		/**  */
		public CoverPhotoImageView imgvCover;
		/**   */
		public TextView tvTitle;
		/**  */
		public TextView tvSubtitle;
		/**  */
		public ImageButton btnDelete;
		@Override
		public String toString() {
			return "ViewHolder [imgvCover=" + imgvCover + ", tvTitle="
					+ tvTitle + ", tvSubtitle=" + tvSubtitle + ", btnDelete="
					+ btnDelete + "]";
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
	public void onLoadFinished(Loader<Cursor> loader, final Cursor newCursor) {
		AppLog.in(LOG_TAG, "onLoadFinished() loader=%s, newCursor=%s", loader, newCursor);

		final ListAdapter listAdapter = getListAdapter();
		if (listAdapter instanceof FullTestCursorAdapter) {
			AppLog.debug(LOG_TAG, "onLoadFinished() Swap cursor.");
			final FullTestCursorAdapter cursorAdapter = (FullTestCursorAdapter) listAdapter;
			cursorAdapter.changeCursor(newCursor);
		} else {
			AppLog.debug(LOG_TAG, "onLoadFinished() Create adapter.");
			final FragmentActivity activity = getActivity();
			final FullTestCursorAdapter cursorAdapter = new FullTestCursorAdapter(activity, newCursor, true);
			setListAdapter(cursorAdapter);
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
		if (adapter instanceof FullTestCursorAdapter) {
			final FullTestCursorAdapter cursorAdapter = (FullTestCursorAdapter) adapter;
			cursorAdapter.changeCursor(null);
		}
		setListAdapter(null);

		AppLog.out(LOG_TAG, "onLoaderReset()");		
	}

}
