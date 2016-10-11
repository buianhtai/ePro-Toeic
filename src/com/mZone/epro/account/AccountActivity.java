package com.mZone.epro.account;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
//import com.google.ads.Ad;
//import com.google.ads.AdListener;
//import com.google.ads.AdRequest;
//import com.google.ads.AdRequest.ErrorCode;
//import com.google.ads.AdSize;
//import com.google.ads.AdView;
import com.mZone.epro.R;
import com.mZone.epro.account.dialog.CheckTwitterOauthDialogFragment;
import com.mZone.epro.account.dialog.ChoosePaymentMethodDialog;
import com.mZone.epro.account.dialog.PhonecardPaymentDialog;
import com.mZone.epro.account.dialog.PhonecardPaymentDialog.PhonecardPaymentDialogDelegate;
import com.mZone.epro.account.twitter.TwitterConstants;
import com.mZone.epro.account.twitter.TwitterDataController;
import com.mZone.epro.client.dialog.AccountChoosingDialog;
import com.mZone.epro.client.dialog.AccountChoosingDialog.AccountChoosingDialogListener;
import com.mZone.epro.client.utility.AESHelper;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;
import com.mZone.epro.client.utility.Constants;
import com.mZone.epro.client.utility.GenerateKey;
import com.mZone.epro.client.utility.JSONParser;
import com.mZone.epro.client.utility.MyPreference;
import com.mZone.epro.client.utility.Utils;
import com.mZone.epro.client.utility.MyPreference.Account;
import com.purchase.utility.IabHelper;
import com.purchase.utility.IabHelper.OnIabSetupFinishedListener;
import com.purchase.utility.IabResult;
import com.purchase.utility.Inventory;
import com.purchase.utility.Purchase;

