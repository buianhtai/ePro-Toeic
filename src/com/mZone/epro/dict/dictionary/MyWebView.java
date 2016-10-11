package com.mZone.epro.dict.dictionary;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MyWebView extends WebView implements MyJavaScriptListener,
		OnTouchListener, OnLongClickListener {

	Context mContext;
	MyWebViewCLient mywebviewclient;
	Dialog originalDialog;

	/** The selected text. */
	protected String mSelectedText = "";

	/** Javascript interface for catching text selection. */
	protected MyJavaScript mMyJavaScript = null;

	/** The current scale of the web view. */
	protected float mCurrentScale = 1.0f;

	protected float mLastTouchX, mLastTouchY;
	
	public static interface OnWordClickListener{
		public void showAnotherWord(String word);
	}
	private OnWordClickListener delegate;
	
	public MyWebView(Context context) {
		super(context);
		mContext = context;
		setup(context);
	}

	public MyWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		setup(context);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		setup(context);
	}


	/**
	 * Setups up the web view.
	 * 
	 * @param context
	 */
	protected void setup(Context context) {
		// On Touch Listener
		setOnLongClickListener(this);
		setOnTouchListener(this);

		// Webview setup
		getSettings().setJavaScriptEnabled(true);
		getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		getSettings().setBuiltInZoomControls(true);
		getSettings().setDisplayZoomControls(false);
		mywebviewclient = new MyWebViewCLient();
		setWebViewClient(mywebviewclient);
		mMyJavaScript = new MyJavaScript(context, this);
		addJavascriptInterface(mMyJavaScript, mMyJavaScript.getInterfaceName());
	}

	@Override
	public boolean onLongClick(View v) {
		String startTouchUrl = String.format(
				"var range = document.caretRangeFromPoint(%f,%f);",
				mLastTouchX, mLastTouchY);

		loadUrl("javascript:(function() { "
				+ startTouchUrl
				+ "range.expand('word');"
				+ "window.getSelection().addRange(range);"
				+ "var text = range.startContainer.textContent.substring(range.startOffset, range.endOffset);"
				// +"var text = window.getSelection().toString();"
				+ "window.TextSelection.selectionChanged(text);" + "})()");

		// Don't let the webview handle it
		return true;

	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float x = event.getX();
		float y = event.getY();
		float xPoint = getDensityIndependentValue(x, mContext) /
		getDensityIndependentValue(getScale(), mContext);
		float yPoint = getDensityIndependentValue(y, mContext) /
		getDensityIndependentValue(getScale(), mContext);
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mLastTouchX = xPoint;
			mLastTouchY = yPoint;
		}
		return false;
	}

	@Override
	public void tsjiSelectionChanged(String word) {
		if (TextUtils.isEmpty(word)) return;
		delegate.showAnotherWord(word);
	}

	/**
	 * Returns the density independent value of the given float
	 * 
	 * @param val
	 * @param ctx
	 * @return
	 */
	public float getDensityIndependentValue(float val, Context ctx) {

		// Get display from context
		Display display = ((WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		// Calculate min bound based on metrics
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		return val / (metrics.densityDpi / 160f);

		// return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, val,
		// metrics);
		
	}
	
	class MyWebViewCLient extends WebViewClient {
		
		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			// TODO Auto-generated method stub
			super.onScaleChanged(view, oldScale, newScale);
			mCurrentScale = newScale;
		}
	}
	
	public void setOriginalDialog(Dialog d){
		this.originalDialog = d;
	}
	
	public void setOnWordClickListenerDelegate(OnWordClickListener d){
		delegate = d;
	}
	
	
}
