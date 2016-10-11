package com.mZone.epro.dict.dialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import com.mZone.epro.R;
import com.mZone.epro.dict.data.FavoriteWordPreference;
import com.mZone.epro.dict.dialog.ShowDictWordDialog.ShowDictWordDialogDelegate;
import com.mZone.epro.dict.dialog.ShowDictWordDialogForSearchFragment.ShowDictWordDialogForSearchFragmentDelegate;
import com.mZone.epro.dict.dictLibrary.DictionaryManager;
import com.mZone.epro.dict.dictionary.ClearableEditText;
import com.mZone.epro.dict.dictionary.ClearableEditText.ClearableEditTextListener;
import com.mZone.epro.launch.fragment.LaunchDictionaryFragment.WordDataExtenstion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DictionarySearchDialogFragment extends DialogFragment implements ClearableEditTextListener, ShowDictWordDialogForSearchFragmentDelegate, ShowDictWordDialogDelegate{

	public static interface DictionarySearchDialogFragmentInterface{
		public DictionaryManager getDictManager();
	}
	
	WeakReference<DictionarySearchDialogFragmentInterface> delegate;
	
	private static final int TIME_TO_WAIT = 100;
	
	//View
	private ClearableEditText editText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private Button btnFavorite;
	
	private DictionarySearchTask searchTask;
	private ArrayList<String> searchResult;
	private HashMap<String, ArrayList<WordDataExtenstion>> searchResultHashmap;
	
	//favorite
	private boolean isFavoriteList = false;
	private ArrayList<String> favoriteWords;
	
	public DictionarySearchDialogFragment() {
	}
	
	public void setDelegate(DictionarySearchDialogFragmentInterface delegate){
		this.delegate = new WeakReference<DictionarySearchDialogFragmentInterface>(delegate);
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View rootView = inflater.inflate(R.layout.toeic_dictionary_fragment, null);
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
		builder.setView(rootView);
		builder.setCancelable(false);
		builder.setNegativeButton(R.string.toeic_dictionary_fragment_close, null);
		builder.setTitle(R.string.toeic_dictionary_fragment_title);
		AlertDialog dialog = builder.create();
		return dialog;
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
		searchResultHashmap = this.delegate.get().getDictManager().searchWordToHashmap(searchStr);
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

	@Override
	public DictionaryManager getDictionaryManager() {
		return delegate.get().getDictManager();
	}

	@Override
	public DictionaryManager getDictManager() {
		return delegate.get().getDictManager();
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
		String content = delegate.get().getDictManager().getExplaination(wordData); 
		Bundle args = new Bundle();
		args.putString(ShowDictWordDialogForSearchFragment.ARGS_WORD, word);
		args.putString(ShowDictWordDialogForSearchFragment.ARGS_CONTENT, content);
		ShowDictWordDialogForSearchFragment dialog = new ShowDictWordDialogForSearchFragment();
		dialog.setArguments(args);
		dialog.setDelegate(this);
		dialog.show(getChildFragmentManager(), "ShowDictWordDialog");
	}
	
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
}
