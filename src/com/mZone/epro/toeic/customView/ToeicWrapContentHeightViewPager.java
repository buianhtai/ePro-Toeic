package com.mZone.epro.toeic.customView;

import com.mZone.epro.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ToeicWrapContentHeightViewPager extends ViewPager {
	int smallerViewNewPos = 0;
	int smallerViewIndex = -1;
	int isHavingVirtualBackground = 0;
	private static final int DEFAULT_IS_HAVING_VIRTUAL_BACKGROUND = 0;
    public ToeicWrapContentHeightViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    public ToeicWrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
			TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToeicWrapContentHeightViewPager);
			if (ta != null) {
				isHavingVirtualBackground = ta.getInt(R.styleable.ToeicWrapContentHeightViewPager_isHavingVirtualBackground, DEFAULT_IS_HAVING_VIRTUAL_BACKGROUND);
			}	
		}
        
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height1 = 0;
        int height2 = 0;
        smallerViewNewPos = 0;
    	smallerViewIndex = -1;
        // find the first child view
        ViewGroup view1 = (ViewGroup) getChildAt(0);
        
        if (view1 != null) {
            // measure the first child view with the specified measure spec
        	
            view1.measure(widthMeasureSpec, heightMeasureSpec);
            height1 = measureHeight(heightMeasureSpec, view1);
        }
        ViewGroup view2 = (ViewGroup) getChildAt(1);
        if (view2 != null) {
            // measure the first child view with the specified measure spec
            view2.measure(widthMeasureSpec, heightMeasureSpec);
            height2 = measureHeight(heightMeasureSpec, view2);
        }
        
        
//        Log.e("test height", String.valueOf(heightMeasureSpec) + "   " + String.valueOf(height1) + "   " +String.valueOf(height2));
        int height = height1 > height2 ? height1 : height2;
        
        
        if (view2 != null){
        	smallerViewIndex = height1 > height2 ? 1 : 0;
        	smallerViewNewPos = Math.abs(height1 - height2)/2;
        }
        
        ViewPager.LayoutParams params = new ViewPager.LayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        if (view1 != null){
        	view1.setLayoutParams(params);
        }
        if (view2 != null){
        	view2.setLayoutParams(params);
        }

        setMeasuredDimension(getMeasuredWidth(), height);
        if (isHavingVirtualBackground != DEFAULT_IS_HAVING_VIRTUAL_BACKGROUND){
        	ViewGroup parent = (ViewGroup) this.getParent();
            View backgroundView = parent.getChildAt(0);
            RelativeLayout.LayoutParams backgroundParams = (android.widget.RelativeLayout.LayoutParams) backgroundView.getLayoutParams();
            backgroundParams.height = height;
            backgroundView.setLayoutParams(backgroundParams);
            backgroundView.invalidate();
        }
        invalidate();
    }
    
    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b){
    	if (smallerViewIndex != -1){
    		ViewGroup view = (ViewGroup) getChildAt(smallerViewIndex);
        	if (view != null){
        		view.setY(smallerViewNewPos);
        	}
    	}
    	
    	super.onLayout(changed, l, t, r, b);
    }
    
    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @param view the base view with already measured height
     *
     * @return The height of the view, honoring constraints from measureSpec
     */
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
