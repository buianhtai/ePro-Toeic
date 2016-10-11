package com.mZone.epro;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mZone.epro.EproApplication.TrackerName;
import com.mZone.epro.client.utility.AppLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public abstract class EproBaseActivity extends ActionBarActivity {

	private static final String LOG_TAG = "EproBaseActivity";
	
	/**  */
	private static final int LOADER_ID_DEFAULT_VALUE = 0;
	/**  */
	private static final String SAVED_INSTANCE_STATE_KEY_LOADER_ID = "saved_instance_state_key_loader_id";
	/**  */
	private int mLoaderId = LOADER_ID_DEFAULT_VALUE;
	
	protected static final String INTENT_EXTRA_KEY_OVERRIDE_PENDING_TRANSITION = "intent_extra_key_override_pending_transition";
	/** (int) */
	protected static final String INTENT_EXTRA_KEY_OVERRIDE_PENDING_TRANSITION_ENTER_ANIM = "intent_extra_key_override_pending_transition_enter_anim";
	/** (int) */
	protected static final String INTENT_EXTRA_KEY_OVERRIDE_PENDING_TRANSITION_EXIT_ANIM = "intent_extra_key_override_pending_transition_exit_anim";
	/**  */
	protected static final int OVERRIDE_PENDING_TRANSITION_NO_ANIM = 0;


	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		AppLog.i(LOG_TAG, "[IN]onCreate() savedInstanceState=%s", savedInstanceState);
		super.onCreate(savedInstanceState);
		
		mLoaderId = LOADER_ID_DEFAULT_VALUE;
		if (savedInstanceState != null) {
			mLoaderId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_KEY_LOADER_ID, LOADER_ID_DEFAULT_VALUE);
		}
		
		final EproApplication app = (EproApplication) getApplication();
		final Tracker tracker = app.getTracker(TrackerName.APP_TRACKER);
		AppLog.debug(LOG_TAG, "onCreate() tracker=%s", tracker);

		AppLog.i(LOG_TAG, "[OUT]onCreate()");
	}

	@Override
	public void onStart() {
		AppLog.i(LOG_TAG, "[IN]onStart()");
		super.onStart();

		AppLog.debug(LOG_TAG, "onStart() GoogleAnalytics.reportActivityStart=%s", this);
		final Context appContext = getApplicationContext();
		final GoogleAnalytics analytics = GoogleAnalytics.getInstance(appContext);
		analytics.reportActivityStart(this);

		AppLog.i(LOG_TAG, "[OUT]onStart()");
	}

	@Override
	public void onStop() {
		AppLog.i(LOG_TAG, "[IN]onStop()");

		AppLog.debug(LOG_TAG, "onStart() GoogleAnalytics.reportActivityStop=%s", this);
		final Context appContext = getApplicationContext();
		final GoogleAnalytics analytics = GoogleAnalytics.getInstance(appContext);
		analytics.reportActivityStop(this);

		// 子クラス→親クラスの順で実施
		super.onStop();

		AppLog.i(LOG_TAG, "[OUT]onStop()");
	}

	@Override
	protected void onDestroy() {
		AppLog.i(LOG_TAG, "[IN]onDestroy()");

		// ライセンスチェッカーの破棄
//		if (mGooglePlayLicenseChecker != null) {
//			AppLog.d(LOG_TAG, "[DEBUG]onDestroy() LicenseChecker destroy.");
//			mGooglePlayLicenseChecker.onDestroy();
//		}

		// 子クラス→親クラスの順で破棄
		super.onDestroy();

		AppLog.i(LOG_TAG, "[OUT]onDestroy()");
	}

	@Override
	protected void onSaveInstanceState(final Bundle outState) {
		AppLog.i(LOG_TAG, "[IN]onSaveInstanceState() outState=%s", outState);
		super.onSaveInstanceState(outState);

		// ローダーIDを保存
		outState.putInt(SAVED_INSTANCE_STATE_KEY_LOADER_ID, mLoaderId);
		AppLog.d(LOG_TAG, "[DEBUG]onSaveInstanceState() outState=%s", outState);

		AppLog.i(LOG_TAG, "[OUT]onSaveInstanceState()");
	}

	/**
	 * ローダー用のIDを採番します。
	 * @return 新しいローダーID
	 */
	public int getNewLoaderId() {
		AppLog.i(LOG_TAG, "[IN]getNewLoaderId()");

		// ローダーIDをインクリメント
		mLoaderId += 1;

		AppLog.i(LOG_TAG, "[OUT]getNewLoaderId() mLoaderId=%d", mLoaderId);
		return mLoaderId;
	}

	@Override
	public void finish() {
		AppLog.i(LOG_TAG, "[IN]finish()");

		// finish処理
		super.finish();

		// アニメーションを上書き
		final Intent intent = getIntent();
		final boolean overridePendingTransition = intent.getBooleanExtra(INTENT_EXTRA_KEY_OVERRIDE_PENDING_TRANSITION, false);
		if (overridePendingTransition) {
			AppLog.d(LOG_TAG, "[DEBUG]finish() Override Pending Transition.");
			final int enterAnim = intent.getIntExtra(INTENT_EXTRA_KEY_OVERRIDE_PENDING_TRANSITION_ENTER_ANIM, OVERRIDE_PENDING_TRANSITION_NO_ANIM);
			final int exitAnim = intent.getIntExtra(INTENT_EXTRA_KEY_OVERRIDE_PENDING_TRANSITION_EXIT_ANIM, OVERRIDE_PENDING_TRANSITION_NO_ANIM);
			overridePendingTransition(enterAnim, exitAnim);
		}

		AppLog.i(LOG_TAG, "[OUT]finish()");
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		AppLog.in(LOG_TAG, "onOptionsItemSelected() item=%s", item);
		boolean consumed = false;

		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				consumed = true;
				break;
			default:
				// 親クラスに譲渡
				consumed = super.onOptionsItemSelected(item);
				break;
		}

		AppLog.out(LOG_TAG, "onOptionsItemSelected() consumed=%s", consumed);
		return consumed;
	}
}
