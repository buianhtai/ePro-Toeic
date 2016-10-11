package com.mZone.epro.account.twitter;

import com.mZone.epro.BuildConfig;
import com.mZone.epro.R;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class TwitterWebLoginActivity extends ActionBarActivity {
	/** 認証トークン取得失敗ダイアログ用フラグメントタグ */
	private static final String FRAGMENT_TAG_DIALOG_OAUTH_REQUEST_TOKEN_ERROR = "FRAGMENT_TAG_DIALOG_OAUTH_REQUEST_TOKEN_ERROR";
	/**  */
	private Twitter mTwitter;
	/**  */
	private String mOauthVerifier;
	/**  */
	private WebView mWebView;

	/**  */
	private final Handler mShowDialogHandler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			switch (msg.what) {
				case MESSAGE_ID_OAUTH_REQUEST_TOKEN_ERROR_DIALOG:
					final Object obj = msg.obj;
					if (obj instanceof DialogFragment) {
						// ダイアログを表示
						final DialogFragment dialogFragment = (DialogFragment) obj;
						dialogFragment.show(getSupportFragmentManager(), FRAGMENT_TAG_DIALOG_OAUTH_REQUEST_TOKEN_ERROR);
					}
					break;
				default:
					break;
			}
		}
	};
	/**  */
	private static final int MESSAGE_ID_OAUTH_REQUEST_TOKEN_ERROR_DIALOG = 1;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// レイアウトを読み込み
		setContentView(R.layout.account_twitter_web_login_activity);

		// ActionBarの設定
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// WebViewの準備
		mWebView = (WebView) findViewById(R.id.webView);
		CookieSyncManager.createInstance(this);
