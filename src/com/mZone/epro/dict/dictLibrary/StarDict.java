package com.mZone.epro.dict.dictLibrary;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import java.util.List;

import com.mZone.epro.dict.dictLibrary.WordData.SubIndex;
import com.mZone.epro.dict.dictionary.Inflector;

class Location {
	public int offset;
	public int size;
}

public class StarDict {	

	private static final int SEARCH_INDEX = 0;
	private static final int SEARCH_INDEX_SUCCESS = 1;

	DictZipFile mContentFile;
	RandomAccessFile mIndexFile;	
	private ArrayList<SubIndex> subIndexArrays;
	
	public StarDict(String dictPath, String dictName) {
		try {
			File indexFile = new File(dictPath + dictName + ".idx");
			File dictdz = new File(dictPath + dictName + ".dict.dz");
			
			//load subindex
			InputStream subIndexInfoInStream = new FileInputStream(dictPath + dictName + ".idx.idx");
			loadSubIndex(subIndexInfoInStream);
			
			this.mContentFile = new DictZipFile(dictdz);
			this.mIndexFile = new RandomAccessFile(indexFile, "r");
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private byte[] getBytesFromInputStream(InputStream in) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024000];
			int bytesRead;
			while ((bytesRead = in.read(b)) != -1) {
			   bos.write(b, 0, bytesRead);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}
	
	public void releaseDict(){
		try {
			this.mContentFile.close();
			this.mIndexFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadSubIndex(InputStream idx){
		subIndexArrays = new ArrayList<WordData.SubIndex>();
		try {
			byte[] bytes = getBytesFromInputStream(idx);
			int i=0;
			int offset;
			offset = 0;
			while (i< bytes.length) {
				if (bytes[i] == 0) {
					
					String word = new String(bytes, offset, i-offset,"UTF-8");
					int indexOffset = ( ( (bytes[i+1] & 0xFF) << 24) |
									((bytes[i+2] & 0xFF) << 16) |
									((bytes[i+3] & 0xFF) << 8) |
									((bytes[i+4] & 0xFF) )) & 0xFFFFFFFF;
					SubIndex subIndex = new SubIndex(word, indexOffset);
					subIndexArrays.add(subIndex);
					offset = i+5;
					i+=4;
				}
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String Smart_Find_Word(String word) {
		WordData wordData = null;
		if (word.length()==0) return null;
		wordData = findWordIndex(word);
		if (wordData == null) {//if not found in dictionary
			Inflector inflector = Inflector.getInstance();
			List<String> list = inflector.getOriginal(word);//get all potential list word
			for (String s : list) {
				wordData = findWordIndex(s);
				if (wordData != null) break;
			}
		}
		
		if (wordData != null) {
			//get explanation
			return getExplanation(wordData);
		} else {
			return null;
		}
	}
	
	private WordData findWordIndex(String word){
		int left = 0;
		int right = subIndexArrays.size() - 1;
		int mid = 0;
		int status = SEARCH_INDEX;
		while (left <= right){
			mid = (left + right)/2;
			String leftWord = subIndexArrays.get(mid).word.toLowerCase(Locale.ENGLISH);
			String rightWord = leftWord;
			if (mid != subIndexArrays.size() - 1){
				rightWord = subIndexArrays.get(mid + 1).word.toLowerCase(Locale.ENGLISH);
			}
			if (leftWord.compareTo(word) <= 0 && rightWord.compareTo(word) >= 0){
				if (rightWord.compareTo(word) == 0){
					if (mid < subIndexArrays.size() - 1){
						mid++;
					}	
				}
				status = SEARCH_INDEX_SUCCESS;
				break;
			}
			else{
				if (leftWord.compareTo(word) < 0){
					left = mid + 1;
				}
				else{
					right = mid - 1;
				}
			}
		}
		
		ArrayList<WordData> words = null;
		if (status == SEARCH_INDEX_SUCCESS){
			words = getWordFromSubIndex(mid);
		}
		WordData wordData = null;
		if (words != null){
			wordData = findWordDataInArray(word, words);
		}
		return wordData;
	}
	
	private ArrayList<WordData> getWordFromSubIndex(int index){
		ArrayList<WordData> words = new ArrayList<WordData>();
		int wordOffset;
		int size;
		if (index == subIndexArrays.size() - 1){
			String wordStr = subIndexArrays.get(index).word;
			byte[] wordBytes = wordStr.getBytes(Charset.forName("UTF-8"));
			wordOffset = subIndexArrays.get(index).offset;
			size = wordBytes.length + 9;
			
		}
		else{
			wordOffset = subIndexArrays.get(index).offset;
			int rightOffset = subIndexArrays.get(index + 1).offset;
			size = rightOffset - wordOffset;
		}
		byte [] buffer = new byte[size];
		
		try {
			int i =0;
			int offset = 0;
			this.mIndexFile.seek(wordOffset);
			this.mIndexFile.read(buffer, 0, size);
			while (i < buffer.length) {
				if (buffer[i] == 0) {
					WordData word = new WordData();
					word.word = new String(buffer, offset, i-offset,"UTF-8");
					word.offset = ( ( (buffer[i+1] & 0xFF) << 24) |
									((buffer[i+2] & 0xFF) << 16) |
									((buffer[i+3] & 0xFF) << 8) |
									((buffer[i+4] & 0xFF) )) & 0xFFFFFFFF;
					word.size = (  ( (buffer[i+5] & 0xFF) << 24) |
							((buffer[i+6] & 0xFF) << 16) |
							( (buffer[i+7]& 0xFF) << 8) |
							( (buffer[i+8]& 0xFF) )) & 0xFFFFFFFF;
					words.add(word);
					offset = i+9;
					i+=8;
				}
				i++;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return words;
	}
	
	private WordData findWordDataInArray(String wordStr, ArrayList<WordData> words){
		int i = 0;
		int max = words.size() - 1;
		String w = "";
		int mid = 0;
		while( i<=max ) {
			mid = (i + max)/2;
			if (mid >= words.size()) return null;
			w = words.get(mid).word.toLowerCase(Locale.ENGLISH);
			if (w.compareTo(wordStr)>0) {
				max = mid-1;
			}
			else if(w.compareTo(wordStr)<0) {
				i = mid+1;
			} 
			else {
				return words.get(mid);
			}
		}
		return null;
	}
	
	public String getExplanation(WordData data) {
		Location l = new Location();
		String exp="";
		if (data!=null) {
			l.offset = data.offset;
			l.size = data.size;
			//get explanation
			byte [] buffer = new byte[l.size];
			this.mContentFile.seek(l.offset);
			try {
				this.mContentFile.read(buffer, l.size);
			}
			catch(Exception e) {
				buffer = null;
				exp = e.toString();
			}
			
			try {
				if (buffer == null) {
					exp = "Error when reading data\n"+exp;
				}
				else {
					exp = new String(buffer, "UTF8");
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			return data.word+"\n"+exp;
		} else {
			return exp;
		}
	}
	
	public ArrayList<WordData> searchKeyword(String word){
		int left = 0;
		int right = subIndexArrays.size() - 1;
		int mid = 0;
		int status = SEARCH_INDEX;
		while (left <= right){
			mid = (left + right)/2;
			String leftWord = subIndexArrays.get(mid).word.toLowerCase(Locale.ENGLISH);
			String rightWord = leftWord;
			if (mid != subIndexArrays.size() - 1){
				rightWord = subIndexArrays.get(mid + 1).word.toLowerCase(Locale.ENGLISH);
			}
			if (leftWord.compareTo(word) <= 0 && rightWord.compareTo(word) >= 0){
				if (rightWord.compareTo(word) == 0){
					if (mid < subIndexArrays.size() - 1){
						mid++;
					}	
				}
				status = SEARCH_INDEX_SUCCESS;
				break;
			}
			else{
				if (leftWord.compareTo(word) < 0){
					left = mid + 1;
				}
				else{
					right = mid - 1;
				}
			}
		}
		ArrayList<WordData> result = new ArrayList<WordData>();
		ArrayList<WordData> words = new ArrayList<WordData>();
		if (status == SEARCH_INDEX_SUCCESS){
			words.addAll(getWordFromSubIndex(mid));
		}
		for (int i = 0; i < words.size(); i++){
			WordData wordData = words.get(i);
			if (wordData.word.indexOf(word) == 0){
				result.add(wordData);
			}
		}
		
		//load more word if necessary
		if (words.size() > 0 && result.contains(words.get(words.size() - 1))){
			words = new ArrayList<WordData>();
			if (mid < subIndexArrays.size() - 1){
				words.addAll(getWordFromSubIndex(mid + 1));
			}
			for (int i = 0; i < words.size(); i++){
				WordData wordData = words.get(i);
				if (wordData.word.indexOf(word) == 0){
					result.add(wordData);
				}
				else {
					break;
				}
			}
		}
		return result;
	}
}