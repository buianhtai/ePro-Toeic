package com.mZone.epro.toeic.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.mZone.epro.R;
import com.mZone.epro.toeic.activity.ToeicHorizontalSubviewTableElementAcitivity;
import com.mZone.epro.toeic.activity.ToeicZoomableSubviewImageElementActivity;
import com.mZone.epro.toeic.customInterface.ToeicSingleCheckResultListener;
import com.mZone.epro.toeic.customInterface.ToeicSingleCheckResultObserver;
import com.mZone.epro.toeic.customInterface.ToeicSubviewDoubleClickListener;
import com.mZone.epro.toeic.customView.ToeicCustomHorizontalScrollView;
import com.mZone.epro.toeic.customView.ToeicCustomTouchListenerImageView;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.data.ToeicUtil;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementImage;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementTable;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupElement;
import com.mZone.epro.toeic.dataStructure.ToeicReadingGroupQuestion;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ToeicFragmentTabviewScrollableFragment extends Fragment implements ToeicSingleCheckResultListener,
																				ToeicSubviewDoubleClickListener{	
	private View rootView;
	private LayoutInflater inflater;
	
	static final String PART_INDEX_ARGUMENT = "Part_Index";
	static final String GROUP_QUESTION_INDEX_ARGUMENT = "Group_Question_Index";
	private int partIndex;
	private int groupQuestionIndex;
	
	private ToeicDataController dataController = ToeicDataController.getInstance();
	private boolean onDoubleClick = false;
	
	public ToeicFragmentTabviewScrollableFragment() {
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		partIndex = getArguments().getInt(PART_INDEX_ARGUMENT);
		groupQuestionIndex = getArguments().getInt(GROUP_QUESTION_INDEX_ARGUMENT);
		this.inflater = inflater;
		rootView = this.inflater.inflate(
				R.layout.toeic_fragment_tabview_scrollview, container, false);
		addSubView();
		return rootView;
	}

	@Override
	public void onPause (){
		super.onPause();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (onDoubleClick){	//return from double click event
			onDoubleClick = false;
			refreshView();
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	}
	
	//Child FragmentManager retained after detaching parent Fragment
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} 
		catch (NoSuchFieldException e) {
		}
		sChildFragmentManagerField = f;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} 
			catch (Exception e) {
			}
		}
	}
	
	private void addSubView(){
		LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.scrollViewLinearLayout);
		ToeicReadingGroupQuestion groupQuestion = dataController.getReadingGroupQuestion(partIndex, groupQuestionIndex);
		for (int i = 0; i < groupQuestion.getNumberOfElementView(); i++){
			ToeicReadingGroupElement element = groupQuestion.getElementAtIndex(i);
			if (!(element instanceof ToeicReadingElementQuestion)){
				View subView = element.getView(inflater, getActivity());
				if (subView != null) layout.addView(subView);
				if (element instanceof ToeicReadingElementTable){
					ToeicCustomHorizontalScrollView scrollView = (ToeicCustomHorizontalScrollView) subView;
					scrollView.setDelegate(this, i);
				}
				else if (element instanceof ToeicReadingElementImage){
					ToeicCustomTouchListenerImageView imageView = (ToeicCustomTouchListenerImageView) subView;
					imageView.setDelegate(this, i);
				}
			}
			else {
				View subView = element.getView(inflater, getActivity());
				if (subView != null) layout.addView(subView);
				registerObserver((ToeicSingleCheckResultObserver) element);
			}
		}
		if (ToeicUtil.isPracticeMode()) {
			layout.addView(getCheckResultView());
		}
	}
	
	private void refreshView(){
		LinearLayout layout = (LinearLayout)rootView.findViewById(R.id.scrollViewLinearLayout);
		layout.removeAllViews();
		addSubView();
	}
	
	private View getCheckResultView(){
		LinearLayout layout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		layout.setOrientation(LinearLayout.VERTICAL);
		Resources rs = getActivity().getResources();
		int marginLR = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_check_result_layout_margin_lr), 
		        rs.getDisplayMetrics()
		);
		int marginBottom = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_check_result_layout_margin_b), 
		        rs.getDisplayMetrics()
		);
		int marginTop = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_check_result_layout_margin_t), 
		        rs.getDisplayMetrics()
		);
		linearParams.setMargins(marginLR, marginTop, marginLR, marginBottom);
		layout.setLayoutParams(linearParams);
		layout.setGravity(Gravity.RIGHT);
		
		int buttonWidth = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_check_result_button_width), 
		        rs.getDisplayMetrics()
		);
		int buttonHeight = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_check_result_button_height), 
		        rs.getDisplayMetrics()
		);
		ImageButton checkButton = new ImageButton(getActivity());
		LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(buttonWidth, buttonHeight);
		checkButton.setLayoutParams(buttonParam);
		checkButton.setScaleType(ScaleType.CENTER_CROP);
		checkButton.setImageResource(R.drawable.check_result_btn_selector);
		checkButton.setBackgroundResource(R.drawable.check_result_background_with_border);
		int questionID = dataController.getQuestionID(partIndex, groupQuestionIndex, 0) - 1;
		if (dataController.getCheckResultState(questionID) != ToeicDataController.CHECK_RESULT_SELECTED_FIRST_STATE){
			checkButton.setEnabled(false);
			checkButton.setBackgroundResource(R.drawable.check_result_background_with_border_disable);
		}
		else{
			checkButton.setEnabled(true);
			checkButton.setBackgroundResource(R.drawable.check_result_background_with_border);
			checkButton.setOnClickListener(new ImageButton.OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dataController.setCheckResultState(partIndex, groupQuestionIndex);
					v.setEnabled(false);
					v.setBackgroundResource(R.drawable.check_result_background_with_border_disable);
					notifyObserverArray(groupQuestionIndex);
				}
			});
		}
		layout.addView(checkButton);
		return layout;
	}
	
	ArrayList<ToeicSingleCheckResultObserver> singleCheckResultObservers = new ArrayList<ToeicSingleCheckResultObserver>();
	
	@Override
	public void registerObserver(ToeicSingleCheckResultObserver o) {
		singleCheckResultObservers.add(o);
	}
	@Override
	public void removeObserver(ToeicSingleCheckResultObserver o) {
		for (int i = 0; i < singleCheckResultObservers.size(); i++){
			if (singleCheckResultObservers.get(i) == o) singleCheckResultObservers.remove(i);
		}
	}
	@Override
	public void notifyObserverArray(int rowID) {
		for (int i = 0; i < singleCheckResultObservers.size(); i++){
			singleCheckResultObservers.get(i).onCheckResultClicked(rowID);
		}
	}

	@Override
	public void onSubviewDoubleClick(int elementIndex) {
		ToeicReadingGroupQuestion groupQuestion = dataController.getReadingGroupQuestion(partIndex, groupQuestionIndex);
		ToeicReadingGroupElement element = groupQuestion.getElementAtIndex(elementIndex);
		Intent subActivityIntent = null;
		if (element instanceof ToeicReadingElementTable){
			subActivityIntent = new Intent(getActivity(), ToeicHorizontalSubviewTableElementAcitivity.class);
			subActivityIntent.putExtra(ToeicHorizontalSubviewTableElementAcitivity.INTENT_PART_INDEX, partIndex);
			subActivityIntent.putExtra(ToeicHorizontalSubviewTableElementAcitivity.INTENT_GROUP_QUESTION_INDEX, groupQuestionIndex);
			subActivityIntent.putExtra(ToeicHorizontalSubviewTableElementAcitivity.INTENT_ELEMENT_INDEX, elementIndex);
			startActivity(subActivityIntent);
		}
		else if (element instanceof ToeicReadingElementImage){
			subActivityIntent = new Intent(getActivity(), ToeicZoomableSubviewImageElementActivity.class);
			subActivityIntent.putExtra(ToeicZoomableSubviewImageElementActivity.INTENT_PART_INDEX, partIndex);
			subActivityIntent.putExtra(ToeicZoomableSubviewImageElementActivity.INTENT_GROUP_QUESTION_INDEX, groupQuestionIndex);
			subActivityIntent.putExtra(ToeicZoomableSubviewImageElementActivity.INTENT_ELEMENT_INDEX, elementIndex);
			startActivity(subActivityIntent);
		}
		onDoubleClick = true;
	}
}
