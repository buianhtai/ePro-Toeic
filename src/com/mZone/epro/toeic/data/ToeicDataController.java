package com.mZone.epro.toeic.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.testhisotry.data.TestHistoryItem;
import com.mZone.epro.toeic.activity.ToeicTestActivity;
import com.mZone.epro.toeic.dataStructure.ToeicBasicSentence;
import com.mZone.epro.toeic.dataStructure.ToeicListeningGroupQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicListeningQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicListeningScript;
import com.mZone.epro.toeic.dataStructure.ToeicListeningSubScript;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementParagraph;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTable;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTableRow;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTableRowCell;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTableRowCellText;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupQuestion;
import com.mZone.epro.toeic.parser.ToeicParser;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Xml;

public class ToeicDataController {

	//singleton
	private static ToeicDataController singleton = new ToeicDataController();
	
	//Toeic parser singleton
	ToeicParser toeicParser = ToeicParser.getInstance();
	
	//constant
	public static final int NUMBER_OF_PART = 7;
	public static final int TOEIC_PART_1 = 0;
	public static final int TOEIC_PART_2 = 1;
	public static final int TOEIC_PART_3 = 2;
	public static final int TOEIC_PART_4 = 3;
	public static final int TOEIC_PART_5 = 4;
	public static final int TOEIC_PART_6 = 5;
	public static final int TOEIC_PART_7 = 6;
	
	public static final int TOTAL_NUMBER_OF_QUESTION = 200;
	
	//local variable
	private String mDataPath;
	private int mTestMode;
	private String mTestHistoryFile;
	
	//check if data is already
	private boolean[] isLoadedArray = new boolean[NUMBER_OF_PART];
	
	private ArrayList<ToeicListeningQuestion> part1;
	private ArrayList<ToeicListeningQuestion> part2;
	private ArrayList<ToeicListeningGroupQuestion> part3;
	private ArrayList<ToeicListeningGroupQuestion> part4;
	private ArrayList<ToeicReadingElementQuestion> part5;
	private ArrayList<ToeicReadingGroupQuestion> part6;
	private ArrayList<ToeicReadingGroupQuestion> part7;
	
	
	//list viewpager index remember
	private int[][] rowPagerSelectedIndex = new int[4][]; //swiped page index
	private int[] radioButtonSelectedState = new int[TOTAL_NUMBER_OF_QUESTION];
	private int[] checkResultButtonSelectedState = new int[TOTAL_NUMBER_OF_QUESTION];
	
	public static final int RADIO_BUTTON_SELECTED_FIRST_STATE = -1;
	public static final int CHECK_RESULT_SELECTED_FIRST_STATE = -1;
	
	public ToeicDataController() {
		// TODO Auto-generated constructor stub
		for (int i = 0; i < NUMBER_OF_PART; i++){
			isLoadedArray[i] = false;
		}
		for (int i = 0; i < TOTAL_NUMBER_OF_QUESTION; i++){
			radioButtonSelectedState[i] = RADIO_BUTTON_SELECTED_FIRST_STATE;
			checkResultButtonSelectedState[i] = CHECK_RESULT_SELECTED_FIRST_STATE;
		}
	}
	
	//return singleton
	public static ToeicDataController getInstance(){
		if (singleton == null){
			singleton = new ToeicDataController();
		}
		return singleton;
	}
	
	//init for all data
	public void dataInit(String dataPath, int testMode, int testStatus){
		mDataPath = dataPath;
		mTestMode = testMode;
		this.testStatus = testStatus;		
		for (int i = 0; i < NUMBER_OF_PART; i++){
			isLoadedArray[i] = false;
		}
		for (int i = 0; i < TOTAL_NUMBER_OF_QUESTION; i++){
			radioButtonSelectedState[i] = RADIO_BUTTON_SELECTED_FIRST_STATE;
			checkResultButtonSelectedState[i] = CHECK_RESULT_SELECTED_FIRST_STATE;
		}
		toeicParser.dataInit(mDataPath, mTestMode);
	}
	
	public void dataInitForRestoreInstance(String dataPath, int testMode, int testStatus){
		mDataPath = dataPath;
		mTestMode = testMode;
		this.testStatus = testStatus;		
		toeicParser.dataInit(mDataPath, mTestMode);
	}
	
	public void setTestHistoryFile(String historyFile){
		this.mTestHistoryFile = historyFile;
	}
	
	

	public void releaseData(){
		if (part1 != null){
			for (int i = 0; i < part1.size(); i++){
				ToeicListeningQuestion question = part1.get(i);
				question.getImgBitmap().recycle();
			}
		}
	}
	
	public void getDataFromXML(int partIndex){
		if (!isLoadedArray[partIndex]){
			switch (partIndex) {
				case TOEIC_PART_1:
					initPart1();
					break;
				case TOEIC_PART_2:
					initPart2();
					break;
				case TOEIC_PART_3:
					initPart3();
					break;
				case TOEIC_PART_4:
					initPart4();
					break;
				case TOEIC_PART_5:
					initPart5();
					break;
				case TOEIC_PART_6:
					initPart6();
					break;
				case TOEIC_PART_7:
					initPart7();
					break;
				default:
					break;
			}
			isLoadedArray[partIndex] = true;
		}
	}
	
