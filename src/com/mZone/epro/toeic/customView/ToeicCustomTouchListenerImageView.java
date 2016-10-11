package com.mZone.epro.toeic.customView;

import com.mZone.epro.toeic.customInterface.ToeicSubviewDoubleClickListener;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class ToeicCustomTouchListenerImageView extends ToeicResizableImageView implements GestureDetector.OnGestureListener,
																				GestureDetector.OnDoubleTapListener{
	
	private GestureDetectorCompat mDetector;
	private ToeicSubviewDoubleClickListener mDelegate;
	private int mElementIndex;

	
	public ToeicCustomTouchListenerImageView(Context context) {
		super(context);
		init(context);
	}

	public ToeicCustomTouchListenerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ToeicCustomTouchListenerImageView(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private void init(final Context context){
		mDetector = new GestureDetectorCompat(context, this);
		mDetector.setOnDoubleTapListener(this);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		this.mDetector.onTouchEvent(event);
		return true;
	}
	
	public void setDelegate(ToeicSubviewDoubleClickListener delegate, int elementIndex){
		this.mDelegate = delegate;
		this.mElementIndex = elementIndex;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		if (mDelegate != null)
			mDelegate.onSubviewDoubleClick(mElementIndex);
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}
