package com.mZone.epro.toeic.fragment;


import com.mZone.epro.R;
import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customInterface.ToeicMediaListenerObserver;
import com.mZone.epro.toeic.customView.CustomWordClickableScriptTextview;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentRowScriptPart_4 extends ToeicFragmentRowAbstract implements ToeicMediaListenerObserver{

	static final int content_id = R.id.content;
	CustomWordClickableScriptTextview contentTextview;
	public ToeicFragmentRowScriptPart_4() {
		// TODO Auto-generated constructor stub
	}
	
	public ToeicFragmentRowScriptPart_4(int partIndex, ToeicMediaListener listener) {
		super(partIndex, listener);
		// TODO Auto-generated constructor stub
		listener.registerObserver(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_script_part_4, container, false);
		contentTextview = (CustomWordClickableScriptTextview) rootView.findViewById(content_id);
		return rootView;
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setText();
    }
	
	@Override
	public void onPause (){
		super.onPause();
	}
	
	@Override
	void rowChanged(int rowID) {
		// TODO Auto-generated method stub
		this.rowID = rowID;
		if (contentTextview != null) 
			setText();
	}
	
	private void setText(){
		if (this.rowID == mediaListener.getScriptCurrentRowID()){
			contentTextview.setText(dataController.getSubScriptArray(partIndex, rowID, 0), mediaListener.getScriptCurrentSubID());
		}
		else{
			contentTextview.setText(dataController.getSubScriptArray(partIndex, rowID, 0), -1);
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
