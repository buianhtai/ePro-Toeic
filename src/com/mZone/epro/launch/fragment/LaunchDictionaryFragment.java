package com.mZone.epro.launch.fragment;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import com.mZone.epro.R;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;
import com.mZone.epro.dict.data.DictionaryItem;
import com.mZone.epro.dict.data.FavoriteWordPreference;
import com.mZone.epro.dict.dialog.ShowDictWordDialog;
import com.mZone.epro.dict.dialog.ShowDictWordDialogForSearchFragment;
import com.mZone.epro.dict.dialog.ShowDictWordDialog.ShowDictWordDialogDelegate;
import com.mZone.epro.dict.dialog.ShowDictWordDialogForSearchFragment.ShowDictWordDialogForSearchFragmentDelegate;
import com.mZone.epro.dict.dictLibrary.DictionaryManager;
import com.mZone.epro.dict.dictLibrary.WordData;
import com.mZone.epro.dict.dictionary.ClearableEditText;
import com.mZone.epro.dict.dictionary.ClearableEditText.ClearableEditTextListener;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class LaunchDictionaryFragment extends Fragment implements ClearableEditTextListener, 
															ShowDictWordDialogForSearchFragmentDelegate,
															ShowDictWordDialogDelegate{

	private static final int TIME_TO_WAIT = 100;
	
	//View
	private ClearableEditText editText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private Button btnFavorite;
	
	//Search
	DictionarySearchTask searchTask;
	
	DictionaryManager dictManager;
	private ArrayList<String> searchResult;
	private HashMap<String, ArrayList<WordDataExtenstion>> searchResultHashmap;
	
	public static class WordDataExtenstion{
		public WordData wordData;
		public int dictionaryID;
		public WordDataExtenstion (WordData w, int dictID){
			this.wordData = w;
			this.dictionaryID = dictID;
		}
	}
	
	//favorite
	private boolean isFavoriteList = false;
	private ArrayList<String> favoriteWords;
	
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
		View rootView = inflater.inflate(R.layout.launch_dictionary_fragment, container, false);
		editText = (ClearableEditText)rootView.findViewById(R.id.edit_text_clearable);
		listView = (ListView)rootView.findViewById(R.id.search_result_listview);
		editText.setEditTextDelegate(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (isFavoriteList){
					showFavoriteWordDialog(position);
				}
				else{
					showDialogAtPosition(position);
				}				
			}
		});
		btnFavorite = (Button)rootView.findViewById(R.id.dict_favorite);
		btnFavorite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onFavoriteButtonClicked();
			}
		});
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroy();
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.edit_text.getWindowToken(), 0);
		if (dictManager != null) {
	    	dictManager.releaseDict();
	    }
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		final LoaderManager loaderManager = getLoaderManager();
		final DictionaryLoadingTaskLoaderCallbacks callback = new DictionaryLoadingTaskLoaderCallbacks();
		loaderManager.initLoader(R.id.launch_dictionary_loading_task_loader_id, null, callback);
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
	public void afterTextChanged(String searchStr) {
		if (searchTask != null){
			searchTask.cancel(true);
		}
		searchTask = new DictionarySearchTask();
		searchTask.execute(searchStr);
	}

	private void showDialogAtPosition(int position){
		String word = searchResult.get(position);
		ArrayList<WordDataExtenstion> wordData = searchResultHashmap.get(word);
		String content = dictManager.getExplaination(wordData); 
		Bundle args = new Bundle();
		args.putString(ShowDictWordDialogForSearchFragment.ARGS_WORD, word);
		args.putString(ShowDictWordDialogForSearchFragment.ARGS_CONTENT, content);
		ShowDictWordDialogForSearchFragment dialog = new ShowDictWordDialogForSearchFragment();
		dialog.setArguments(args);
		dialog.setDelegate(this);
		dialog.show(getChildFragmentManager(), "ShowDictWordDialog");
	}
	
	private class DictionarySearchTask extends AsyncTask<String, Void, Integer>{
		public static final int STATUS_SUCCESS = 0;
		public static final int STATUS_FAILURE = 1;

		@Override
		protected Integer doInBackground(final String... search) {
			//do search
			final String searchStr = search[0];
			try {
				Thread.sleep(TIME_TO_WAIT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (isCancelled()) {
				return STATUS_FAILURE;
			}
			else{
				return search(searchStr);
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(final Integer result) {
			if (result == STATUS_SUCCESS) {
				postExecute();
			}
			else if (result == STATUS_FAILURE) {
				//fail
			}

		}
	}
		
	private int search(String searchStr) {
		searchResultHashmap = dictManager.searchWordToHashmap(searchStr);
		searchResult = new ArrayList<String>();
		for (String key : searchResultHashmap.keySet()){
			searchResult.add(key);
		}
		Collections.sort(searchResult);
		return DictionarySearchTask.STATUS_SUCCESS;
	}
	
	private void postExecute() {
		isFavoriteList = false;
		FavoriteWordPreference.removeFavoriteWordsPreferencesChangedListener(getActivity().getApplicationContext(),spChanged);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, searchResult);
		listView.setAdapter(adapter);
	}
		
	/***********************************************************************************/
	/***********************************************************************************/
	/***************************** Dictionary loading  *********************************/
	
	private static class DictionaryLoadingTaskLoader extends AbstractCacheAsyncTaskLoader<DictionaryManager>{

		Context mContext;
		public DictionaryLoadingTaskLoader(Context context) {
			super(context);
			mContext = context;
		}

		@Override
		public DictionaryManager loadInBackground() {
			ArrayList<String> listDictNames = new ArrayList<String>();
			ArrayList<String> listDictRealNames = new ArrayList<String>();
			String[] projection = new String[]{
												DictionaryItem.Columns.DICT_ID,
												DictionaryItem.Columns.TITLE,
											};
			StringBuilder selection = new StringBuilder();
			selection.append(DictionaryItem.Columns.STATUS).append(" = ? ");
			selection.append(" AND ").append(DictionaryItem.Columns.ACTIVE).append(" = ? ");
			
			String[] selectionArgs = new String[]{
				String.valueOf(DictionaryItem.STATUS_UNZIP_SUCESSFULL),
				String.valueOf(1),
			};
			
			ContentResolver cr = mContext.getContentResolver();
			Cursor cursor = cr.query(DictionaryItem.CONTENT_URI, projection, selection.toString(), selectionArgs, DictionaryItem.DEFAULT_SORT_ORDER);
			if (cursor != null){
				while(cursor.moveToNext()){
					String dictID = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.DICT_ID));
					String dictTitle = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.TITLE));
					listDictNames.add(dictID);
					listDictRealNames.add(dictTitle);
				}
			}
			DictionaryManager dictionaryManager = new DictionaryManager(null, listDictNames, listDictRealNames, new WeakReference<Context>(mContext));
			return dictionaryManager;
		}
	}
	
	private class DictionaryLoadingTaskLoaderCallbacks implements LoaderCallbacks<DictionaryManager>{

		@Override
		public Loader<DictionaryManager> onCreateLoader(int id, Bundle bundle) {
			editText.enableEditText(false);
			btnFavorite.setEnabled(false);
			final DictionaryLoadingTaskLoader loader = new DictionaryLoadingTaskLoader(getActivity().getApplicationContext());
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<DictionaryManager> loader, DictionaryManager dictionaryManager) {
			dictManager = dictionaryManager;
			editText.enableEditText(true);
			btnFavorite.setEnabled(true);
		}

		@Override
		public void onLoaderReset(Loader<DictionaryManager> loader) {
			dictManager = null;
		}
		
	}
	
	public DictionaryManager getDict(){
		return dictManager;
	}

	@Override
	public DictionaryManager getDictManager() {
		// TODO Auto-generated method stub
		return dictManager;
	}
		
	/***************************** Dictionary loading  *********************************/
	/***********************************************************************************/
	/***********************************************************************************/
	
	/***********************************************************************************/
	/***********************************************************************************/
	/***************************** Favorite words **************************************/
	SharedPreferences.OnSharedPreferenceChangeListener spChanged = new
            SharedPreferences.OnSharedPreferenceChangeListener() {
				@Override
				public void onSharedPreferenceChanged(
						SharedPreferences sharedPreferences, String key) {
					// TODO Auto-generated method stub
					if (isFavoriteList){
						onPreferenceChanged();
					}
				}

	};
	
	public void onFavoriteButtonClicked(){
		isFavoriteList = true;
		favoriteWords = FavoriteWordPreference.getFavoriteWords(getActivity().getApplicationContext());
		FavoriteWordPreference.setFavoriteWordsPreferencesChangedListener(getActivity().getApplicationContext(),spChanged);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, favoriteWords);
		listView.setAdapter(adapter);
	}
	
	public void onPreferenceChanged(){
		favoriteWords = FavoriteWordPreference.getFavoriteWords(getActivity().getApplicationContext());
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, favoriteWords);
		listView.setAdapter(adapter);
	}
	
	public void showFavoriteWordDialog(int position){
		String word = favoriteWords.get(position);
		word = word.toLowerCase(Locale.ENGLISH);
		Bundle args = new Bundle();
		args.putString(ShowDictWordDialog.ARGS_WORD, word);
		ShowDictWordDialog dialog = new ShowDictWordDialog();
		dialog.setDelegate(this);
		dialog.setArguments(args);
		dialog.show(getChildFragmentManager(), "ShowDictWordDialog");
	}

	@Override
	public DictionaryManager getDictionaryManager() {
		// TODO Auto-generated method stub
		return dictManager;
	}
	
	/***************************** Favorite words **************************************/
	/***********************************************************************************/
	/***********************************************************************************/

}
