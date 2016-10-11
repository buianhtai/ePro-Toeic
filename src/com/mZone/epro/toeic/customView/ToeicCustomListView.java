package com.mZone.epro.toeic.customView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ToeicCustomListView extends ListView {

	public ToeicCustomListView(Context context){
		super(context);
		// TODO Auto-generated constructor stub
	}
	public ToeicCustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	public ToeicCustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event){
		int direction = getDirection(event);
		if (direction == 1 || direction == 2){
			return false;
		}
		return super.onInterceptTouchEvent(event);
	}
	
	float downXValue;
	float downYValue;
	private int getDirection(MotionEvent event){
		int direction= 0;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downXValue = event.getX();
				downYValue = event.getY();
				break;
			case MotionEvent.ACTION_UP:
				float currentX = event.getX();
				float currentY = event.getY();
				if (Math.abs(downXValue - currentX) > Math.abs(downYValue - currentY)){
					if (downXValue < currentX){
						direction = 1;	//right
					}
					if (downXValue > currentX){
						direction = 2;	//left
					}
				}
				else{
					if (downYValue < currentY){
						direction = 3;	//down
					}
					if (downYValue > currentY){
						direction = 4;	//up
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				currentX = event.getX();
				currentY = event.getY();
				if (Math.abs(downXValue - currentX) > Math.abs(downYValue - currentY)){
					if (downXValue < currentX){
						direction = 1;	//right
					}
					if (downXValue > currentX){
						direction = 2;	//left
					}
				}
				else{
					if (downYValue < currentY){
						direction = 3;	//down
					}
					if (downYValue > currentY){
						direction = 4;	//up
					}
				}
				break;
		}
		return direction;
	}

}
