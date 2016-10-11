package com.mZone.epro.toeic.fragment;

import com.mZone.epro.R;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup.OnStateChangedListener;
import com.mZone.epro.toeic.data.ToeicDataController;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentRowContentPart_5 extends ToeicFragmentRowAbstract
		implements OnStateChangedListener {

	static final int radioGroupID = R.id.rowRadioGroupSingle;
	private ToeicCustomRadioGroup radioGroup;
	public ToeicFragmentRowContentPart_5() {
		// TODO Auto-generated constructor stub
	}

	public ToeicFragmentRowContentPart_5(int partIndex) {
		super(partIndex);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_content_part_5, container, false);
		radioGroup = (ToeicCustomRadioGroup)rootView.findViewById(radioGroupID);
		return rootView;
	}
	@Override
	public void onPause (){
		super.onPause();
	}

	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRadioGroupSelectedState();
        if (radioGroup != null){
        	radioGroup.setDelegate(this);
        }
    }
	
	@Override
	public void onStateChanged(int questionID, int state) {
		// TODO Auto-generated method stub
		dataController.setRadioButtonSelectedState(questionID, state);
	}

	@Override
	void rowChanged(int rowID) {
		// TODO Auto-generated method stub
		this.rowID = rowID;
		setRadioGroupSelectedState();
	}
	
	private void setRadioGroupSelectedState(){
		if (radioGroup == null) return;
		int questionID = dataController.getQuestionID(ToeicDataController.TOEIC_PART_5, rowID, 0) - 1;
		int state = dataController.getRadioButtonSelectedState(questionID);
		int resultState = dataController.getCheckResultState(questionID);
		radioGroup.setCombineState(state, resultState);
		radioGroup.setQuestionID(questionID);
		radioGroup.setReadingText(dataController.getReadingQuestion(ToeicDataController.TOEIC_PART_5, rowID, 0));
	}

	@Override
	public void onCheckResultClicked(int rowID) {
		// TODO Auto-generated method stub
		if (this.rowID == rowID){
			setRadioGroupSelectedState();
		}
	}

}
