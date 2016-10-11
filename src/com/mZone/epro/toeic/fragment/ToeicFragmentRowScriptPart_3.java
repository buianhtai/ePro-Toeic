package com.mZone.epro.toeic.fragment;


import com.mZone.epro.R;
import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customInterface.ToeicMediaListenerObserver;
import com.mZone.epro.toeic.customView.CustomWordClickableScriptTextview;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToeicFragmentRowScriptPart_3 extends ToeicFragmentRowAbstract implements ToeicMediaListenerObserver{

	private int[] subject_id = {R.id.subject_1, R.id.subject_2, R.id.subject_3, R.id.subject_4, R.id.subject_5};
	private int[] content_id = {R.id.content_1, R.id.content_2, R.id.content_3, R.id.content_4, R.id.content_5};
	private int[] layout_id = {R.id.layout_1, R.id.layout_2, R.id.layout_3, R.id.layout_4, R.id.layout_5};
	private int[] divider_id = {R.id.divider_1, R.id.divider_2, R.id.divider_3, R.id.divider_4};
	private TextView[] subjectTextview = new TextView[subject_id.length]; 
	private CustomWordClickableScriptTextview[] contentTextview = new CustomWordClickableScriptTextview[content_id.length];
	private LinearLayout[] layout = new LinearLayout[layout_id.length];
	private View[] divider = new View[divider_id.length];
	public ToeicFragmentRowScriptPart_3() {
		// TODO Auto-generated constructor stub
	}

	public ToeicFragmentRowScriptPart_3(int partIndex, ToeicMediaListener listener) {
		super(partIndex, listener);
		// TODO Auto-generated constructor stub
		listener.registerObserver(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview_rowpager_script_part_3, container, false);
		for (int i = 0; i < subject_id.length; i++){
			subjectTextview[i] = (TextView)rootView.findViewById(subject_id[i]);
			contentTextview[i] = (CustomWordClickableScriptTextview)rootView.findViewById(content_id[i]);
			layout[i] = (LinearLayout)rootView.findViewById(layout_id[i]);
		}
		for (int i = 0; i < divider.length; i++){
			divider[i] = rootView.findViewById(divider_id[i]);
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
		if (subjectTextview[0] != null) 
			setText();
	}
	
	private void setText(){
		int highlightIndex = -1;
		int sizeOfSubScript = dataController.getSubScriptArraySize(partIndex, rowID);	
		
		for (int i = 0; i < subject_id.length; i++){
			if (i < sizeOfSubScript){
				subjectTextview[i].setText(dataController.getSubjectForContent(rowID, i));
				if (this.rowID == mediaListener.getScriptCurrentRowID() && i == mediaListener.getScriptCurrentSubID()){
					highlightIndex = 0;
				}
				else{
					highlightIndex = -1;
				}
				contentTextview[i].setText(dataController.getSubScriptArray(partIndex, rowID, i), highlightIndex);
				layout[i].setVisibility(View.VISIBLE);
				if (i > 0) divider[i - 1].setVisibility(View.VISIBLE);
			}
			else{
				if (i > 0) divider[i - 1].setVisibility(View.GONE);
				layout[i].setVisibility(View.GONE);
			}
			
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
