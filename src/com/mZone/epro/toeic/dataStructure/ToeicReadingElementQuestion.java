package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.R;
import com.mZone.epro.toeic.customInterface.ToeicSingleCheckResultObserver;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup.OnStateChangedListener;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.parser.ToeicParser;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ToeicReadingElementQuestion extends ToeicReadingGroupElement implements OnStateChangedListener, ToeicSingleCheckResultObserver{

	private int id;
	private int ans;
	private ToeicBasicSentence question;
	private ArrayList<ToeicBasicSentence> responseArray;
	ToeicCustomRadioGroup elementQuestion;
	public ToeicReadingElementQuestion() {
		// TODO Auto-generated constructor stub
		id = -1;
		ans = -1;
		question = null;
		responseArray = null;
	}
	public ToeicReadingElementQuestion(int id, int ans, ToeicBasicSentence question, ArrayList<ToeicBasicSentence> responseArray){
		this.id = id;
		this.ans = ans;
		this.question = question;
		this.responseArray = responseArray;
	}
	public ToeicReadingElementQuestion(int id, int ans){
		this.id = id;
		this.ans = ans;
		this.question = null;
		this.responseArray = new ArrayList<ToeicBasicSentence>();
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAns() {
		return ans;
	}
	public void setAns(int ans) {
		this.ans = ans;
	}
	public ToeicBasicSentence getQuestion() {
		return question;
	}
	public void setQuestion(ToeicBasicSentence question) {
		this.question = question;
	}
	public ArrayList<ToeicBasicSentence> getResponseArray() {
		return responseArray;
	}
	public void setResponseArray(ArrayList<ToeicBasicSentence> responseArray) {
		this.responseArray = responseArray;
	}
	public ToeicBasicSentence getResponseAtIndex(int index){
		return responseArray.get(index);
	}
	public void addResponse(ToeicBasicSentence response){
		responseArray.add(response);
	}
	@Override
	public int getTypeOfElement() {
		// TODO Auto-generated method stub
		return ToeicReadingGroupElement.QUESTION_TYPE;
	}
	@Override
	public View getView(LayoutInflater inflater, Context mContext) {
		// TODO Auto-generated method stub
		LinearLayout rootView = (LinearLayout)inflater.inflate(R.layout.toeic_reading_element_question, null);
//		LinearLayout.LayoutParams params = (LayoutParams) rootView.getLayoutParams();
//		Resources rs = mContext.getResources();
//		int marginBottom = (int) TypedValue.applyDimension(
//		        TypedValue.COMPLEX_UNIT_PX,
//		        rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_layoutmargin_bottom), 
//		        rs.getDisplayMetrics()
//		);
//		params.bottomMargin = marginBottom;
//		rootView.setLayoutParams(params);
		elementQuestion = (ToeicCustomRadioGroup) rootView.findViewById(R.id.elementQuestion);
		ToeicDataController dataController = ToeicDataController.getInstance();
		int state = dataController.getRadioButtonSelectedState(id - 1);
		int resultState = dataController.getCheckResultState(id - 1);
		elementQuestion.setCombineState(state, resultState);
		elementQuestion.setQuestionID(id - 1);
		elementQuestion.setReadingText(this);
		elementQuestion.setDelegate(this);
		return rootView;
	}
	@Override
	public void refresh(){
	}
	
	public int getANS(){
		return ans;
	}
	@Override
	public void onStateChanged(int questionID, int state) {
		// TODO Auto-generated method stub
		ToeicDataController dataController = ToeicDataController.getInstance();
		dataController.setRadioButtonSelectedState(questionID, state);
	}
	@Override
	public void onCheckResultClicked(int rowID) {
		// TODO Auto-generated method stub
		ToeicDataController dataController = ToeicDataController.getInstance();
		int state = dataController.getRadioButtonSelectedState(id - 1);
		int resultState = dataController.getCheckResultState(id - 1);
		elementQuestion.setCombineState(state, resultState);
	}
	@Override
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState) {
		// TODO Auto-generated method stub
		try {
			xmlSerializer.startTag("", ToeicParser.QUESTION_ELEMTENT_TAG);
			xmlSerializer.attribute("", ToeicParser.ID_TAG, String.valueOf(id));
			xmlSerializer.attribute("", ToeicParser.SELECT_STATE_TAG, String.valueOf(radioButtonSelectedState[id - 1]));
			xmlSerializer.attribute("", ToeicParser.CHECK_STATE_TAG, String.valueOf(checkResultButtonSelectedState[id - 1]));
			xmlSerializer.startTag("", ToeicParser.DATATEXT_TAG);
			xmlSerializer.startTag("", ToeicParser.QUESTION_TAG);
			question.writeHighLightArrayToXML(xmlSerializer);
			xmlSerializer.endTag("", ToeicParser.QUESTION_TAG);
			for (int k = 0; k < responseArray.size(); k++){
				xmlSerializer.startTag("", ToeicParser.RESPONSE_TAG);
				responseArray.get(k).writeHighLightArrayToXML(xmlSerializer);
				xmlSerializer.endTag("", ToeicParser.RESPONSE_TAG);
			}
			xmlSerializer.endTag("", ToeicParser.DATATEXT_TAG);
			xmlSerializer.endTag("", ToeicParser.QUESTION_ELEMTENT_TAG);
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
