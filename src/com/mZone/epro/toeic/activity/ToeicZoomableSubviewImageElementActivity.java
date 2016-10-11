package com.mZone.epro.toeic.activity;

import com.mZone.epro.R;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementImage;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupElement;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupQuestion;
import com.polites.android.GestureImageView;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class ToeicZoomableSubviewImageElementActivity extends Activity {

	public static final String INTENT_PART_INDEX = "ToeicZoomableSubviewActivity.INTENT_PART_INDEX";
	public static final String INTENT_GROUP_QUESTION_INDEX = "ToeicZoomableSubviewActivity.INTENT_GROUP_QUESTION_INDEX";
	public static final String INTENT_ELEMENT_INDEX = "ToeicZoomableSubviewActivity.INTENT_ELEMENT_INDEX";
	
	private int partIndex;
	private int groupQuestionIndex;
	private int elementIndex;
	
	private ToeicDataController dataController = ToeicDataController.getInstance();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toeic_zoomable_subview_image_element);
		Intent intent = getIntent();
		partIndex = intent.getIntExtra(INTENT_PART_INDEX, -1);
		groupQuestionIndex = intent.getIntExtra(INTENT_GROUP_QUESTION_INDEX, -1);
		elementIndex = intent.getIntExtra(INTENT_ELEMENT_INDEX, -1);
		GestureImageView imageView = (GestureImageView) findViewById(R.id.imageView);
		ToeicReadingGroupQuestion groupQuestion = dataController.getReadingGroupQuestion(partIndex, groupQuestionIndex);
		ToeicReadingGroupElement element = groupQuestion.getElementAtIndex(elementIndex);
		if (element instanceof ToeicReadingElementImage){
			ToeicReadingElementImage elementImage = (ToeicReadingElementImage) element;
			imageView.setImageBitmap(elementImage.getBitmap());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toeic_zoomable_subview, menu);
		return true;
	}

}
