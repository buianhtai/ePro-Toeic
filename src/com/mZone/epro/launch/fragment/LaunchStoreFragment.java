package com.mZone.epro.launch.fragment;

import java.lang.reflect.Field;
import java.util.HashMap;
import com.mZone.epro.LaunchActivity;
import com.mZone.epro.R;
import com.mZone.epro.account.AccountActivity;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookItemServer;
import com.mZone.epro.client.data.ClientDataController;
import com.mZone.epro.client.data.TransactionParameter;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.dialog.DownloadDialog;
import com.mZone.epro.client.dialog.MyDialogInterface;
import com.mZone.epro.client.dialog.NetworkErrorDialog;
import com.mZone.epro.client.task.JSONGetAccountInfoAsyncTask;
import com.mZone.epro.client.utility.Constants;
import com.mZone.epro.client.utility.MyPreference;
import com.mZone.epro.client.utility.Utils;
import com.mZone.epro.launch.customView.CustomProgressButton;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchStoreFragment extends Fragment implements MyDialogInterface, LoaderCallbacks<Cursor>{

	public static final String ARGS_START_INDEX = "com.mZone.epro.launch.fragment.LaunchStoreFragment.ARGS_START_INDEX";
	private int startIndex = 0;
	
	private ClientDataController dataController = ClientDataController.getInstance();
	
	private View rootView;
	private GridView gridView;
	private GridViewAdapter adapter;
	
	//loading data from content provider (for checking whether the book is existed in local or not
	private  LoaderManager loadermanager;
	
	//variable for download
	private BookItemServer currentClickBookItem;
	private int currentClickPosition;
	ProgressDialog progress;
	
	//for download UI update
	private HashMap<Integer, Long> downloadingBookMap = new HashMap<Integer, Long>();
	private HashMap<Integer, Double> progressMap = new HashMap<Integer, Double>();
	
	private HashMap<Integer, Long> downloadedBookMap = new HashMap<Integer, Long>();
	
	@Override
	public void onAttach(Activity activity){
		 super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.launch_store_fragment, container, false);
		gridView = (GridView)rootView.findViewById(R.id.gridviewStore);
		adapter = new GridViewAdapter(getActivity());
		gridView.setAdapter(adapter);
		Bundle args = getArguments();
		if (args != null){
			startIndex = args.getInt(ARGS_START_INDEX, 0);
			gridView.setSelection(startIndex);
		}
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				onClickAtItem(index);
			}
		});
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
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
		isRunning = false;
		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} 
			catch (Exception e) {
			}
		}
	}
	
	private class GridViewAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater mLayoutInflater;
		
		public class Holder{
			ImageView imgv;
			TextView tvTitle;
			TextView tvSubTitle;
			TextView tvAuthor;
			CustomProgressButton btnAction;
		}
		
		public GridViewAdapter(Context c){
			mContext = c;
			mLayoutInflater = LayoutInflater.from(mContext);
		}
		
		@Override
		public int getCount() {
			if (dataController.isNewBookArrayReady()){
				return dataController.getNewBookArraySize();
			}
			else{
				return 0;
			}
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null){
				convertView = mLayoutInflater.inflate(R.layout.launch_store_fragment_gridview_item, parent, false);
				holder = new Holder();
				holder.imgv = (ImageView)convertView.findViewById(R.id.imgv);
				holder.tvTitle = (TextView)convertView.findViewById(R.id.tvTitle);
				holder.tvSubTitle = (TextView)convertView.findViewById(R.id.tvSubTitle);
				holder.tvAuthor = (TextView)convertView.findViewById(R.id.tvAuthor);
				holder.btnAction = (CustomProgressButton)convertView.findViewById(R.id.btnAction);
				convertView.setTag(holder);
			}
			else{
				holder = (Holder)convertView.getTag();
			}

			BookItemServer bookItem = dataController.getBookItemFromNewArray(position);
			String[] bookName = bookItem.getBookName().split("-");
			holder.tvTitle.setText(bookName[0]);
			holder.tvSubTitle.setText(bookName[1]);
			holder.tvAuthor.setText(bookItem.getBookAuthor());
			
			if (dataController.checkIfBookExistedInLocal(bookItem.getBookID())){
				final BookItemLocal localBookItem = dataController.getBookItemFromLocal(bookItem.getBookID());
				progressMap.remove(position);
				holder.btnAction.disableProgress();
				holder.btnAction.setBackgroundResource(R.drawable.launch_store_grid_item_btnaction_open_selector);
				holder.btnAction.setText(getResources().getString(R.string.launch_store_fragment_open));
				holder.btnAction.setTextColor(Color.WHITE);
				holder.btnAction.setGravity(Gravity.CENTER);
				holder.btnAction.setPadding(0, 0, 0, 0);
				holder.btnAction.setEnabled(true);
				holder.btnAction.setIsBitmap(false);
				holder.btnAction.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						onBookItemOpen(localBookItem);
					}
				});
			}
			else if (downloadedBookMap.containsKey(bookItem.getBookID())){
				holder.btnAction.setBackgroundResource(R.drawable.launch_store_grid_item_btnaction_selector);
				holder.btnAction.setTextColor(Color.WHITE);
				holder.btnAction.setGravity(Gravity.CENTER);
				holder.btnAction.setPadding(0, 0, 0, 0);
				holder.btnAction.setEnabled(false);
				holder.btnAction.setIsBitmap(false);
				holder.btnAction.setProgress(1.0f);
			}
			else{
				holder.btnAction.setBackgroundResource(R.drawable.launch_store_grid_item_btnaction_selector);
				if (bookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_FREE){
					holder.btnAction.setText(R.string.launch_store_fragment_free_book);
				}
				else if (bookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_BACKUP 
						|| bookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_REQUIRE_UPDATE){
					holder.btnAction.setText(R.string.launch_store_fragment_backup_book);
				}
				else{
					holder.btnAction.setText(String.valueOf(bookItem.getBookPrice()) + " credits");
				}
				
				Double progress = progressMap.get(position);
				if (progress == null){
					holder.btnAction.setTextColor(Color.BLACK);
					holder.btnAction.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
					holder.btnAction.setPadding(10, 0, 0, 0);
					holder.btnAction.setEnabled(true);
					holder.btnAction.setIsBitmap(true);
					holder.btnAction.disableProgress();
				}
				else{
					holder.btnAction.setTextColor(Color.WHITE);
					holder.btnAction.setGravity(Gravity.CENTER);
					holder.btnAction.setPadding(0, 0, 0, 0);
					holder.btnAction.setEnabled(false);
					holder.btnAction.setIsBitmap(false);
					holder.btnAction.setProgress(progress.floatValue());
				}
				
				holder.btnAction.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						onBookItemClick(position);
					}
				});
			}
			ImageView imageView = holder.imgv;
			dataController.setImage(imageView, bookItem.getBookID());
			return convertView;
		}
	}

	@Override
	public void showDownloadDialog(TransactionParameter parameters) {
		DownloadDialog downloadDialog = new DownloadDialog(getActivity(), this, parameters);
		downloadDialog.show(getChildFragmentManager(), "DownloadDialog");
	}

	@Override
	public void startDownloadAction(TransactionParameter parameters) {
		((LaunchActivity)getActivity()).delegateStartDownloadTransaction(parameters);
	}
	
	@Override
	public void startAccountActivity(TransactionParameter parameters) {
		Intent intent = new Intent(getActivity(), AccountActivity.class);
		getActivity().startActivity(intent);
	}

	
	private void onBookItemClick(int position){
		if (Utils.isNetworkAvailable(getActivity())){
			String currentAccount = MyPreference.getCurrentAccount(getActivity());
			currentClickBookItem = dataController.getBookItemFromNewArray(position);
			currentClickPosition = position;
			if (currentClickBookItem != null && currentClickBookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_FREE){
				startDownloadTransaction();
				return;
			}
			if (TextUtils.isEmpty(currentAccount)){
				AlertDialog.Builder noAccountDialog = new AlertDialog.Builder(getActivity());
				noAccountDialog.setTitle(R.string.no_account_dialog_tittle);
				noAccountDialog.setMessage(R.string.no_account_dialog_message);
				noAccountDialog.setNegativeButton(R.string.no_account_dialog_cancel_btn, null);
				noAccountDialog.setPositiveButton(R.string.no_account_dialog_setup_btn, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(getActivity(), AccountActivity.class);
						intent.putExtra(AccountActivity.NO_ACCOUNT_START_ACTIVITY_ARGS, true);
						startActivity(intent);
					}
				});
				noAccountDialog.create().show();
			}
			else{
				if (currentClickBookItem != null){
					startDownloadTransaction();
				}
			}
		}
		else{
			new NetworkErrorDialog(getActivity()).show(getChildFragmentManager(), "Network Error");
		}
		
	}
	
	private void onBookItemOpen(BookItemLocal item){
		((LaunchActivity)getActivity()).openTest(item);
	}
	
	private void startDownloadTransaction(){
		BookItemServer bookItem = currentClickBookItem;
		String account = MyPreference.getCurrentAccount(getActivity());
		String imei = Utils.getDeviceImei(getActivity());
		TransactionParameter parameters = new TransactionParameter(bookItem, account, imei);
		if (bookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_BACKUP
				|| bookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_FREE){
			startDirectDownload(parameters);
		}
		else{
			if (bookItem.getPaymentRequire() == Constants.DOWNLOAD_REQUIRE_STATUS_REQUIRE_UPDATE){
				parameters.setPaymentRequired(false);
			}
			else{
				parameters.setPaymentRequired(true);
			}
			JSONGetAccountInfoAsyncTask task = new JSONGetAccountInfoAsyncTask(getActivity(), parameters, this);
			task.execute();
		}
		currentClickBookItem = null;
		currentClickPosition = -1;
	}
	
	private void startDirectDownload(TransactionParameter parameters){
		int firstVisible = gridView.getFirstVisiblePosition();
        int lastVisible = gridView.getLastVisiblePosition();
        for (int j = firstVisible; j <= lastVisible; j++){
        	if (j == currentClickPosition){
        		View rowView = gridView.getChildAt(j - firstVisible);
        		CustomProgressButton btnAction = (CustomProgressButton)rowView.findViewById(R.id.btnAction);
        		btnAction.setTextColor(Color.                                                                                                                                                                                                                                                                                                                                                                                                                                                 WHITE);
				btnAction.setGravity(Gravity.CENTER);
				btnAction.setPadding(0, 0, 0, 0);
            	btnAction.setEnabled(false);
            	btnAction.updateProgressWithAnimate(0);
        	}
        }

		Bitmap bitmap = ClientDataController.getInstance().getExistedBitmap(parameters.getBookItem().getBookID());
  	  	parameters.setBitmap(bitmap);
  	  	startDownloadAction(parameters);
	}
	
	public void swapCursor(){
		adapter.notifyDataSetChanged();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
//		gridView.setVisibility(View.INVISIBLE);
		String[] projection = new String[]{
				String.valueOf(LocalBookTableMetaData.BOOK_ID),
				String.valueOf(LocalBookTableMetaData.STATUS),
			};
		StringBuilder selection = new StringBuilder();
		selection.append(LocalBookTableMetaData.STATUS).append(" > ? ");
		selection.append(" OR ").append(LocalBookTableMetaData.STATUS).append(" = ? ");
		
		String[] selectionArgs = new String[]{
			String.valueOf(0),
			String.valueOf(BookItemLocal.STATUS_DOWNLOAD_SUCESSFULL),
		};
		CursorLoader cursor = new CursorLoader(getActivity(), LocalBookTableMetaData.CONTENT_URI, projection, selection.toString(), selectionArgs, LocalBookTableMetaData.DEFAULT_SORT_ORDER);
		return cursor;
	}

	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (cursor.moveToFirst()){
			HashMap<Integer, Long> bookMap = new HashMap<Integer, Long>();
			HashMap<Integer, Long> downloadedMap = new HashMap<Integer, Long>();
			do{
				long status = cursor.getLong(cursor.getColumnIndex(LocalBookTableMetaData.STATUS));
				if (status > 0){
					bookMap.put(cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_ID)),
							status);
				}
				else if (status == BookItemLocal.STATUS_DOWNLOAD_SUCESSFULL){
					downloadedMap.put(cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_ID)),
							status);
				}
			}
			while (cursor.moveToNext());
			synchronized (downloadingBookMap) {
				downloadedBookMap.clear();
				downloadedBookMap.putAll(downloadedMap);
				downloadingBookMap.clear();
				downloadingBookMap.putAll(bookMap);
				if (downloadingBookMap.size() > 0 && !isRunning){
					isRunning = true;
					downloadUpdateThread = new DownLoadUpdateThread();
					downloadUpdateThread.start();
				}
			}
		}
		else{
			synchronized (downloadingBookMap){
				downloadingBookMap.clear();
				downloadedBookMap.clear();
			}
		}
