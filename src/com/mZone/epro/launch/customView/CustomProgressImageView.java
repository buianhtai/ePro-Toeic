package com.mZone.epro.launch.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CustomProgressImageView extends ImageView {

	float viewWidth, viewHeight;
	float arcHorizontalRadius, arcVerticalRadius;
	RectF arcBoundRect;
	float arc = 0;
	public CustomProgressImageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomProgressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomProgressImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected void onDraw(Canvas canvas)
    {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        System.out.println("Painting content");
        Paint paint  = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(150);
        canvas.drawArc(arcBoundRect, 0, arc, true, paint);
//        canvas.draw
    }
	
	@Override
	 protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
	     super.onSizeChanged(xNew, yNew, xOld, yOld);
	     viewWidth = (float)xNew;
	     viewHeight = (float)yNew;
	     arcHorizontalRadius = viewWidth/(float)Math.sqrt(2);
	     arcVerticalRadius = viewHeight/(float)Math.sqrt(2);
	     arcBoundRect = new RectF(viewWidth/2 -  arcHorizontalRadius, viewHeight/2 - arcVerticalRadius, viewWidth/2  + arcHorizontalRadius, viewHeight/2 + arcVerticalRadius);
	     
	}
	
	public void setProgressScale(float scale){
		arc = -360*(1-scale);
		invalidate();
	}

}
