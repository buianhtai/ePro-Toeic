package com.mZone.epro;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger.LogLevel;
import com.google.android.gms.analytics.Tracker;
import com.mZone.epro.client.utility.AppLog;

import android.app.Application;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;

public class EproApplication extends Application {
	
	private static final String LOG_TAG = "TrinosApplication";

	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}
	
	private final HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	@Override
	public void onCreate() {
		AppLog.in(LOG_TAG, "onCreate()");
		super.onCreate();

		// デバッグをON
		FragmentManager.enableDebugLogging(BuildConfig.DEBUG);
		LoaderManager.enableDebugLogging(BuildConfig.DEBUG);

		// ログ、オートレポートをON
		final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
		analytics.getLogger().setLogLevel(BuildConfig.DEBUG?LogLevel.VERBOSE:LogLevel.WARNING);
		analytics.enableAutoActivityReports(this);

		AppLog.out(LOG_TAG, "onCreate()");
	}

	/**
	 *
	 * @param trackerId
	 * @return
	 */
	public synchronized Tracker getTracker(final TrackerName trackerId) {
		AppLog.in(LOG_TAG, "getTracker() trackerId=%s", trackerId);

		// Trackerの有無チェック
		final boolean contains = mTrackers.containsKey(trackerId);
		AppLog.debug(LOG_TAG, "getTracker() contains=%s", contains);

		if (!contains) {
			// Trackerがない場合はtrackerIdに合わせてTrackerオブジェクトを生成
			final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

			final Tracker tracker;
			if (trackerId == TrackerName.APP_TRACKER) {
				tracker = analytics.newTracker(R.xml.app_tracker);
			} else if (trackerId == TrackerName.GLOBAL_TRACKER) {
				tracker = analytics.newTracker(R.xml.global_tracker);
			} else {
				tracker = analytics.newTracker(R.xml.ecommerce_tracker);
			}

			// 保存
			mTrackers.put(trackerId, tracker);
		}

		// Trackerを取得
		final Tracker tracker = mTrackers.get(trackerId);

		AppLog.out(LOG_TAG, "getTracker() tracker=%s", tracker);
		return tracker;
	}

}
