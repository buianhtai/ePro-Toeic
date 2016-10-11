package com.mZone.epro.toeic.fragment;

import com.mZone.epro.R;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup.OnStateChangedListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentRowContentPart_4 extends ToeicFragmentRowAbstract
		implements OnStateChangedListener {

	static final int[] radioGroupID = {R.id.rowRadioGroup1, R.id.rowRadioGroup2, R.id.rowRadioGroup3};
	ToeicCustomRadioGroup[] radioGroupsArray;
	
	public ToeicFragmentRowContentPart_4() {
		// TODO Auto-generated constructor stub
	}

	public ToeicFragmentRowContentPart_4(int partIndex) {
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
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_content_part_3, container, false);
		radioGroupsArray = new ToeicCustomRadioGroup[radioGroupID.length];
		for (int i = 0; i < radioGroupID.length; i++){
			radioGroupsArray[i] = (ToeicCustomRadioGroup)rootView.findViewById(radioGroupID[i]);
		}
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
        if (radioGroupsArray != null){
        	for (int i = 0; i < radioGroupID.length; i++){
    			if (radioGroupsArray[i] != null){
    				radioGroupsArray[i].setDelegate(this);
    			}
        	}
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
		if (radioGroupsArray == null) return;
		for (int i = 0; i < radioGroupID.length; i++){
			if (radioGroupsArray[i] != null){
				int questionID = dataController.getQuestionID(partIndex, rowID, i) - 1;
				int state = dataController.getRadioButtonSelectedState(questionID);
				int resultState = dataController.getCheckResultState(questionID);
				radioGroupsArray[i].setCombineState(state, resultState);
				radioGroupsArray[i].setQuestionID(questionID);
				radioGroupsArray[i].setText(dataController.getListeningQuestion(partIndex, rowID, i));
			}
		}
	}

	@Override
	public void onCheckResultClicked(int rowID) {
		// TODO Auto-generated method stub
		if (this.rowID == rowID){
			setRadioGroupSelectedState();
		}
	}

}
