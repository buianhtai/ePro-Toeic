package com.mZone.epro.toeic.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.toeic.dataStructure.ToeicBasicSentence;
import com.mZone.epro.toeic.dataStructure.ToeicListeningGroupQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicListeningQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicListeningScript;
import com.mZone.epro.toeic.dataStructure.ToeicListeningSubScript;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementImage;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementParagraph;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTable;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTableRow;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTableRowCell;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTableRowCellText;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupElement;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicBasicSentence.Word;

public class ToeicParser {

	//singleton
	private static ToeicParser singleton = new ToeicParser();
	
	//local variable
	private String mDataPath;
//	private String xmlFilePath;
	
	//local variable for parser
	private XmlPullParser xpp;
	private InputStream stream;
	
	//constant for all part
	public static final String QUESTION_ELEMTENT_TAG = "question_element";
	public static final String ID_TAG = "id";
	public static final String IMAGE_TAG = "image";
	public static final String ANSWER_TAG = "answer";
	public static final String VOICE_FROM_TAG = "voice_from";
	public static final String VOICE_TO_TAG = "voice_to";
	public static final String SCRIPT_TAG = "script";
	public static final String SUBSCRIPT_TAG = "subscript";
	public static final String QUESTION_TAG = "question";
	public static final String RESPONSE_TAG = "response";
	public static final String SUBJECT_TAG = "subject";
	public static final String TEXT_TAG = "text";
	public static final String WORD_TAG = "word";
	public static final String WORD_START_TAG = "start_field";
	public static final String WORD_STOP_TAG = "stop_field";
	public static final String WORD_DICT_TAG = "dict_word";
	
	public static final String GROUP_QUESTION_TAG = "group_question";
	public static final String DATATEXT_TAG = "data_text";
	public static final String NUMBER_OF_QUESTION_TAG = "number_of_question";
	public static final String PARAGRAPH_TAG = "paragraph";
	public static final String PARAGRAPH_STYLE_PROP_TAG = "style";
	public static final String PARAGRAPH_TEXT_FONT_STYLE_TAG = "font_style";
	public static final String PARAGRAPH_TEXT_FONT_SIZE_TAG = "font_size";
	public static final String PARAGRAPH_TEXT_FONT_GRAVITY_TAG = "font_gravity";
	
	public static final String TEXTBOX_TAG = "textbox";
	public static final String TEXTBOX_STYLE_PROP_TAG = "style";
	public static final String TABLE_TAG = "table";
	public static final String TABLE_SIZE_PROP_TAG = "size";
	public static final String TABLE_BORDER_PROP_TAG = "border";
	public static final String TABLE_ROW_TAG = "row";
	public static final String TABLE_ROW_NUMBER_OF_COLUM_PROP_TAG = "number_of_colum";
	public static final String TABLE_ROW_BACKGROUND_PROP_TAG = "background";
	public static final String TABLE_COLUM_TAG = "colum";
	public static final String TABLE_COLUM_STYLE_PROP_TAG = "style";
	public static final String TABLE_COLUM_WIDTH_PROP_TAG = "width";
	public static final String TABLE_COLUM_TEXT_FONT_STYLE_PROP_TAG = "font_style";
	public static final String TABLE_COLUM_TEXT_FONT_SIZE_PROP_TAG = "font_size";
	public static final String TABLE_COLUM_TEXT_FONT_GRAVITY_PROP_TAG = "font_gravity";
	public static final String TABLE_COLUM_CONTENT_TAG = "content";
	public static final String TEXTBOX_IMAGE_TAG = "image";
	public static final String IMAGE_WIDTH_SIZE_PROP_TAG = "width_size";
	public static final String IMAGE_NAME_TAG = "name";
	
	//constant for different part
	public static final String PART_1_TAG = "part1";
	public static final String PART_2_TAG = "part2";
	public static final String PART_3_TAG = "part3";
	public static final String PART_4_TAG = "part4";
	public static final String PART_5_TAG = "part5";
	public static final String PART_6_TAG = "part6";
	public static final String PART_7_TAG = "part7";
	
	//for saving current data
	public static final String SELECT_STATE_TAG = "select_state";
	public static final String CHECK_STATE_TAG = "check_state";
	
	public static ToeicParser getInstance()
	{
		if (singleton == null){
			singleton = new ToeicParser();
		}
		return singleton;
	}
	
	public void dataInit(String dataPath, int testMode){
		mDataPath = dataPath;
//		xmlFilePath = mDataPath + "toeic.xml";
	}
	
