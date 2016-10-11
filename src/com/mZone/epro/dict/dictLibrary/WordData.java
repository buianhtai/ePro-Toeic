package com.mZone.epro.dict.dictLibrary;

public class WordData {
	public String word;
	public int offset, size;
	public WordData() {
		// TODO Auto-generated constructor stub
		
	}
	public WordData(String w, int off, int si) {
		// TODO Auto-generated constructor stub
		word = w;
		offset = off;
		size = si;
	}
	
	public static class SubIndex{
		public String word;
		public int offset;
		public SubIndex(String word, int offset){
			this.word = word;
			this.offset = offset;
		}
	}

	@Override
	public String toString() {
		return "WordData [word=" + word + "]";
	}
}
