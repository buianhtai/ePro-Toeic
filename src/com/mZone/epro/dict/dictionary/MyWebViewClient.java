package com.mZone.epro.dict.dictionary;

import android.content.res.AssetManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MyWebViewClient extends WebViewClient {
	AssetManager asManager;
	public MyWebViewClient(AssetManager am) {
		asManager = am;
	}
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	return false;
    }
    
    @Override
    public void onPageFinished(WebView view, String url) {
    	super.onPageFinished(view, url);
    	
    }
}