//		final CookieManager cookieManager = CookieManager.getInstance();
//		cookieManager.removeAllCookie();
//		cookieManager.setAcceptCookie(false);
//		final WebSettings ws = mWebView.getSettings();
//		ws.setSaveFormData(false);
//		ws.setSavePassword(false);
		mWebView.setWebViewClient(new LoginTwitterWebViewClient());

		// Twitterアクセスオブジェクトを作成
		final ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setDebugEnabled(true);
		builder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY);
		builder.setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET);
		final Configuration configuration = builder.build();
		mTwitter = new TwitterFactory(configuration).getInstance();

		// 読み込み開始
		final LoaderManager loaderManager = getSupportLoaderManager();
		final GetTwitterOAuthRequestTokenLoaderCallback callback = new GetTwitterOAuthRequestTokenLoaderCallback();
		loaderManager.restartLoader(R.id.twitter_web_login_activity_load_oauth_request_token, null, callback);

		//
		setResult(RESULT_CANCELED);
	}

	/**
	 *
	 * @param url
	 */
	private void loadUrl(final String url) {
		mWebView.loadUrl(url);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.twitter_web_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		boolean consumed = false;

		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				consumed = true;
			default:
				// 自クラスで消費しない場合は親クラスに譲渡する
				consumed = super.onOptionsItemSelected(item);
				break;
		}

		return consumed;
	}

	/**
	 *
	 */
	private class LoginTwitterWebViewClient extends WebViewClient {
		/** ログ用タグ */
		@Override
		public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
			boolean handled = false;

			if (url.contains(TwitterConstants.CALLBACK_URL)) {
				// トークンのもとを取得
				final Uri uri = Uri.parse(url);
				mOauthVerifier = uri.getQueryParameter("oauth_verifier");

				// トークンを取得
				if (!TextUtils.isEmpty(mOauthVerifier)){
					final LoaderManager loaderManager = getSupportLoaderManager();
					final GetAccessTokenLoaderCallback callback = new GetAccessTokenLoaderCallback();
					loaderManager.initLoader(R.id.twitter_web_login_activity_load_access_token, null, callback);
					handled = true;
				}
				else{
					finishActivity();
				}
			}

			return handled;
		}
	}

	/**
	 *
	 */
	private static class GetTwitterOAuthRequestTokenLoader extends AbstractCacheAsyncTaskLoader<TwitterOAuthRequestToken> {
		/**  */
		private final Twitter mTwitter;

		/**
		 *
		 * @param context
		 * @param twitter
		 */
		public GetTwitterOAuthRequestTokenLoader(final Context context, final Twitter twitter) {
			super(context);
			mTwitter = twitter;
		}

		@Override
		public TwitterOAuthRequestToken loadInBackground() {
			final TwitterOAuthRequestToken result = new TwitterOAuthRequestToken();

			try {
				final RequestToken requestToken = mTwitter.getOAuthRequestToken(TwitterConstants.CALLBACK_URL);
				result.isCausedByNetworkIssue = false;
				result.isErrorMessageAvailable = false;
				result.errorCode = 0;
				result.authenticationURL = requestToken.getAuthenticationURL();
			} catch (final TwitterException e) {
				e.printStackTrace();
				result.isCausedByNetworkIssue = e.isCausedByNetworkIssue();
				result.isErrorMessageAvailable = e.isErrorMessageAvailable();
				result.errorCode = e.getErrorCode();
				result.exceptionCode = e.getExceptionCode();
				result.errorMessage = e.getErrorMessage();
				result.authenticationURL = null;
			}

			return result;
		}
	}

	/**
	 *
	 */
	private static final class TwitterOAuthRequestToken {
		/**  */
		public boolean isCausedByNetworkIssue;
		/**  */
		public boolean isErrorMessageAvailable;
		/**  */
		public int errorCode;
		/**  */
		public String exceptionCode;
		/**  */
		public String errorMessage;
		/**  */
		public String authenticationURL;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((authenticationURL == null) ? 0 : authenticationURL.hashCode());
			result = (prime * result) + errorCode;
			result = (prime * result) + ((errorMessage == null) ? 0 : errorMessage.hashCode());
			result = (prime * result) + ((exceptionCode == null) ? 0 : exceptionCode.hashCode());
			result = (prime * result) + (isCausedByNetworkIssue ? 1231 : 1237);
			result = (prime * result) + (isErrorMessageAvailable ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final TwitterOAuthRequestToken other = (TwitterOAuthRequestToken) obj;
			if (authenticationURL == null) {
				if (other.authenticationURL != null) {
					return false;
				}
			} else if (!authenticationURL.equals(other.authenticationURL)) {
				return false;
			}
			if (errorCode != other.errorCode) {
				return false;
			}
			if (errorMessage == null) {
				if (other.errorMessage != null) {
					return false;
				}
			} else if (!errorMessage.equals(other.errorMessage)) {
				return false;
			}
			if (exceptionCode == null) {
				if (other.exceptionCode != null) {
					return false;
				}
			} else if (!exceptionCode.equals(other.exceptionCode)) {
				return false;
			}
			if (isCausedByNetworkIssue != other.isCausedByNetworkIssue) {
				return false;
			}
			if (isErrorMessageAvailable != other.isErrorMessageAvailable) {
				return false;
			}
			return true;
		}

		@Override
		public String toString() {
			return "TwitterOAuthRequestToken [isCausedByNetworkIssue=" + isCausedByNetworkIssue + ", isErrorMessageAvailable=" + isErrorMessageAvailable + ", errorCode=" + errorCode + ", exceptionCode=" + exceptionCode + ", errorMessage=" + errorMessage + ", authenticationURL=" + authenticationURL + "]";
		}
	}

	private class GetTwitterOAuthRequestTokenLoaderCallback implements LoaderCallbacks<TwitterOAuthRequestToken> {
		@Override
		public Loader<TwitterOAuthRequestToken> onCreateLoader(final int id, final Bundle args) {
			final GetTwitterOAuthRequestTokenLoader loader = new GetTwitterOAuthRequestTokenLoader(getApplicationContext(), mTwitter);
			return loader;
		}

		@Override
		public void onLoadFinished(final Loader<TwitterOAuthRequestToken> loader, final TwitterOAuthRequestToken twitterOAuthRequestToken) {

			// URLの読み込み
			final String authenticationURL = twitterOAuthRequestToken.authenticationURL;
			if (!TextUtils.isEmpty(authenticationURL)) {
				loadUrl(authenticationURL);
			} else {
				// エラーダイアログを表示
				final OAuthRequestTokenErrorDialogFragment dialogFragment = OAuthRequestTokenErrorDialogFragment.newInstance(twitterOAuthRequestToken.isCausedByNetworkIssue, twitterOAuthRequestToken.isErrorMessageAvailable, twitterOAuthRequestToken.exceptionCode, twitterOAuthRequestToken.errorCode, twitterOAuthRequestToken.errorMessage);
				final Message msg = mShowDialogHandler.obtainMessage(MESSAGE_ID_OAUTH_REQUEST_TOKEN_ERROR_DIALOG, dialogFragment);
				mShowDialogHandler.sendMessage(msg);
			}

		}

		@Override
		public void onLoaderReset(final Loader<TwitterOAuthRequestToken> loader) {
		}
	}

	/**
	 *
	 */
	private static class GetAccessTokenLoader extends AbstractCacheAsyncTaskLoader<Boolean> {
		/**  */
		private final Twitter mTwitter;
		/**  */
		private final String mOauthVerifier;

		/**
		 *
		 * @param context
		 */
		public GetAccessTokenLoader(final Context context, final Twitter twitter, final String oauthVerifier) {
			super(context);
			mTwitter = twitter;
			mOauthVerifier = oauthVerifier;
		}

		@Override
		public Boolean loadInBackground() {
			boolean successful = false;

			try {
				// トークンを取得
				final AccessToken accessToken = mTwitter.getOAuthAccessToken(mOauthVerifier);
				final String token = accessToken.getToken();
				final String tokenSecret = accessToken.getTokenSecret();

				// トークンを保存
				final TwitterDataController twitterDataController = TwitterDataController.getInstance(getContext());
				successful = twitterDataController.saveOAuthAccessToken(token, tokenSecret);
				if (!successful) {
					return successful;
				}

				// Twitterオブジェクトを再生成
				final ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setDebugEnabled(BuildConfig.DEBUG);
				builder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET);
				builder.setOAuthAccessToken(token);
				builder.setOAuthAccessTokenSecret(tokenSecret);
				final Configuration configuration = builder.build();

				// スクリーン名を取得
				final Twitter twitter = new TwitterFactory(configuration).getInstance();
				final String screenName = twitter.getScreenName();

				// スクリーン名を保存
				successful = twitterDataController.saveAccountName(screenName);
			} catch (final IllegalStateException e) {
				e.printStackTrace();
				successful = false;
			} catch (final TwitterException e) {
				e.printStackTrace();
				successful = false;
			}

			return successful;
		}
	}

	/**
	 *
	 */
	private class GetAccessTokenLoaderCallback implements LoaderCallbacks<Boolean> {

		@Override
		public Loader<Boolean> onCreateLoader(final int id, final Bundle args) {

			// ローダーを作成
			final GetAccessTokenLoader loader = new GetAccessTokenLoader(getApplicationContext(), mTwitter, mOauthVerifier);

			return loader;
		}

		@Override
		public void onLoadFinished(final Loader<Boolean> loader, final Boolean successful) {

			if (successful) {
				// Activityを終了
				if (!isFinishing()) {
					setResult(RESULT_OK);
					finish();
				}
			}

		}

		@Override
		public void onLoaderReset(final Loader<Boolean> loader) {
		}
	}

	/**
	 *
	 */
	public static class OAuthRequestTokenErrorDialogFragment extends DialogFragment {

		/**  */
		private static final String FRAGMENT_ARGS_KEY_IS_CAUSED_BY_NETWORK_ISSUE = "isCausedByNetworkIssue";
		/**  */
		private static final String FRAGMENT_ARGS_KEY_IS_ERROR_MESSAGE_AVAILAVLE = "isErrorMessageAvailable";
		/**  */
		private static final String FRAGMENT_ARGS_KEY_EXCEPTION_CODE = "exceptionCode";
		/**  */
		private static final String FRAGMENT_ARGS_KEY_ERROR_CODE = "errorCode";
		/**  */
		private static final String FRAGMENT_ARGS_KEY_ERROR_MESSAGE = "errorMessage";

		/**
		 *
		 * @param isCausedByNetworkIssue
		 * @param isErrorMessageAvailable
		 * @param exceptionCode
		 * @param errorCode
		 * @param errorMessage
		 * @return
		 */
		public static OAuthRequestTokenErrorDialogFragment newInstance(final boolean isCausedByNetworkIssue, final boolean isErrorMessageAvailable, final String exceptionCode, final int errorCode, final String errorMessage) {

			// フラグメントを作成
			final OAuthRequestTokenErrorDialogFragment fragment = new OAuthRequestTokenErrorDialogFragment();

			// フラグメントの引数を作成
			final Bundle args = new Bundle();
			args.putBoolean(FRAGMENT_ARGS_KEY_IS_CAUSED_BY_NETWORK_ISSUE, isCausedByNetworkIssue);
			args.putBoolean(FRAGMENT_ARGS_KEY_IS_ERROR_MESSAGE_AVAILAVLE, isErrorMessageAvailable);
			args.putString(FRAGMENT_ARGS_KEY_EXCEPTION_CODE, exceptionCode);
			args.putInt(FRAGMENT_ARGS_KEY_ERROR_CODE, errorCode);
			args.putString(FRAGMENT_ARGS_KEY_ERROR_MESSAGE, errorMessage);
			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public Dialog onCreateDialog(final Bundle savedInstanceState) {

			// TODO : メッセージをxmlに変更すること
			final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.twitter_web_login_activity_oauth_request_token_error);

			// 引数を取得
			boolean isCausedByNetworkIssue = false;
			boolean isErrorMessageAvailable = false;
			String exceptionCode = null;
			int errorCode = 0;
			String errorMessage = null;
			final Bundle args = getArguments();
			if (args != null) {
				isCausedByNetworkIssue = args.getBoolean(FRAGMENT_ARGS_KEY_IS_CAUSED_BY_NETWORK_ISSUE);
				isErrorMessageAvailable = args.getBoolean(FRAGMENT_ARGS_KEY_IS_ERROR_MESSAGE_AVAILAVLE);
				exceptionCode = args.getString(FRAGMENT_ARGS_KEY_EXCEPTION_CODE);
				errorCode = args.getInt(FRAGMENT_ARGS_KEY_ERROR_CODE);
				errorMessage = args.getString(FRAGMENT_ARGS_KEY_ERROR_MESSAGE);
			}

			// エラー内容によりメッセージを切り替え
			if (isCausedByNetworkIssue) {
				builder.setMessage(R.string.twitter_web_login_activity_oauth_request_token_network_error);
			} else if (isErrorMessageAvailable) {
				final StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(errorMessage).append("\nErrorCode=").append(errorCode).append("\nExceptionCode=").append(exceptionCode).append("\n");
				builder.setMessage(stringBuilder.toString());
			} else {
				builder.setMessage(R.string.twitter_web_login_activity_oauth_request_token_unknown_error);
			}

			// リスナーを作成
			final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// Activityを閉じる
							dialog.dismiss();
							getActivity().finish();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
						default:
							// 何もしない
							break;
					}
				}
			};
			builder.setPositiveButton(R.string.twitter_web_login_activity_oauth_request_token_positive_button, listener);
			builder.setCancelable(false);

			// ダイアログを作成
			final Dialog dialog = builder.create();
			return dialog;
		}
	}
	
	private void finishActivity(){
		finish();
	}
}
