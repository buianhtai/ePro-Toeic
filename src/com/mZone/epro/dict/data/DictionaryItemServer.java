package com.mZone.epro.dict.data;

public class DictionaryItemServer {

	public static final String DICT_ID = "id";
	public static final String DICT_TITLE = "title";
	public static final String DICT_DES = "description";
	public static final String DICT_LANG = "langs";
	public static final String DICT_URL = "download-url";
	
	public String dictID;
	public String title;
	public String description;
	public String lang;
	public String url;
	
	public DictionaryItemServer(String dictID, String title, String description, String lang, String url) {
		super();
		this.dictID = dictID;
		this.title = title;
		this.description = description;
		this.lang = lang;
		this.url = url;
	}

	@Override
	public String toString() {
		return "DictionaryItemServer [dictID=" + dictID + ", title=" + title
				+ ", description=" + description + ", lang=" + lang + ", url="
				+ url + "]";
	}
	
}
