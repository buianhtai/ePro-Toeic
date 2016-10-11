package com.mZone.epro.toeic.result;

import com.mZone.epro.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;

public class CustomResultPieChart extends AbstractBaseView {

	private int boundStrokeWidth = 0;
	private int boundStrokeColor = 0x000000;
	private int baseColor = 0x000000;
	private int[] pieColor = {0x000000, 0x000000, 0x000000, 0x000000, 0x000000};
	private float[] pieScale = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f,};
	private int pieDividerStrokeWidth = 0;
	private int pieDividerStrokeColor = 0x000000;
	private RectF pieRect;
	private PointF pieCenter;
	private float pieRadius;
	private int textSize = 16;
	
	private int currentPie; 
	private float currentPieScale;
	private static final int FRAME_RATE = 30;
	private Handler handler = new Handler();
	
	private Runnable runnable = new Runnable() {
        @Override
        public void run() {
        	currentPieScale += 0.02;
        	if (currentPie < pieScale.length && currentPieScale > pieScale[currentPie]){
        		currentPie++;
        		currentPieScale = 0;
        	}
        	if (currentPie >= pieScale.length) {
        		currentPieScale = 0;
        		return;
        	}
        	else{
        		invalidate();
        	}
        }
	};
	
	public CustomResultPieChart(Context context) {
		super(context);
	}

	public CustomResultPieChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.ResultPieChart);
		if (type != null){
			boundStrokeWidth = type.getDimensionPixelSize(R.styleable.ResultPieChart_boundStrokeWidth, boundStrokeWidth);
			boundStrokeColor = type.getColor(R.styleable.ResultPieChart_boundStrokeColor, boundStrokeColor);
			baseColor = type.getColor(R.styleable.ResultPieChart_baseColor, baseColor);
			pieColor[0] = type.getColor(R.styleable.ResultPieChart_pieColor1, pieColor[0]);
			pieColor[1] = type.getColor(R.styleable.ResultPieChart_pieColor2, pieColor[1]);
			pieColor[2] = type.getColor(R.styleable.ResultPieChart_pieColor3, pieColor[2]);
			pieColor[3] = type.getColor(R.styleable.ResultPieChart_pieColor4, pieColor[3]);
			pieColor[4] = type.getColor(R.styleable.ResultPieChart_pieColor5, pieColor[4]);
			pieDividerStrokeWidth = type.getDimensionPixelSize(R.styleable.ResultPieChart_pieDividerStrokeWidth, pieDividerStrokeWidth);
			pieDividerStrokeColor = type.getColor(R.styleable.ResultPieChart_boundStrokeColor, pieDividerStrokeColor);
			textSize = type.getDimensionPixelSize(R.styleable.ResultPieChart_textSize, textSize);
		}
		type.recycle();
	}

	public CustomResultPieChart(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		
		TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.ResultPieChart, defStyleAttr, 0);
		if (type != null){
			boundStrokeWidth = type.getDimensionPixelSize(R.styleable.ResultPieChart_boundStrokeWidth, boundStrokeWidth);
			boundStrokeColor = type.getColor(R.styleable.ResultPieChart_boundStrokeColor, boundStrokeColor);
			baseColor = type.getColor(R.styleable.ResultPieChart_boundStrokeColor, baseColor);
			pieColor[0] = type.getColor(R.styleable.ResultPieChart_pieColor1, pieColor[0]);
			pieColor[1] = type.getColor(R.styleable.ResultPieChart_pieColor2, pieColor[1]);
			pieColor[2] = type.getColor(R.styleable.ResultPieChart_pieColor3, pieColor[2]);
			pieColor[3] = type.getColor(R.styleable.ResultPieChart_pieColor4, pieColor[3]);
			pieColor[4] = type.getColor(R.styleable.ResultPieChart_pieColor5, pieColor[4]);
			pieDividerStrokeWidth = type.getDimensionPixelSize(R.styleable.ResultPieChart_pieDividerStrokeWidth, pieDividerStrokeWidth);
			pieDividerStrokeColor = type.getColor(R.styleable.ResultPieChart_boundStrokeColor, pieDividerStrokeColor);
			textSize = type.getDimensionPixelSize(R.styleable.ResultPieChart_textSize, textSize);
		}
		type.recycle();
	}

	@Override
	protected int hGetMaximumHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int hGetMaximumWidth() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void onDraw(Canvas canvas){
		super.onDraw(canvas);
		canvas.drawCircle(pieCenter.x, pieCenter.y, pieRadius, getBasePaint(baseColor, false));
		float argc = -0.25f;
		for (int i = 0; i < currentPie; i++){
			canvas.drawArc(pieRect, argc*360, pieScale[i]*360, true, getBasePaint(pieColor[i], true));
			argc += pieScale[i];
		}
		if (currentPie < pieScale.length)
			canvas.drawArc(pieRect, argc*360, currentPieScale*360, true, getBasePaint(pieColor[currentPie], true));
		argc += currentPieScale;
		canvas.drawCircle(pieCenter.x, pieCenter.y, pieRadius, getStrokePaint(boundStrokeWidth, boundStrokeColor));
		String percent = String.format("%.1f", (argc + 0.25)*100) + "%";
		Paint textPaint = getTextPaint();
		Rect textBound = new Rect(0, 0, 0, 0);
		textPaint.getTextBounds(percent, 0, percent.length(), textBound);
		canvas.drawText(percent, pieCenter.x - textBound.width()/2, pieCenter.y + textBound.height()/2, textPaint);
		if (currentPie < pieScale.length){
			handler.postDelayed(runnable, FRAME_RATE);
		}
    }
	
	public void setPieScale(float[] scale){
		for (int i = 0; i < scale.length; i++){
			pieScale[i] = scale[i];
		}
		currentPieScale = 0.0f;
		currentPie = 0;
		invalidate();
	}
	
	private Paint getStrokePaint(int width, int color){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(width);
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		return paint;
	}
	
	private Paint getBasePaint(int color, boolean haveGradient){
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setStyle(Paint.Style.FILL);
		if (haveGradient) {
			paint.setShader(new LinearGradient(0, 0, pieRadius, pieRadius, color/2, color, TileMode.MIRROR));
//			paint.setShader(new SweepGradient(pieCenter.x, pieCenter.y, color/2, color));
//			paint.setShader(new RadialGradient(pieCenter.x, pieCenter.y, pieRadius, color/2, color, TileMode.MIRROR));
		}
		else{
			paint.setShader(new RadialGradient(pieCenter.x, pieCenter.y, pieRadius, 0xFFFFFF, color, TileMode.MIRROR));
		}
		return paint;
	}
	
	private Paint getTextPaint(){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setTextSize(textSize);
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		return paint;
	}
	
	@Override
	protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
		float width = getWidth();
		float height = getHeight();
		pieRadius = width > height ? height : width;
		pieRadius = pieRadius/2 - 5;
		pieCenter = new PointF(width/2, height/2);
		pieRect = new RectF(pieCenter.x - pieRadius, pieCenter.y - pieRadius, pieCenter.x + pieRadius, pieCenter.y + pieRadius);
	}
}
