package com.mZone.epro.toeic.customView;

import java.util.ArrayList;

import com.mZone.epro.toeic.activity.ToeicTestActivity;
import com.mZone.epro.toeic.data.ToeicUtil;
import com.mZone.epro.toeic.dataStructure.ToeicBasicSentence;
import com.mZone.epro.toeic.dataStructure.ToeicListeningSubScript;
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

public class CustomWordClickableScriptTextview extends TextView implements TextView.OnTouchListener{

	private Context activityContext;
	public CustomWordClickableScriptTextview(Context context) {
		super(context);
		activityContext = context;
	}

	public CustomWordClickableScriptTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
		activityContext = context;
	}

	public CustomWordClickableScriptTextview(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activityContext = context;
	}
	
	private static final int TIME_FLY = 150;
	private static boolean mItemPress = false;
	float downXValue;
	float downYValue;
	long timeDown;
	int offDown;
	int previousOffMove;	//previous highlighting location
	boolean isPressed;
	boolean isMoved;
	boolean isActionDown;
	
	ArrayList<ToeicListeningSubScript> subScriptArray;
	int highlightIndex;
	
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
				isMoved = false;
				wordIsHightlight = false;
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
				if (isPressed){
					isPressed = false;
					if (!isMoved){
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
					v.getParent().requestDisallowInterceptTouchEvent(false);
					if (wordIsHightlight){
						if (offDown > offDownEnd){
							buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDownEnd, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						else{
							buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDown, offDownEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						}
						restoreText();
					}
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
					if ((Math.abs(currentX - downXValue) > 10 || Math.abs(currentY - downYValue) > 10) && isMoved == false){
						isMoved = true;
					}
	                return true;
				}
			case (MotionEvent.ACTION_CANCEL) :
				handler.removeCallbacks(mLongPressed);
				suddenlyExit = true;
				if (wordIsHightlight){
					if (offDown > offDownEnd){
						buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDownEnd, offDown, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					else{
						buffer.setSpan(new BackgroundColorSpan(Color.TRANSPARENT), offDown, offDownEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					restoreText();
				}
				mItemPress = false;
	            return true;
        }
		return true;
	}
	public void setText(ArrayList<ToeicListeningSubScript> array, int highlightIndex){
		this.subScriptArray = array;
		this.highlightIndex = highlightIndex;
		SpannableStringBuilder string = new SpannableStringBuilder();
		for (int i = 0; i < subScriptArray.size(); i++){
			SpannableStringBuilder text = new SpannableStringBuilder(array.get(i).getText().getText());
			if (i == highlightIndex){
				text.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (i > 0){
				string.append(" ");
			}
			string.append(text);
		}
		super.setText(string, BufferType.SPANNABLE);
		this.setOnTouchListener(this);
	}
	public void checkClickedWord(int location){
		int offset = 0;
		for (int i = 0; i < subScriptArray.size(); i++){
			ToeicBasicSentence sentence = subScriptArray.get(i).getText();
			ArrayList<ToeicBasicSentence.Word> wordArray = sentence.getWordArray();
			if (wordArray == null) continue;
			for (int j = 0; j < wordArray.size(); j++){
				ToeicBasicSentence.Word word = wordArray.get(j);
				if (location - offset >= word.start_field && location - offset <= word.stop_field){
					((ToeicTestActivity)activityContext).showDictDialog(word.word);
					return;
				}
			}
			offset += sentence.getText().length() + 1;
		}
	}
	
	private void restoreText(){
		SpannableStringBuilder string = new SpannableStringBuilder();
		for (int i = 0; i < subScriptArray.size(); i++){
			SpannableStringBuilder text = new SpannableStringBuilder(subScriptArray.get(i).getText().getText());
			if (i == highlightIndex){
				text.setSpan(new BackgroundColorSpan(Color.YELLOW), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (i > 0){
				string.append(" ");
			}
			string.append(text);
		}
		super.setText(string, BufferType.SPANNABLE);
	}
}
