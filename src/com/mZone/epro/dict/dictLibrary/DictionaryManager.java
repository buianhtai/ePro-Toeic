package com.mZone.epro.dict.dictLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.mZone.epro.client.utility.DecompressZip;
import com.mZone.epro.client.utility.ExternalStorage;
import com.mZone.epro.launch.fragment.LaunchDictionaryFragment.WordDataExtenstion;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

public class DictionaryManager {

	private ArrayList<StarDict> listDictionary;
	private String originalPath;
	private ArrayList<String> listDictNames;
	private ArrayList<String> listDictRealNames;
	
	private static final String appDictionaryName = "English Free Dictionary";
	private static final String appDictionaryPath = "enfree";
	
	public DictionaryManager(String originalPath, ArrayList<String> listDictNames, ArrayList<String> listDictRealNames, WeakReference<Context> weakContext) {
		//init File for app dictionary (copy from assets)
		Context context = weakContext.get();
		final String source = ExternalStorage.getSDCacheDir(context.getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + appDictionaryPath + ".zip";
		File sourceZip = new File(source);
		if (!sourceZip.exists()){
			copyFileFromAssets(context);
			sourceZip = new File(source);
			if (sourceZip.exists()){
				File outputDir = ExternalStorage.getSDCacheDir(context.getApplicationContext(), "dictionary" );
				DecompressZip decomp = new DecompressZip( sourceZip.getPath(), 
						outputDir.getPath() + File.separator );
				if (decomp.unzip()){
				}
				else{
				}
			}
		}
//		this.originalPath = originalPath;
//		this.originalPath = Environment.getExternalStorageDirectory() + "/Download/toeic2";
		this.originalPath = ExternalStorage.getSDCacheDir(context.getApplicationContext(), "dictionary" ).toString();
		this.listDictNames = listDictNames;
		this.listDictRealNames = listDictRealNames;
		initDictData(context);
	}
	
	private void initDictData(Context context){
		listDictionary = new ArrayList<StarDict>(listDictNames.size() + 1);
		//init other dictionary
		for (int i = 0; i < listDictNames.size(); i++){
			String dictName = listDictNames.get(i);
			String dictPath = originalPath + "/" + dictName + "/";
			StarDict startDict = new StarDict(dictPath, dictName);
			listDictionary.add(startDict);
		}
		//init in app dictionary
		StarDict appDict = new StarDict(ExternalStorage.getSDCacheDir(context.getApplicationContext(), "dictionary" ).toString() + "/" + appDictionaryPath + "/", appDictionaryPath);
		listDictionary.add(appDict);
		listDictNames.add(appDictionaryPath);
		listDictRealNames.add(appDictionaryName);
	}
	
	public void releaseDict(){
		for (int i = 0; i < listDictionary.size(); i++) {
			StarDict dict = listDictionary.get(i);
			dict.releaseDict();
		}
	}
	
	public String Smart_Find_Word(String word){
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < listDictionary.size(); i++) {
			StarDict dict = listDictionary.get(i);
			String s = dict.Smart_Find_Word(word);
			if (s == null){
				s = "Cannot find this word";
			}
			else{
				if (!listDictNames.get(i).equals("mtBab-finalrelease")){
					String[] subString = s.split("\n");
					StringBuilder filterString = new StringBuilder();
					for (int k = 0; k < subString.length; k++){
						if (!subString[k].equals(word)){
							filterString.append(subString[k]);
						}
					}
					s = filterString.toString();
				}
			}
			result.append("<h2>" + listDictRealNames.get(i) + "</h2>");
			result.append(s);
		}
		return result.toString();
	}
	
	public ArrayList<String> searchWord(String searchStr){
		searchStr = searchStr.toLowerCase(Locale.ENGLISH);
		ArrayList<WordData> words = listDictionary.get(0).searchKeyword(searchStr);
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < words.size(); i++){
			result.add(words.get(i).word);
		}
		return result;
	}
	
	public HashMap<String, ArrayList<WordDataExtenstion>> searchWordToHashmap(String searchStr){
		searchStr = searchStr.toLowerCase(Locale.ENGLISH);
		HashMap<String, ArrayList<WordDataExtenstion>> result = new HashMap<String, ArrayList<WordDataExtenstion>>();
		for (int i = 0; i < listDictionary.size(); i++) {
			ArrayList<WordData> words = listDictionary.get(i).searchKeyword(searchStr);
			for (int j = 0; j < words.size(); j++){
				WordData wordData = words.get(j);
				if (result.containsKey(wordData.word)){
					ArrayList<WordDataExtenstion> object = result.get(wordData.word);
					object.add(new WordDataExtenstion(wordData, i));
					result.put(wordData.word, object);
				}
				else{
					ArrayList<WordDataExtenstion> object = new ArrayList<WordDataExtenstion>();
					object.add(new WordDataExtenstion(wordData, i));
					result.put(wordData.word, object);
				}
			}
		}
		return result;
	}
	
	public String getExplaination(ArrayList<WordDataExtenstion> wordDatas){
		StringBuffer result = new StringBuffer();
		int previousDictID = -1;
		for (int i = 0; i < wordDatas.size(); i++){
			WordDataExtenstion wordData = wordDatas.get(i);
			StarDict dict = listDictionary.get(wordData.dictionaryID);
			String s = dict.getExplanation(wordData.wordData);
			if (s == null){
				s = "Cannot find this word";
			}
			else{
				if (!listDictNames.get(wordData.dictionaryID).equals("mtBab-finalrelease")){
					String[] subString = s.split("\n");
					StringBuilder filterString = new StringBuilder();
					for (int k = 0; k < subString.length; k++){
						if (!subString[k].equals(wordData.wordData.word)){
							filterString.append(subString[k]);
						}
					}
					s = filterString.toString();
				}
			}
			if (wordData.dictionaryID != previousDictID){
				previousDictID = wordData.dictionaryID;
				result.append("<h2>" + listDictRealNames.get(wordData.dictionaryID) + "</h2>");
			}
			else{
				result.append("<br>");
			}
			result.append(s);
		}
		return result.toString();
	}
	
	private void copyFileFromAssets(Context context) {
	    AssetManager assetManager = context.getAssets();
	    final String source = ExternalStorage.getSDCacheDir(context.getApplicationContext(), Environment.DIRECTORY_DOWNLOADS).toString() + "/" + appDictionaryPath + ".zip";
        InputStream in = null;
        OutputStream out = null;
        try {
        	in = assetManager.open(appDictionaryPath + "/" + appDictionaryPath + ".zip");
        	File outFile = new File(source);
        	out = new FileOutputStream(outFile);
        	copyFile(in, out);
        	in.close();
        	in = null;
        	out.flush();
        	out.close();
        	out = null;
        } catch(IOException e) {
        }       

	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}

}