	//init part 1
	public ArrayList<ToeicListeningQuestion> parsingPart1(){
		ArrayList<ToeicListeningQuestion> questionArray = new ArrayList<ToeicListeningQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part1.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_1_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	                
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_1_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG)){
	            	ToeicListeningQuestion question = new ToeicListeningQuestion();
	            	int id = Integer.valueOf(xpp.getAttributeValue(0));
	            	question.setId(id);
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG))){
	            		eventType = xpp.next();
	    	            tagName = xpp.getName();
	    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	            	if (tagName.equals(IMAGE_TAG)){
	    	            		xpp.next();
		    	            	String imgPath = mDataPath + xpp.getText();
		    	            	question.setImgPath(imgPath);
	    	            	}
	    	            	else if (tagName.equals(ANSWER_TAG)){
	    	            		xpp.next();
	    	            		int ans = Integer.valueOf(xpp.getText());
	    	            		question.setAns(ans);
	    	            	}
	    	            	else if (tagName.equals(VOICE_FROM_TAG)){
	    	            		xpp.next();
	    	            		int voiceFrom = Integer.valueOf(xpp.getText());
	    	            		question.setVoiceStart(voiceFrom);
	    	            	}
	    	            	else if (tagName.equals(VOICE_TO_TAG)){
	    	            		xpp.next();
	    	            		int voiceTo = Integer.valueOf(xpp.getText());
	    	            		question.setVoiceStop(voiceTo);
	    	            	}
	    	            	else if (tagName.equals(SCRIPT_TAG)){
	    	            		ToeicListeningScript script = new ToeicListeningScript();
	    	            		ArrayList<ToeicListeningSubScript> subScriptArray = new ArrayList<ToeicListeningSubScript>();
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(SCRIPT_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(RESPONSE_TAG)){
	    		    	            	xpp.next();
	    		    	            	String scripText = null;
	    		    	            	int voiceStart = -1;
	    		    	            	int voiceStop = -1;
	    		    	            	ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
	    		    	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(RESPONSE_TAG))){
	    		    	            		eventType = xpp.next();
	    	    		    	            tagName = xpp.getName();
	    	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	    		    	            	if (tagName.equals(VOICE_FROM_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		voiceStart = Integer.valueOf(xpp.getText());
	    	    		    	            	}
	    	    		    	            	else if (tagName.equals(VOICE_TO_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		voiceStop = Integer.valueOf(xpp.getText());
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(TEXT_TAG)){
	    	    		    	            		xpp.next();
		    	    		    	            	scripText = xpp.getText();
	    	    		    	            	}
	    	    		    	            	else if (tagName.equals(WORD_TAG)){
	    	    		    	            		xpp.next();xpp.next();xpp.next();
	    	    		    	            		int start = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		int stop = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		String word = xpp.getText();
	    	    		    	            		wordArray.add(new ToeicBasicSentence.Word(start, stop, word));
	    	    		    	            	}
	    	    		    	            }
	    		    	            	}
	    		    	            	ToeicBasicSentence response = new ToeicBasicSentence(scripText, wordArray);
	    		    	            	ToeicListeningSubScript subScript = new ToeicListeningSubScript();
	    		    	            	subScript.setVoiceStart(voiceStart);
	    		    	            	subScript.setVoiceStop(voiceStop);
	    		    	            	subScript.setText(response);
	    		    	            	subScriptArray.add(subScript);	    		    	            	
	    		    	            }	
	    	            		}
	    	            		script.setSubScriptArray(subScriptArray);
	    	            		question.setScript(script);
	    	            	}
	    	            }
	            	}
	            	questionArray.add(question);
	            }
	        }
	        
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionArray;
	}
	
	//init part 2
	public ArrayList<ToeicListeningQuestion> parsingPart2(){
		ArrayList<ToeicListeningQuestion> questionArray = new ArrayList<ToeicListeningQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part2.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_2_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	                
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_2_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG)){
	            	ToeicListeningQuestion question = new ToeicListeningQuestion();
	            	int id = Integer.valueOf(xpp.getAttributeValue(0));
	            	question.setId(id);
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG))){
	            		eventType = xpp.next();
	    	            tagName = xpp.getName();
	    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	            	if (tagName.equals(IMAGE_TAG)){
	    	            		xpp.next();
		    	            	String imgPath = mDataPath + xpp.getText();
		    	            	question.setImgPath(imgPath);
	    	            	}
	    	            	else if (tagName.equals(ANSWER_TAG)){
	    	            		xpp.next();
	    	            		int ans = Integer.valueOf(xpp.getText());
	    	            		question.setAns(ans);
	    	            	}
	    	            	else if (tagName.equals(VOICE_FROM_TAG)){
	    	            		xpp.next();
	    	            		int voiceFrom = Integer.valueOf(xpp.getText());
	    	            		question.setVoiceStart(voiceFrom);
	    	            	}
	    	            	else if (tagName.equals(VOICE_TO_TAG)){
	    	            		xpp.next();
	    	            		int voiceTo = Integer.valueOf(xpp.getText());
	    	            		question.setVoiceStop(voiceTo);
	    	            	}
	    	            	else if (tagName.equals(SCRIPT_TAG)){
	    	            		ToeicListeningScript script = new ToeicListeningScript();
	    	            		ArrayList<ToeicListeningSubScript> subScriptArray = new ArrayList<ToeicListeningSubScript>();
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(SCRIPT_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && (tagName.equals(RESPONSE_TAG) || tagName.equals(QUESTION_TAG))){
	    		    	            	xpp.next();
	    		    	            	String scripText = null;
	    		    	            	int voiceStart = -1;
	    		    	            	int voiceStop = -1;
	    		    	            	ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
	    		    	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && (tagName.equals(RESPONSE_TAG)|| tagName.equals(QUESTION_TAG)))){
	    		    	            		eventType = xpp.next();
	    	    		    	            tagName = xpp.getName();
	    	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	    		    	            	if (tagName.equals(VOICE_FROM_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		voiceStart = Integer.valueOf(xpp.getText());
	    	    		    	            	}
	    	    		    	            	else if (tagName.equals(VOICE_TO_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		voiceStop = Integer.valueOf(xpp.getText());
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(TEXT_TAG)){
	    	    		    	            		xpp.next();
		    	    		    	            	scripText = xpp.getText();
	    	    		    	            	}
	    	    		    	            	else if (tagName.equals(WORD_TAG)){
	    	    		    	            		xpp.next();xpp.next();xpp.next();
	    	    		    	            		int start = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		int stop = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		String word = xpp.getText();
	    	    		    	            		wordArray.add(new ToeicBasicSentence.Word(start, stop, word));
	    	    		    	            	}
	    	    		    	            }
	    		    	            	}
	    		    	            	ToeicBasicSentence response = new ToeicBasicSentence(scripText, wordArray);
	    		    	            	ToeicListeningSubScript subScript = new ToeicListeningSubScript();
	    		    	            	subScript.setVoiceStart(voiceStart);
	    		    	            	subScript.setVoiceStop(voiceStop);
	    		    	            	subScript.setText(response);
	    		    	            	subScriptArray.add(subScript);	    		    	            	
	    		    	            }	
	    	            		}
	    	            		script.setSubScriptArray(subScriptArray);
	    	            		question.setScript(script);
	    	            	}
	    	            }
	            	}
	            	questionArray.add(question);
	            }
	        }
	        
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionArray;
	}
	
	//init part 3
	public ArrayList<ToeicListeningGroupQuestion> parsingPart3(){
		ArrayList<ToeicListeningGroupQuestion> groupQuestionArray = new ArrayList<ToeicListeningGroupQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part3.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_3_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	                
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_3_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG)){
	            	ToeicListeningGroupQuestion groupQuestion = new ToeicListeningGroupQuestion();
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG))){
	            		eventType = xpp.next();
	    	            tagName = xpp.getName();
	    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	            	if (tagName.equals(VOICE_FROM_TAG)){
	    	            		xpp.next();
	    	            		int voiceFrom = Integer.valueOf(xpp.getText());
	    	            		groupQuestion.setVoiceStart(voiceFrom);
	    	            	}
	    	            	else if (tagName.equals(VOICE_TO_TAG)){
	    	            		xpp.next();
	    	            		int voiceTo = Integer.valueOf(xpp.getText());
	    	            		groupQuestion.setVoiceStop(voiceTo);
	    	            	}
	    	            	else if (tagName.equals(QUESTION_ELEMTENT_TAG)){
	    	            		ToeicListeningQuestion question = new ToeicListeningQuestion();
	    		            	int id = Integer.valueOf(xpp.getAttributeValue(0));
	    		            	question.setId(id);
	    		            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG))){
	    		            		eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    		    	            	 if (tagName.equals(ANSWER_TAG)){
	 	    	    	            		xpp.next();
	 	    	    	            		int ans = Integer.valueOf(xpp.getText());
	 	    	    	            		question.setAns(ans);
	 	    	    	            	}
	    		    	            	else if (tagName.equals(DATATEXT_TAG)){
	    		    	            		ArrayList<ToeicBasicSentence> responseArray = new ArrayList<ToeicBasicSentence>();
	    		    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(DATATEXT_TAG))){
	    		    	            			eventType = xpp.next();
	    		    		    	            tagName = xpp.getName();
	    		    		    	            boolean isQuestion = false;
	    		    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && (tagName.equals(RESPONSE_TAG) || tagName.equals(QUESTION_TAG))){
	    		    		    	            	if (tagName.equals(QUESTION_TAG)) 
	    		    		    	            		isQuestion = true;
	    		    		    	            	xpp.next();
	    		    		    	            	String text = null;
	    		    		    	            	ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
	    		    		    	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && (tagName.equals(RESPONSE_TAG)|| tagName.equals(QUESTION_TAG)))){
	    		    		    	            		eventType = xpp.next();
	    		    	    		    	            tagName = xpp.getName();
	    		    	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    		    	    		    	            	if(tagName.equals(TEXT_TAG)){
	    		    	    		    	            		xpp.next();
	    		    	    		    	            		text = xpp.getText();
	    		    	    		    	            	}
	    		    	    		    	            	else if (tagName.equals(WORD_TAG)){
	    		    	    		    	            		xpp.next();xpp.next();xpp.next();
	    		    	    		    	            		int start = Integer.valueOf(xpp.getText());
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		int stop = Integer.valueOf(xpp.getText());
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		String word = xpp.getText();
	    		    	    		    	            		wordArray.add(new ToeicBasicSentence.Word(start, stop, word));
	    		    	    		    	            	}
	    		    	    		    	            }
	    		    		    	            	}
	    		    		    	            	if (isQuestion){
	    		    		    	            		ToeicBasicSentence questionSetence = new ToeicBasicSentence(text, wordArray);
	    		    		    	            		question.setQuestion(questionSetence);
	    		    		    	            		isQuestion = false;
	    		    		    	            	}
	    		    		    	            	else{
	    		    		    	            		ToeicBasicSentence response = new ToeicBasicSentence(text, wordArray);
		    		    		    	            	responseArray.add(response);
	    		    		    	            	}
	    		    		    	            	
	    		    		    	            }
	    		    	            		}
	    		    	            		question.setResponseArray(responseArray);
	    		    	            	}
	    		    	            }
	    		            	}
	    		            	groupQuestion.addQuestion(question);
	    	            	}
	    	            	else if (tagName.equals(SCRIPT_TAG)){
	    	            		ToeicListeningScript script = new ToeicListeningScript();
	    	            		ArrayList<ToeicListeningSubScript> subScriptArray = new ArrayList<ToeicListeningSubScript>();
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(SCRIPT_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(SUBSCRIPT_TAG)){
	    		    	            	ToeicListeningSubScript subScript = new ToeicListeningSubScript();
	    		    	            	xpp.next();
	    		    	            	String scripText = null;
	    		    	            	ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
	    		    	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(SUBSCRIPT_TAG))){
	    		    	            		eventType = xpp.next();
	    	    		    	            tagName = xpp.getName();
	    	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	    		    	            	if(tagName.equals(VOICE_FROM_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		int voiceFrom = Integer.valueOf(xpp.getText());
	    	    		    	            		subScript.setVoiceStart(voiceFrom);
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(VOICE_TO_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		int voiceTo = Integer.valueOf(xpp.getText());
	    	    		    	            		subScript.setVoiceStop(voiceTo);
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(SUBJECT_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		subScript.setSubject(xpp.getText());
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(TEXT_TAG)){
	    	    		    	            		xpp.next();
		    	    		    	            	scripText = xpp.getText();
	    	    		    	            	}
	    	    		    	            	else if (tagName.equals(WORD_TAG)){
	    	    		    	            		xpp.next();xpp.next();xpp.next();
	    	    		    	            		int start = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		int stop = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		String word = xpp.getText();
	    	    		    	            		wordArray.add(new ToeicBasicSentence.Word(start, stop, word));
	    	    		    	            	}
	    	    		    	            }
	    		    	            	}
	    		    	            	ToeicBasicSentence subScriptText = new ToeicBasicSentence(scripText, wordArray);
	    		    	            	subScript.setText(subScriptText);
	    		    	            	subScriptArray.add(subScript);	    		    	            	
	    		    	            }	
	    	            		}
	    	            		script.setSubScriptArray(subScriptArray);
	    	            		groupQuestion.setScript(script);
	    	            	}
	    	            }
	            	}
	            	groupQuestionArray.add(groupQuestion);
	            }
	        }
	        
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupQuestionArray;
	}
	
	//init part 4
	public ArrayList<ToeicListeningGroupQuestion> parsingPart4(){
		ArrayList<ToeicListeningGroupQuestion> groupQuestionArray = new ArrayList<ToeicListeningGroupQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part4.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_4_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	                
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_4_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG)){
	            	ToeicListeningGroupQuestion groupQuestion = new ToeicListeningGroupQuestion();
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG))){
	            		eventType = xpp.next();
	    	            tagName = xpp.getName();
	    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	            	if (tagName.equals(VOICE_FROM_TAG)){
	    	            		xpp.next();
	    	            		int voiceFrom = Integer.valueOf(xpp.getText());
	    	            		groupQuestion.setVoiceStart(voiceFrom);
	    	            	}
	    	            	else if (tagName.equals(VOICE_TO_TAG)){
	    	            		xpp.next();
	    	            		int voiceTo = Integer.valueOf(xpp.getText());
	    	            		groupQuestion.setVoiceStop(voiceTo);
	    	            	}
	    	            	else if (tagName.equals(QUESTION_ELEMTENT_TAG)){
	    	            		ToeicListeningQuestion question = new ToeicListeningQuestion();
	    		            	int id = Integer.valueOf(xpp.getAttributeValue(0));
	    		            	question.setId(id);
	    		            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG))){
	    		            		eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    		    	            	 if (tagName.equals(ANSWER_TAG)){
	 	    	    	            		xpp.next();
	 	    	    	            		int ans = Integer.valueOf(xpp.getText());
	 	    	    	            		question.setAns(ans);
	 	    	    	            	}
	    		    	            	else if (tagName.equals(DATATEXT_TAG)){
	    		    	            		ArrayList<ToeicBasicSentence> responseArray = new ArrayList<ToeicBasicSentence>();
	    		    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(DATATEXT_TAG))){
	    		    	            			eventType = xpp.next();
	    		    		    	            tagName = xpp.getName();
	    		    		    	            boolean isQuestion = false;
	    		    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && (tagName.equals(RESPONSE_TAG) || tagName.equals(QUESTION_TAG))){
	    		    		    	            	if (tagName.equals(QUESTION_TAG)) 
	    		    		    	            		isQuestion = true;
	    		    		    	            	xpp.next();
	    		    		    	            	String text = null;
	    		    		    	            	ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
	    		    		    	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && (tagName.equals(RESPONSE_TAG)|| tagName.equals(QUESTION_TAG)))){
	    		    		    	            		eventType = xpp.next();
	    		    	    		    	            tagName = xpp.getName();
	    		    	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    		    	    		    	            	if(tagName.equals(TEXT_TAG)){
	    		    	    		    	            		xpp.next();
	    		    	    		    	            		text = xpp.getText();
	    		    	    		    	            	}
	    		    	    		    	            	else if (tagName.equals(WORD_TAG)){
	    		    	    		    	            		xpp.next();xpp.next();xpp.next();
	    		    	    		    	            		int start = Integer.valueOf(xpp.getText());
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		int stop = Integer.valueOf(xpp.getText());
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		
	    		    	    		    	            		xpp.next();xpp.next();
	    		    	    		    	            		String word = xpp.getText();
	    		    	    		    	            		wordArray.add(new ToeicBasicSentence.Word(start, stop, word));
	    		    	    		    	            	}
	    		    	    		    	            }
	    		    		    	            	}
	    		    		    	            	if (isQuestion){
	    		    		    	            		ToeicBasicSentence questionSetence = new ToeicBasicSentence(text, wordArray);
	    		    		    	            		question.setQuestion(questionSetence);
	    		    		    	            		isQuestion = false;
	    		    		    	            	}
	    		    		    	            	else{
	    		    		    	            		ToeicBasicSentence response = new ToeicBasicSentence(text, wordArray);
		    		    		    	            	responseArray.add(response);
	    		    		    	            	}
	    		    		    	            	
	    		    		    	            }
	    		    	            		}
	    		    	            		question.setResponseArray(responseArray);
	    		    	            	}
	    		    	            }
	    		            	}
	    		            	groupQuestion.addQuestion(question);
	    	            	}
	    	            	else if (tagName.equals(SCRIPT_TAG)){
	    	            		ToeicListeningScript script = new ToeicListeningScript();
	    	            		ArrayList<ToeicListeningSubScript> subScriptArray = new ArrayList<ToeicListeningSubScript>();
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(SCRIPT_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(SUBSCRIPT_TAG)){
	    		    	            	ToeicListeningSubScript subScript = new ToeicListeningSubScript();
	    		    	            	xpp.next();
	    		    	            	String scripText = null;
	    		    	            	ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
	    		    	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(SUBSCRIPT_TAG))){
	    		    	            		eventType = xpp.next();
	    	    		    	            tagName = xpp.getName();
	    	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	    		    	            	if(tagName.equals(VOICE_FROM_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		int voiceFrom = Integer.valueOf(xpp.getText());
	    	    		    	            		subScript.setVoiceStart(voiceFrom);
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(VOICE_TO_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		int voiceTo = Integer.valueOf(xpp.getText());
	    	    		    	            		subScript.setVoiceStop(voiceTo);
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(SUBJECT_TAG)){
	    	    		    	            		xpp.next();
	    	    		    	            		subScript.setSubject(xpp.getText());
	    	    		    	            	}
	    	    		    	            	else if(tagName.equals(TEXT_TAG)){
	    	    		    	            		xpp.next();
		    	    		    	            	scripText = xpp.getText();
	    	    		    	            	}
	    	    		    	            	else if (tagName.equals(WORD_TAG)){
	    	    		    	            		xpp.next();xpp.next();xpp.next();
	    	    		    	            		int start = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		int stop = Integer.valueOf(xpp.getText());
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		
	    	    		    	            		xpp.next();xpp.next();
	    	    		    	            		String word = xpp.getText();
	    	    		    	            		wordArray.add(new ToeicBasicSentence.Word(start, stop, word));
	    	    		    	            	}
	    	    		    	            }
	    		    	            	}
	    		    	            	ToeicBasicSentence subScriptText = new ToeicBasicSentence(scripText, wordArray);
	    		    	            	subScript.setText(subScriptText);
	    		    	            	subScriptArray.add(subScript);	    		    	            	
	    		    	            }	
	    	            		}
	    	            		script.setSubScriptArray(subScriptArray);
	    	            		groupQuestion.setScript(script);
	    	            	}
	    	            }
	            	}
	            	groupQuestionArray.add(groupQuestion);
	            }
	        }
	        
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupQuestionArray;
	}	
	
	public ArrayList<ToeicReadingElementQuestion> parsingPart5(){
		ArrayList<ToeicReadingElementQuestion> questionArray = new ArrayList<ToeicReadingElementQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part5.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_5_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_5_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(QUESTION_ELEMTENT_TAG)){
	            	ToeicReadingElementQuestion question = getQuestion();
            		question.setType(ToeicReadingGroupElement.QUESTION_TYPE);
            		questionArray.add(question);
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionArray;
	}
	
	public ArrayList<ToeicReadingGroupQuestion> parsingPart6(){
		ArrayList<ToeicReadingGroupQuestion> groupQuestionArray = new ArrayList<ToeicReadingGroupQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part6.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_6_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_6_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            ToeicReadingGroupQuestion groupQuestion = null;
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG)){
	            	groupQuestion = parseGroupQuestion();
	            }
	            if (groupQuestion != null) {
	            	groupQuestionArray.add(groupQuestion);
	            }
	        	
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupQuestionArray;
	}
	
	public ArrayList<ToeicReadingGroupQuestion> parsingPart7(){
		ArrayList<ToeicReadingGroupQuestion> groupQuestionArray = new ArrayList<ToeicReadingGroupQuestion>();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        this.xpp = factory.newPullParser();
	        File file = new File(mDataPath + "part7.xml");
	        this.stream = new FileInputStream(file);
	        xpp.setInput(stream, "UTF-8");
	        
	        System.out.println("Start document");
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(PART_7_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        xpp.next();
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PART_7_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            ToeicReadingGroupQuestion groupQuestion = null;
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG)){
	            	groupQuestion = parseGroupQuestion();
	            }
	            if (groupQuestion != null) {
	            	groupQuestionArray.add(groupQuestion);
	            }
	        	
	        }
