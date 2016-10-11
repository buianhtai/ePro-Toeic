package com.mZone.epro.toeic.activity;

import com.mZone.epro.R;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.data.ToeicUtil;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTable;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupElement;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupQuestion;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

public class ToeicHorizontalSubviewTableElementAcitivity extends ActionBarActivity {

	public static final String INTENT_PART_INDEX = "ToeicHorizontalSubviewTableElementAcitivity.INTENT_PART_INDEX";
	public static final String INTENT_GROUP_QUESTION_INDEX = "ToeicHorizontalSubviewTableElementAcitivity.INTENT_GROUP_QUESTION_INDEX";
	public static final String INTENT_ELEMENT_INDEX = "ToeicHorizontalSubviewTableElementAcitivity.INTENT_ELEMENT_INDEX";
	
	private int partIndex;
	private int groupQuestionIndex;
	private int elementIndex;
	
	private ToeicDataController dataController = ToeicDataController.getInstance();
	private LayoutInflater inflater;
	LinearLayout layout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toeic_horizontal_subview_table_element);
		Intent intent = getIntent();
		partIndex = intent.getIntExtra(INTENT_PART_INDEX, -1);
		groupQuestionIndex = intent.getIntExtra(INTENT_GROUP_QUESTION_INDEX, -1);
		elementIndex = intent.getIntExtra(INTENT_ELEMENT_INDEX, -1);
		this.inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layout = (LinearLayout)findViewById(R.id.scrollViewLinearLayout);
		setupSubView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(
				R.menu.toeic_horizontal_subview_table_element_acitivity, menu);
		return true;
	}
	
	private void setupSubView(){
		ToeicReadingGroupQuestion groupQuestion = dataController.getReadingGroupQuestion(partIndex, groupQuestionIndex);
		ToeicReadingGroupElement element = groupQuestion.getElementAtIndex(elementIndex);
		if (element instanceof ToeicReadingElementTable){
			View subView = element.getView(inflater, this);
			if (subView != null) layout.addView(subView);
		}
	}
	
	public void showDictDialog(String word){
		if (ToeicUtil.isPracticeMode()){
		}
	}

}
