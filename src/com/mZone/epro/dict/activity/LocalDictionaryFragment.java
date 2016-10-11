package com.mZone.epro.dict.activity;

import java.io.File;

import com.mZone.epro.R;
import com.mZone.epro.client.utility.ExternalStorage;
import com.mZone.epro.dict.data.DictionaryItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

public class LocalDictionaryFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	
	private static final String FRAGMENT_TAG_DIALOG_DELETE = "FRAGMENT_TAG_DIALOG_DELETE";

	private static final int ASYNC_QUERY_HANDLER_TOKEN_UPDATE_ACTIVE_FLAG = 1;
	private DictAsyncQueryHandler mQueryHandler;
	private static boolean queryRunning = false;
	
	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View footer = View.inflate(getActivity(), R.layout.dictionary_local_manage_fragment_footer, null);
		this.getListView().addFooterView(footer);
		Button addingBtn = (Button)footer.findViewById(R.id.addDictionaryBtn);
		final DictionaryManagerActivity activity = (DictionaryManagerActivity) getActivity();
		addingBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				activity.moveToLanguageSelectionFragment();
			}
		});
		final ContentResolver cr = getActivity().getContentResolver();
		mQueryHandler = new DictAsyncQueryHandler(cr, getActivity());
//		setEmptyText(getResources().getString(R.string.dictionary_manager_local_dict_fragment_no_dict_string));
		setListAdapter();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	public void setListAdapter() {
		final LoaderManager lm = getLoaderManager();
		lm.restartLoader(R.id.dictionary_manager_acitivity_local_dict_manager_loader_id, null, this);
	}
	
	private class LocalDictAdapter extends CursorAdapter {
		
		public static final String MASTER_ID = "MASTER_ID";
		public static final String ORDER = "ORDER";
		public static final String PRE_MASTER_ID = "PRE_MASTER_ID";
		public static final String PRE_ORDER = "PRE_ORDER";
		public static final String NEXT_MASTER_ID = "NEXT_MASTER_ID";
		public static final String NEXT_ORDER = "NEXT_ORDER";
		
		public LocalDictAdapter(final Context context, final Cursor cursor, final boolean autoRequery) {
			super(context, cursor, autoRequery);
		}

		@Override
		public void bindView(final View view, final Context context, final Cursor cursor) {
			final ViewHolder holder = (ViewHolder) view.getTag();
			final long masterId = cursor.getLong(cursor.getColumnIndex(DictionaryItem.Columns._ID));
			String tittle = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.TITLE));
			String lang = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.LANG));
			String dictID = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.DICT_ID));
			final boolean isActive = (cursor.getInt(cursor.getColumnIndex(DictionaryItem.Columns.ACTIVE)) == 1);
			final int order = cursor.getInt(cursor.getColumnIndex(DictionaryItem.Columns.ORDER));
//			final int sum = cursor.getCount();
			holder.tittleTv.setText(tittle);
			holder.langTv.setText(lang);
			holder.dictOnOffCb.setChecked(isActive);
			holder.dictOnOffCb.setTag(masterId);
			holder.deleteBtn.setTag(dictID);
			
			Bundle preTagObject = new Bundle();
			Bundle nextTagObject = new Bundle();
			preTagObject.putLong(MASTER_ID, masterId);
			preTagObject.putInt(ORDER, order);
			nextTagObject.putLong(MASTER_ID, masterId);
			nextTagObject.putInt(ORDER, order);

			holder.upBtn.setVisibility(View.INVISIBLE);
			holder.downBtn.setVisibility(View.INVISIBLE);
			
			final Cursor preCursor = cursor;
			final Cursor nextCursor = cursor;
			if (preCursor.moveToPrevious()){
				final long preMasterID = preCursor.getLong(preCursor.getColumnIndex(DictionaryItem.Columns._ID));
				final int preOrder = preCursor.getInt(preCursor.getColumnIndex(DictionaryItem.Columns.ORDER));
				preTagObject.putLong(PRE_MASTER_ID, preMasterID);
				preTagObject.putInt(PRE_ORDER, preOrder);
				holder.upBtn.setVisibility(View.VISIBLE);
			}
			nextCursor.moveToNext();
			if (nextCursor.moveToNext()){
				final long nextMasterID = nextCursor.getLong(nextCursor.getColumnIndex(DictionaryItem.Columns._ID));
				final int nextOrder = nextCursor.getInt(nextCursor.getColumnIndex(DictionaryItem.Columns.ORDER));
				nextTagObject.putLong(NEXT_MASTER_ID, nextMasterID);
				nextTagObject.putInt(NEXT_ORDER, nextOrder);
				holder.downBtn.setVisibility(View.VISIBLE);
			}