	private void initPart1(){
		part1 = toeicParser.parsingPart1();
		rowPagerSelectedIndex[TOEIC_PART_1] = new int[part1.size()];
		for (int i = 0; i < part1.size(); i++){
			part1.get(i).getImgBitmap();
			rowPagerSelectedIndex[TOEIC_PART_1][i] = 0;
		}
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_1, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part1.size(); i++){
				setCheckResultState(TOEIC_PART_1, i);
			}
		}
	}
	private void initPart2(){
		part2 = toeicParser.parsingPart2();
		rowPagerSelectedIndex[TOEIC_PART_2] = new int[part2.size()];
		for (int i = 0; i < part2.size(); i++){
			rowPagerSelectedIndex[TOEIC_PART_2][i] = 0;
		}
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_2, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part2.size(); i++){
				setCheckResultState(TOEIC_PART_2, i);
			}
		}
	}
	private void initPart3(){
		part3 = toeicParser.parsingPart3();
		rowPagerSelectedIndex[TOEIC_PART_3] = new int[part3.size()];
		for (int i = 0; i < part3.size(); i++){
			rowPagerSelectedIndex[TOEIC_PART_3][i] = 0;
		}
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_3, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part3.size(); i++){
				setCheckResultState(TOEIC_PART_3, i);
			}
		}
	}
	private void initPart4(){
		part4 = toeicParser.parsingPart4();
		rowPagerSelectedIndex[TOEIC_PART_4] = new int[part4.size()];
		for (int i = 0; i < part4.size(); i++){
			rowPagerSelectedIndex[TOEIC_PART_4][i] = 0;
		}
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_4, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part4.size(); i++){
				setCheckResultState(TOEIC_PART_4, i);
			}
		}
	}
	private void initPart5(){
		part5 = toeicParser.parsingPart5();
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_5, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part5.size(); i++){
				setCheckResultState(TOEIC_PART_5, i);
			}
		}
	}
	private void initPart6(){
		part6 = toeicParser.parsingPart6();
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_6, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part6.size(); i++){
				setCheckResultState(TOEIC_PART_6, i);
			}
		}
	}
	private void initPart7(){
		part7 = toeicParser.parsingPart7();
		if (mTestHistoryFile != null) readSavedTestFromXML(TOEIC_PART_7, mTestHistoryFile);
		if (testStatus == TestHistoryItem.STATUS_FINISH){
			for (int i = 0; i < part7.size(); i++){
				setCheckResultState(TOEIC_PART_7, i);
			}
		}
	}
	
	
	public Bitmap getBitmapInPart1(int rowID){
		return part1.get(rowID).getImgBitmap();
	}
	
	public int getNumberOfRowInListview(int partIndex){
		if (isLoadedArray[partIndex] == false) 
			return 0;
		switch (partIndex) {
			case TOEIC_PART_1:
				return part1.size();
			case TOEIC_PART_2:
				return part2.size();
			case TOEIC_PART_3:
				return part3.size();
			case TOEIC_PART_4:
				return part4.size();
			case TOEIC_PART_5:
				return part5.size();
			default:
				break;
		}
		return 0;
	}
	public String getStringForRowHeaderInListview(int partIndex, int rowID){
		if (isLoadedArray[partIndex] == false){
			return null;
		}
		String result = "";
		switch (partIndex) {
			case TOEIC_PART_1:
				int id = part1.get(rowID).getId();
				if (id < 10){
					result += "0";
				}
				result += String.valueOf(part1.get(rowID).getId());
				break;
			case TOEIC_PART_2:
				result = String.valueOf(part2.get(rowID).getId());
				break;
			case TOEIC_PART_3:
				result = part3.get(rowID).getStringForRowHeaderInListview();
				break;
			case TOEIC_PART_4:
				result = part4.get(rowID).getStringForRowHeaderInListview();
				break;
			case TOEIC_PART_5:
				result = String.valueOf(part5.get(rowID).getId());
				break;
			default:
				break;
		}
		return result + ".";
	}
	
	public int getSubScriptArraySize(int partIndex, int rowID){
		int result = 0;
		switch (partIndex) {
			case TOEIC_PART_3:
				result = part3.get(rowID).getScript().getSubScriptArraySize();
				break;
		}
		return result;
	}
	
	public ArrayList<ToeicListeningSubScript> getSubScriptArray(int partIndex, int rowID, int id){
		ArrayList<ToeicListeningSubScript> result = new ArrayList<ToeicListeningSubScript>();
		switch (partIndex) {
			case TOEIC_PART_1:
				result.add(part1.get(rowID).getScript().getSubScript(id));
				break;
			case TOEIC_PART_2:
				result.add(part2.get(rowID).getScript().getSubScript(id));
				break;
			case TOEIC_PART_3:
				result.add(part3.get(rowID).getScript().getSubScript(id));
				break;
			case TOEIC_PART_4:
				result = part4.get(rowID).getScript().getSubScriptArray();
				break;
			default:
				break;
		}
		return result;
	}
	
	public String getSubjectForContent(int rowID, int id){
		return part3.get(rowID).getScript().getSubScript(id).getSubject();
	}
	
	public int getIndexForScriptHighlightContent(int partIndex, int rowID, int currentTime){
		int result = -1;
		ToeicListeningScript script = null;
		switch (partIndex) {
			case TOEIC_PART_3:
				script = part3.get(rowID).getScript();
				break;
			case TOEIC_PART_4:
				script = part4.get(rowID).getScript();
				break;
			default:
				break;
		}
		for (int i = 0; i < script.getSubScriptArraySize(); i++){
			if (currentTime >= script.getSubScript(i).getVoiceStart() && currentTime <= script.getSubScript(i).getVoiceStop()){
				result = i;
				break;
			}
		}
		return result;
	}
	
	public int getRowPagerSelectedIndex(int partIndex, int rowID){
		if (partIndex > TOEIC_PART_4) return 0;
		return rowPagerSelectedIndex[partIndex][rowID];
	}
	public void  setRowPagerSelectedIndex(int partIndex, int rowID, int value){
		if (partIndex > TOEIC_PART_4) return;
		rowPagerSelectedIndex[partIndex][rowID] = value;
	}
	public int getRadioButtonSelectedState(int id){
		return radioButtonSelectedState[id];
	}
	public void setRadioButtonSelectedState(int id, int state){
		radioButtonSelectedState[id] = state;
	}
	
	public int getQuestionID(int partIndex, int rowID, int id){
		int result = -1;
		switch (partIndex) {
			case TOEIC_PART_1:
				result = part1.get(rowID).getId();
				break;
			case TOEIC_PART_2:
				result = part2.get(rowID).getId();
				break;
			case TOEIC_PART_3:
				result = part3.get(rowID).getQuestion(id).getId();
				break;
			case TOEIC_PART_4:
				result = part4.get(rowID).getQuestion(id).getId();
				break;
			case TOEIC_PART_5:
				result = part5.get(rowID).getId();
				break;
			case TOEIC_PART_6:
				ArrayList<ToeicReadingGroupQuestion.CheckResultPairValue> pairValueArray = part6.get(rowID).getReadingElementQuestionCheckResultMap();
				ToeicReadingGroupQuestion.CheckResultPairValue pair = pairValueArray.get(id);
				result = pair.questionID + 1;
				break;
			case TOEIC_PART_7:
				pairValueArray = part7.get(rowID).getReadingElementQuestionCheckResultMap();
				pair = pairValueArray.get(id);
				result = pair.questionID + 1;
				break;
			default:
				break;
		}
		return result;
	}
	
	public ToeicListeningQuestion getListeningQuestion(int partIndex, int rowID, int id){
		ToeicListeningQuestion question = null;
		switch (partIndex) {
			case TOEIC_PART_3:
				question = part3.get(rowID).getQuestion(id);
				break;
			case TOEIC_PART_4:
				question = part4.get(rowID).getQuestion(id);
				break;
			default:
				break;
		}
		return question;
	}
	
	public ToeicReadingElementQuestion getReadingQuestion(int partIndex, int rowID, int id){
		ToeicReadingElementQuestion question = null;
		switch (partIndex) {
			case TOEIC_PART_5:
				question = part5.get(rowID);
				break;
			default:
				break;
		}
		return question;
	}
	
	public String getAudioPath(int partIndex){
		String result = mDataPath + "part" + String.valueOf(partIndex + 1) + ".mp3";
		return result;
	}
	public int getAudioTimeStart(int partIndex, int rowID){
		int result = -1;
		switch (partIndex) {
			case TOEIC_PART_1:
				result = part1.get(rowID).getVoiceStart();
				break;
			case TOEIC_PART_2:
				result = part2.get(rowID).getVoiceStart();
				break;
			case TOEIC_PART_3:
				result = part3.get(rowID).getVoiceStart();
				break;
			case TOEIC_PART_4:
				result = part4.get(rowID).getVoiceStart();
				break;	
			default:
				break;
		}
		return result;
	}
	
	public int getScriptTimeStart(int partIndex, int rowID, int subID){
		int result = -1;
		switch (partIndex) {
			case TOEIC_PART_1:
				if (subID == -2){
					result = part1.get(rowID).getScript().getSubScript(part1.get(rowID).getScript().getSubScriptArraySize() - 1).getVoiceStop();
				}
				else{
					result = part1.get(rowID).getScript().getSubScript(subID).getVoiceStart();
				}
				break;
			case TOEIC_PART_2:
				if (subID == -2){
					result = part2.get(rowID).getScript().getSubScript(part2.get(rowID).getScript().getSubScriptArraySize() - 1).getVoiceStop();
				}
				else{
					result = part2.get(rowID).getScript().getSubScript(subID).getVoiceStart();
				}
				break;
			case TOEIC_PART_3:
				if (subID == -2){
					result = part3.get(rowID).getScript().getSubScript(part3.get(rowID).getScript().getSubScriptArraySize() - 1).getVoiceStop();
				}
				else{
					result = part3.get(rowID).getScript().getSubScript(subID).getVoiceStart();
				}
				break;
			case TOEIC_PART_4:
				if (subID == -2){
					result = part4.get(rowID).getScript().getSubScript(part4.get(rowID).getScript().getSubScriptArraySize() - 1).getVoiceStop();
				}
				else{
					result = part4.get(rowID).getScript().getSubScript(subID).getVoiceStart();
				}
				break;	
			default:
				break;
		}
		return result;
	}
	
	public int getScriptTimeStop(int partIndex, int rowID, int subID){
		int result = -1;
		switch (partIndex) {
			case TOEIC_PART_1:
				if (subID == -1){
					result = part1.get(rowID).getScript().getSubScript(0).getVoiceStart();
				}
				else{
					result = part1.get(rowID).getScript().getSubScript(subID).getVoiceStop();
				}
				break;
			case TOEIC_PART_2:
				if (subID == -1){
					result = part2.get(rowID).getScript().getSubScript(0).getVoiceStart();
				}
				else{
					result = part2.get(rowID).getScript().getSubScript(subID).getVoiceStop();
				}
				break;
			case TOEIC_PART_3:
				if (subID == -1){
					result = part3.get(rowID).getScript().getSubScript(0).getVoiceStart();
				}
				else{
					result = part3.get(rowID).getScript().getSubScript(subID).getVoiceStop();
				}
				break;
			case TOEIC_PART_4:
				if (subID == -1){
					result = part4.get(rowID).getScript().getSubScript(0).getVoiceStart();
				}
				else{
					result = part4.get(rowID).getScript().getSubScript(subID).getVoiceStop();
				}
				break;	
			default:
				break;
		}
		return result;
	}
	
	public int getAudioCurrentRowID(int partIndex, int currentTime){
		int result = -1;
		switch (partIndex) {
			case TOEIC_PART_1:
				if (part1 == null) return result;
				for (int i = 0; i < part1.size(); i++){
					ToeicListeningQuestion question = part1.get(i);
					int voiceStart = question.getVoiceStart();
					int voiceStop = question.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			case TOEIC_PART_2:
				if (part2 == null) return result;
				for (int i = 0; i < part2.size(); i++){
					ToeicListeningQuestion question = part2.get(i);
					int voiceStart = question.getVoiceStart();
					int voiceStop = question.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			case TOEIC_PART_3:
				if (part3 == null) return result;
				for (int i = 0; i < part3.size(); i++){
					ToeicListeningGroupQuestion question = part3.get(i);
					int voiceStart = question.getVoiceStart();
					int voiceStop = question.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			case TOEIC_PART_4:
				if (part4 == null) return result;
				for (int i = 0; i < part4.size(); i++){
					ToeicListeningGroupQuestion question = part4.get(i);
					int voiceStart = question.getVoiceStart();
					int voiceStop = question.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;	
			default:
				break;
		}
		return result;
	}
	
	public int getAudioCurrentSubID(int partIndex, int currentTime, int rowID){
		int result = -1;
		ToeicListeningScript script;
		switch (partIndex) {
			case TOEIC_PART_1:
				script = part1.get(rowID).getScript();
				if (currentTime < script.getSubScript(0).getVoiceStart()){
					break;
				}
				if (currentTime > script.getSubScript(script.getSubScriptArraySize() - 1).getVoiceStop()){
					result = -2;
					break;
				}
				for (int i = 0; i < script.getSubScriptArraySize(); i++){
					ToeicListeningSubScript subScript = script.getSubScript(i);
					int voiceStart = subScript.getVoiceStart();
					int voiceStop = subScript.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			case TOEIC_PART_2:
				script = part2.get(rowID).getScript();
				if (currentTime < script.getSubScript(0).getVoiceStart()){
					break;
				}
				if (currentTime > script.getSubScript(script.getSubScriptArraySize() - 1).getVoiceStop()){
					result = -2;
					break;
				}
				for (int i = 0; i < script.getSubScriptArraySize(); i++){
					ToeicListeningSubScript subScript = script.getSubScript(i);
					int voiceStart = subScript.getVoiceStart();
					int voiceStop = subScript.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			case TOEIC_PART_3:
				result = -1;
				script = part3.get(rowID).getScript();
				if (currentTime < script.getSubScript(0).getVoiceStart()){
					break;
				}
				if (currentTime > script.getSubScript(script.getSubScriptArraySize() - 1).getVoiceStop()){
					result = -2;
					break;
				}
				for (int i = 0; i < script.getSubScriptArraySize(); i++){
					ToeicListeningSubScript subScript = script.getSubScript(i);
					int voiceStart = subScript.getVoiceStart();
					int voiceStop = subScript.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			case TOEIC_PART_4:
				script = part4.get(rowID).getScript();
				if (currentTime < script.getSubScript(0).getVoiceStart()){
					break;
				}
				if (currentTime > script.getSubScript(script.getSubScriptArraySize() - 1).getVoiceStop()){
					result = -2;
					break;
				}
				for (int i = 0; i < script.getSubScriptArraySize(); i++){
					ToeicListeningSubScript subScript = script.getSubScript(i);
					int voiceStart = subScript.getVoiceStart();
					int voiceStop = subScript.getVoiceStop();
					if (currentTime >= voiceStart && currentTime <= voiceStop){
						result = i;
						break;
					}
				}
				break;
			default:
				break;
		}
		return result;
	}
	
	public static final int NEXT_TIME_STATE_ERROR = -1;
	public static final int NEXT_TIME_STATE_NEXT_PART = -2;
	public static final int NEXT_TIME_STATE_PREVIOUS_PART = -2;
	public int[] getForwardPlayTime(int partIndex, int currentT){
		int[] result = new int[2]; //0 is state (questionIndex) and 1 is time
		switch (partIndex) {
			case TOEIC_PART_1:
				for (int i = 0; i < part1.size(); i++){
					int questionTimeStart = part1.get(i).getVoiceStart();
					if (questionTimeStart > currentT){
						result[0] = i;
						result[1] = questionTimeStart;
						return result;
					}
				}
				break;
			case TOEIC_PART_2:
				for (int i = 0; i < part2.size(); i++){
					int questionTimeStart = part2.get(i).getVoiceStart();
					if (questionTimeStart > currentT){
						result[0] = i;
						result[1] = questionTimeStart;
						return result;
					}
				}
				break;
			case TOEIC_PART_3:
				for (int i = 0; i < part3.size(); i++){
					int questionTimeStart = part3.get(i).getVoiceStart();
					if (questionTimeStart > currentT){
						result[0] = i;
						result[1] = questionTimeStart;
						return result;
					}
				}
				break;
			case TOEIC_PART_4:
				for (int i = 0; i < part4.size(); i++){
					int questionTimeStart = part4.get(i).getVoiceStart();
					if (questionTimeStart > currentT){
						result[0] = i;
						result[1] = questionTimeStart;
						return result;
					}
				}
				break;
			default:
				break;
		}
		if (partIndex == TOEIC_PART_4) 
			result[0] = NEXT_TIME_STATE_ERROR;
		else 
			result[0] = NEXT_TIME_STATE_NEXT_PART;
		return result;
	}
	
	public int[] getBackwardPlayTime(int partIndex, int currentT){
		int[] result = new int[2]; //0 is state (questionIndex) and 1 is time
		switch (partIndex) {
			case TOEIC_PART_1:
				for (int i = part1.size()-1; i >= 0; i--){
					int questionTimeStop = part1.get(i).getVoiceStop();
					if (currentT > questionTimeStop){
						result[0] = i;
						result[1] = part1.get(i).getVoiceStart();
						return result;
					}
				}
				break;
			case TOEIC_PART_2:
				for (int i = part2.size()-1; i >= 0; i--){
					int questionTimeStop = part2.get(i).getVoiceStop();
					if (currentT > questionTimeStop){
						result[0] = i;
						result[1] = part2.get(i).getVoiceStart();
						return result;
					}
				}
				break;
			case TOEIC_PART_3:
				for (int i = part3.size()-1; i >= 0; i--){
					int questionTimeStop = part3.get(i).getVoiceStop();
					if (currentT > questionTimeStop){
						result[0] = i;
						result[1] = part3.get(i).getVoiceStart();
						return result;
					}
				}
				break;
			case TOEIC_PART_4:
				for (int i = part4.size()-1; i >= 0; i--){
					int questionTimeStop = part4.get(i).getVoiceStop();
					if (currentT > questionTimeStop){
						result[0] = i;
						result[1] = part4.get(i).getVoiceStart();
						return result;
					}
				}
				break;
			default:
				break;
		}
		if (partIndex == TOEIC_PART_1) 
			result[0] = NEXT_TIME_STATE_ERROR;
		else 
			result[0] = NEXT_TIME_STATE_PREVIOUS_PART;
		return result;
	}
	
	
	public ToeicReadingGroupQuestion getReadingGroupQuestion(int partIndex, int index){
		ToeicReadingGroupQuestion result = null;
		switch (partIndex) {
			case TOEIC_PART_6:
				result = part6.get(index);
				break;
			case TOEIC_PART_7:
				result = part7.get(index);
				break;		
			default:
				break;
		}
		return result;
	}
	
	public int getReadingGroupQuestionArraySize(int partIndex){
		int result = -1;
		switch (partIndex) {
			case TOEIC_PART_6:
				result = part6.size();
				break;
			case TOEIC_PART_7:
				result = part7.size();
				break;		
			default:
				break;
		}
		return result;
	}
	
	public String getReadingGroupQuestionTitle(int partIndex, int index){
		String result = "";
		ToeicReadingGroupQuestion groupQuestion = null;
		switch (partIndex) {
			case TOEIC_PART_6:
				groupQuestion = part6.get(index);
				break;
			case TOEIC_PART_7:
				groupQuestion = part7.get(index);
				break;		
			default:
				break;
		}
		if (groupQuestion != null){
			ArrayList<Integer> rangeQuestion = groupQuestion.getRangeQuestion();
			result = rangeQuestion.get(0) + "-" + rangeQuestion.get(rangeQuestion.size()-1);
		}
		return result;
	}
	
	public void setCheckResultState(int partIndex, int rowID){
		switch (partIndex) {
		case TOEIC_PART_1:
			ToeicListeningQuestion question = part1.get(rowID);
			int questionID = question.getId() - 1;
			checkResultButtonSelectedState[questionID] = question.getAns();
			break;
		case TOEIC_PART_2:
			question = part2.get(rowID);
			questionID = question.getId() - 1;
			checkResultButtonSelectedState[questionID] = question.getAns();
			break;
		case TOEIC_PART_3:
			ToeicListeningGroupQuestion groupQuestion = part3.get(rowID);
			for (int i = 0; i < groupQuestion.getNumberOfQuestionInGroup(); i++){
				question = groupQuestion.getQuestion(i);
				questionID = question.getId() - 1;
				checkResultButtonSelectedState[questionID] = question.getAns();
			}
			break;
		case TOEIC_PART_4:
			groupQuestion = part4.get(rowID);
			for (int i = 0; i < groupQuestion.getNumberOfQuestionInGroup(); i++){
				question = groupQuestion.getQuestion(i);
				questionID = question.getId() - 1;
				checkResultButtonSelectedState[questionID] = question.getAns();
			}
			break;
		case TOEIC_PART_5:
			ToeicReadingElementQuestion readingElementQuestion = part5.get(rowID);
			questionID = readingElementQuestion.getId() - 1;
			checkResultButtonSelectedState[questionID] = readingElementQuestion.getAns();
			break;
		case TOEIC_PART_6:
			ArrayList<ToeicReadingGroupQuestion.CheckResultPairValue> pairValueArray = part6.get(rowID).getReadingElementQuestionCheckResultMap();
			for (int i = 0; i < pairValueArray.size(); i++){
				ToeicReadingGroupQuestion.CheckResultPairValue pair = pairValueArray.get(i);
				checkResultButtonSelectedState[pair.questionID] = pair.ANS;
			}
			break;
		case TOEIC_PART_7:
			pairValueArray = part7.get(rowID).getReadingElementQuestionCheckResultMap();
			for (int i = 0; i < pairValueArray.size(); i++){
				ToeicReadingGroupQuestion.CheckResultPairValue pair = pairValueArray.get(i);
				checkResultButtonSelectedState[pair.questionID] = pair.ANS;
			}
			break;
		default:
			break;
		}
	}
	public int getCheckResultState(int questionID){
		return checkResultButtonSelectedState[questionID];
	}
		
	/*
	 * save and read saved test data from saved xml
	 */
	
	public void saveCurrentTest(String filePath){
		File file = new File(filePath);
		try {
			file.createNewFile();
			FileOutputStream f = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(f); 
			XmlSerializer xmlSerializer = Xml.newSerializer();
			xmlSerializer.setOutput(osw);
			xmlSerializer.startDocument("UTF-8", true);
			xmlSerializer.startTag("", "toeic");
			if (isLoadedArray[TOEIC_PART_1] == true){
				toeicParser.writeToXmlPart1(xmlSerializer, part1, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			if (isLoadedArray[TOEIC_PART_2] == true){
				toeicParser.writeToXmlPart2(xmlSerializer, part2, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			if (isLoadedArray[TOEIC_PART_3] == true){
				toeicParser.writeToXmlPart3(xmlSerializer, part3, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			if (isLoadedArray[TOEIC_PART_4] == true){
				toeicParser.writeToXmlPart4(xmlSerializer, part4, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			if (isLoadedArray[TOEIC_PART_5] == true){
				toeicParser.writeToXmlPart5(xmlSerializer, part5, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			if (isLoadedArray[TOEIC_PART_6] == true)
				toeicParser.writeToXmlPart6(xmlSerializer, part6, radioButtonSelectedState, checkResultButtonSelectedState);
			if (isLoadedArray[TOEIC_PART_7] == true)
				toeicParser.writeToXmlPart7(xmlSerializer, part7, radioButtonSelectedState, checkResultButtonSelectedState);
			xmlSerializer.endTag("", "toeic");
			xmlSerializer.endDocument();
			osw.flush();
		    osw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (this.mTestHistoryFile != null && !TextUtils.isEmpty(mTestHistoryFile)){
			try {
				copyXMLDataFromOriginalToNewFile(mDataPath + mTestHistoryFile, filePath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void copyXMLDataFromOriginalToNewFile(String originalPath, String newPath) throws FileNotFoundException, SAXException, IOException, ParserConfigurationException, TransformerException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc1 = db.parse(new FileInputStream(new File(originalPath)));
		Document doc2 = db.parse(new FileInputStream(new File(newPath)));
		Element rootElement = (Element) doc2.getElementsByTagName("toeic").item(0);
		if (isLoadedArray[TOEIC_PART_7] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_7_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.appendChild(copiedNode);
			}
			else{
				Element child = doc2.createElement(ToeicParser.PART_7_TAG);
				rootElement.appendChild(child);
			}
		}
		if (isLoadedArray[TOEIC_PART_6] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_6_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.insertBefore(copiedNode, doc2.getElementsByTagName(ToeicParser.PART_7_TAG).item(0));
			}
			else{
				Element child = doc2.createElement(ToeicParser.PART_6_TAG);
				rootElement.insertBefore(child, doc2.getElementsByTagName(ToeicParser.PART_7_TAG).item(0));
			}
			
		}
		if (isLoadedArray[TOEIC_PART_5] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_5_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.insertBefore(copiedNode, doc2.getElementsByTagName(ToeicParser.PART_6_TAG).item(0));
			}
			else{
				Element child = doc2.createElement(ToeicParser.PART_5_TAG);
				rootElement.insertBefore(child, doc2.getElementsByTagName(ToeicParser.PART_6_TAG).item(0));
			}
			
		}
		if (isLoadedArray[TOEIC_PART_4] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_4_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.insertBefore(copiedNode, doc2.getElementsByTagName(ToeicParser.PART_5_TAG).item(0));
			}
			else{
				Element child = doc2.createElement(ToeicParser.PART_4_TAG);
				rootElement.insertBefore(child, doc2.getElementsByTagName(ToeicParser.PART_5_TAG).item(0));
			}
		}
		if (isLoadedArray[TOEIC_PART_3] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_3_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.insertBefore(copiedNode, doc2.getElementsByTagName(ToeicParser.PART_4_TAG).item(0));
			}
			else{
				Element child = doc2.createElement(ToeicParser.PART_3_TAG);
				rootElement.insertBefore(child, doc2.getElementsByTagName(ToeicParser.PART_4_TAG).item(0));
			}
		}
		if (isLoadedArray[TOEIC_PART_2] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_2_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.insertBefore(copiedNode, doc2.getElementsByTagName(ToeicParser.PART_3_TAG).item(0));
			}
			else{
				Element child = doc2.createElement(ToeicParser.PART_2_TAG);
				rootElement.insertBefore(child, doc2.getElementsByTagName(ToeicParser.PART_3_TAG).item(0));
			}
		}
		if (isLoadedArray[TOEIC_PART_1] == false){
			NodeList list = doc1.getElementsByTagName(ToeicParser.PART_1_TAG);
			if (list.getLength() > 0){
				Element element = (Element) list.item(0);
				Node copiedNode = doc2.importNode(element, true);
				rootElement.insertBefore(copiedNode, doc2.getElementsByTagName(ToeicParser.PART_2_TAG).item(0));	
			}
		}
		
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        trans.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc2);
        StreamResult result = new StreamResult(new File(newPath));
        trans.transform(source, result);
	}
	
	public void readSavedTestFromXML(int partIndex, String filePath){
		if (filePath == null || TextUtils.isEmpty(filePath)){
			return;
		}
		switch (partIndex) {
			case TOEIC_PART_1:
				readSavedTestFromXMLPart1(mDataPath + filePath);
				break;
			case TOEIC_PART_2:
				readSavedTestFromXMLPart2(mDataPath + filePath);
				break;
			case TOEIC_PART_3:
				readSavedTestFromXMLPart3(mDataPath + filePath);
				break;
			case TOEIC_PART_4:
				readSavedTestFromXMLPart4(mDataPath + filePath);
				break;
			case TOEIC_PART_5:
				readSavedTestFromXMLPart5(mDataPath + filePath);
				break;
			case TOEIC_PART_6:
				readSavedTestFromXMLPart6(mDataPath + filePath);
				break;
			case TOEIC_PART_7:
				readSavedTestFromXMLPart7(mDataPath + filePath);
				break;
			default:
				break;
		}
	}
	
	public void readSavedTestFromXMLPart1(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_1_TAG))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_1_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
	            	int id = Integer.valueOf(xpp.getAttributeValue(0));
	            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
					int checkState = Integer.valueOf(xpp.getAttributeValue(2));
					radioButtonSelectedState[id - 1] = selectState;
					checkResultButtonSelectedState[id - 1] = checkState;
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
	            		eventType = xpp.next();
	            		tagName = xpp.getName();
	            	}
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSavedTestFromXMLPart2(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_2_TAG))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_2_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
	            	int id = Integer.valueOf(xpp.getAttributeValue(0));
	            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
					int checkState = Integer.valueOf(xpp.getAttributeValue(2));
					radioButtonSelectedState[id - 1] = selectState;
					checkResultButtonSelectedState[id - 1] = checkState;
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
	            		eventType = xpp.next();
	            		tagName = xpp.getName();
	            	}
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSavedTestFromXMLPart3(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_3_TAG)) || (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic"))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        int countGroupQuestion = 0;
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_3_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG)){
		            int countQuestionElement = 0;
		            while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG))){
		            	eventType = xpp.next();
			            tagName = xpp.getName();
			            if (eventType == XmlPullParser.START_TAG && tagName != null){
			            	if (tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
			            		int id = Integer.valueOf(xpp.getAttributeValue(0));
				            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
								int checkState = Integer.valueOf(xpp.getAttributeValue(2));
								radioButtonSelectedState[id - 1] = selectState;
								checkResultButtonSelectedState[id - 1] = checkState;
								int countResponse = 0;
								while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
									eventType = xpp.next();
						            tagName = xpp.getName();
									if (eventType == XmlPullParser.START_TAG && tagName != null){
										if (tagName.equals(ToeicParser.QUESTION_TAG)){
											while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_TAG))){
												eventType = xpp.next();
									            tagName = xpp.getName();
									            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
									            	part3.get(countGroupQuestion).getQuestion(countQuestionElement).getQuestion().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
									            }
											}									
										}
										else if (tagName.equals(ToeicParser.RESPONSE_TAG)){
											while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.RESPONSE_TAG))){
												eventType = xpp.next();
									            tagName = xpp.getName();
									            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
									            	part3.get(countGroupQuestion).getQuestion(countQuestionElement).getResponseAtIndex(countResponse).addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
									            }
											}
											countResponse++;
										}
									}
								}
								countQuestionElement++;
								
			            	}
			            }
		            }
		            countGroupQuestion++;
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void readSavedTestFromXMLPart4(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_4_TAG)) || (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic"))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        int countGroupQuestion = 0;
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_4_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG)){
		            int countQuestionElement = 0;
		            while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG))){
		            	eventType = xpp.next();
			            tagName = xpp.getName();
			            if (eventType == XmlPullParser.START_TAG && tagName != null){
			            	if (tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
			            		int id = Integer.valueOf(xpp.getAttributeValue(0));
				            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
								int checkState = Integer.valueOf(xpp.getAttributeValue(2));
								radioButtonSelectedState[id - 1] = selectState;
								checkResultButtonSelectedState[id - 1] = checkState;
								int countResponse = 0;
								while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
									eventType = xpp.next();
						            tagName = xpp.getName();
									if (eventType == XmlPullParser.START_TAG && tagName != null){
										if (tagName.equals(ToeicParser.QUESTION_TAG)){
											while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_TAG))){
												eventType = xpp.next();
									            tagName = xpp.getName();
									            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
									            	part4.get(countGroupQuestion).getQuestion(countQuestionElement).getQuestion().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
									            }
											}									
										}
										else if (tagName.equals(ToeicParser.RESPONSE_TAG)){
											while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.RESPONSE_TAG))){
												eventType = xpp.next();
									            tagName = xpp.getName();
									            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
									            	part4.get(countGroupQuestion).getQuestion(countQuestionElement).getResponseAtIndex(countResponse).addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
									            }
											}
											countResponse++;
										}
									}
								}
								countQuestionElement++;
								
			            	}
			            }
		            }
		            countGroupQuestion++;
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSavedTestFromXMLPart5(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_5_TAG)) || (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic"))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        int countQuestionElement = 0;
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_5_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	            	if (tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
	            		int id = Integer.valueOf(xpp.getAttributeValue(0));
		            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
						int checkState = Integer.valueOf(xpp.getAttributeValue(2));
						radioButtonSelectedState[id - 1] = selectState;
						checkResultButtonSelectedState[id - 1] = checkState;
						int countResponse = 0;
						while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
							eventType = xpp.next();
				            tagName = xpp.getName();
							if (eventType == XmlPullParser.START_TAG && tagName != null){
								if (tagName.equals(ToeicParser.QUESTION_TAG)){
									while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_TAG))){
										eventType = xpp.next();
							            tagName = xpp.getName();
							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
							            	part5.get(countQuestionElement).getQuestion().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
							            }
									}									
								}
								else if (tagName.equals(ToeicParser.RESPONSE_TAG)){
									while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.RESPONSE_TAG))){
										eventType = xpp.next();
							            tagName = xpp.getName();
							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
							            	part5.get(countQuestionElement).getResponseAtIndex(countResponse).addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
							            }
									}
									countResponse++;
								}
							}
						}
						countQuestionElement++;
	            	}
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSavedTestFromXMLPart6(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_6_TAG))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        int countGroupQuestion = 0;
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_6_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) return;
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG)){
	    	        int countToeicReadingElement = 0;
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG))){
	    	        	eventType = xpp.next();
	    	            tagName = xpp.getName();
	    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	            	if (tagName.equals(ToeicParser.IMAGE_NAME_TAG)){
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.IMAGE_NAME_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    	            		}
	    	            	}
	    	            	else if (tagName.equals(ToeicParser.PARAGRAPH_TAG)){
    		    	            ToeicReadingElementParagraph paragraphElement = (ToeicReadingElementParagraph)part6.get(countGroupQuestion).getElementAtIndex(countToeicReadingElement);
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PARAGRAPH_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
	    		    	            	paragraphElement.getContent().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
						            }
	    	            		}
	    	            	}
	    	            	else if (tagName.equals(ToeicParser.TABLE_TAG)){
	    	            		ToeicReadingElementTable tableElement = (ToeicReadingElementTable) part6.get(countGroupQuestion).getElementAtIndex(countToeicReadingElement);
	    	            		int countRow = 0;
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
						            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_ROW_TAG)){
						            	ToeicReadingElementTableRow tableRow = tableElement.getRowArray().get(countRow);
						            	int countColum = 0;
						            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_ROW_TAG))){
						            		eventType = xpp.next();
			    		    	            tagName = xpp.getName();
								            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_COLUM_TAG)){
								            	ToeicReadingElementTableRowCell cell = tableRow.getCell(countColum);
								            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_COLUM_TAG))){
								            		eventType = xpp.next();
		    							            tagName = xpp.getName();
		    							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
		    							            	((ToeicReadingElementTableRowCellText)cell).getContent().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
		    							            }
								            	}
								            	countColum++;
								            }
						            	}
						            	countRow++;
						            }
	    	            		}
	    	            	}
	    	            	else if (tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
	    	            		ToeicReadingElementQuestion questionElement = (ToeicReadingElementQuestion)part6.get(countGroupQuestion).getElementAtIndex(countToeicReadingElement);
	    	            		int id = Integer.valueOf(xpp.getAttributeValue(0));
	    		            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
	    						int checkState = Integer.valueOf(xpp.getAttributeValue(2));
	    						radioButtonSelectedState[id - 1] = selectState;
	    						checkResultButtonSelectedState[id - 1] = checkState;
	    	            		int countResponse = 0;
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    								if (tagName.equals(ToeicParser.QUESTION_TAG)){
	    									while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_TAG))){
	    										eventType = xpp.next();
	    							            tagName = xpp.getName();
	    							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
	    							            	questionElement.getQuestion().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
	    							            }
	    									}									
	    								}
	    								else if (tagName.equals(ToeicParser.RESPONSE_TAG)){
	    									while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.RESPONSE_TAG))){
	    										eventType = xpp.next();
	    							            tagName = xpp.getName();
	    							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
	    							            	questionElement.getResponseAtIndex(countResponse).addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
	    							            }
	    									}
	    									countResponse++;
	    								}
	    							}
	    	            		}
	    	            	}
	    	            	countToeicReadingElement++;
	    	            }
	    	        }
	    	        countGroupQuestion++;
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void readSavedTestFromXMLPart7(String filePath){
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();
	        File file = new File(filePath);
	        if (!file.exists()) return;
	        InputStream stream = new FileInputStream(file);
	        xpp.setInput(stream, null);
	        int eventType = xpp.getEventType();
	        String tagName = xpp.getName();
	        while (!(eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.PART_7_TAG))){
	        	if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) 
	        		return;
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	        }
	        int countGroupQuestion = 0;
	        while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PART_7_TAG))){
	        	eventType = xpp.next();
	            tagName = xpp.getName();
	            if (eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals("toeic")) return;
	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG)){
	    	        int countToeicReadingElement = 0;
	            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.GROUP_QUESTION_TAG))){
	    	        	eventType = xpp.next();
	    	            tagName = xpp.getName();
	    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    	            	if (tagName.equals(ToeicParser.IMAGE_TAG)){
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.IMAGE_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    	            		}
	    	            	}
	    	            	else if (tagName.equals(ToeicParser.PARAGRAPH_TAG)){
    		    	            ToeicReadingElementParagraph paragraphElement = (ToeicReadingElementParagraph)part7.get(countGroupQuestion).getElementAtIndex(countToeicReadingElement);
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.PARAGRAPH_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
	    		    	            	paragraphElement.getContent().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
						            }
	    	            		}
	    	            	}
	    	            	else if (tagName.equals(ToeicParser.TABLE_TAG)){
	    	            		ToeicReadingElementTable tableElement = (ToeicReadingElementTable) part7.get(countGroupQuestion).getElementAtIndex(countToeicReadingElement);
	    	            		int countRow = 0;
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
						            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_ROW_TAG)){
						            	ToeicReadingElementTableRow tableRow = tableElement.getRowArray().get(countRow);
						            	int countColum = 0;
						            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_ROW_TAG))){
						            		eventType = xpp.next();
			    		    	            tagName = xpp.getName();
								            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_COLUM_TAG)){
								            	ToeicReadingElementTableRowCell cell = tableRow.getCell(countColum);
								            	while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.TABLE_COLUM_TAG))){
								            		eventType = xpp.next();
		    							            tagName = xpp.getName();
		    							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
		    							            	((ToeicReadingElementTableRowCellText)cell).getContent().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
		    							            }
								            	}
								            	countColum++;
								            }
						            	}
						            	countRow++;
						            }
	    	            		}
	    	            	}
	    	            	else if (tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG)){
	    	            		ToeicReadingElementQuestion questionElement = (ToeicReadingElementQuestion)part7.get(countGroupQuestion).getElementAtIndex(countToeicReadingElement);
	    	            		int id = Integer.valueOf(xpp.getAttributeValue(0));
	    		            	int selectState = Integer.valueOf(xpp.getAttributeValue(1));
	    						int checkState = Integer.valueOf(xpp.getAttributeValue(2));
	    						radioButtonSelectedState[id - 1] = selectState;
	    						checkResultButtonSelectedState[id - 1] = checkState;
	    	            		int countResponse = 0;
	    	            		while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_ELEMTENT_TAG))){
	    	            			eventType = xpp.next();
	    		    	            tagName = xpp.getName();
	    		    	            if (eventType == XmlPullParser.START_TAG && tagName != null){
	    								if (tagName.equals(ToeicParser.QUESTION_TAG)){
	    									while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.QUESTION_TAG))){
	    										eventType = xpp.next();
	    							            tagName = xpp.getName();
	    							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
	    							            	questionElement.getQuestion().addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
	    							            }
	    									}									
	    								}
	    								else if (tagName.equals(ToeicParser.RESPONSE_TAG)){
	    									while (!(eventType == XmlPullParser.END_TAG && tagName != null && tagName.equals(ToeicParser.RESPONSE_TAG))){
	    										eventType = xpp.next();
	    							            tagName = xpp.getName();
	    							            if (eventType == XmlPullParser.START_TAG && tagName != null && tagName.equals(ToeicBasicSentence.HIGHLIGHT_ELEMENT_TAG)){
	    							            	questionElement.getResponseAtIndex(countResponse).addHighlightSpace(Integer.valueOf(xpp.getAttributeValue(0)), Integer.valueOf(xpp.getAttributeValue(1)));
	    							            }
	    									}
	    									countResponse++;
	    								}
	    							}
	    	            		}
	    	            	}
	    	            	countToeicReadingElement++;
	    	            }
	    	        }
	    	        countGroupQuestion++;
	            }
	        }
	        stream.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static final int LISTENING_QUESTION_NUMBER = 100;
	public static final int READING_QUESTION_NUMBER = 100;
	public static final int[] PART_QUESTION_NUMBER = {10, 30, 30, 30, 40, 12, 48};
	private int testStatus;
	public int[] onFinishTestListener(){
		int[] rightAns = new int[PART_QUESTION_NUMBER.length];
		for (int i = 0; i < PART_QUESTION_NUMBER.length; i++){
			rightAns[i] = 0;
			switch (i) {
				case TOEIC_PART_1:
					if (isLoadedArray[i] == false){
						initPart1();
					}
					if (part1 != null){
						for (int j = 0; j < part1.size(); j++){
							ToeicListeningQuestion question = part1.get(j);
							int questionID = question.getId() - 1;
							checkResultButtonSelectedState[questionID] = question.getAns();
							if (radioButtonSelectedState[questionID] == checkResultButtonSelectedState[questionID]){
								rightAns[i]++;
							}
						}
					}
					break;
				case TOEIC_PART_2:
					if (isLoadedArray[i] == false){
						initPart2();
					}
					if (part2 != null){
						for (int j = 0; j < part2.size(); j++){
							ToeicListeningQuestion question = part2.get(j);
							int questionID = question.getId() - 1;
							checkResultButtonSelectedState[questionID] = question.getAns();
							if (radioButtonSelectedState[questionID] == checkResultButtonSelectedState[questionID]){
								rightAns[i]++;
							}
						}
					}
					break;
				case TOEIC_PART_3:
					if (isLoadedArray[i] == false){
						initPart3();
					}
					if (part3 != null){
						for (int j = 0; j < part3.size(); j++){
							ToeicListeningGroupQuestion groupQuestion = part3.get(j);
							for (int k = 0; k < groupQuestion.getNumberOfQuestionInGroup(); k++){
								ToeicListeningQuestion question = groupQuestion.getQuestion(k);
								int questionID = question.getId() - 1;
								checkResultButtonSelectedState[questionID] = question.getAns();
								if (radioButtonSelectedState[questionID] == checkResultButtonSelectedState[questionID]){
									rightAns[i]++;
								}
							}
						}
					}
					break;
				case TOEIC_PART_4:
					if (isLoadedArray[i] == false){
						initPart4();
					}
					if (part4 != null){
						for (int j = 0; j < part4.size(); j++){
							ToeicListeningGroupQuestion groupQuestion = part4.get(j);
							for (int k = 0; k < groupQuestion.getNumberOfQuestionInGroup(); k++){
								ToeicListeningQuestion question = groupQuestion.getQuestion(k);
								int questionID = question.getId() - 1;
								checkResultButtonSelectedState[questionID] = question.getAns();
								if (radioButtonSelectedState[questionID] == checkResultButtonSelectedState[questionID]){
									rightAns[i]++;
								}
							}
						}
					}
					break;
				case TOEIC_PART_5:
					if (isLoadedArray[i] == false){
						initPart5();
					}
					if (part5 != null){
						for (int j = 0; j < part5.size(); j++){
							ToeicReadingElementQuestion readingElementQuestion = part5.get(j);
							int questionID = readingElementQuestion.getId() - 1;
							checkResultButtonSelectedState[questionID] = readingElementQuestion.getAns();
							if (radioButtonSelectedState[questionID] == checkResultButtonSelectedState[questionID]){
								rightAns[i]++;
							}
						}
					}
					break;
				case TOEIC_PART_6:
					if (isLoadedArray[i] == false){
						initPart6();
					}
					if (part6 != null){
						for (int j = 0; j < part6.size(); j++){
							ArrayList<ToeicReadingGroupQuestion.CheckResultPairValue> pairValueArray = part6.get(j).getReadingElementQuestionCheckResultMap();
							for (int k = 0; k < pairValueArray.size(); k++){
								ToeicReadingGroupQuestion.CheckResultPairValue pair = pairValueArray.get(k);
								checkResultButtonSelectedState[pair.questionID] = pair.ANS;
								if (radioButtonSelectedState[pair.questionID] == checkResultButtonSelectedState[pair.questionID]){
									rightAns[i]++;
								}
							}
						}
					}
					break;
				case TOEIC_PART_7:
					if (isLoadedArray[i] == false){
						initPart7();
					}
					if (part7 != null){
						for (int j = 0; j < part7.size(); j++){
							ArrayList<ToeicReadingGroupQuestion.CheckResultPairValue> pairValueArray = part7.get(j).getReadingElementQuestionCheckResultMap();
							for (int k = 0; k < pairValueArray.size(); k++){
								ToeicReadingGroupQuestion.CheckResultPairValue pair = pairValueArray.get(k);
								checkResultButtonSelectedState[pair.questionID] = pair.ANS;
								if (radioButtonSelectedState[pair.questionID] == checkResultButtonSelectedState[pair.questionID]){
									rightAns[i]++;
								}
							}
						}
					}
					break;
			}
		}
		this.testStatus = TestHistoryItem.STATUS_FINISH;
		this.mTestMode = ToeicTestActivity.TEST_MODE_PRACTICE;
		return rightAns;
	}

	public int getTestStatus() {
		return testStatus;
	}

	public void setTestStatus(int testStatus) {
		this.testStatus = testStatus;
	}
	
	
}
