package com.mZone.epro.dict.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class FavoriteWordPreference {

	public static final String PREFS_NAME = "com.mZone.epro.dict.data.MyPreference.FavoriteWords";
	public static final Integer WORD_ACTIVE = 1;
	public static final Integer WORD_INACTIVE = -1;
	
	public static Integer getFavoriteWord(String word, Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		Integer favorite = pref.getInt(word, WORD_INACTIVE);
		return favorite;
	}
	
	public static void putFavoriteWord(String word, Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(word, WORD_ACTIVE);
		editor.commit();
	}
	
	public static void clearFavoriteWord(String word, Context mContext){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.remove(word);
		editor.commit();
	}
	
	public static ArrayList<String> getFavoriteWords(Context mContext){
		ArrayList<String> words = new ArrayList<String>();
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		
		@SuppressWarnings("unchecked")
		Map<String,Integer> keys = (Map<String, Integer>) pref.getAll();
		for(Map.Entry<String,Integer> entry : keys.entrySet()){
			if (entry.getValue() == WORD_ACTIVE){
				words.add(entry.getKey());
			}
		}
		Collections.sort(words);
		return words;
	}
	
	public static void setFavoriteWordsPreferencesChangedListener(Context mContext, SharedPreferences.OnSharedPreferenceChangeListener listener){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		pref.registerOnSharedPreferenceChangeListener(listener);
	}
	
	public static void removeFavoriteWordsPreferencesChangedListener(Context mContext, SharedPreferences.OnSharedPreferenceChangeListener listener){
		SharedPreferences pref = mContext.getSharedPreferences(PREFS_NAME, 0);
		pref.unregisterOnSharedPreferenceChangeListener(listener);
	}

}
