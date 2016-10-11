package com.mZone.epro.toeic.customView;

import java.util.ArrayList;

import com.mZone.epro.toeic.activity.ToeicHorizontalSubviewTableElementAcitivity;
import com.mZone.epro.toeic.activity.ToeicTestActivity;
import com.mZone.epro.toeic.data.ToeicUtil;
import com.mZone.epro.toeic.dataStructure.ToeicBasicSentence;


import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class CustomWordClickableTextview extends TextView implements TextView.OnTouchListener{

	private Context activityContext;
	public CustomWordClickableTextview(Context context) {
		super(context);
		activityContext = context;
	}

	public CustomWordClickableTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		activityContext = context;
	}

	public CustomWordClickableTextview(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		activityContext = context;
	}

	private static final int TIME_FLY = 200;
	private static boolean mItemPress = false;
	float downXValue;
	float downYValue;
	long timeDown;
	int offDown;
	int previousOffMove;	//previous highlighting location
	boolean isPressed;
	boolean isHighlightState;
	boolean isActionDown;
	
	
	boolean isClickable = false;
	ToeicBasicSentence content;
	ToeicCustomRadioGroup delegate = null;
	int indexInRadioGroup = -1;
	Spannable buffer;
	int offDownEnd;
	boolean wordIsHightlight;
	boolean suddenlyExit = false;
	final Handler handler = new Handler(); 
	Runnable mLongPressed = new Runnable() { 
	    @Override
		public void run() { 
	    	suddenlyExit = false;
	    	wordIsHightlight = false;
	    	if (offDown == buffer.length()) {
	    		return;
	    	}
	        offDownEnd = offDown;
	        for (int i = offDown; i < buffer.length(); i++){
	        	if (buffer.charAt(i) == ' ' || buffer.charAt(i) == '\n' || i == buffer.length() - 1){
	        		offDownEnd = i;
	        		if (i == buffer.length() - 1) offDownEnd++;
	        		break;
	        	}
	        }
	        for (int i = offDown - 1; i >= 0; i--){
	        	if (buffer.charAt(i) == ' ' || buffer.charAt(i) == '\n' || i == 0){
	        		offDown = i + 1;
	        		if (i == 0) offDown = 0;
	        		break;
	        	}
	        }
	        if (suddenlyExit || offDown >= offDownEnd) return;
	        wordIsHightlight = true;
	        buffer.setSpan(new BackgroundColorSpan(ToeicUtil.wordClickColor), offDown, offDownEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	    }   
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		TextView widget = (TextView) v;
        Object text = widget.getText();
        buffer = (Spannable) text;
        switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mItemPress) {
					return false;
				}
				mItemPress = true;
				downXValue = event.getX();
				downYValue = event.getY();
				int textDownX = (int) downXValue;
				int textDowny = (int) downYValue;
				textDownX -= widget.getTotalPaddingLeft();
				textDowny -= widget.getTotalPaddingTop();
                textDownX += widget.getScrollX();
                textDowny += widget.getScrollY();
				timeDown = System.currentTimeMillis();
				Layout layout = widget.getLayout();
                int lineDown = layout.getLineForVertical(textDowny);
                offDown = layout.getOffsetForHorizontal(lineDown, textDownX);
				previousOffMove = -1;
				isActionDown = true;
				isHighlightState = false;
				handler.postDelayed(mLongPressed, TIME_FLY);
				return true;
			case MotionEvent.ACTION_UP:
				float currentX = event.getX();
				float currentY = event.getY();
				handler.removeCallbacks(mLongPressed);
				suddenlyExit = true;
				if (isActionDown && (System.currentTimeMillis() - timeDown > TIME_FLY)){
					isPressed = true;
				}
				if (!isPressed){
					if (isClickable){
						if (delegate != null){
							//do something to delegate
							delegate.onTextviewClickLisener(indexInRadioGroup);
						}
					}
					mItemPress = false;
					return true;
				}
				else{
					isPressed = false;
					if (!isHighlightState){
						int currentTextMoveX = (int) currentX;
						int currentTextMovey = (int) currentY;
						currentTextMoveX -= widget.getTotalPaddingLeft();
						currentTextMovey -= widget.getTotalPaddingTop();
						currentTextMoveX += widget.getScrollX();
						currentTextMovey += widget.getScrollY();
						layout = widget.getLayout();
		                int currentLineMove = layout.getLineForVertical(currentTextMovey);
		                int offUp = layout.getOffsetForHorizontal(currentLineMove, currentTextMoveX);
		                checkClickedWord(offUp);
					}
					else{
						ToeicTestActivity.insertToUndoList(this, this.content);
						if (offDown > previousOffMove){
							content.addHighlightSpace(previousOffMove, offDown);
						}
						else{
							content.addHighlightSpace(offDown, previousOffMove);
						}
					}
					if (wordIsHightlight){
						if (offDown > offDownEnd){
							if (!(offDown > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDownEnd, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						else{
							if (!(offDownEnd > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDown, offDownEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						restoreText();
					}
					v.getParent().requestDisallowInterceptTouchEvent(false);
				}
				previousOffMove = -1;
				mItemPress = false;
				break;
			case MotionEvent.ACTION_MOVE:
				currentX = event.getX();
				currentY = event.getY();
				
				if (isActionDown && Math.abs(currentX - downXValue) <= 10 && Math.abs(currentY - downYValue) <= 10) {
					if (System.currentTimeMillis() - timeDown > TIME_FLY) {
						isPressed = true;
					}
					return true;
				}
				if (isActionDown) {
					isActionDown = false;
				}
				if (!isPressed){
					handler.removeCallbacks(mLongPressed);
					suddenlyExit = true;
					v.getParent().requestDisallowInterceptTouchEvent(false);
					return true;
				}
				
				else{
					if ((Math.abs(currentX - downXValue) > 10 || Math.abs(currentY - downYValue) > 10) && isHighlightState == false){
						isHighlightState = true;
					}
					else if (!isHighlightState){
						return true;
					}
//					Spannable buffer = (Spannable) text;
					if (wordIsHightlight){
						if (offDown > offDownEnd){
							if (!(offDown > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDownEnd, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						else{
							if (!(offDownEnd > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDown, offDownEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
					}
					v.getParent().requestDisallowInterceptTouchEvent(true);
					int currentTextMoveX = (int) currentX;
					int currentTextMovey = (int) currentY;
					currentTextMoveX -= widget.getTotalPaddingLeft();
					currentTextMovey -= widget.getTotalPaddingTop();
					currentTextMoveX += widget.getScrollX();
					currentTextMovey += widget.getScrollY();
					layout = widget.getLayout();
	                int currentLineMove = layout.getLineForVertical(currentTextMovey);
	                int offMove = layout.getOffsetForHorizontal(currentLineMove, currentTextMoveX);
	                if (previousOffMove != -1){
	                	 if (offDown > previousOffMove){
	                		 if (!(offDown > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), previousOffMove, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		                }
		                else{
		                	if (!(previousOffMove > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDown, previousOffMove, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		                }
	                }
	                if (offDown > offMove){
	                	if (!(offDown > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.YELLOW), offMove, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	                else{
	                	if (!(offMove > buffer.length())) buffer.setSpan(new BackgroundColorSpan(Color.YELLOW), offDown, offMove, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	                }
	                previousOffMove = offMove;
	                return true;
				}
			case (MotionEvent.ACTION_CANCEL) :
				handler.removeCallbacks(mLongPressed);
				suddenlyExit = true;
				if (wordIsHightlight){
					if (offDown > offDownEnd){
						if (!(offDown > buffer.length()))
							buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDownEnd, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					else{
						if (!(offDownEnd > buffer.length()))
							buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDown, offDownEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					restoreText();
				}
				mItemPress = false;
	            return true;
        }
		return true;
	}
	
	//Normal setText
	public void setText(ToeicBasicSentence content){
		this.content = content;
		SpannableStringBuilder string = new SpannableStringBuilder(content.getText());
		ArrayList<ToeicBasicSentence.HighLightSpace> highlightSpaceArray = content.getHighLightSpaceArray();
		for (int i = 0; i < highlightSpaceArray.size(); i++){
			string.setSpan(new BackgroundColorSpan(Color.YELLOW), highlightSpaceArray.get(i).startHighlight, highlightSpaceArray.get(i).stopHighlight, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		super.setText(string, BufferType.SPANNABLE);
		this.setOnTouchListener(this);
	}
	
	public void restoreText(){
		SpannableStringBuilder string = new SpannableStringBuilder(content.getText());
		ArrayList<ToeicBasicSentence.HighLightSpace> highlightSpaceArray = content.getHighLightSpaceArray();
		for (int i = 0; i < highlightSpaceArray.size(); i++){
			string.setSpan(new BackgroundColorSpan(Color.YELLOW), highlightSpaceArray.get(i).startHighlight, highlightSpaceArray.get(i).stopHighlight, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		super.setText(string, BufferType.SPANNABLE);
	}
	
	//Set Script (not able to be highlighted neither clickable)
	public void setClickableTextView(ToeicBasicSentence content, ToeicCustomRadioGroup d, int index){
		this.content = content;
		this.delegate = d;
		this.indexInRadioGroup = index;
		isClickable = true;
		SpannableStringBuilder string = new SpannableStringBuilder(content.getText());
		ArrayList<ToeicBasicSentence.HighLightSpace> highlightSpaceArray = content.getHighLightSpaceArray();
		for (int i = 0; i < highlightSpaceArray.size(); i++){
			string.setSpan(new BackgroundColorSpan(Color.YELLOW), highlightSpaceArray.get(i).startHighlight, highlightSpaceArray.get(i).stopHighlight, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		super.setText(string, BufferType.SPANNABLE);
		this.setOnTouchListener(this);
	}
	
	
	public void checkClickedWord(int location){
		ArrayList<ToeicBasicSentence.Word> wordArray = content.getWordArray();
		if (wordArray == null) return;
		for (int i = 0; i < wordArray.size(); i++){
			ToeicBasicSentence.Word word = wordArray.get(i);
			if (location >= word.start_field && location <= word.stop_field){
				if (activityContext instanceof ToeicTestActivity){
					((ToeicTestActivity)activityContext).showDictDialog(word.word);
				}
				else if (activityContext instanceof ToeicHorizontalSubviewTableElementAcitivity){
					((ToeicHorizontalSubviewTableElementAcitivity)activityContext).showDictDialog(word.word);
				}
				return;
			}
		}
	}
	@Override
	protected void onDetachedFromWindow() {
	    super.onDetachedFromWindow();
	    // View is now detached, and about to be destroyed
	    ToeicTestActivity.removeViewInUndoList(this);
	}
}
