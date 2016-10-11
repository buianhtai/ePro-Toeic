package com.mZone.epro.toeic.result;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

public abstract class AbstractBaseView extends View {

	public static String LOG_TAG ="AbstractBaseView";
	
	public AbstractBaseView(Context context) {
		super(context);
	}

	public AbstractBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public AbstractBaseView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		logSpec(MeasureSpec.getMode(widthMeasureSpec));
		setMeasuredDimension(getImprovedDefaultWidth(widthMeasureSpec), getImprovedDefaultHeight(heightMeasureSpec));
	}
	
	private void logSpec(int specMode) {	
		if (specMode == MeasureSpec.UNSPECIFIED) {
			return;
		}
		if (specMode == MeasureSpec.AT_MOST) {
			return;
		}
		if (specMode == MeasureSpec.EXACTLY) {
			return;
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w,h,oldw,oldh);
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable p) {
		super.onRestoreInstanceState(p);
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable p = super.onSaveInstanceState();
		return p;
	}
	
	private int getImprovedDefaultHeight(int measureSpec) {
		//int result = size;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				return hGetMaximumHeight();
			case MeasureSpec.EXACTLY:
				return specSize;
			case MeasureSpec.AT_MOST:
				return hGetMinimumHeight();
		}
		
		return specSize;
	}
		
	private int getImprovedDefaultWidth(int measureSpec) {
		//int result = size;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				return hGetMaximumWidth();
			case MeasureSpec.EXACTLY:
				return specSize;
			case MeasureSpec.AT_MOST:
				return hGetMinimumWidth();
		}
		
		return specSize;
	}
	
	//Override these methods to provide a maximum size
	//"h" stands for hook pattern
	abstract protected int hGetMaximumHeight();
	abstract protected int hGetMaximumWidth();
		
	//For minimum height use the View's methods
	protected int hGetMinimumHeight() {
		return this.getSuggestedMinimumHeight();
	}
		
	protected int hGetMinimumWidth() {
		return this.getSuggestedMinimumWidth();
	}

}
