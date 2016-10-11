package com.mZone.epro.toeic.customView;

import com.mZone.epro.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class ToeicCustomHorizontalLinearLayout extends LinearLayout {
	private static final int DEFAULT_DISTANCE_FROM_TEXTVIEW_TO_BOTTOM = 10;
	private int distanceFromTextviewToBotton = 0;
	
	public ToeicCustomHorizontalLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ToeicCustomHorizontalLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToeicCustomHorizontalLinearLayout);
			if (ta != null) {
				distanceFromTextviewToBotton = ta.getInt(R.styleable.ToeicCustomHorizontalLinearLayout_distanceFromTextviewToBottom, DEFAULT_DISTANCE_FROM_TEXTVIEW_TO_BOTTOM);
			}	
		}
	}

	public ToeicCustomHorizontalLinearLayout(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToeicCustomRadioGroup);
			if (ta != null) {
				distanceFromTextviewToBotton = ta.getInt(R.styleable.ToeicCustomHorizontalLinearLayout_distanceFromTextviewToBottom, DEFAULT_DISTANCE_FROM_TEXTVIEW_TO_BOTTOM);
			}	
		}
	}
	
	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height1 = 0;
        int height2 = 0;
        // find the first child view
        View view1 = (View) getChildAt(0);
        
        if (view1 != null) {
            // measure the first child view with the specified measure spec
            height1 = measureHeight(heightMeasureSpec, view1);
        }
        /*
        View view2 = (View) getChildAt(1);
        if (view2 != null) {
            // measure the first child view with the specified measure spec
            view2.measure(widthMeasureSpec, heightMeasureSpec);
            height2 = measureHeight(heightMeasureSpec, view2);
        }
        */
        
//        Log.e("test height", String.valueOf(heightMeasureSpec) + "   " + String.valueOf(height1) + "   " +String.valueOf(height2));
        int height = height1 > height2 ? height1 : height2;
        setMeasuredDimension(getMeasuredWidth(), height + distanceFromTextviewToBotton);
    }
	
	private int measureHeight(int measureSpec, View view) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            // set the height from the base view if available
            if (view != null) {
                result = view.getMeasuredHeight();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

}
