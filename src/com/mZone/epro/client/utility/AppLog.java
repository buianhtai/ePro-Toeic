package com.mZone.epro.client.utility;

import com.mZone.epro.BuildConfig;
import android.util.Log;

/**
 *
 */
public class AppLog {

	/** ログ出力ON/OFF */
	private static final boolean mOutputLog = BuildConfig.DEBUG;
	/** ログ出力レベル */
	private static final int mOutputLogLevel = android.util.Log.VERBOSE;

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static void v(final String tag, final String format, final Object... args) {
		if (mOutputLog && (mOutputLogLevel <= android.util.Log.VERBOSE)) {
			Log.v(tag, String.format(format, args));
		}
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void d(final String tag, final String format, final Object... args) {
		if (mOutputLog && (mOutputLogLevel <= android.util.Log.DEBUG)) {
			Log.d(tag, String.format(format, args));
		}
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void i(final String tag, final String format, final Object... args) {
		if (mOutputLog && (mOutputLogLevel <= android.util.Log.INFO)) {
			Log.i(tag, String.format(format, args));
		}
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void w(final String tag, final String format, final Object... args) {
		if (mOutputLog && (mOutputLogLevel <= android.util.Log.WARN)) {
			Log.w(tag, String.format(format, args));
		}
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void e(final String tag, final String format, final Object... args) {
		if (mOutputLog && (mOutputLogLevel <= android.util.Log.ERROR)) {
			Log.e(tag, String.format(format, args));
		}
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void in(final String tag, final String format, final Object... args) {
		i(tag, "[IN]" + format, args);
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void out(final String tag, final String format, final Object... args) {
		i(tag, "[OUT]" + format, args);
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void debug(final String tag, final String format, final Object... args) {
		d(tag, "[DEBUG]" + format, args);
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void warn(final String tag, final String format, final Object... args) {
		w(tag, "[WARN]" + format, args);
	}

	/**
	 *
	 * @param tag
	 * @param format
	 * @param args
	 */
	public static final void error(final String tag, final String format, final Object... args) {
		e(tag, "[ERROR]" + format, args);
	}
}
