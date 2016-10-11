package com.mZone.epro.dict.dictionary;

import com.mZone.epro.R;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class ClearableEditText extends RelativeLayout {
	LayoutInflater inflater = null;
	public EditText edit_text;
	Button btn_Search;
	Button btn_clear;
	
	public static interface ClearableEditTextListener{
		public void afterTextChanged(String searchStr);
	}
	ClearableEditTextListener delegate;
	
	public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initViews();
	}

	public ClearableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews();

	}

	public ClearableEditText(Context context) {
		super(context);
		initViews();
	}

	void initViews() {
		inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.dict_clearable_edit_text, this, true);
		edit_text = (EditText) findViewById(R.id.clearable_edit);
		btn_Search = (Button) findViewById(R.id.clearable_button_search);
		btn_clear = (Button) findViewById(R.id.clearable_button_clear);
		btn_clear.setVisibility(View.INVISIBLE);
		clearText();
		showHideClearButton();
	}

	void clearText() {
		btn_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				edit_text.setText("");
			}
		});
	}

	void showHideClearButton() {
		edit_text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.length() > 0)
					btn_clear.setVisibility(View.VISIBLE);
				else
					btn_clear.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchStr = s.toString();
				if (TextUtils.isEmpty(searchStr)){
					return;
				}
				if (delegate != null){
					delegate.afterTextChanged(searchStr);
				}
			}
		});
	}

	public Editable getText() {
		Editable text = edit_text.getText();
		return text;
	}
	
	public void setEditTextDelegate(ClearableEditTextListener d) {
		this.delegate = d;
	}
	
	public void setHintText(String text) {
		edit_text.setHint(text);
	}
	
	public void setBtnSearchVisible(int v) {
		btn_Search.setVisibility(v);
		if (v == View.INVISIBLE) {
			edit_text.setPadding(20, 0, 35, 0);
		} else {
			edit_text.setPadding(30, 0, 35, 0);
		}
	}
	
	public void enableEditText(boolean b){
		edit_text.setEnabled(b);
	}
}