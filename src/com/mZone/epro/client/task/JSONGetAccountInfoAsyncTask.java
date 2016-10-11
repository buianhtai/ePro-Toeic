package com.mZone.epro.client.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mZone.epro.R;
import com.mZone.epro.client.data.TransactionParameter;
import com.mZone.epro.client.dialog.MyDialogInterface;
import com.mZone.epro.client.utility.AESHelper;
import com.mZone.epro.client.utility.Constants;
import com.mZone.epro.client.utility.JSONParser;
import com.mZone.epro.client.utility.MyPreference;
import com.mZone.epro.client.utility.MyPreference.Account;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class JSONGetAccountInfoAsyncTask extends
		AsyncTask<String, String, JSONObject> {
	
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private TransactionParameter parameters;
	private MyDialogInterface delegate;
	
	public JSONGetAccountInfoAsyncTask(Context mContext, TransactionParameter parameters, MyDialogInterface delegate){
		this.mContext = mContext;
		this.parameters = parameters;
		this.delegate = delegate;
	}
	
	@Override
	protected void onPreExecute() {
    	showProgress();
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {
		//update coin
		List<NameValuePair> mParams = new ArrayList<NameValuePair>();		
		Account account = MyPreference.getAccount(mContext.getApplicationContext());
		mParams.add(new BasicNameValuePair(Constants.PARAM_USER_ID, account.account));
		mParams.add(new BasicNameValuePair(Constants.PARAM_NUMBER_OF_CLICK, String.valueOf(account.numberOfClick)));
		JSONParser jsonAccountInfo = new JSONParser();
		JSONObject jsonInfo = jsonAccountInfo.makeHttpRequest(AESHelper.URLDescript(Constants.url_user_infor), "GET", mParams);
		return jsonInfo;
	}
	
	 @Override
     protected void onPostExecute(JSONObject json) {
         try {
        	dismissProgress();
         	if (json != null) {
             	int successSearch = json.getInt(Constants.PARAM_SUCCESS_003);
 				if (successSearch == 1) {
     				// successfully search
     				JSONArray searchResultObj = json.getJSONArray(Constants.PARAM_USER);// JSON Array
					JSONObject arrElement = searchResultObj.getJSONObject(0);
					int credits = Integer.valueOf(arrElement.getString(Constants.PARAM_CREDIT));
					MyPreference.setAccountCredit(mContext.getApplicationContext(), credits);
			    	MyPreference.resetNumberOfClick(mContext.getApplicationContext());
					parameters.setAccountCredit(credits);
 				}
         	}
         	//call dialog layout 2
         	delegate.showDownloadDialog(parameters);

        } catch (JSONException e) {
            e.printStackTrace();
        }
     }
	
	private void showProgress( ) {
	  	mProgressDialog = new ProgressDialog(mContext);
	  	mProgressDialog.setMessage(mContext.getResources().getString(R.string.loading_account_info_dialog_message));
	  	mProgressDialog.setIndeterminate( true );
	  	mProgressDialog.show();
	}
	
	// dismiss Progress Dialog
	private void dismissProgress() {
	 	//dismiss progress
	 	if (mProgressDialog != null && mProgressDialog.isShowing() && mProgressDialog.getWindow() != null) {
	 		try {
	 			mProgressDialog.dismiss();
	 		} catch ( IllegalArgumentException ignore ) { ; }
	 	}
	 	mProgressDialog = null;
	 }

}
