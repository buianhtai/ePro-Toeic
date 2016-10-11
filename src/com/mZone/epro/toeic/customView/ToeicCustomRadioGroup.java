package com.mZone.epro.toeic.customView;

import java.util.ArrayList;
import com.mZone.epro.R;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.dataStructure.ToeicListeningQuestion;
import com.mZone.epro.toeic.dataStructure.ToeicReadingElementQuestion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ToeicCustomRadioGroup extends LinearLayout implements View.OnClickListener {

	OnStateChangedListener delegate;
	public static interface OnStateChangedListener{
		public void onStateChanged(int questionID, int state);
	}
	private ArrayList<ImageButton> radioButtonArray;
	private static final float scaleLarge = 1.2f;
//	private static final float scaleSmall = 0.8f;
	private static final int[] radioButtonIDArray = {R.id.radioButtonA, R.id.radioButtonB, R.id.radioButtonC, R.id.radioButtonD};
//	private int[] radioButtonDrawableNormalArray = {R.drawable.anormal, R.drawable.bnormal, R.drawable.cnormal, R.drawable.dnormal};
//	private int[] radioButtonDrawableSelectedArray = {R.drawable.aselected, R.drawable.bselected, R.drawable.cselected, R.drawable.dselected};
	private int[] radioButtonDrawableDisableArray = {R.drawable.adisable, R.drawable.bdisable, R.drawable.cdisable, R.drawable.ddisable};
	private int[] radioButtonDrawableWrongArray = {R.drawable.awrong, R.drawable.bwrong, R.drawable.cwrong, R.drawable.dwrong};
	private int[] radioButtonDrawableRightArray = {R.drawable.aright, R.drawable.bright, R.drawable.cright, R.drawable.dright};
	private int[] radioButtonDrawableRightWrongArray = {R.drawable.arightwrong, R.drawable.barightwrong, R.drawable.carightwrong, R.drawable.darightwrong};
	private int[] radioButtonDrawableStyleArray = {R.drawable.custom_radio_controll_button_style_a, R.drawable.custom_radio_controll_button_style_b, R.drawable.custom_radio_controll_button_style_c, R.drawable.custom_radio_controll_button_style_d};
//	private int[] subViewIDArray = {R.id.subViewA, R.id.subViewB, R.id.subViewC, R.id.subViewD};
	
	static final int[] STATE_ARRAY = {0, 1, 2, 3};
	private int state = -1;
	private int resultState = -1;
	private int questionID = -1;
	
	//number of choice
	private static int DEFAULT_NUMBER_OF_CHOICE = 4;
	private int numberOfChoice;
	
	private static int DEFAULT_CUSTOM_ORIENTATION = 0;
	private int orientation;
	
	private static int DEFAULT_IS_CONNECTED_WITH_TEXT = 0;
	private int isConnectedWithText;
	
	private static final int[] responseTextviewIDArray = {R.id.responseTextA, R.id.responseTextB, R.id.responseTextC, R.id.responseTextD};
	private static final int questionTextviewID = R.id.questionText;
	private ArrayList<CustomWordClickableTextview> responseTextviewArray;
	private CustomWordClickableTextview questionTextview;
	
	private boolean interactionEnable = true;
	
	public ToeicCustomRadioGroup(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ToeicCustomRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToeicCustomRadioGroup);
			if (ta != null) {
				numberOfChoice = ta.getInt(R.styleable.ToeicCustomRadioGroup_numberOfChoice, DEFAULT_NUMBER_OF_CHOICE);
				orientation = ta.getInt(R.styleable.ToeicCustomRadioGroup_customOrientation, DEFAULT_CUSTOM_ORIENTATION);
				isConnectedWithText = ta.getInt(R.styleable.ToeicCustomRadioGroup_isConnectedWithText, DEFAULT_IS_CONNECTED_WITH_TEXT);
			}
			
		}

		LayoutInflater inflater = (LayoutInflater) context
		        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (isConnectedWithText == DEFAULT_IS_CONNECTED_WITH_TEXT){
			if (orientation == DEFAULT_CUSTOM_ORIENTATION){
				inflater.inflate(R.layout.toeic_custom_radio_group, this, true);
			}
			else{
				inflater.inflate(R.layout.toeic_custom_radio_group_horizontal, this, true);
			}
		}
		else{
			inflater.inflate(R.layout.toeic_custom_radio_group_vertical_with_text, this, true);
		}
		
		radioButtonArray = new ArrayList<ImageButton>(radioButtonIDArray.length);
		for (int i = 0; i < radioButtonIDArray.length; i++){
			ImageButton radioButton = (ImageButton)findViewById(radioButtonIDArray[i]);
			radioButton.setOnClickListener(this);
			radioButton.setSoundEffectsEnabled(false);
			radioButtonArray.add(radioButton);
		}
		if (numberOfChoice < DEFAULT_NUMBER_OF_CHOICE){
			radioButtonArray.get(numberOfChoice).setVisibility(View.GONE);
		}
		if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
			responseTextviewArray = new ArrayList<CustomWordClickableTextview>(responseTextviewIDArray.length);
			for (int i = 0; i < responseTextviewIDArray.length; i++){
				CustomWordClickableTextview responseText = (CustomWordClickableTextview)findViewById(responseTextviewIDArray[i]);
				responseText.setSoundEffectsEnabled(false);
				responseTextviewArray.add(responseText);
			}
			questionTextview = (CustomWordClickableTextview)findViewById(questionTextviewID);
		}
	}

	public ToeicCustomRadioGroup(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (interactionEnable){
			int value = 0;
			for (int i = 0; i < radioButtonIDArray.length; i++){
				if (v.getId() == radioButtonIDArray[i] || (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT && v.getId() == responseTextviewIDArray[i])){
					value = STATE_ARRAY[i];
					break;
				}
			}
			setState(value);
			delegate.onStateChanged(questionID ,state);
		}
	}
	
	public void setState(int value){
		int preState = state;
		if (preState == value) return;
		state = value;
		for (int i = 0; i < radioButtonIDArray.length; i++){
			if (i != state){
				radioButtonArray.get(i).setSelected(false);
				if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
					responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_unselected_color));
				}
			}
			else{
				final int index = i;
				radioButtonArray.get(i).setSelected(true);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					radioButtonArray.get(i).animate().setInterpolator(new OvershootInterpolator(10f)).scaleX(scaleLarge).scaleY(scaleLarge).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 radioButtonArray.get(index).animate().setInterpolator(new OvershootInterpolator(5f)).scaleX(1.0f).scaleY(1.0f).setListener(new AnimatorListenerAdapter(){
								 @Override
								 public void onAnimationEnd(Animator animation) {
									 radioButtonArray.get(index).setScaleX(1.0f);
									 radioButtonArray.get(index).setScaleY(1.0f);
								 }
							});
						 }
					});
				}
				else{
					radioButtonArray.get(i).animate().setInterpolator(new OvershootInterpolator(10f)).scaleX(scaleLarge).scaleY(scaleLarge).withEndAction(new Runnable() {
						
						@Override
						public void run() {
							radioButtonArray.get(index).animate().setInterpolator(new OvershootInterpolator(5f)).scaleX(1.0f).scaleY(1.0f).withEndAction(new Runnable() {
								
								@Override
								public void run() {
									radioButtonArray.get(index).setScaleX(1.0f);
									radioButtonArray.get(index).setScaleY(1.0f);
								}
							});
						}
					});
				}
				if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
					responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_normal_color));
				}
			}
		}
	}
	
	public void setCombineState(int checkState, int result){
		state = checkState;
		this.resultState = result;
		if (resultState != ToeicDataController.CHECK_RESULT_SELECTED_FIRST_STATE){
			interactionEnable = false;
			for (int i = 0; i < radioButtonIDArray.length; i++){
				radioButtonArray.get(i).setEnabled(false);
				if (state == -1){
					if (i == resultState){
						radioButtonArray.get(i).setImageResource(radioButtonDrawableRightWrongArray[i]);
						if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
							responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_rightwrong_color));
						}
					}
					else{
						radioButtonArray.get(i).setImageResource(radioButtonDrawableDisableArray[i]);
						if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
							responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_unselected_color));
						}
					}
				}
				else{
					if (state == resultState){	//right
						if (i == resultState){
							radioButtonArray.get(i).setImageResource(radioButtonDrawableRightArray[i]);
							if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
								responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_normal_color));
							}
						}
						else{
							radioButtonArray.get(i).setImageResource(radioButtonDrawableDisableArray[i]);
							if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
								responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_unselected_color));
							}
						}
					}
					else{	//false
						if (i == state){
							radioButtonArray.get(i).setImageResource(radioButtonDrawableWrongArray[i]);
							if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
								responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_normal_color));
							}
						}
						else if (i == resultState){
							radioButtonArray.get(i).setImageResource(radioButtonDrawableRightWrongArray[i]);
							if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
								responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_rightwrong_color));
							}
						}
						else{
							radioButtonArray.get(i).setImageResource(radioButtonDrawableDisableArray[i]);
							if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
								responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_unselected_color));
							}
						}
					}
				}
				
			}
		}
		else{
			interactionEnable = true;
			for (int i = 0; i < radioButtonIDArray.length; i++){
				radioButtonArray.get(i).setEnabled(true);
				radioButtonArray.get(i).setImageResource(radioButtonDrawableStyleArray[i]);
				if (state == -1){
					radioButtonArray.get(i).setSelected(false);
					if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
						responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_normal_color));
					}
				}
				else{
					if (i != state){
						radioButtonArray.get(i).setSelected(false);
						if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
							responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_unselected_color));
						}
					}
					else{
						radioButtonArray.get(i).setSelected(true);
						if (isConnectedWithText != DEFAULT_IS_CONNECTED_WITH_TEXT){
							responseTextviewArray.get(i).setTextColor(getResources().getColor(R.color.text_normal_color));
						}
					}
				}
			}
		}
	}

	public int getQuestionID() {
		return questionID;
	}

	public void setQuestionID(int questionID) {
		this.questionID = questionID;
	}

	public OnStateChangedListener getDelegate() {
		return delegate;
	}

	public void setDelegate(OnStateChangedListener delegate) {
		this.delegate = delegate;
	}
	
	public void setText(ToeicListeningQuestion question){
		
		if (questionTextview != null){
			questionTextview.setText(question.getQuestion());
		}
		for (int i = 0; i < responseTextviewIDArray.length; i++){
			responseTextviewArray.get(i).setClickableTextView(question.getResponseAtIndex(i), this, i);
		}
	}
	
	public void setReadingText(ToeicReadingElementQuestion question){
		if (questionTextview != null){
			questionTextview.setText(question.getQuestion());
		}
		for (int i = 0; i < responseTextviewIDArray.length; i++){
			responseTextviewArray.get(i).setClickableTextView(question.getResponseAtIndex(i), this, i);
		}
	}
	
	public void onTextviewClickLisener(int value){
		if (interactionEnable){
			setState(value);
			delegate.onStateChanged(questionID ,state);
		}
	}
	
}
