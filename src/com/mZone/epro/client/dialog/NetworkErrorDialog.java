package com.mZone.epro.client.dialog;

import com.mZone.epro.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class NetworkErrorDialog extends DialogFragment {

	private Context mContext;
	public NetworkErrorDialog(Context c) {
		// TODO Auto-generated constructor stub
		mContext = c;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.network_error_dialog_tittle);
		builder.setMessage(R.string.network_error_dialog_message)
			.setPositiveButton(R.string.network_error_dialog_position_btn, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					mContext.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
	            }
			})
	        .setNegativeButton(R.string.network_error_dialog_negative_btn, null);
	        // Create the AlertDialog object and return it
	    return builder.create();
	}
}
