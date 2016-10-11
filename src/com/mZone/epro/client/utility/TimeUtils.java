package com.mZone.epro.client.utility;

import com.mZone.epro.R;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 *
 * @author FSI
 *
 */
public class TimeUtils {
	/** ログ用タグ */
	private static final String LOG_TAG = "TimeUtils";

	/**
	 *
	 * @param context
	 * @param milliSecond
	 * @return
	 */
	public static final String getDateTime(final Context context, final long milliSecond) {
		AppLog.in(LOG_TAG, "getDateTime() context=%s, milliSecond=%d", context, milliSecond);

		final String dateTime = getDateTime(context, milliSecond, new Time());

		AppLog.out(LOG_TAG, "getDateTime() dateTime=%s", dateTime);
		return dateTime;
	}

	/**
	 *
	 * @param context
	 * @param milliSecond
	 * @param time
	 * @return
	 */
	public static final String getDateTime(final Context context, final long milliSecond, final Time time) {
		AppLog.in(LOG_TAG, "getDateTime() context=%s, milliSecond=%d, time=%s", context, milliSecond, time);
		String dateTime = null;

		// 時刻の設定
		time.set(milliSecond);
		final int julianDay = Time.getJulianDay(milliSecond, time.gmtoff);
		final int year = time.year;
		AppLog.debug(LOG_TAG, "getDateTime() milliSecond=%d, julianDay=%d, year=%d, time=%s", milliSecond, julianDay, year, time);

		// 現在時刻を取得
		final long nowMillis = System.currentTimeMillis();
		time.set(nowMillis);
		final int nowJulianDay = Time.getJulianDay(nowMillis, time.gmtoff);
		final int nowYear = time.year;
		AppLog.debug(LOG_TAG, "getDateTime() nowMillis=%d, nowJulianDay=%d, nowYear=%d, time=%s", nowMillis, nowJulianDay, nowYear, time);

		if ((nowMillis - milliSecond) < DateUtils.HOUR_IN_MILLIS) {
			// １時間以内の場合は、○分前表示
			final long minutesAgo = ((nowMillis - milliSecond) / DateUtils.MINUTE_IN_MILLIS);
			dateTime = context.getString(R.string.time_utils_date_time_minutes_ago_format, minutesAgo);
		} else if (julianDay == nowJulianDay) {
			// 今日の場合は、XX:XX表示
			dateTime = DateUtils.formatDateTime(context, milliSecond, DateUtils.FORMAT_SHOW_TIME);
		} else if (julianDay == (nowJulianDay - 1)) {
			// 昨日の場合は、昨日 XX:XX表示
			final String yesterday = DateUtils.formatDateTime(context, milliSecond, DateUtils.FORMAT_SHOW_TIME);
			dateTime = context.getString(R.string.time_utils_date_time_yesterday_format, yesterday);
		} else if ((nowJulianDay - 31) < julianDay) {
			// 31日以内の場合は、XX/XX(X) XX:XX表示
			final int flag = (DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_WEEKDAY);
			dateTime = DateUtils.formatDateTime(context, milliSecond, flag);
		} else if (nowYear == year) {
			// 年内の場合は、XX/XX(X) 表示
			final int flag = (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_ABBREV_WEEKDAY);
			dateTime = DateUtils.formatDateTime(context, milliSecond, flag);
		} else {
			// その他、XXXX/XX/XX表示
			final int flag = (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH);
			dateTime = DateUtils.formatDateTime(context, milliSecond, flag);
		}

		AppLog.out(LOG_TAG, "getDateTime() dateTime=%s", dateTime);
		return dateTime;
	}
}
