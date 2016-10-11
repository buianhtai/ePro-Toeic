package com.mZone.epro.toeic.fragment;

import com.mZone.epro.R;
import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customInterface.ToeicMediaListenerObserver;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup;
import com.mZone.epro.toeic.customView.ToeicResizableImageView;
import com.mZone.epro.toeic.customView.ToeicCustomRadioGroup.OnStateChangedListener;
import com.mZone.epro.toeic.data.ToeicDataController;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentRowContentPart_2 extends ToeicFragmentRowAbstract
		implements OnStateChangedListener, ToeicMediaListenerObserver{

	ToeicCustomRadioGroup radioGroup;
	ToeicResizableImageView imgView;
	
	public ToeicFragmentRowContentPart_2() {
		// TODO Auto-generated constructor stub
	}

	public ToeicFragmentRowContentPart_2(int partIndex) {
		super(partIndex);
		// TODO Auto-generated constructor stub
	}
	
	public ToeicFragmentRowContentPart_2(int partIndex, ToeicMediaListener listener) {
		super(partIndex, listener);
		// TODO Auto-generated constructor stub
		listener.registerObserver(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_content_part_2, container, false);
		radioGroup = (ToeicCustomRadioGroup)rootView.findViewById(R.id.rowRadioGroup);
		imgView = (ToeicResizableImageView)rootView.findViewById(R.id.rowTemporaryImage);
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
        radioGroup.setDelegate(this);
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
		if (radioGroup != null){
			int questionID = dataController.getQuestionID(ToeicDataController.TOEIC_PART_2, rowID, -1) - 1;
			int state = dataController.getRadioButtonSelectedState(questionID);
			int resultState = dataController.getCheckResultState(questionID);
			radioGroup.setCombineState(state, resultState);
			radioGroup.setQuestionID(questionID);
		}
		setImageView();
	}
	
	private void setImageView(){
		if (imgView != null){
			if (this.rowID == mediaListener.getScriptCurrentRowID()){
				imgView.setImageResource(R.drawable.temporary_listening_background_anim);
				AnimationDrawable imgViewAnimation = (AnimationDrawable) imgView.getDrawable();
				imgViewAnimation.start();
			}
			else{
				imgView.setImageResource(R.drawable.temporary_listening_background_0);
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

	@Override
	public void setHighlight(int subID) {
		// TODO Auto-generated method stub
		setImageView();
	}
}
