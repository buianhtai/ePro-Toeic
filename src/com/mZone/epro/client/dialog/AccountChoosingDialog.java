package com.mZone.epro.client.dialog;

import java.util.ArrayList;
import java.util.regex.Pattern;

import com.mZone.epro.R;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;

public class AccountChoosingDialog extends DialogFragment {

	public interface AccountChoosingDialogListener{
		public void onAccountSelect(String account);
	}

	AccountChoosingDialogListener mListener;
	Context mContext;
	
	public AccountChoosingDialog(AccountChoosingDialogListener mListener){
		this.mListener = mListener;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mContext = activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final String[] accountList = getAccountList();
	    builder.setTitle(R.string.choose_account_dialog_tittle).
	    setItems(accountList, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   mListener.onAccountSelect(accountList[which]);
	           }
	    });
	    return builder.create();

	}
	
	private String[] getAccountList(){
		ArrayList<String> listAccounts = new ArrayList<String>();
		String possibleEmail = "";
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(mContext.getApplicationContext()).getAccounts();
		for (Account account : accounts) {
		    if (emailPattern.matcher(account.name).matches()) {
		    	possibleEmail = account.name;
		    	if (listAccounts.indexOf(possibleEmail) < 0) {
		    		listAccounts.add(possibleEmail);
		    		
		    	}
		    	
		    }
		}
		String[] result = listAccounts.toArray(new String[listAccounts.size()]);
		return result;
	}

}
