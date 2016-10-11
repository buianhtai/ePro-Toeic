package com.mZone.epro.dict.dictionary;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class MyJavaScript {
	
	/** The javascript interface name for adding to web view. */
	private final String interfaceName = "TextSelection";
	
	/** The webview to work with. */
	private MyJavaScriptListener listener;
	
	/** The context. */
	Context mContext;
	/**
	 * Constructor accepting context.
	 * @param c
	 */
	public MyJavaScript(Context c){
		this.mContext = c;
	}
	
	/**
	 * Constructor accepting context and listener.
	 * @param c
	 * @param listener
	 */
	public MyJavaScript(Context c, MyJavaScriptListener listener){
		this.mContext = c;
		this.listener = listener;
	}
	
	@JavascriptInterface
	public void selectionChanged(String text){
		if(this.listener != null)
			this.listener.tsjiSelectionChanged(text);
    	
	}
	
	/**
	 * Gets the interface name
	 * @return
	 */
    @JavascriptInterface
	public String getInterfaceName(){
		return this.interfaceName;
	}
	
}