import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class AccountActivity extends ActionBarActivity implements OnClickListener, 
													AccountChoosingDialogListener,
													PhonecardPaymentDialogDelegate{

	public static final String ACCOUNT_CHANGE_BROADCAST_INTENT = "com.mZone.epro.account.AccountActivity.ACCOUNT_CHANGE_BROADCAST_INTENT";
	public static final String PHONECARD_DIALOG_FRAGMENT_TAG = "PHONECARD_DIALOG_FRAGMENT_TAG";
	public static final int START_ACTIVITY_REQUEST_CODE_LOGIN_TWITTER = 1;
	private static final String FRAGMENT_TAG_DIALOG_CHECK_TWITTER_OAUTH = "FRAGMENT_TAG_DIALOG_CHECK_TWITTER_OAUTH";
	private static final int TOAST_TIME = 1000;
	
	private static final int CREDIT_PER_CLICK = 1;
	private static final int RC_REQUEST = 10001;
	
	public static final String NO_ACCOUNT_START_ACTIVITY_ARGS = "com.mZone.epro.account.AccountActivity.NO_ACCOUNT_START_ACTIVITY_ARGS";
	
//	private static final String EPRO_APP_LINK = "http://www.mzone.com.vn";
	
	//account info layout
	private Account mAccount;
	
	//view
	private ProgressDialog mProgressDialog;
	private TextView mAccountTv;
	private Button mLogBtn;
	private TextView mCreditTv;
	
	private View mBuyCreditParent1;
	private View mBuyCreditParent2;
	private View mBuyCreditParent3;
	
	//buy credit layout
	private Button mBuyCredit1;	//0.99
	private Button mBuyCredit2;	//1.99
	private Button mBuyCredit3;	//4.99
	
	//buy phonecard
	private Button mBuyPhonecard;
	
	//ads layout
//	private AdView mAdView;
	
	//google payment
	private IabHelper mHelper = null;
	private boolean isOnPurchaseFlow = false;
	
	//facebook
	private ImageButton facebookShareBtn;
	private UiLifecycleHelper uiHelper;
	
	//twitter
	private ImageButton twitterShareBtn;
	private boolean twitterIsOauth;
	private boolean twitterThreadFinish = true;
			
	/****************************************************************************************/
	/****************************************************************************************/
	/********************************** Activity life cycle start ***************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    
	    //Not show phonecard payment on other language system
	    String languageCode = getResources().getString(R.string.app_first_load_language_code);
	    if (!TextUtils.isEmpty(languageCode) && !languageCode.equals("vi") && !languageCode.equals("en")){
	    	View phonecardLayout = findViewById(R.id.account_activity_buy_credit_by_phonecard_parent);
	    	phonecardLayout.setVisibility(View.GONE);
	    }
	    
	    //account info layout
	    mLogBtn = (Button) findViewById(R.id.activity_account_log_btn);
	    mLogBtn.setOnClickListener(this);
	    mAccountTv = (TextView) findViewById(R.id.activity_account_account_tv);
	    mCreditTv = (TextView) findViewById(R.id.activity_account_credit_tv);
	    
	    mBuyCreditParent1 = findViewById(R.id.account_activity_google_credit_btn_parent_1);
	    mBuyCreditParent2 = findViewById(R.id.account_activity_google_credit_btn_parent_2);
	    mBuyCreditParent3 = findViewById(R.id.account_activity_google_credit_btn_parent_3);
	    mBuyCreditParent1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPurchaseFlow(Constants.PAYMENT_OPTION[0]);
			}
		});
	    
	    mBuyCreditParent2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPurchaseFlow(Constants.PAYMENT_OPTION[1]);
			}
		});

	    mBuyCreditParent3.setOnClickListener(new OnClickListener() {
	
	    	@Override
	    	public void onClick(View v) {
	    		startPurchaseFlow(Constants.PAYMENT_OPTION[2]);
	    	}
	    });
	    
		//buy credit layout
	    mBuyCredit1 = (Button) findViewById(R.id.account_activity_google_credit_btn_1);
	    mBuyCredit2 = (Button) findViewById(R.id.account_activity_google_credit_btn_2);
	    mBuyCredit3 = (Button) findViewById(R.id.account_activity_google_credit_btn_3);
	    mBuyCredit1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPurchaseFlow(Constants.PAYMENT_OPTION[0]);
			}
		});
		mBuyCredit2.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				startPurchaseFlow(Constants.PAYMENT_OPTION[1]);
			}
		});
		mBuyCredit3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startPurchaseFlow(Constants.PAYMENT_OPTION[2]);
			}
		});
		
		mBuyPhonecard = (Button)findViewById(R.id.account_activity_buy_credit_by_phonecard);
		mBuyPhonecard.setOnClickListener(this);
		
		facebookShareBtn = (ImageButton)findViewById(R.id.activity_account_facebook_share);
		facebookShareBtn.setOnClickListener(this);
		uiHelper = new UiLifecycleHelper(this, null);
		uiHelper.onCreate(savedInstanceState);
		
		twitterShareBtn = (ImageButton)findViewById(R.id.activity_account_twitter_share);
		twitterShareBtn.setOnClickListener(this);
		final TwitterDataController twitterDataController = TwitterDataController.getInstance(this);
		twitterIsOauth = twitterDataController.isAccountOAuthed();

		//ads layout
		/*
		int adSize = this.getResources().getInteger(R.integer.ad_size);
		if (adSize == 1){
			mAdView = new AdView(this, AdSize.BANNER, Constants.GOOGLE_ADS);
		}
		else if (adSize == 2){
			mAdView = new AdView(this, AdSize.IAB_BANNER, Constants.GOOGLE_ADS);
		}
		mAdView.setAdListener(new AdListener() {
			@Override
			public void onReceiveAd(Ad arg0) {
				mAdView.requestLayout();
			}
			@Override
			public void onPresentScreen(Ad arg0) {
				//After one click on google ads.
				if (mAccount != null){
					int status = MyPreference.checkIfCanClickAd(getApplicationContext());
					int nClick = -1;
					switch (status) {
						case MyPreference.PREVIOUS_AD_CLICK_STATUS_UNABLE:
							showAdOverClickDialog();
							return;
						case MyPreference.PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_WITHOUT_RESET:
							nClick = MyPreference.increaseNumberOfClickNormal(getApplicationContext());
							break;
						case MyPreference.PREVIOUS_AD_CLICK_STATUS_ABLE_TO_SAVE_AND_RESET:
							nClick = MyPreference.increaseNumberOfClickAndReset(getApplicationContext());
							break;
	
						default:
							break;
					}
					if (nClick > 0){
						mAccount.numberOfClick = nClick;
						mCreditTv.setText(String.valueOf(mAccount.credits + nClick*CREDIT_PER_CLICK) + " " + getResources().getString(R.string.account_activity_credit));
					}
				}
				else{
					showCannotEarnFreeCreditBecauseNoAccount();
				}
			}
			@Override
			public void onLeaveApplication(Ad arg0) {}
			@Override
			public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {}
			@Override
			public void onDismissScreen(Ad arg0) {}
		});
		ViewGroup mAdsContainer = (ViewGroup) findViewById(R.id.account_activity_ad_view);
		mAdsContainer.addView(mAdView);
		mAdView.loadAd(new AdRequest());
		*/
		setupInAppPurchase();
		readData();
		updateUI();
		
		boolean isNeedShowAccountDialog = this.getIntent().getBooleanExtra(NO_ACCOUNT_START_ACTIVITY_ARGS, false);
		if (isNeedShowAccountDialog){
			showLoginDialog();
		}
	}
		
	@Override
    public void onDestroy() {
//		mAdView.destroy();
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
        uiHelper.onDestroy();
        super.onDestroy();
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (mHelper != null && !mHelper.handleActivityResult(requestCode,resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
				case START_ACTIVITY_REQUEST_CODE_LOGIN_TWITTER:
					final TwitterDataController twitterDataController = TwitterDataController.getInstance(this);
//					final String screenName = twitterDataController.getAccountName();
					twitterIsOauth = twitterDataController.isAccountOAuthed();
					if (twitterIsOauth){
						showTwitterShareConfirmDialog();
					}
					break;
				default:
					uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
				        @Override
				        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
				        }

				        @Override
				        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
				        	if (data == null){
				        		return;
				        	}
				            boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
				            String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
//				            String postId = FacebookDialog.getNativeDialogPostId(data);
				            if (didCancel){
				            	if (!TextUtils.isEmpty(completionGesture) && completionGesture.equals("post")){
				    				int nClick = -1;
				    				nClick = MyPreference.increaseCreditByFacebookShare(getApplicationContext());
				    				if (nClick > 0){
				    					mAccount.numberOfClick = nClick;
				    					mCreditTv.setText(String.valueOf(mAccount.credits + nClick*CREDIT_PER_CLICK) + " " + getResources().getString(R.string.account_activity_credit));
				    				}		
				            	}
				            }
				        }
				    });
					break;
			}
		}
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
//	    mAdView.resume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
//	    mAdView.pause();
	    uiHelper.onPause();
	    super.onPause();
	}
	
	/********************************** Activity life cycle end *****************************/
	/****************************************************************************************/
	/****************************************************************************************/
	
	/****************************************************************************************/
	/****************************************************************************************/
	/********************************** Data and UI start ***********************************/
	
	protected void saveData(){
		MyPreference.setAccount(getApplicationContext(), mAccount);
	}
	
	protected void readData(){
		mAccount = MyPreference.getAccount(getApplicationContext());
	}
	
	protected void updateUI(){
		if (mAccount == null){
			mAccountTv.setText(R.string.account_activity_no_account);
			mLogBtn.setText(R.string.account_activity_login);
			mCreditTv.setText(getResources().getString(R.string.account_activity_no_credit));
		}
		else{
			mAccountTv.setText(mAccount.account);
			mLogBtn.setText(R.string.account_activity_logout);
			mCreditTv.setText(String.valueOf(mAccount.credits + mAccount.numberOfClick*CREDIT_PER_CLICK) + " " + getResources().getString(R.string.account_activity_credit));
		}
	}
	
	protected void showProgress( ) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(getString(R.string.loading_account_info_dialog_message));
		mProgressDialog.setIndeterminate( true );
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	protected void dismissProgress() {
		if (mProgressDialog != null && mProgressDialog.isShowing() && mProgressDialog.getWindow() != null) {
			try {
				mProgressDialog.dismiss();
			} catch ( IllegalArgumentException ignore ) { ; }
		}
	 	mProgressDialog = null;
	}
	
	protected void showNetworkErrorDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.network_error_dialog_tittle);
		builder.setMessage(R.string.network_error_dialog_message)
			.setPositiveButton(R.string.network_error_dialog_position_btn, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					(AccountActivity.this).startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
	            }
			})
	        .setNegativeButton(R.string.network_error_dialog_negative_btn, null);
		builder.create().show();
	}
	
	private void startPurchaseFlow(String sku){
		if (mAccount == null){
			//Show no account dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.account_activity_no_account_dialog_tittle);
			builder.setMessage(R.string.account_activity_no_account_dialog_message);
			builder.setNegativeButton(R.string.account_activity_no_account_dialog_cancel, null);
			builder.setPositiveButton(R.string.account_activity_no_account_dialog_login, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (!Utils.isNetworkAvailable(getApplicationContext())){
						showNetworkErrorDialog();
					}
					else{
						showLoginDialog();
					}
				}
			});
			builder.create().show();
		}
		else{
			if (isOnPurchaseFlow) return;
			String payload = getResources().getString(R.string.facebook_app_id);
			isOnPurchaseFlow = true;
			mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, mPurchaseFinishedListener, payload);
		}
	}
	
	private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        if (!TextUtils.isEmpty(payload) && payload.equals(getResources().getString(R.string.facebook_app_id))){
        	return true;
        }

        return false;
    }

	/********************************** Data and UI end *************************************/
	/****************************************************************************************/
	/****************************************************************************************/

	
	/****************************************************************************************/
	/****************************************************************************************/
	/********************************** Action bar start ************************************/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.account, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		int viewID = v.getId();
		switch (viewID) {
			case R.id.activity_account_log_btn:
				if (mAccount != null){
					showLogoutDialog();
				}
				else{
					if (!Utils.isNetworkAvailable(this)){
						showNetworkErrorDialog();
					}
					else{
						showLoginDialog();
					}
				}
				break;
			case R.id.account_activity_buy_credit_by_phonecard:
				if (mAccount == null){
					showCannotEarnFreeCreditBecauseNoAccount();
					return;
				}
				else{
					if (!Utils.isNetworkAvailable(this)){
						showNetworkErrorDialog();
					}
					else{
						showPhonecardPaymentDialog();
					}
				}
				break;
			case R.id.activity_account_facebook_share:
				if (mAccount == null){
					showCannotEarnFreeCreditBecauseNoAccount();
					return;
				}
				if (!MyPreference.checkIfAbleToShareFacebook(getApplicationContext())){
					showFacebookOverClickDialog();
					return;
				}
				if (!Utils.isNetworkAvailable(this)){
					showNetworkErrorDialog();
				}
				else{
					if (FacebookDialog.canPresentShareDialog(getApplicationContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
						final String appPackageName = getPackageName();
						String appLink = "http://play.google.com/store/apps/details?id=" + appPackageName;
						FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(this)
																.setLink(appLink)
																.setDescription(getResources().getString(R.string.epro_app_share_message))
																.build();
						uiHelper.trackPendingDialogCall(shareDialog.present());
					}
					else{
//						publishFeedDialog();
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_facebook_app_error), Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case R.id.activity_account_twitter_share:
				if (mAccount == null){
					showCannotEarnFreeCreditBecauseNoAccount();
					return;
				}
				if (!MyPreference.checkIfAbleToShareTwitter(getApplicationContext())){
					showTwitterOverClickDialog();
					return;
				}
				if (!Utils.isNetworkAvailable(this)){
					showNetworkErrorDialog();
				}
				else if (twitterIsOauth){
					showTwitterShareConfirmDialog();
				}
				else{
					askTwitterOAuth();
				}
				break;
			default:
				break;
		}
	}
	
	/********************************** Action bar end ************************************/
	/****************************************************************************************/
	/****************************************************************************************/

	
	/****************************************************************************************/
	/****************************************************************************************/
	/********************************** Account Log start ***********************************/

	
	/**
	 * showLoginDialog
	 */
	private void showLoginDialog(){
		//Check Internet connection first
		AccountChoosingDialog dialog = new AccountChoosingDialog(this);
		dialog.show(getSupportFragmentManager(), "AccountChoosingDialog");
	}

	@Override
	public void onAccountSelect(String account) {
		final LoginTaskLoaderCallback callback = new LoginTaskLoaderCallback();
		Bundle args = new Bundle();
		args.putString(LoginTaskLoaderCallback.ARGS_ACCOUNT_NAME, account);
		final LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.restartLoader(R.id.login_task_loader_id, args, callback);
	}
	
	/**
	 * showLogoutDialog
	 */
	private void showLogoutDialog(){
		//Check Internet connection first
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.account_activity_logout_alert_tittle);
		dialog.setMessage(R.string.account_activity_logout_alert_message);
		dialog.setNegativeButton(R.string.account_activity_logout_alert_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onAccountLogout();
			}
		});
		dialog.setPositiveButton(R.string.account_activity_logout_alert_cancel, null);
		dialog.create().show();
	}
	
	/**
	 * Logout account
	 */
	private void onAccountLogout(){
		mAccount = null;
		Account oldAccount = MyPreference.getAccount(getApplicationContext());
		MyPreference.removeCurrentAccount(getApplicationContext());
		final LogoutTaskLoaderCallback callback = new LogoutTaskLoaderCallback();
		Bundle args = new Bundle();
		args.putParcelable(LogoutTaskLoaderCallback.ARGS_ACCOUNT, oldAccount);
		final LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.restartLoader(R.id.logout_task_loader_id, args, callback);
		updateUI();
		broadcastAccountChange();
	}
	
	private void broadcastAccountChange(){
		Intent intent = new Intent(ACCOUNT_CHANGE_BROADCAST_INTENT);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	/**
	 * Update click add number of old to server
	 * @author Tony Huynh
	 *
	 */
	private static class LogoutTaskLoader extends AbstractCacheAsyncTaskLoader<Integer>{
		private Account oldAccount;
		public LogoutTaskLoader(Context context, Account oldAccount) {
			super(context);
			this.oldAccount = oldAccount;
		}
		@Override
		public Integer loadInBackground() {
			if (!TextUtils.isEmpty(oldAccount.account) && oldAccount.numberOfClick > 0){
				String nClick = String.valueOf(oldAccount.numberOfClick);
				List<NameValuePair> mParams = new ArrayList<NameValuePair>();
				mParams.add(new BasicNameValuePair(Constants.PARAM_GOOGLE_ACCOUNT, oldAccount.account));
				mParams.add(new BasicNameValuePair(Constants.PARAM_NUMBER_OF_CLICK, nClick));
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonResult = jsonParser.makeHttpRequest(AESHelper.URLDescript(Constants.url_plus_coins), "POST", mParams);
				if (jsonResult != null){
					try {
						int status = jsonResult.getInt(Constants.PARAM_SUCCESS_006);
						if (status == 1) {
							return status;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						return -1;
					}
				}
				else{
					return -1;
				}
			}
			return 0;
		}
	}
	
	/**
	 * LogoutTaskLoaderCallback
	 * Args = oldAccount
	 * @author Tony Huynh
	 *
	 */
	private class LogoutTaskLoaderCallback implements LoaderCallbacks<Integer>{

		public static final String ARGS_ACCOUNT = "LogoutTaskLoaderCallback.ARGS_ACCOUNT";
		@Override
		public Loader<Integer> onCreateLoader(int id, Bundle args) {
			Account oldAccount = args.getParcelable(ARGS_ACCOUNT);
			final LogoutTaskLoader loader = new LogoutTaskLoader(getApplicationContext(), oldAccount);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Integer> loader, Integer status) {
			if (status != -1){
			}
			else{
			}
		}

		@Override
		public void onLoaderReset(Loader<Integer> loader) {
			
		}
	}
	
	/**
	 * LoginTaskLoader
	 * @author Tony Huynh
	 *
	 */
	private static class LoginTaskLoader extends AbstractCacheAsyncTaskLoader<Integer>{
		
		public static final int STATUS_ERROR = -1;
		private String account;
		
		public LoginTaskLoader(Context context, String account) {
			super(context);
			this.account = account;
		}

		@Override
		public Integer loadInBackground() {
			List<NameValuePair> paramsSearch = new ArrayList<NameValuePair>();
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_USER_ID, account));
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_NUMBER_OF_CLICK, String.valueOf(0)));
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonSearch = jsonParser.makeHttpRequest(AESHelper.URLDescript(Constants.url_user_infor), "GET", paramsSearch);
			if (jsonSearch != null){
				try {
					int status = jsonSearch.getInt(Constants.PARAM_SUCCESS_003);
					if (status == 1){
						JSONArray searchResultObj = jsonSearch.getJSONArray(Constants.PARAM_USER);
						JSONObject arrElement = searchResultObj.getJSONObject(0);
						int credits = arrElement.getInt(Constants.PARAM_CREDIT);
						return credits;
					}
					else{
						return status;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return STATUS_ERROR;
				}
			}
			return STATUS_ERROR;
		}
	}
	
	/**
	 * LoginTaskLoaderCallback
	 * @author Tony Huynh
	 *
	 */
	private class LoginTaskLoaderCallback implements LoaderCallbacks<Integer>{

		public static final String ARGS_ACCOUNT_NAME = "LoginTaskLoaderCallback.ARGS_ACCOUNT_NAME";
		String account;
		
		@Override
		public Loader<Integer> onCreateLoader(int id, Bundle args) {
			showProgress();
			account = args.getString(ARGS_ACCOUNT_NAME);
			final LoginTaskLoader loader = new LoginTaskLoader(getApplicationContext(), account);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Integer> loader, Integer credits) {
			if (credits == LoginTaskLoader.STATUS_ERROR){
				dismissProgress();
				showLoginErrorDialog();
			}
			else{
				mAccount = new Account(account, credits);
				saveData();
				dismissProgress();
				updateUI();
				broadcastAccountChange();
			}
		}

		@Override
		public void onLoaderReset(Loader<Integer> loader) {
			
		}
	}
	
	/**
	 * check if login have error
	 */
	private void showLoginErrorDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_login_error_tittle);
		builder.setMessage(R.string.account_activity_login_error_message)
	        .setNegativeButton(R.string.account_activity_login_error_ok_btn, null);
		builder.create().show();
	}
	
	/********************************** Account Log end *************************************/
	/****************************************************************************************/
	/****************************************************************************************/
	
	
	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** Payment start ************************************/
	
	private void setupInAppPurchase(){
		mHelper = new IabHelper(this, Constants.base64EncodedPublicKey);
		mHelper.enableDebugLogging(false);
		mHelper.startSetup(new OnIabSetupFinishedListener() {
			
			@Override
			public void onIabSetupFinished(IabResult result) {
				if (!result.isSuccess()) {
					return;
				}
				if (mHelper == null){
					return;
				}
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}
	
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		@Override
		public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
			if (mHelper == null) {
				return;
			}
			if (result.isFailure()) {
                return;
            }
			if (mAccount == null){
				return;
			}
			
			Purchase purchase1 = inventory.getPurchase(Constants.PAYMENT_OPTION[0]);
			if (purchase1 != null && verifyDeveloperPayload(purchase1)){
				UpdateCreditsTaskLoaderCallback callback = new UpdateCreditsTaskLoaderCallback(purchase1);
				LoaderManager loaderManager = getSupportLoaderManager();
				if (loaderManager != null){
					loaderManager.restartLoader(R.id.google_purchase_finish_task_loader_id, null, callback);
				}
				return;
			}
			Purchase purchase2 = inventory.getPurchase(Constants.PAYMENT_OPTION[1]);
			if (purchase2 != null && verifyDeveloperPayload(purchase2)){
				UpdateCreditsTaskLoaderCallback callback = new UpdateCreditsTaskLoaderCallback(purchase2);
				LoaderManager loaderManager = getSupportLoaderManager();
				if (loaderManager != null){
					loaderManager.restartLoader(R.id.google_purchase_finish_task_loader_id, null, callback);
				}
				return;
			}
			Purchase purchase3 = inventory.getPurchase(Constants.PAYMENT_OPTION[2]);
			if (purchase3 != null && verifyDeveloperPayload(purchase3)){
				UpdateCreditsTaskLoaderCallback callback = new UpdateCreditsTaskLoaderCallback(purchase3);
				LoaderManager loaderManager = getSupportLoaderManager();
				if (loaderManager != null){
					loaderManager.restartLoader(R.id.google_purchase_finish_task_loader_id, null, callback);
				}
				return;
			}
		}
	};
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			isOnPurchaseFlow = false;
			if (mHelper == null) return;
			if (result.isFailure()) {
				return;
			}
			if (!verifyDeveloperPayload(purchase)) {
                Toast.makeText(getApplicationContext(),"Error purchasing. Authenticity verification failed.", Toast.LENGTH_LONG).show();
                return;
            }
			if (result.isSuccess()) {
				UpdateCreditsTaskLoaderCallback callback = new UpdateCreditsTaskLoaderCallback(purchase);
				LoaderManager loaderManager = getSupportLoaderManager();
				if (loaderManager != null){
					loaderManager.restartLoader(R.id.google_purchase_finish_task_loader_id, null, callback);
				}
        	}
		}
	};
	
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        @Override
		public void onConsumeFinished(Purchase purchase, IabResult result) {
        	if (mHelper == null) return;
        	saveData();
			updateUI();
        }
	};
	 
	
	/**
	 * UpdateCreditsTaskLoader
	 * @author Tony Huynh
	 *
	 */
	private static class UpdateCreditsTaskLoader extends AbstractCacheAsyncTaskLoader<Integer>{
		public static final int STATUS_SUCCESS = 1;
		public static final int STATUS_FAIL = 0;
		public static final int STATUS_HACK = 2;
		
		private Account account;
		private String paymentOption;
		private String purchaseData;
		private String purchaseSignature;
		
		public UpdateCreditsTaskLoader(Context context, Account account, String paymentOption, String purchaseData, String purchaseSignature) {
			super(context);
			this.account = account;
			this.paymentOption = paymentOption;
			this.purchaseData = purchaseData;
			this.purchaseSignature = purchaseSignature;
		}
		@Override
		public Integer loadInBackground() {
			GenerateKey gen = new GenerateKey();
//			DesSecurity des = new DesSecurity();
    		String randomKey = gen.generateKey();
    		
    		List<NameValuePair> paramsSearch = new ArrayList<NameValuePair>();
 			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_SECURITY_KEY, randomKey));
 			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_USER_ID, account.account));
 			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_PAYMENT_TYPE, String.valueOf(ChoosePaymentMethodDialog.PAYMENT_METHOD_GOOGLE)));
 			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_PRODUCT_TYPE, paymentOption));
 			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_SERIAL_NO, ""));
 			paramsSearch.add(new BasicNameValuePair("INAPP_PURCHASE_DATA", purchaseData));
 			paramsSearch.add(new BasicNameValuePair("INAPP_DATA_SIGNATURE", purchaseSignature));
 			
 			JSONParser jsonParserGooglePay = new JSONParser();
 			JSONObject jsonGooglePay = jsonParserGooglePay.makeHttpRequest(AESHelper.URLDescript(Constants.url_validate_payment_google_check), "POST", paramsSearch);
 			if (jsonGooglePay != null){
 				try {
					int success = jsonGooglePay.getInt(Constants.PARAM_SUCCESS_002);
					if (success == 1){
						return STATUS_SUCCESS;
					}
					else if (success == 2){
						return STATUS_HACK;
					}
					else{
						return STATUS_FAIL;
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return STATUS_FAIL;
				}
 			}
    		return STATUS_FAIL;
		}
	}
	
	/**
	 * UpdateCreditsTaskLoaderCallback
	 * @author Tony Huynh
	 *
	 */
	private class UpdateCreditsTaskLoaderCallback implements LoaderCallbacks<Integer>{
		
		private Purchase purchase;
		
		public UpdateCreditsTaskLoaderCallback(Purchase purchase){
			super();
			this.purchase = purchase;
		}
		
		@Override
		public Loader<Integer> onCreateLoader(int id, Bundle args) {
			showProgress();
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("orderId", purchase.getOrderId());
				jsonObject.put("packageName", purchase.getPackageName());
				jsonObject.put("productId", purchase.getSku());
				jsonObject.put("purchaseTime", purchase.getPurchaseTime());
				jsonObject.put("purchaseState", purchase.getPurchaseState());
				jsonObject.put("developerPayload", purchase.getDeveloperPayload());
				jsonObject.put("purchaseToken", purchase.getToken());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final UpdateCreditsTaskLoader loader = new UpdateCreditsTaskLoader(getApplicationContext(), mAccount, purchase.getSku(), jsonObject.toString(), purchase.getSignature());
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Integer> loader, Integer status) {
			dismissProgress();
			if (status == UpdateCreditsTaskLoader.STATUS_FAIL){
				//handler failure, save to database and update later
				return;
			}
			if (status == UpdateCreditsTaskLoader.STATUS_SUCCESS){
				int plusCredits = 0;
				if (Constants.PAYMENT_OPTION[0].equals(purchase.getSku())){
					plusCredits = 99;
				}
				else if (Constants.PAYMENT_OPTION[1].equals(purchase.getSku())){
					plusCredits = 199;
				}
				else{
					plusCredits = 499;
				}
				mAccount.credits = mAccount.credits + plusCredits;
			}
			else if (status == UpdateCreditsTaskLoader.STATUS_HACK){
				Toast.makeText(getApplicationContext(), "You should not hack this app, your purchase is not valid",  Toast.LENGTH_LONG).show();
			}
			mHelper.consumeAsync(purchase, mConsumeFinishedListener);
		}

		@Override
		public void onLoaderReset(Loader<Integer> loader) {
			
		}
		
	}
	
	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** Payment end **************************************/

	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** twitter start ************************************/
	
	private void askTwitterOAuth() {
		final CheckTwitterOauthDialogFragment dialog = new CheckTwitterOauthDialogFragment();
		dialog.setCancelable(false);
		dialog.show(getSupportFragmentManager(), FRAGMENT_TAG_DIALOG_CHECK_TWITTER_OAUTH);
	}
	
	public void twitterFollow() {
		if (!twitterThreadFinish) return;
		twitterThreadFinish = false;
		Thread postThread = new Thread(new Runnable() {
			@Override
			public void run() {
				final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
				configurationBuilder.setDebugEnabled(true);
				configurationBuilder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY);
				configurationBuilder.setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET);
				final TwitterDataController twitterDataController = TwitterDataController.getInstance(getApplicationContext());
				final String token = twitterDataController.getToken();
				final String tokenSecret = twitterDataController.getTokenSecret();
				configurationBuilder.setOAuthAccessToken(token);
				configurationBuilder.setOAuthAccessTokenSecret(tokenSecret);
				final Twitter mTwitter = new TwitterFactory(configurationBuilder.build()).getInstance();
				try {
					mTwitter.createFriendship("eProToeic", true);
				} catch (final TwitterException e) {
					e.printStackTrace();
				}
				twitterThreadFinish = true;
			}
		});
		postThread.start();
		Toast.makeText(this, R.string.account_activity_twitter_follow_success_message, TOAST_TIME).show();
	}
	
	public void twitterPostMessage(final String message){
		if (!twitterThreadFinish) return;
		twitterThreadFinish = false;
		Thread postThread = new Thread(new Runnable() {
			@Override
			public void run() {
				final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
				configurationBuilder.setDebugEnabled(true);
				configurationBuilder.setOAuthConsumerKey(TwitterConstants.CONSUMER_KEY);
				configurationBuilder.setOAuthConsumerSecret(TwitterConstants.CONSUMER_SECRET);
				final TwitterDataController twitterDataController = TwitterDataController.getInstance(getApplicationContext());
				final String token = twitterDataController.getToken();
				final String tokenSecret = twitterDataController.getTokenSecret();
				configurationBuilder.setOAuthAccessToken(token);
				configurationBuilder.setOAuthAccessTokenSecret(tokenSecret);
				final Twitter mTwitter = new TwitterFactory(configurationBuilder.build()).getInstance();
				try {
					mTwitter.updateStatus(message);
				} catch (final TwitterException e) {
					e.printStackTrace();
				}
				twitterThreadFinish = true;
			}
		});
		postThread.start();
		Toast.makeText(this, R.string.account_activity_twitter_post_message_success_message, TOAST_TIME).show();
	}
		
	public void showTwitterShareConfirmDialog(){
		final String message = getResources().getString(R.string.epro_app_share_message);
		final String appPackageName = getPackageName();
		String appLink = "http://play.google.com/store/apps/details?id=" + appPackageName;
		final String shareMessage = message + "\n" + appLink;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_twitter_post_message_dialog_tittle);
		builder.setMessage(shareMessage);
		builder.setNegativeButton(R.string.account_activity_twitter_post_message_dialog_cancel, null);
		builder.setPositiveButton(R.string.account_activity_twitter_post_message_dialog_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				twitterPostMessage(shareMessage);
				int nClick = -1;
				nClick = MyPreference.increaseCreditByTwitterShare(getApplicationContext());
				if (nClick > 0){
					mAccount.numberOfClick = nClick;
					mCreditTv.setText(String.valueOf(mAccount.credits + nClick*CREDIT_PER_CLICK) + " " + getResources().getString(R.string.account_activity_credit));
				}		
			}
		});
		builder.setNeutralButton(R.string.account_activity_twitter_post_message_dialog_logout_twitter, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TwitterDataController twitterDataControler = TwitterDataController.getInstance(getApplicationContext());
				twitterDataControler.clearAccountInfo();
				twitterIsOauth = twitterDataControler.isAccountOAuthed();
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	
	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** twitter end **************************************/
	
	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** facebook start ***********************************/

	@SuppressWarnings("unused")
	private void publishFeedDialog() {
	    Bundle params = new Bundle();
	    params.putString("name", "Facebook SDK for Android");
	    params.putString("caption", "Build great social apps and get more installs.");
	    params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	    params.putString("link", "https://developers.facebook.com/android");
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

	    WebDialog feedDialog = (
	        new WebDialog.FeedDialogBuilder(this, Session.getActiveSession(), params))
	        	.setOnCompleteListener(new WebDialog.OnCompleteListener() {
					@Override
					public void onComplete(Bundle values, FacebookException error) {
						// TODO Auto-generated method stub
						if (error == null) {
		                    // When the story is posted, echo the success
		                    // and the post Id.
		                    final String postId = values.getString("post_id");
		                    if (postId != null) {
		                        Toast.makeText(getApplicationContext(), "Posted story, id: "+postId, Toast.LENGTH_SHORT).show();
		                    } else {
		                        // User clicked the Cancel button
		                        Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
		                    }
		                } else if (error instanceof FacebookOperationCanceledException) {
		                    // User clicked the "x" button
		                    Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
		                } else {
		                    // Generic, ex: network error
		                    Toast.makeText(getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
		                }
					}
	        })
	        .build();
	    feedDialog.show();
	}
	
	/**************************************** facebook end *************************************/
	/*******************************************************************************************/
	/*******************************************************************************************/

	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** Other ********************************************/
	
	public void showCannotEarnFreeCreditBecauseNoAccount(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_no_account_dialog_tittle);
		builder.setMessage(R.string.account_activity_no_account_dialog_message_earn_free_credit);
		builder.setNegativeButton(R.string.account_activity_no_account_dialog_cancel, null);
		builder.setPositiveButton(R.string.account_activity_no_account_dialog_login, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (!Utils.isNetworkAvailable(getApplicationContext())){
					showNetworkErrorDialog();
				}
				else{
					showLoginDialog();
				}
			}
		});
		builder.create().show();
	}
	
	public void showAdOverClickDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_ad_over_click_tittle);
		builder.setMessage(R.string.account_activity_ad_over_click_message);
		builder.create().show();
	}
	
	public void showTwitterOverClickDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_twitter_post_over_click_tittle);
		builder.setMessage(R.string.account_activity_twitter_post_over_click_message);
		builder.create().show();
	}
	
	public void showFacebookOverClickDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_facebook_post_over_click_tittle);
		builder.setMessage(R.string.account_activity_facebook_post_over_click_message);
		builder.create().show();
	}

	/*******************************************************************************************/
	/*******************************************************************************************/
	/**************************************** Phone card payment *******************************/
	
	private void showPhonecardPaymentDialog(){
		PhonecardPaymentDialog dialog = new PhonecardPaymentDialog();
		FragmentManager fm = getSupportFragmentManager();
		dialog.show(fm, PHONECARD_DIALOG_FRAGMENT_TAG);
	}
	
	@Override
	public void startPaymentTransaction(String cardType, String serialNumber,
			String pinNumber) {
		PhonecardTransactionAsyncTask transactionAsyncTask = new PhonecardTransactionAsyncTask(this);
		String[] params = new String[4];
		params[0] = mAccount.account;
		params[1] = cardType;
		params[2] = serialNumber;
		params[3] = pinNumber;
		transactionAsyncTask.execute(params);
	}

	@Override
	public void alertByLackOfInformation(String cardType, String serialNumber,
			String pinNumber) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.account_activity_phonecard_lack_info_alert_dialog_tittle);
		builder.setMessage(R.string.account_activity_phonecard_lack_info_alert_dialog_message);
		builder.create().show();
	}
	
	
	private class PhonecardTransactionAsyncTask extends AsyncTask<String, Void, JSONObject>
	{
		WeakReference<Context> mContext;
		
		public PhonecardTransactionAsyncTask(Context context){
			mContext = new WeakReference<Context>(context);
		}
		
		@Override
		protected void onPreExecute ()
		{
			showProgress();
		}

		@Override
		protected JSONObject doInBackground(String... params) {
			String accountName = params[0];
			String cardTypeCode = params[1];
			String serial = params[2];
			String pin = params[3];
			
			JSONParser jsonParser = new JSONParser();
 			List<NameValuePair> phoneCardParam = new ArrayList<NameValuePair>();
 			phoneCardParam.add(new BasicNameValuePair(Constants.USER_HOLDER, accountName));
 			phoneCardParam.add(new BasicNameValuePair(Constants.CARDNO, serial));
 			phoneCardParam.add(new BasicNameValuePair(Constants.SERI, pin));
 			phoneCardParam.add(new BasicNameValuePair(Constants.CARDTYPE, cardTypeCode));
 			JSONObject jsonResult = jsonParser.makeHttpRequest(AESHelper.URLDescript(Constants.phoneCard_process), "POST", phoneCardParam);
			return jsonResult;
		}
		
		@Override
        protected void onPostExecute(JSONObject json){
			dismissProgress();
			try {
				if (json != null) {
	         	    // successfully response from server
	             	int successSearch = json.getInt(Constants.PHONECARD_SUCCESS);
	 				String msg = json.getString(Constants.PHONECARD_MESSAGE);	
	 				if (successSearch == 1) {
						int credits = Integer.valueOf(json.getString(Constants.CURRENT_CREDIT));
						mAccount.credits = credits;
						saveData();
						updateUI();
						AlertDialog.Builder builder = new AlertDialog.Builder(mContext.get());
						builder.setTitle(R.string.account_activity_phonecard_transaction_success_alert_dialog_tittle);
						builder.setMessage(msg);
						builder.create().show();
	 				} 
	 				else { //Failure
	 					AlertDialog.Builder builder = new AlertDialog.Builder(mContext.get());
						builder.setTitle(R.string.account_activity_phonecard_transaction_failure_alert_dialog_tittle);
						builder.setMessage(msg);
						builder.create().show();
	 				}
	         	}
	         	else {
	         		return;
	         	}
			}
			catch (JSONException e) {
                e.printStackTrace();
            }
		}
	}
	
}