//	        Log.e("test", "number of question  " + count);
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupQuestionArray;
	}
	
	private ToeicReadingGroupQuestion parseGroupQuestion(){
		ToeicReadingGroupQuestion groupQuestion = new ToeicReadingGroupQuestion();
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			if (xpp.getAttributeCount() > 0){
        		if (xpp.getAttributeName(0).equals(NUMBER_OF_QUESTION_TAG)){
        			groupQuestion.setNumberOfQuestion(Integer.valueOf(xpp.getAttributeValue(0)));
        		}
        	}
			while(!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(GROUP_QUESTION_TAG))){
	    		eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	            	if (tagName.equals(PARAGRAPH_TAG)){
	            		ToeicReadingElementParagraph paragraph = getParagraph();
	            		paragraph.setType(ToeicReadingGroupElement.PARAGRAPH_TYPE);
	            		groupQuestion.addElementArray(paragraph);
	            	}
	            	else if (tagName.equals( TABLE_TAG)){
	            		ToeicReadingElementTable table = getTable();
	            		table.setType(ToeicReadingGroupElement.TABLE_TYPE);
	            		groupQuestion.addElementArray(table);
	            	}
	            	else if (tagName.equals(IMAGE_TAG)){
	            		ToeicReadingElementImage image = getImageElement();
	            		image.setType(ToeicReadingGroupElement.IMAGE_TYPE);
	            		groupQuestion.addElementArray(image);
	            	}
	            	else if (tagName.equals(QUESTION_ELEMTENT_TAG)){
	            		ToeicReadingElementQuestion question = getQuestion();
	            		question.setType(ToeicReadingGroupElement.QUESTION_TYPE);
	            		groupQuestion.addElementArray(question);
	            	}
	            }
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groupQuestion;
	}
	
	private ToeicReadingElementParagraph getParagraph(){
		ToeicReadingElementParagraph paragraph = null;
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			int countAtribute = xpp.getAttributeCount();
			String style = null, fontStyle = null, fontSize = null, fontGravity = null;
			for (int i = 0; i < countAtribute; i++){
				if (xpp.getAttributeName(i).equals(PARAGRAPH_STYLE_PROP_TAG)){
					style = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(PARAGRAPH_TEXT_FONT_STYLE_TAG)){
					fontStyle = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(PARAGRAPH_TEXT_FONT_SIZE_TAG)){
					fontSize = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(PARAGRAPH_TEXT_FONT_GRAVITY_TAG)){
					fontGravity = xpp.getAttributeValue(i);
				}
			}
			paragraph = new ToeicReadingElementParagraph(style, fontStyle, fontSize, fontGravity);
			xpp.next(); //ending start tag
			eventType = xpp.next();
			tagName = xpp.getName();
			if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(TEXT_TAG)){
				String text = xpp.nextText();
				ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(PARAGRAPH_TAG))){
            		eventType = xpp.next();
        			tagName = xpp.getName();
        			if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(WORD_TAG)){
        				xpp.next();xpp.next();xpp.next();
        				int start = Integer.valueOf(xpp.getText());
                		xpp.next();xpp.next();
                		
                		xpp.next();xpp.next();
                		int stop = Integer.valueOf(xpp.getText());
                		xpp.next();xpp.next();
                		
                		xpp.next();xpp.next();
                		String word = xpp.getText();
                		xpp.next();xpp.next();
                		
                		wordArray.add(new Word(start, stop, word));
        			}
            	}
            	ToeicBasicSentence sentence = new ToeicBasicSentence(text, wordArray);
				paragraph.setContent(sentence);
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return paragraph;
	}
	
	private ToeicReadingElementTable getTable(){
		ToeicReadingElementTable table = null;
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			int countAtribute = xpp.getAttributeCount();
			String size = null;
			int border = 0;
			for (int i = 0; i < countAtribute; i++){
				if (xpp.getAttributeName(i).equals(TABLE_SIZE_PROP_TAG)){
					size = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(TABLE_BORDER_PROP_TAG)){
					border = Integer.valueOf(xpp.getAttributeValue(i));
				}
			}
			table = new ToeicReadingElementTable(size);
			table.setBorder(border);
			while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(TABLE_TAG))){
				eventType = xpp.next();
    			tagName = xpp.getName();
    			if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(TABLE_ROW_TAG)){
    				ToeicReadingElementTableRow row = null;
    				countAtribute = xpp.getAttributeCount();
    				int numberOfColum = -1;
    				String backgroundStyle = null;
    				for (int i = 0; i < countAtribute; i++){
    					if (xpp.getAttributeName(i).equals(TABLE_ROW_NUMBER_OF_COLUM_PROP_TAG)){
    						numberOfColum = Integer.valueOf(xpp.getAttributeValue(i));
    					}
    					else if (xpp.getAttributeName(i).equals(TABLE_ROW_BACKGROUND_PROP_TAG)){
    						backgroundStyle = xpp.getAttributeValue(i);
    					}
    				}
    				row = new ToeicReadingElementTableRow(numberOfColum, backgroundStyle);
    				while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(TABLE_ROW_TAG))){
    					eventType = xpp.next();
    	    			tagName = xpp.getName();
    	    			if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(TABLE_COLUM_TAG)){
    	    				ToeicReadingElementTableRowCell cell = getCell();
    	    				row.addCell(cell);
    	    			}
    				}
    				table.addRow(row);
    				
    			}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return table;
	}
	
	private ToeicReadingElementTableRowCell getCell(){
		ToeicReadingElementTableRowCell cell = null;
		int countAtribute = xpp.getAttributeCount();
		String style = null;
		for (int i = 0; i < countAtribute; i++){
			if (xpp.getAttributeName(i).equals(TABLE_COLUM_STYLE_PROP_TAG)){
				style = xpp.getAttributeValue(i);
			}
		}
		if (style.equals(ToeicReadingElementTableRowCell.STYLE_TEXT_STR) || style.equals(ToeicReadingElementTableRowCell.STYLE_NORMAL_STR)){
			cell = getTextCell();
		}
		return cell;
		/*
		ToeicReadingElementTableRowCell cell = null;
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			int countAtribute = xpp.getAttributeCount();
			String style = null;
			for (int i = 0; i < countAtribute; i++){
				if (xpp.getAttributeName(i).equals(TABLE_COLUM_STYLE_PROP_TAG)){
					style = xpp.getAttributeValue(i);
				}
			}
			if (style.equals(ToeicReadingElementTableRowCell.STYLE_TEXT_STR)){
				cell = getTextCell();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return cell;
		*/
	}
	
	private ToeicReadingElementTableRowCellText getTextCell(){
		ToeicReadingElementTableRowCellText cell = null;
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			int countAtribute = xpp.getAttributeCount();
			String style = null, fontStyle = null, fontSize = null, fontGravity = null;
			float width = -1;
			for (int i = 0; i < countAtribute; i++){
				if (xpp.getAttributeName(i).equals(TABLE_COLUM_STYLE_PROP_TAG)){
					style = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(TABLE_COLUM_WIDTH_PROP_TAG)){
					width = Float.valueOf(xpp.getAttributeValue(i));
				}
				else if (xpp.getAttributeName(i).equals(TABLE_COLUM_TEXT_FONT_STYLE_PROP_TAG)){
					fontStyle = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(TABLE_COLUM_TEXT_FONT_SIZE_PROP_TAG)){
					fontSize = xpp.getAttributeValue(i);
				}
				else if (xpp.getAttributeName(i).equals(TABLE_COLUM_TEXT_FONT_GRAVITY_PROP_TAG)){
					fontGravity = xpp.getAttributeValue(i);
				}
			}
			cell = new ToeicReadingElementTableRowCellText(style, width, fontStyle, fontSize, fontGravity);
			while(!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(TEXT_TAG))){
				eventType = xpp.next();
				tagName = xpp.getName();
			}
			String text = xpp.nextText();
			ArrayList<ToeicBasicSentence.Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
        	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(TABLE_COLUM_CONTENT_TAG))){
        		eventType = xpp.next();
    			tagName = xpp.getName();
    			if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(WORD_TAG)){
    				xpp.next();xpp.next();xpp.next();
    				int start = Integer.valueOf(xpp.getText());
            		xpp.next();xpp.next();
            		
            		xpp.next();xpp.next();
            		int stop = Integer.valueOf(xpp.getText());
            		xpp.next();xpp.next();
            		
            		xpp.next();xpp.next();
            		String word = xpp.getText();
            		xpp.next();xpp.next();
            		wordArray.add(new Word(start, stop, word));
    			}
        	}
        	ToeicBasicSentence sentence = new ToeicBasicSentence(text, wordArray);
			cell.setContent(sentence);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cell;
	}
	
	private ToeicReadingElementImage getImageElement(){
		ToeicReadingElementImage imgElement = null;
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			int countAtribute = xpp.getAttributeCount();
			String widthStyle = null;
			for (int i = 0; i < countAtribute; i++){
				if (xpp.getAttributeName(i).equals(IMAGE_WIDTH_SIZE_PROP_TAG)){
					widthStyle = xpp.getAttributeValue(i);
				}
			}
			while(!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(IMAGE_TAG))){
				eventType = xpp.next();
				tagName = xpp.getName();
				if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(IMAGE_NAME_TAG)){
					String name = xpp.nextText();
					name = this.mDataPath + name;
					imgElement = new ToeicReadingElementImage(widthStyle, name);
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imgElement;
	}
	
	private ToeicReadingElementQuestion getQuestion(){
		ToeicReadingElementQuestion question = null;
		int eventType;
		String tagName;
		try {
			eventType = xpp.getEventType();
			tagName = xpp.getName();
			int countAtribute = xpp.getAttributeCount();
			int id = -1;
			for (int i = 0; i < countAtribute; i++){
				if (xpp.getAttributeName(i).equals(ID_TAG)){
					id = Integer.valueOf(xpp.getAttributeValue(i));
				}
			}
			xpp.next();xpp.next();xpp.next();
            int ans = Integer.valueOf(xpp.getText());
            question = new ToeicReadingElementQuestion(id, ans);
			while(!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(DATATEXT_TAG))){
				eventType = xpp.next();
				tagName = xpp.getName();
				if (eventType == XmlPullParser.START_TAG && tagName != null && (tagName.equals(QUESTION_TAG) || tagName.equals(RESPONSE_TAG))){
					xpp.next();xpp.next();
					String text = xpp.nextText();
					ArrayList<Word> wordArray = new ArrayList<ToeicBasicSentence.Word>();
					
					String saveTagName = tagName;
					while (!(eventType == XmlPullParser.END_TAG && tagName != null && (tagName.equals(QUESTION_TAG) || tagName.equals(RESPONSE_TAG)))){
		        		eventType = xpp.next();
		    			tagName = xpp.getName();
		    			if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(WORD_TAG)){
		    				xpp.next();xpp.next();xpp.next();
		    				int start = Integer.valueOf(xpp.getText());
		            		xpp.next();xpp.next();
		            		
		            		xpp.next();xpp.next();
		            		int stop = Integer.valueOf(xpp.getText());
		            		xpp.next();xpp.next();
		            		
		            		xpp.next();xpp.next();
		            		String word = xpp.getText();
		            		xpp.next();xpp.next();
		            		wordArray.add(new Word(start, stop, word));
		    			}
		        	}
					ToeicBasicSentence sentence = new ToeicBasicSentence(text, wordArray);
					if(saveTagName.equals(QUESTION_TAG)){
						question.setQuestion(sentence);
					}
					else if (saveTagName.equals(RESPONSE_TAG)){
						question.addResponse(sentence);
					}
				}
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return question;
	}
	
	public void writeToXmlPart1(XmlSerializer xmlSerializer, ArrayList<ToeicListeningQuestion> questionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try { 
			xmlSerializer.startTag("", PART_1_TAG);
			for (int i = 0; i < questionArray.size(); i++){
				int questionID = questionArray.get(i).getId();
				int selectState = radioButtonSelectedState[questionID - 1];
				int checkState = checkResultButtonSelectedState[questionID - 1];
				xmlSerializer.startTag("", QUESTION_ELEMTENT_TAG);
				xmlSerializer.attribute("", ID_TAG, String.valueOf(questionID));
				xmlSerializer.attribute("", SELECT_STATE_TAG, String.valueOf(selectState));
				xmlSerializer.attribute("", CHECK_STATE_TAG, String.valueOf(checkState));
				xmlSerializer.endTag("", QUESTION_ELEMTENT_TAG);
			}
			xmlSerializer.endTag("", PART_1_TAG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToXmlPart2(XmlSerializer xmlSerializer, ArrayList<ToeicListeningQuestion> questionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try { 
			xmlSerializer.startTag("", PART_2_TAG);
			for (int i = 0; i < questionArray.size(); i++){
				int questionID = questionArray.get(i).getId();
				int selectState = radioButtonSelectedState[questionID - 1];
				int checkState = checkResultButtonSelectedState[questionID - 1];
				xmlSerializer.startTag("", QUESTION_ELEMTENT_TAG);
				xmlSerializer.attribute("", ID_TAG, String.valueOf(questionID));
				xmlSerializer.attribute("", SELECT_STATE_TAG, String.valueOf(selectState));
				xmlSerializer.attribute("", CHECK_STATE_TAG, String.valueOf(checkState));
				xmlSerializer.endTag("", QUESTION_ELEMTENT_TAG);
			}
			xmlSerializer.endTag("", PART_2_TAG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToXmlPart3(XmlSerializer xmlSerializer, ArrayList<ToeicListeningGroupQuestion> groupQuestionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try { 
			xmlSerializer.startTag("", PART_3_TAG);
			for (int i = 0; i < groupQuestionArray.size(); i++){
				xmlSerializer.startTag("", GROUP_QUESTION_TAG);
				for (int j = 0; j < groupQuestionArray.get(i).getNumberOfQuestionInGroup(); j++){
					ToeicListeningQuestion question = groupQuestionArray.get(i).getQuestion(j);
					int questionID = question.getId();
					int selectState = radioButtonSelectedState[questionID - 1];
					int checkState = checkResultButtonSelectedState[questionID - 1];
					xmlSerializer.startTag("", QUESTION_ELEMTENT_TAG);
					xmlSerializer.attribute("", ID_TAG, String.valueOf(questionID));
					xmlSerializer.attribute("", SELECT_STATE_TAG, String.valueOf(selectState));
					xmlSerializer.attribute("", CHECK_STATE_TAG, String.valueOf(checkState));
					
					xmlSerializer.startTag("", DATATEXT_TAG);
					ToeicBasicSentence questionSentence = question.getQuestion();
					xmlSerializer.startTag("", QUESTION_TAG);
					questionSentence.writeHighLightArrayToXML(xmlSerializer);
					xmlSerializer.endTag("", QUESTION_TAG);
					ArrayList<ToeicBasicSentence> responseArray = question.getResponseArray();
					for (int k = 0; k < responseArray.size(); k++){
						xmlSerializer.startTag("", RESPONSE_TAG);
						responseArray.get(k).writeHighLightArrayToXML(xmlSerializer);
						xmlSerializer.endTag("", RESPONSE_TAG);
					}
					xmlSerializer.endTag("", DATATEXT_TAG);
					xmlSerializer.endTag("", QUESTION_ELEMTENT_TAG);
				}
				xmlSerializer.endTag("", GROUP_QUESTION_TAG);
			}
			xmlSerializer.endTag("", PART_3_TAG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToXmlPart4(XmlSerializer xmlSerializer, ArrayList<ToeicListeningGroupQuestion> groupQuestionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try { 
			xmlSerializer.startTag("", PART_4_TAG);
			for (int i = 0; i < groupQuestionArray.size(); i++){
				xmlSerializer.startTag("", GROUP_QUESTION_TAG);
				for (int j = 0; j < groupQuestionArray.get(i).getNumberOfQuestionInGroup(); j++){
					ToeicListeningQuestion question = groupQuestionArray.get(i).getQuestion(j);
					int questionID = question.getId();
					int selectState = radioButtonSelectedState[questionID - 1];
					int checkState = checkResultButtonSelectedState[questionID - 1];
					xmlSerializer.startTag("", QUESTION_ELEMTENT_TAG);
					xmlSerializer.attribute("", ID_TAG, String.valueOf(questionID));
					xmlSerializer.attribute("", SELECT_STATE_TAG, String.valueOf(selectState));
					xmlSerializer.attribute("", CHECK_STATE_TAG, String.valueOf(checkState));
					
					xmlSerializer.startTag("", DATATEXT_TAG);
					ToeicBasicSentence questionSentence = question.getQuestion();
					xmlSerializer.startTag("", QUESTION_TAG);
					questionSentence.writeHighLightArrayToXML(xmlSerializer);
					xmlSerializer.endTag("", QUESTION_TAG);
					ArrayList<ToeicBasicSentence> responseArray = question.getResponseArray();
					for (int k = 0; k < responseArray.size(); k++){
						xmlSerializer.startTag("", RESPONSE_TAG);
						responseArray.get(k).writeHighLightArrayToXML(xmlSerializer);
						xmlSerializer.endTag("", RESPONSE_TAG);
					}
					xmlSerializer.endTag("", DATATEXT_TAG);
					xmlSerializer.endTag("", QUESTION_ELEMTENT_TAG);
				}
				xmlSerializer.endTag("", GROUP_QUESTION_TAG);
			}
			xmlSerializer.endTag("", PART_4_TAG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToXmlPart5(XmlSerializer xmlSerializer, ArrayList<ToeicReadingElementQuestion> questionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try {
			xmlSerializer.startTag("", PART_5_TAG);
			for (int i = 0; i < questionArray.size(); i++){
				ToeicReadingElementQuestion question = questionArray.get(i);
				int questionID = question.getId();
				int selectState = radioButtonSelectedState[questionID - 1];
				int checkState = checkResultButtonSelectedState[questionID - 1];
				xmlSerializer.startTag("", QUESTION_ELEMTENT_TAG);
				xmlSerializer.attribute("", ID_TAG, String.valueOf(questionID));
				xmlSerializer.attribute("", SELECT_STATE_TAG, String.valueOf(selectState));
				xmlSerializer.attribute("", CHECK_STATE_TAG, String.valueOf(checkState));
				xmlSerializer.startTag("", DATATEXT_TAG);
				ToeicBasicSentence questionSentence = question.getQuestion();
				xmlSerializer.startTag("", QUESTION_TAG);
				questionSentence.writeHighLightArrayToXML(xmlSerializer);
				xmlSerializer.endTag("", QUESTION_TAG);
				ArrayList<ToeicBasicSentence> responseArray = question.getResponseArray();
				for (int k = 0; k < responseArray.size(); k++){
					xmlSerializer.startTag("", RESPONSE_TAG);
					responseArray.get(k).writeHighLightArrayToXML(xmlSerializer);
					xmlSerializer.endTag("", RESPONSE_TAG);
				}
				xmlSerializer.endTag("", DATATEXT_TAG);
				xmlSerializer.endTag("", QUESTION_ELEMTENT_TAG);
			}
			xmlSerializer.endTag("", PART_5_TAG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToXmlPart6(XmlSerializer xmlSerializer, ArrayList<ToeicReadingGroupQuestion> groupQquestionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try {
			xmlSerializer.startTag("", PART_6_TAG);
			for (int i = 0; i < groupQquestionArray.size(); i++){
				groupQquestionArray.get(i).writeHighLightArrayToXML(xmlSerializer, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			xmlSerializer.endTag("", PART_6_TAG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeToXmlPart7(XmlSerializer xmlSerializer, ArrayList<ToeicReadingGroupQuestion> groupQquestionArray, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try {
			xmlSerializer.startTag("", PART_7_TAG);
			for (int i = 0; i < groupQquestionArray.size(); i++){
				groupQquestionArray.get(i).writeHighLightArrayToXML(xmlSerializer, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			xmlSerializer.endTag("", PART_7_TAG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
