package com.mZone.epro.dict.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DictionaryClientDataController {

	private static DictionaryClientDataController singleton = null;
	public static DictionaryClientDataController getInstance(){
		if (singleton == null){
			singleton = new DictionaryClientDataController();
		}
		return singleton;
	}
	
	private boolean isDataFromServerReady = false;
	private HashMap<String, ArrayList<String>> languageMap;
	private HashMap<String, DictionaryItemServer> dictionaryMap;
	private ArrayList<String> languageArray;
	
	public DictionaryClientDataController() {
		isDataFromServerReady = false;
		languageMap = new HashMap<String, ArrayList<String>>();
		dictionaryMap = new HashMap<String, DictionaryItemServer>();
		languageArray = new ArrayList<String>();
	}
	
	public void setDataFromServer(HashMap<String, ArrayList<String>> languageMap, HashMap<String, DictionaryItemServer> dictionaryMap){
		this.languageMap = languageMap;
		this.dictionaryMap = dictionaryMap;
		languageArray = new ArrayList<String>();
		for (String lang : languageMap.keySet()){
			languageArray.add(lang);
		}
		Collections.sort(languageArray);
		isDataFromServerReady = true;
	}
	
	public boolean isDataFromServerReady(){
		return isDataFromServerReady;
	}
	
	public String[] getLanguagesArray(){
		return languageArray.toArray(new String[languageArray.size()]);
	}
	
	public ArrayList<String> getDictionaryArray(int position){
		String lang = languageArray.get(position);
		ArrayList<String> dicts = languageMap.get(lang);
		return dicts;
	}
	
	public DictionaryItemServer getDictionaryFromID(String id){
		return dictionaryMap.get(id);
	}
	
	public DictionaryItemServer appFirstLoadGetDictionary(String languageCode){
		int position = languageArray.indexOf(languageCode);
		if (position >= 0){
			String firstDictID = languageMap.get(languageCode).get(0);
			return dictionaryMap.get(firstDictID);
		}
		return null;
		
	}

}
