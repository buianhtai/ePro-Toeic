package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlSerializer;

public class ToeicBasicSentence {
	private String text;
	private ArrayList<Word> wordArray;
	private ArrayList<HighLightSpace> highlightSpaceArray = new ArrayList<ToeicBasicSentence.HighLightSpace>();
	
	public static final String HIGHLIGHT_ARRAY_TAG = "highlight_array";
	public static final String HIGHLIGHT_ELEMENT_TAG = "highlight_element";
	public static final String HIGHLIGHT_ELEMENT_START = "highlight_start";
	public static final String HIGHLIGHT_ELEMENT_STOP = "highlight_stop";
	
	//Word in sentence
	public static class Word{
		public int start_field;
		public int stop_field;
		public String word;
		public Word(int start, int stop, String w){
			start_field = start;
			stop_field = stop;
			word = w;
		}
	}
	
	public static class HighLightSpace{
		public int startHighlight;
		public int stopHighlight;
		public HighLightSpace(int start, int stop){
			startHighlight = start;
			stopHighlight = stop;
		}
	}
	
	
	//constructor
	public ToeicBasicSentence(String text, ArrayList<ToeicBasicSentence.Word> wArray)
	{
		this.text = text;
		if (wArray != null && wArray.size() > 0){
			wordArray = new ArrayList<ToeicBasicSentence.Word>(wArray.size());
			for (int i = 0; i < wArray.size(); i++){
				wordArray.add(wArray.get(i));
			}
		}
		else{
			wordArray = null;
		}
		
	}
	
	
	public String getText()
	{
		return this.text;
	}
	
	
	public void addHighlightSpace(int start, int stop){
		highlightSpaceArray.add(new HighLightSpace(start, stop));
	}
	public ArrayList<HighLightSpace> getHighLightSpaceArray(){
		return highlightSpaceArray;
	}
	
	public ArrayList<Word> getWordArray(){
		return this.wordArray;
	}
	
	public void removeHighlightSpace(){
		if (highlightSpaceArray.size() > 0){
			highlightSpaceArray.remove(highlightSpaceArray.size() - 1);
		}
	}
	
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer){
		try {
			xmlSerializer.startTag("", HIGHLIGHT_ARRAY_TAG);
			for (int i = 0; i < highlightSpaceArray.size(); i++){
				xmlSerializer.startTag("", HIGHLIGHT_ELEMENT_TAG);
				xmlSerializer.attribute("", HIGHLIGHT_ELEMENT_START, String.valueOf(highlightSpaceArray.get(i).startHighlight));
				xmlSerializer.attribute("", HIGHLIGHT_ELEMENT_STOP, String.valueOf(highlightSpaceArray.get(i).stopHighlight));
				xmlSerializer.endTag("", HIGHLIGHT_ELEMENT_TAG);
			}
			xmlSerializer.endTag("", HIGHLIGHT_ARRAY_TAG);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
