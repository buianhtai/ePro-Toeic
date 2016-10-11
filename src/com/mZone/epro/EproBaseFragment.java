package com.mZone.epro;

import java.lang.reflect.Field;

import com.mZone.epro.client.utility.AppLog;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

public abstract class EproBaseFragment extends Fragment {

	/** ログ用タグ */
	private static final String LOG_TAG = "EproBaseFragment";

	/**  */
	protected static final String SHADOW_COLOR_KEY_WHITE = "white";
	/**  */
	protected static final String SHADOW_COLOR_KEY_BLACK = "black";

	/**
	 *
	 * @param textView
	 * @param colorKey
	 */
	protected void setTextViewShadowLayer(final TextView textView, final String colorKey) {
		AppLog.in(LOG_TAG, "setTextViewShadowLayer() textView=%s, colorKey=%s", textView, colorKey);

		// シャドウ用のパラメータを作成
		final float radius = 2.0f;
		final float dx = 2.0f;
		final float dy = 2.0f;
		int textColor = Color.BLACK;
		int shadowColor = Color.WHITE;

		if(TextUtils.equals(colorKey, SHADOW_COLOR_KEY_WHITE)) {
			textColor = Color.WHITE;
			shadowColor = Color.BLACK;
		} else if(TextUtils.equals(colorKey, SHADOW_COLOR_KEY_BLACK)) {
			textColor = Color.BLACK;
			shadowColor = Color.WHITE;
		}

		// カラーを設定
		textView.setTextColor(textColor);
		textView.setShadowLayer(radius, dx, dy, shadowColor);

		AppLog.out(LOG_TAG, "setTextViewShadowLayer()");
	}
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} 
		catch (NoSuchFieldException e) {
		}
		sChildFragmentManagerField = f;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} 
			catch (Exception e) {
			}
		}
	}
}
