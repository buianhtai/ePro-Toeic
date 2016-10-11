package com.mZone.epro.dict.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomDownloadProgressButton extends Button {

	@SuppressWarnings("unused")
	private float viewWidth, viewHeight;
	
	@SuppressWarnings("unused")
	private float progress;
	private float animateProgress;
	private float animatingProgress;
	@SuppressWarnings("unused")
	private float velocity = 0;
	
	private boolean isInProgress = false;
	private boolean isInAnimate = false;
	
	private final int FRAME_RATE = 10;
	private Handler handler = new Handler();
	
	@SuppressWarnings("unused")
	private boolean isBitmap;
	@SuppressWarnings("unused")
	private final int bitmapPadding = 10;
	
    private Runnable r = new Runnable() {
        @Override
        public void run() {
        	if (animatingProgress <= animateProgress)
        		setText(String.format("%.0f", animatingProgress*100)+"%");
        	else{
        		setText(String.format("%.0f", animateProgress*100)+"%");
        	}
            invalidate();
        }
    };

	public CustomDownloadProgressButton(Context context) {
		super(context);
	}

	public CustomDownloadProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomDownloadProgressButton(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
	     super.onSizeChanged(xNew, yNew, xOld, yOld);
	     viewWidth = (float)xNew;
	     viewHeight = (float)yNew;
	}

	@Override
	protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (isInAnimate){
          	 handler.postDelayed(r, FRAME_RATE);
   		}
    }
	
	public void setProgress(float p){
		progress = p;
		isInProgress = true;
		if (isInAnimate){
			handler.removeCallbacks(r);
		}
		isInAnimate = false;
		setText(String.format("%.0f", p*100)+"%");
	}
	
	public void disableProgress(){
		if (isInProgress && isInAnimate){
			handler.removeCallbacks(r);
		}
		isInProgress = false;
		isInAnimate = false;
	}
	
	public void updateProgressWithAnimate(float p){
		if (p < 0){
			p = 0;
		}
		if (!isInAnimate){
			progress = p;	
			animateProgress = p;
			animatingProgress = p;
			velocity = 0;
			isInAnimate = true;
			isInProgress = true;
			handler = new Handler();
			handler.postDelayed(r, FRAME_RATE);
		}
		else{
			animatingProgress = animateProgress;
			animateProgress = p;
			velocity = (animateProgress - animatingProgress)/15;
		}
	}
	
	public void setIsBitmap (boolean b){
		isBitmap = b;
	}
}
