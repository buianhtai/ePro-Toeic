package com.mZone.epro.account.twitter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class TwitterDataController {

	private static final String PREF_FILE_NAME = "twitter_data.pref";
	private static TwitterDataController mInstance;
	private SharedPreferences mPref = null;

	/**
	 *
	 * @param context
	 * @return
	 */
	public static synchronized TwitterDataController getInstance(final Context context) {
		if (mInstance == null) {
			final Context appContext = context.getApplicationContext();
			mInstance = new TwitterDataController(appContext);
		}
		return mInstance;
	}

	private TwitterDataController(final Context context) {
		super();
		// プリファレンスを取得
		mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 *
	 * @return
	 */
	public synchronized boolean isAccountOAuthed() {
		final String accessToken = mPref.getString(TwitterConstants.ACCOUNT_ACCESS_TOKEN, null);
		final boolean isAccountOAuthed = !TextUtils.isEmpty(accessToken);
		return isAccountOAuthed;
	}

	/**
	 *
	 * @return
	 */
	public synchronized String getAccountName() {
		final String accountName = mPref.getString(TwitterConstants.ACCOUNT_NAME, null);
		return accountName;
	}

	/**
	 *
	 * @return
	 */
	public synchronized String getToken() {
		final String token = mPref.getString(TwitterConstants.ACCOUNT_ACCESS_TOKEN, null);
		return token;
	}

	/**
	 *
	 * @return
	 */
	public synchronized String getTokenSecret() {
		final String tokenSecret = mPref.getString(TwitterConstants.ACCOUNT_ACCESS_TOKEN_SECRET, null);
		return tokenSecret;
	}

	/**
	 *
	 * @param token
	 * @param tokenSecret
	 * @return
	 */
	public synchronized boolean saveOAuthAccessToken(final String token, final String tokenSecret) {
		final SharedPreferences.Editor editor = mPref.edit();
		editor.putString(TwitterConstants.ACCOUNT_ACCESS_TOKEN, token);
		editor.putString(TwitterConstants.ACCOUNT_ACCESS_TOKEN_SECRET, tokenSecret);
		final boolean successful = editor.commit();
		return successful;
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public synchronized boolean saveAccountName(final String accountName) {
		final SharedPreferences.Editor editor = mPref.edit();
		editor.putString(TwitterConstants.ACCOUNT_NAME, accountName);
		final boolean successful = editor.commit();
		return successful;
	}

	/**
	 *
	 * @return
	 */
	public synchronized boolean clearAccountInfo() {
		final SharedPreferences.Editor editor = mPref.edit();
		editor.putString(TwitterConstants.ACCOUNT_NAME, null);
		editor.putString(TwitterConstants.ACCOUNT_ACCESS_TOKEN, null);
		editor.putString(TwitterConstants.ACCOUNT_ACCESS_TOKEN_SECRET, null);
		final boolean successful = editor.commit();
		return successful;
	}

}