//			Log.e("preCursor", String.format("Bundle=%s", preTagObject));
//			Log.e("nextCursor", String.format("Bundle=%s", nextTagObject));
			holder.upBtn.setTag(preTagObject);
			holder.downBtn.setTag(nextTagObject);
		}

		@Override
		public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
			final View view = View.inflate(context, R.layout.dictionary_local_manage_fragment_list_item, null);
			final ViewHolder holder = new ViewHolder();
			holder.deleteBtn = (ImageButton)view.findViewById(R.id.dict_manage_fragment_delete_btn);
			holder.tittleTv = (TextView)view.findViewById(R.id.dict_manage_fragment_tittle_tv);
			holder.langTv = (TextView)view.findViewById(R.id.dict_manage_fragment_lang_tv);
			holder.dictOnOffLayout = (LinearLayout)view.findViewById(R.id.dict_manage_fragment_dict_onoff_layout);
			holder.dictOnOffCb = (CheckBox)view.findViewById(R.id.dict_manage_fragment_dict_onoff_cb);
			holder.upBtn = (ImageButton)view.findViewById(R.id.dict_manage_fragment_up_btn);
			holder.downBtn = (ImageButton)view.findViewById(R.id.dict_manage_fragment_down_btn);
			view.setTag(holder);
			holder.dictOnOffLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder.dictOnOffCb.toggle();
				}
			});
			final CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener(){

				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					final Object obj = buttonView.getTag();
					if (obj instanceof Long) {
						final long _id = (Long) obj;
						final Uri uri = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, _id);
						final ContentValues values = new ContentValues();
						values.put(DictionaryItem.Columns.ACTIVE, (isChecked ? 1 : 0));
						mQueryHandler.startUpdate(ASYNC_QUERY_HANDLER_TOKEN_UPDATE_ACTIVE_FLAG, obj, uri, values, null, null);
					}
				}
			};
			holder.dictOnOffCb.setOnCheckedChangeListener(checkedChangeListener);
			holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Object obj = v.getTag();
					if (obj instanceof String) {
						final String dictID = (String) obj;
						final FragmentActivity activity = getActivity();
						final FragmentManager fm = activity.getSupportFragmentManager();
						final Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG_DIALOG_DELETE);
						if (fragment == null) {
							final DialogFragment dialogFragment = ConfirmDeleteDialogFragment.newInstance(dictID);
							dialogFragment.show(fm, FRAGMENT_TAG_DIALOG_DELETE);
						}
					}
				}
			});
			holder.upBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (queryRunning) return;
					final Object obj = v.getTag();
					if (obj instanceof Bundle) {
						final Bundle bundle = (Bundle)obj;
						final long masterID = bundle.getLong(MASTER_ID);
						final int order = bundle.getInt(ORDER);
						final long preMasterID = bundle.getLong(PRE_MASTER_ID);
						final int preOrder = bundle.getInt(PRE_ORDER);
						final Uri uri = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, masterID);
						final ContentValues values = new ContentValues();
						values.put(DictionaryItem.Columns.ORDER, preOrder);
						final Uri preUri = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, preMasterID);
						final ContentValues preValues = new ContentValues();
						preValues.put(DictionaryItem.Columns.ORDER, order);
						queryRunning = true;
						mQueryHandler.startUpdate(ASYNC_QUERY_HANDLER_TOKEN_UPDATE_ACTIVE_FLAG, obj, uri, values, null, null);
						mQueryHandler.startUpdate(ASYNC_QUERY_HANDLER_TOKEN_UPDATE_ACTIVE_FLAG, obj, preUri, preValues, null, null);
					}
				}
			});
			holder.downBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (queryRunning) return;
					final Object obj = v.getTag();
					if (obj instanceof Bundle) {
						final Bundle bundle = (Bundle)obj;
						final long masterID = bundle.getLong(MASTER_ID);
						final int order = bundle.getInt(ORDER);
						final long nextMasterID = bundle.getLong(NEXT_MASTER_ID);
						final int nextOrder = bundle.getInt(NEXT_ORDER);
						final Uri uri = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, masterID);
						final ContentValues values = new ContentValues();
						values.put(DictionaryItem.Columns.ORDER, nextOrder);
						final Uri nextUri = ContentUris.withAppendedId(DictionaryItem.CONTENT_URI, nextMasterID);
						final ContentValues nextValues = new ContentValues();
						nextValues.put(DictionaryItem.Columns.ORDER, order);
						queryRunning = true;
						mQueryHandler.startUpdate(ASYNC_QUERY_HANDLER_TOKEN_UPDATE_ACTIVE_FLAG, obj, uri, values, null, null);
						mQueryHandler.startUpdate(ASYNC_QUERY_HANDLER_TOKEN_UPDATE_ACTIVE_FLAG, obj, nextUri, nextValues, null, null);
					}
				}
			});
			return view;
		}
	}
	
	private static class ViewHolder{
		public ImageButton deleteBtn;
		public TextView tittleTv;
		public TextView langTv;
		public LinearLayout dictOnOffLayout;
		public CheckBox dictOnOffCb;
		public ImageButton upBtn;
		public ImageButton downBtn;
	}
	
	private static class DictAsyncQueryHandler extends AsyncQueryHandler {
		
		private Context mAppContext;
		public DictAsyncQueryHandler(final ContentResolver cr, final Context context) {
			super(cr);
			mAppContext = context.getApplicationContext();
		}

		@Override
		protected void onUpdateComplete(final int token, final Object cookie, final int result) {
			queryRunning = false;
			if (0 < result) {
				if (cookie instanceof Long) {
					final long _id = (Long) cookie;
					Log.d("DictAsyncQueryHandler", String.format("id=%d", _id));
				}
			}
		}
		
		@Override
		protected void onDeleteComplete(final int token, final Object cookie, final int result){
			queryRunning = false;
			if (0 < result) {
				if (cookie instanceof String){
					final String dictID = (String) cookie;
					Log.d("DictAsyncQueryHandler", String.format("dictID=%s", dictID));
					final String sourceFile = ExternalStorage.getSDCacheDir(mAppContext, DictionaryItem.DICT_FOLDER).toString() + "/" + dictID;
					File deleteFile = new File(sourceFile);
					deleteRecursive(deleteFile);
				}
			}

		}
		
		void deleteRecursive(File fileOrDirectory) {
		    if (fileOrDirectory.isDirectory()){
		    	for (File child : fileOrDirectory.listFiles()){
		        	 deleteRecursive(child);
		        }
		    }
		    fileOrDirectory.delete();
		}
	}
	
	public static class ConfirmDeleteDialogFragment extends DialogFragment{
		
		public static final String FRAGMENT_ARGS_KEY_DICT_ID = "fragment_args_key_dict_id";
		
		private DictAsyncQueryHandler mQueryHandler;
		private static final int ASYNC_QUERY_HANDLER_TOKEN_DELETE_DICT = 2;
		
		public static ConfirmDeleteDialogFragment newInstance(final String dictID) {
			final ConfirmDeleteDialogFragment fragment = new ConfirmDeleteDialogFragment();
			final Bundle args = new Bundle();
			args.putString(FRAGMENT_ARGS_KEY_DICT_ID, dictID);
			fragment.setArguments(args);
			return fragment;
		}	
		
		@Override
		public Dialog onCreateDialog(final Bundle savedInstanceState) {
			final String dictID;
			final Bundle args = getArguments();
			if (args != null) {
				dictID = args.getString(FRAGMENT_ARGS_KEY_DICT_ID, "");
			} else {
				dictID = "";
			}
			final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.dictionary_manager_local_dict_fragment_delete_dialog_tittle);
			builder.setMessage(R.string.dictionary_manager_local_dict_fragment_delete_dialog_message);
			builder.setNegativeButton(R.string.dictionary_manager_local_dict_fragment_delete_dialog_cancel_btn, null);
			builder.setPositiveButton(R.string.dictionary_manager_local_dict_fragment_delete_dialog_confirm_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final Uri uri = DictionaryItem.CONTENT_URI;
					StringBuilder selection = new StringBuilder();
					selection.append(DictionaryItem.Columns.DICT_ID).append(" = ?");
					String[] selectionArgs = {dictID};
					queryRunning = true;
					mQueryHandler.startDelete(ASYNC_QUERY_HANDLER_TOKEN_DELETE_DICT, dictID, uri, selection.toString(), selectionArgs);
				}
			});
			final Dialog dialog = builder.create();
			return dialog;
		}
		
		@Override
		public void onActivityCreated(final Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			final ContentResolver cr = getActivity().getContentResolver();
			mQueryHandler = new DictAsyncQueryHandler(cr, getActivity());
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
		setListShownNoAnimation(false);
		final Uri contentURI = DictionaryItem.CONTENT_URI;
		final String sortOrder = DictionaryItem.DEFAULT_SORT_ORDER;
		String selection = DictionaryItem.Columns.STATUS + " = ?";
		String[] selectionArgs = {String.valueOf(DictionaryItem.STATUS_UNZIP_SUCESSFULL),};
		final CursorLoader loader = new CursorLoader(getActivity(), contentURI, DictionaryItem.PROJECTION, selection, selectionArgs, sortOrder);
		return loader;
	}

	@Override
	public void onLoadFinished(final Loader<Cursor> loader, final Cursor cursor) {
		final LocalDictAdapter adapter = new LocalDictAdapter(getActivity(), cursor, false);
		setListAdapter(adapter);
		setListShown(true);
	}

	@Override
	public void onLoaderReset(final Loader<Cursor> loader) {
		final ListAdapter adapter = getListAdapter();
		if (adapter instanceof LocalDictAdapter){
			final LocalDictAdapter dictAdapter = (LocalDictAdapter)adapter;
			dictAdapter.swapCursor(null);
		}
	}

}
