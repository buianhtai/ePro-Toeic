package com.mZone.epro.toeic.fragment;
import com.mZone.epro.R;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup;
import com.mZone.epro.toeic.customView.ToeicResizableImageView;
import com.mZone.epro.toeic.data.ToeicDataController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentRowContentPart_1 extends ToeicFragmentRowAbstract implements ToeicCustomRadioGroup.OnStateChangedListener{
	//use for image view
	ToeicResizableImageView imageView;
	ToeicCustomRadioGroup radioGroup;

	public ToeicFragmentRowContentPart_1() {
		// TODO Auto-generated constructor stub
	}
	public ToeicFragmentRowContentPart_1(int partIndex) {
		// TODO Auto-generated constructor stub
		super(partIndex);
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_content_part_1, container, false);
		imageView = (ToeicResizableImageView)rootView.findViewById(R.id.rowContentImage);
		radioGroup = (ToeicCustomRadioGroup)rootView.findViewById(R.id.rowRadioGroup);
		return rootView;
	}
	@Override
	public void onPause (){
		super.onPause();
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBitMap();
        setRadioGroupSelectedState();
        radioGroup.setDelegate(this);
    }
	@Override
	public void onStart(){
		super.onStart();
	}
	
	@Override
	void rowChanged(int rowID) {
		// TODO Auto-generated method stub
		this.rowID = rowID;
		setBitMap();
		setRadioGroupSelectedState();
	}
	
	private void setBitMap(){
		if (imageView != null){
			imageView.setImageBitmap(dataController.getBitmapInPart1(rowID));
		}
	}
	private void setRadioGroupSelectedState(){
		if (radioGroup != null){
			int questionID = dataController.getQuestionID(ToeicDataController.TOEIC_PART_1, rowID, -1) - 1;
			int state = dataController.getRadioButtonSelectedState(questionID);
			int resultState = dataController.getCheckResultState(questionID);
			radioGroup.setCombineState(state, resultState);
			radioGroup.setQuestionID(questionID);
		}
	}
	@Override
	public void onStateChanged(int questionID, int state) {
		// TODO Auto-generated method stub
		dataController.setRadioButtonSelectedState(questionID, state);
	}
	@Override
	public void onCheckResultClicked(int rowID) {
		// TODO Auto-generated method stub
		if (this.rowID == rowID){
			setRadioGroupSelectedState();
		}
	}
}
