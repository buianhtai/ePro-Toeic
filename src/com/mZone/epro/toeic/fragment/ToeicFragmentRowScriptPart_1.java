package com.mZone.epro.toeic.fragment;

import com.mZone.epro.R;
import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customInterface.ToeicMediaListenerObserver;
import com.mZone.epro.toeic.customView.CustomWordClickableScriptTextview;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentRowScriptPart_1 extends ToeicFragmentRowAbstract implements ToeicMediaListenerObserver{
	
	private int[] textViewID = {R.id.rowScriptA, R.id.rowScriptB, R.id.rowScriptC, R.id.rowScriptD};
	private CustomWordClickableScriptTextview[] textViewArray = new CustomWordClickableScriptTextview[textViewID.length];
	public ToeicFragmentRowScriptPart_1() {
		// TODO Auto-generated constructor stub
	}

	public ToeicFragmentRowScriptPart_1(int partIndex, ToeicMediaListener listener) {
		super(partIndex, listener);
		// TODO Auto-generated constructor stub
		listener.registerObserver(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_script_part_1, container, false);
		for (int i = 0; i < textViewID.length; i++){
			textViewArray[i] = (CustomWordClickableScriptTextview)rootView.findViewById(textViewID[i]);
		}
		return rootView;
	}
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setText();
    }
	
	@Override
	void rowChanged(int rowID) {
		// TODO Auto-generated method stub
		this.rowID = rowID;
		if (textViewArray[0] != null) 
			setText();
	}

	private void setText(){
		int highlightIndex = -1;
		for (int i = 0; i < textViewID.length; i++){
			if (this.rowID == mediaListener.getScriptCurrentRowID() && i == mediaListener.getScriptCurrentSubID()){
				highlightIndex = 0;
			}
			else{
				highlightIndex = -1;
			}
			textViewArray[i].setText(dataController.getSubScriptArray(partIndex, rowID, i), highlightIndex);
		}
	}


	@Override
	public void setHighlight(int subID) {
		// TODO Auto-generated method stub
//		if (this.rowID == mediaListener.getScriptCurrentRowID()){
			setText();
//		}
	}

	@Override
	public void onCheckResultClicked(int rowID) {
		// TODO Auto-generated method stub
		
	}
}
