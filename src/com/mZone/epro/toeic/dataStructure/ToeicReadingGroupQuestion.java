package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.toeic.parser.ToeicParser;

public class ToeicReadingGroupQuestion {

	private int numberOfQuestion;
	private String style;
	ArrayList<ToeicReadingGroupElement> elementArray;
	public ToeicReadingGroupQuestion() {
		// TODO Auto-generated constructor stub
		numberOfQuestion = -1;
		elementArray = new ArrayList<ToeicReadingGroupElement>();
	}
	public int getNumberOfQuestion() {
		return numberOfQuestion;
	}
	public void setNumberOfQuestion(int numberOfQuestion) {
		this.numberOfQuestion = numberOfQuestion;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public void addElementArray(ToeicReadingGroupElement element){
		elementArray.add(element);
	}
	public ToeicReadingGroupElement getElementAtIndex(int index){
		return elementArray.get(index);
	}
	public int getNumberOfElementView(){
		return elementArray.size();
	}
	public ArrayList<Integer> getRangeQuestion(){
		ArrayList<Integer> result = new ArrayList<Integer>(numberOfQuestion);
		for (int i = 0; i < elementArray.size(); i++){
			if (elementArray.get(i) instanceof ToeicReadingElementQuestion){
				ToeicReadingElementQuestion question = (ToeicReadingElementQuestion) elementArray.get(i);
				result.add(question.getId());
			}
		}
		return result;
	}
	
	public ArrayList<ToeicReadingElementQuestion> getReadingElementQuestionArray(){
		ArrayList<ToeicReadingElementQuestion> result = new ArrayList<ToeicReadingElementQuestion>(numberOfQuestion);
		for (int i = 0; i < elementArray.size(); i++){
			if (elementArray.get(i).getType() == ToeicReadingGroupElement.QUESTION_TYPE){
				result.add((ToeicReadingElementQuestion) elementArray.get(i));
			}
		}
		return result;
	}
	
	public static class CheckResultPairValue{
		public CheckResultPairValue(int id, int ans){
			questionID = id;
			ANS = ans;
		}
		public int questionID;
		public int ANS;
	}
	public ArrayList<CheckResultPairValue> getReadingElementQuestionCheckResultMap(){
		ArrayList<CheckResultPairValue> result = new ArrayList<CheckResultPairValue>(numberOfQuestion);
		for (int i = 0; i < elementArray.size(); i++){
			if (elementArray.get(i).getType() == ToeicReadingGroupElement.QUESTION_TYPE){
				ToeicReadingElementQuestion elementQuestion = (ToeicReadingElementQuestion) elementArray.get(i);
				CheckResultPairValue pair = new CheckResultPairValue(elementQuestion.getId() - 1, elementQuestion.getANS());
				result.add(pair);
			}
		}
		return result;
	}
	
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState){
		try {
			xmlSerializer.startTag("", ToeicParser.GROUP_QUESTION_TAG);
			for (int i = 0; i < elementArray.size(); i++){
				elementArray.get(i).writeHighLightArrayToXML(xmlSerializer, radioButtonSelectedState, checkResultButtonSelectedState);
			}
			xmlSerializer.endTag("", ToeicParser.GROUP_QUESTION_TAG);
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
