package com.mZone.epro.dict.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mZone.epro.R;
import com.mZone.epro.client.utility.AESHelper;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;
import com.mZone.epro.client.utility.Constants;
import com.mZone.epro.client.utility.JSONParser;
import com.mZone.epro.dict.data.DictionaryClientDataController;
import com.mZone.epro.dict.data.DictionaryItemServer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ServerDictionaryLanguageSelectionFragment extends ListFragment {
	
	public static final HashMap<String, String> languageMap;
	static{
		languageMap = new HashMap<String, String>();
		languageMap.put("ara", "Arabic");
		languageMap.put("chi", "Chinese");
		languageMap.put("dutch", "Dutch");
		languageMap.put("fr", "French");
		languageMap.put("id", "Indonesian");
		languageMap.put("ita", "Italian");
		languageMap.put("ja", "Japanese");
		languageMap.put("kor", "Korean");
		languageMap.put("por", "Portuguese");
		languageMap.put("tha", "Thai");
		languageMap.put("ur", "Urdu");
		languageMap.put("vi", "Vietnamese");
	};
	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setEmptyText("Cannot load data from server");
		final LoaderManager loaderManager = getActivity().getSupportLoaderManager();
		final GetDictionaryFromServerLoaderCallback callback = new GetDictionaryFromServerLoaderCallback();
		loaderManager.initLoader(R.id.dictionary_manager_acitivity_loading_from_server_task_loader_id, null, callback);
	}
	
	@Override
	public void onStart() {
		super.onStart();
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
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {
		Bundle args = new Bundle();
		args.putInt(ServerDictionarySelectionFragment.ARGS_LANGUAGE_POSITION, position);
		final DictionaryManagerActivity activity = (DictionaryManagerActivity)getActivity();
		activity.moveToDictionarySelectionFragment(args);
	}
	
	public static class GetDictionaryFromServerTaskLoader extends AbstractCacheAsyncTaskLoader<Integer>{
		
		public static final int STATUS_SUCCESS = 1;
		public static final int STATUS_ERROR = 0;
		
//		private Context mAppContext;
		
		public GetDictionaryFromServerTaskLoader(final Context context) {
			super(context);
//			mAppContext = context.getApplicationContext();
		}
		
		@Override
		public Integer loadInBackground() {
			Integer searchResult = STATUS_ERROR;
			List<NameValuePair> paramsSearch = new ArrayList<NameValuePair>();
			JSONParser jsonParser = new JSONParser();
			JSONArray dictionaryArray = jsonParser.makeHttpRequestArray(AESHelper.URLDescript(Constants.url_dictionary), "GET", paramsSearch);
			if (dictionaryArray != null){
				try {
					HashMap<String, ArrayList<String>> languageMap =  new HashMap<String, ArrayList<String>>();
					HashMap<String, DictionaryItemServer> dictionaryMap = new HashMap<String, DictionaryItemServer>();
					for (int i = 0; i < dictionaryArray.length(); i++){
						JSONObject dictionary = dictionaryArray.getJSONObject(i);
						String id = dictionary.getString(DictionaryItemServer.DICT_ID);
						String title = dictionary.getString(DictionaryItemServer.DICT_TITLE);
						String description = dictionary.getString(DictionaryItemServer.DICT_DES);
						JSONArray langs = dictionary.getJSONArray(DictionaryItemServer.DICT_LANG);
						String lang = langs.getString(langs.length() - 1);
						String url = dictionary.getString(DictionaryItemServer.DICT_URL);
						DictionaryItemServer item = new DictionaryItemServer(id, title, description, lang, url);
						dictionaryMap.put(id, item);
						ArrayList<String> ids = languageMap.get(lang);
						if (ids == null){
							ids = new ArrayList<String>();
						}
						ids.add(id);
						languageMap.put(lang, ids);
					}
					DictionaryClientDataController.getInstance().setDataFromServer(languageMap, dictionaryMap);
					searchResult = STATUS_SUCCESS;
				} catch (JSONException e) {
					e.printStackTrace();
					return searchResult;
				}
			}
			return searchResult;
		}
	}
	
	private class GetDictionaryFromServerLoaderCallback implements LoaderCallbacks<Integer>{

		@Override
		public Loader<Integer> onCreateLoader(final int id, final Bundle args) {
			//init loader
			setListShown(false);
			final GetDictionaryFromServerTaskLoader loader = new GetDictionaryFromServerTaskLoader(getActivity());
			return loader;
		}

		@Override
		public void onLoadFinished(final Loader<Integer> loader, Integer result) {
			String[] languageArray = DictionaryClientDataController.getInstance().getLanguagesArray();
			ArrayList<String> titles = new ArrayList<String>();
			for (int i = 0; i < languageArray.length; i++){
				titles.add("English - " + languageMap.get(languageArray[i]));
			}
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, titles);
			setListAdapter(adapter);
			setListShown(true);

		}

		@Override
		public void onLoaderReset(final Loader<Integer> loader) {	
			setListAdapter(null);
		} 
	 }
	
	public void onCancelLoader(){
		final LoaderManager loaderManager = getActivity().getSupportLoaderManager();
		loaderManager.destroyLoader(R.id.dictionary_manager_acitivity_loading_from_server_task_loader_id);
	}
}
