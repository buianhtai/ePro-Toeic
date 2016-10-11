package com.mZone.epro.common.view;

import com.mZone.epro.client.utility.AppLog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/**
 *
 */
public class NoHorizontalScrollButton extends Button {
	/** ログ用タグ */
	private static final String LOG_TAG = "NoHorizontalScrollButton";

	/**
	 * コンストラクタ
	 * @param context
	 */
	public NoHorizontalScrollButton(final Context context) {
		super(context);
		initialize();
	}

	/**
	 * コンストラクタ
	 * @param context
	 * @param attrs
	 */
	public NoHorizontalScrollButton(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	/**
	 * コンストラクタ
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public NoHorizontalScrollButton(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		initialize();
	}

	@Override
	public boolean canScrollHorizontally(final int direction) {
		AppLog.in(LOG_TAG, "canScrollHorizontally() direction=%d", direction);
		AppLog.out(LOG_TAG, "canScrollHorizontally() false.");
		return false;
	}

	/**
	 *
	 */
	private void initialize() {
		AppLog.in(LOG_TAG, "initialize()");
		AppLog.out(LOG_TAG, "initialize()");
	}
}
