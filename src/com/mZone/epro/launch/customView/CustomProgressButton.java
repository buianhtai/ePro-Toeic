package com.mZone.epro.launch.customView;

import com.mZone.epro.R;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.Button;

public class CustomProgressButton extends Button {

	private float viewWidth, viewHeight;
	
	private float progress;
	private float animateProgress;
	private float animatingProgress;
	private float velocity = 0;
	
	private boolean isInProgress = false;
	private boolean isInAnimate = false;
	
	private final int FRAME_RATE = 30;
	private Handler handler = new Handler();
	
	private boolean isBitmap;
	private final int bitmapPadding = 10;
	
	private Paint paint  = new Paint();
	private RectF drawRect = new RectF();
	
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
	
	public CustomProgressButton(Context context) {
		super(context);
	}

	public CustomProgressButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomProgressButton(Context context, AttributeSet attrs,
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
        paint.setColor(getResources().getColor(R.color.launch_store_grid_item_btnaction_open_normal_color));
		if (isInProgress && !isInAnimate){
//            paint.setAlpha(200);
			drawRect.set(0, 0, viewWidth*progress, viewHeight);
            canvas.drawRoundRect(drawRect, 4, 4, paint);
        }
		else if (isInProgress && isInAnimate){
			if (animatingProgress < animateProgress){
				animatingProgress += velocity;
				drawRect.set(0, 0, viewWidth*animatingProgress, viewHeight);
				canvas.drawRoundRect(drawRect, 4, 4, paint);
			}
			else{
				animatingProgress = animateProgress;
				drawRect.set(0, 0, viewWidth*animateProgress, viewHeight);
				canvas.drawRoundRect(drawRect, 4, 4, paint);
			}
		}
		else if (isBitmap){
			drawRect.set(viewWidth - viewHeight - bitmapPadding, 0, viewWidth - bitmapPadding, viewHeight);
			canvas.drawBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.download_background), 
					null, drawRect, paint);
		}
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