//		gridView.setVisibility(View.VISIBLE);
//		adapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
		
	}

	DownLoadUpdateThread downloadUpdateThread = null;
	private boolean isRunning = false;
	private class DownLoadUpdateThread extends Thread{
		@Override
	    public void run() {
			while (isRunning && downloadingBookMap.size() > 0){
				long[] downloadID;
				int[] bookIDs;
				synchronized (downloadingBookMap) {
					downloadID = new long[downloadingBookMap.size()];
					bookIDs = new int[downloadingBookMap.size()];
					int count = 0;
					for (Integer bookID : downloadingBookMap.keySet()){
						bookIDs[count] = bookID;
						downloadID[count] = downloadingBookMap.get(bookID);
						count++;
					}
				}
				if (downloadID != null && downloadID.length > 0){
					double[] progressArray = new double[bookIDs.length];
					for (int i = 0; i < bookIDs.length; i++){
						progressArray[i] = -2;
					}
					DownloadManager mgr = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
					DownloadManager.Query query = new DownloadManager.Query();
					query.setFilterById(downloadID);
					Cursor cur = mgr.query(query);
			        int index = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
			        if(cur.moveToFirst()) {
			        	do{
			        		int count = 0;
			        		for (int i = 0; i < bookIDs.length; i++){
			        			 if (cur.getLong(cur.getColumnIndex(DownloadManager.COLUMN_ID)) == downloadID[i]){
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
			        	}
			        	while(cur.moveToNext());
			        }
			        Message msgObj = updateDownloadProgress.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("message", "updateProgress");
                    b.putIntArray("bookIDs", bookIDs);
                    b.putDoubleArray("progressArray", progressArray);
                    msgObj.setData(b);
                    if (isRunning == false) return;
                    updateDownloadProgress.sendMessage(msgObj);
				}
				try {
					sleep(1000);
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
			String aResponse = msg.getData().getString("message");
			if (aResponse != null && aResponse.equals("updateProgress")){
				int[] bookIDs = msg.getData().getIntArray("bookIDs");
				double[] progressArray = msg.getData().getDoubleArray("progressArray");
				updateProgress(bookIDs, progressArray);
			}
		}
	};
	
	public void updateProgress(int[] bookIDs, double[] progressArray){
		int firstVisible = gridView.getFirstVisiblePosition();
        int lastVisible = gridView.getLastVisiblePosition();
        for (int i = 0; i < bookIDs.length; i++){
        	double progress = progressArray[i];
        	if (progress >= 0){
        		int bookID = bookIDs[i];
        		for (int j = firstVisible; j <= lastVisible; j++){
        			if (dataController.getBookItemFromNewArray(j).getBookID() == bookID){
        				View rowView = gridView.getChildAt(j - firstVisible);
                    	CustomProgressButton btnAction = (CustomProgressButton)rowView.findViewById(R.id.btnAction);
                    	progressMap.put(j, progress);
                    	btnAction.setTextColor(Color.                                                                                                                                                                                                                                                                                                                                                                                                                                                 WHITE);
    					btnAction.setGravity(Gravity.CENTER);
    					btnAction.setPadding(0, 0, 0, 0);
                    	btnAction.setEnabled(false);
                    	btnAction.updateProgressWithAnimate((float)progress);
        				break;
        			}
            	}
        	}	
        }
	}
	
	public void onClickAtItem (int index){
//		View rowView = gridView.getChildAt(index);
		View rowView = adapter.getView(index, null, gridView);
		CustomProgressButton btnAction = (CustomProgressButton)rowView.findViewById(R.id.btnAction);
		if (btnAction.isEnabled() == true){
			BookItemServer bookItem = dataController.getBookItemFromNewArray(index);
			if (dataController.checkIfBookExistedInLocal(bookItem.getBookID())){
				final BookItemLocal localBookItem = dataController.getBookItemFromLocal(bookItem.getBookID());
				onBookItemOpen(localBookItem);
			}
			else{
				onBookItemClick(index);
			}
		}
	}
		
	public void onLoadingBookFromServerFinish(){
		adapter.notifyDataSetChanged();
	}

}
