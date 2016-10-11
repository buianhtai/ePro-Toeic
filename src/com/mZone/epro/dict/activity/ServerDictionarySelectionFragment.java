package com.mZone.epro.dict.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.mZone.epro.R;
import com.mZone.epro.client.dialog.NetworkErrorDialog;
import com.mZone.epro.client.utility.Utils;
import com.mZone.epro.dict.data.DictionaryClientDataController;
import com.mZone.epro.dict.data.DictionaryItem;
import com.mZone.epro.dict.data.DictionaryItemServer;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ServerDictionarySelectionFragment extends ListFragment implements LoaderCallbacks<Cursor>{

	public static final String ARGS_LANGUAGE_POSITION = "ServerDictionarySelectionFragment.ARGS_LANGUAGE_POSITION";
	
	private int languagePos = -1;
	private String lang = "";
	private ArrayList<String> dictionaryArray;
	
	//Dict in database
	private ArrayList<String> mUnzipedDict = new ArrayList<String>();
	private HashMap<String, Long> mDownloadingDict = new HashMap<String, Long>();
	private ArrayList<String> mStatusUnknownDict = new ArrayList<String>();
	private HashMap<String, Double> mDownloadingProgress = new HashMap<String, Double>();
	
	/** listview adapter */
	private DictionaryAdapter adapter;
	
	public ServerDictionarySelectionFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle args = getArguments();
		languagePos = args.getInt(ARGS_LANGUAGE_POSITION);
		if (languagePos >= 0){
			lang = DictionaryClientDataController.getInstance().getLanguagesArray()[languagePos];
			dictionaryArray = DictionaryClientDataController.getInstance().getDictionaryArray(languagePos);
			adapter = new DictionaryAdapter(getActivity());
			setListAdapter(adapter);
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		LoaderManager loadermanager = getLoaderManager();
		loadermanager.initLoader(1, null, this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroyView();
		isRunning = false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		String[] projection = {BaseColumns._ID, 
									DictionaryItem.Columns.DICT_ID,
									DictionaryItem.Columns.STATUS};
		StringBuilder selection = new StringBuilder();
		selection.append(DictionaryItem.Columns.LANG).append(" LIKE ?");
		String[] selectionArgs = {lang};
		CursorLoader cursor = new CursorLoader(getActivity(), DictionaryItem.CONTENT_URI, projection, selection.toString(), selectionArgs, null);
		return cursor;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		ArrayList<String> unzipedDict = new ArrayList<String>();
		HashMap<String, Long> downloadingDict = new HashMap<String, Long>();
		ArrayList<String> statusUnknown = new ArrayList<String>();
		if (cursor != null && cursor.moveToFirst()){
			do{
				String dictID = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.DICT_ID));
				long status = cursor.getLong(cursor.getColumnIndex(DictionaryItem.Columns.STATUS));
//				long _id = cursor.getLong(cursor.getColumnIndex(DictionaryItem.Columns._ID));
				if (status > 0){
					downloadingDict.put(dictID, status);
				}
				else if (status == DictionaryItem.STATUS_UNZIP_SUCESSFULL){
					unzipedDict.add(dictID);
				}
				else if (status == DictionaryItem.STATUS_DOWNLOADING || status == DictionaryItem.STATUS_DOWNLOAD_SUCESSFULL){
					statusUnknown.add(dictID);
				}
			}
			while(cursor.moveToNext());
			synchronized (mDownloadingDict) {
				mDownloadingDict.clear();
				mDownloadingDict.putAll(downloadingDict);
				mUnzipedDict.clear();
				mUnzipedDict.addAll(unzipedDict);
				mStatusUnknownDict.clear();
				mStatusUnknownDict.addAll(statusUnknown);
				if (mDownloadingDict.size() > 0 && !isRunning){
					isRunning = true;
					downloadUpdateThread = new DownLoadUpdateThread();
					downloadUpdateThread.start();
				}
			}
		}
		else{
			synchronized (mDownloadingDict) {
				mDownloadingDict.clear();
				mUnzipedDict.clear();
				mStatusUnknownDict.clear();
			}
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}

	
	public class DictionaryAdapter extends BaseAdapter{

		private LayoutInflater mLayoutInflater;
		
		public DictionaryAdapter(Context context){
			mLayoutInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			if (dictionaryArray != null){
				return dictionaryArray.size();
			}
			else{
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null){
				convertView = mLayoutInflater.inflate(R.layout.dictionary_selection_fragment_listview_item, parent, false);
				holder = new ViewHolder();
				holder.tvTitle = (TextView) convertView.findViewById(R.id.dictionary_selection_tittle);
				holder.btnDownload = (ImageButton) convertView.findViewById(R.id.dictionary_selection_download);
				holder.progressBar = (ProgressBar) convertView.findViewById(R.id.dictionary_selection_download_progress);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder) convertView.getTag();
			}
			final DictionaryItemServer dict = DictionaryClientDataController.getInstance().getDictionaryFromID(dictionaryArray.get(position));
			holder.tvTitle.setText(dict.title);
			if (mUnzipedDict.contains(dict.dictID) || mStatusUnknownDict.contains(dict.dictID)){	//downloaded
				if (mStatusUnknownDict.contains(dict.dictID)){
					holder.btnDownload.setVisibility(View.INVISIBLE);
				}
				else if (mUnzipedDict.contains(dict.dictID)){
					holder.btnDownload.setEnabled(false);
					holder.btnDownload.setVisibility(View.VISIBLE);
				}
				holder.progressBar.setVisibility(View.INVISIBLE);
			}
			else if (mDownloadingDict.containsKey(dict.dictID)){	//downloading
				holder.btnDownload.setVisibility(View.INVISIBLE);
				holder.btnDownload.setImageResource(R.drawable.dict_download_button_selector);
				holder.progressBar.setVisibility(View.VISIBLE);
				Double progress = mDownloadingProgress.get(dict.dictID);
				if (progress != null){
					holder.progressBar.setProgress((int)progress.doubleValue());
				}
			}
			else{
				holder.progressBar.setVisibility(View.INVISIBLE);
				holder.btnDownload.setEnabled(true);
				holder.btnDownload.setVisibility(View.VISIBLE);
				holder.btnDownload.setImageResource(R.drawable.dict_download_button_selector);
				holder.btnDownload.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (Utils.isNetworkAvailable(getActivity())){
							v.setEnabled(false);
							startDownloadDict(dict);
						}
						else{
							new NetworkErrorDialog(getActivity()).show(getChildFragmentManager(), "Network Error");
						}
						
					}
				});
			}
			return convertView;
		}
	}
	
	public static class ViewHolder{
		TextView tvTitle;
		ImageButton btnDownload;
		ProgressBar progressBar;
	}
	
	public void startDownloadDict(DictionaryItemServer dict){
		((DictionaryManagerActivity)getActivity()).delegateStartDownloadDictionary(dict);
	}
	
	DownLoadUpdateThread downloadUpdateThread = null;
	private boolean isRunning = false;
	
	private class DownLoadUpdateThread extends Thread{
		@Override
	    public void run() {
			while (isRunning && mDownloadingDict.size() > 0){
				long[] downloadIDs;
				String[] dictIDs;
				synchronized (mDownloadingDict) {
					downloadIDs = new long[mDownloadingDict.size()];
					dictIDs = new String[mDownloadingDict.size()];
					int count = 0;
					for (String dictID : mDownloadingDict.keySet()){
						dictIDs[count] = dictID;
						downloadIDs[count] = mDownloadingDict.get(dictID);
						count++;
					}
				}
				if (downloadIDs != null && downloadIDs.length > 0){
					double[] progressArray = new double[dictIDs.length];
					for (int i = 0; i < dictIDs.length; i++){
						progressArray[i] = -2;
					}
					DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
					DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(downloadIDs);
					Cursor cur = mgr.query(query);
			        int index = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
			        if(cur.moveToFirst()) {
			        	do{
			        		int count = 0;
			        		for (int i = 0; i < dictIDs.length; i++){
			        			 if (cur.getLong(cur.getColumnIndex(DownloadManager.COLUMN_ID)) == downloadIDs[i]){
			        				 count = i;
			        				 break;
			        			 }
			        		 }
			        		 if(cur.getInt(index) == DownloadManager.STATUS_RUNNING){
			        			double bytes_downloaded  = cur.getDouble(cur.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
			 	            	double bytes_total = cur.getDouble(cur.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
			 	            	progressArray[count] = bytes_downloaded/bytes_total;
			        		 }
			        		 else if (cur.getInt(index) == DownloadManager.STATUS_SUCCESSFUL){
			        			progressArray[count] = 1.0f; 
			        		 }
			        		 else{
			        			progressArray[count] = 0;
			        		 }
			        		 progressArray[count] *= 100;
			        	}
			        	while(cur.moveToNext());
			        }
			        Message msgObj = updateDownloadProgress.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", "updateProgress");
                    b.putStringArray("dictIDs", dictIDs);
                    b.putDoubleArray("progressArray", progressArray);
                    msgObj.setData(b);
                    if (isRunning == false) return;
                    updateDownloadProgress.sendMessage(msgObj);
				}
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isRunning = false;
		}
	}
	

	
	private Handler updateDownloadProgress = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (isRunning == false) return;
			String aResponse = msg.getData().getString("message");
			if (aResponse != null && aResponse.equals("updateProgress")){
				String[] dictIDs = msg.getData().getStringArray("dictIDs");
				double[] progressArray = msg.getData().getDoubleArray("progressArray");
				updateProgress(dictIDs, progressArray);
			}
		}
	};
	
	public void updateProgress(String[] dictIDs, double[] progressArray){
		if (isRunning == false) return;
		int firstVisible = getListView().getFirstVisiblePosition();
        int lastVisible = getListView().getLastVisiblePosition();
        for (int i = 0; i < dictIDs.length; i++){
        	double progress = progressArray[i];
        	if (progress >= 0){
        		String dictID = dictIDs[i];
        		for (int j = firstVisible; j <= lastVisible; j++){
        			final DictionaryItemServer dict = DictionaryClientDataController.getInstance().getDictionaryFromID(dictionaryArray.get(j));
        			if (dict.dictID.equals(dictID)){
        				View rowView = getListView().getChildAt(j - firstVisible);
                    	ProgressBar progressBar = (ProgressBar)rowView.findViewById(R.id.dictionary_selection_download_progress);
                    	mDownloadingProgress.put(dictID, progress);
                    	progressBar.setVisibility(View.VISIBLE);
                    	progressBar.setProgress((int)progress);
        				break;
        			}
            	}
        	}
        }
	}
}
