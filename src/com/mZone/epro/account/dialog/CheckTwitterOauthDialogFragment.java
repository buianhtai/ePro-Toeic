package com.mZone.epro.account.dialog;

import com.mZone.epro.R;
import com.mZone.epro.account.AccountActivity;
import com.mZone.epro.account.twitter.TwitterWebLoginActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CheckTwitterOauthDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.account_activity_check_twitter_oauth_dialog_title);
		builder.setMessage(R.string.account_activity_check_twitter_oauth_dialog_message);
		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {

				switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						final Intent intent = new Intent(getActivity(), TwitterWebLoginActivity.class);
						getActivity().startActivityForResult(intent, AccountActivity.START_ACTIVITY_REQUEST_CODE_LOGIN_TWITTER);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
					default:
						break;
				}

			}
		};
		builder.setPositiveButton(R.string.account_activity_check_twitter_oauth_dialog_positive_button, listener);
		builder.setNegativeButton(R.string.account_activity_check_twitter_oauth_dialog_negative_button, null);
		final Dialog dialog = builder.create();
		return dialog;
	}

}
