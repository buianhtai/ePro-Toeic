package com.mZone.epro.client.dialog;

import com.mZone.epro.R;
import com.mZone.epro.client.data.ClientDataController;
import com.mZone.epro.client.data.TransactionParameter;
import com.mZone.epro.client.utility.MyPreference;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DownloadDialog extends DialogFragment{

	private MyDialogInterface delegate;
	private TransactionParameter parameters;
	public DownloadDialog(Context mContext, MyDialogInterface delegate, TransactionParameter parameters) {
		// TODO Auto-generated constructor stub
		this.delegate = delegate;
		this.parameters = parameters;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(parameters.getBookItem().getBookName());
		if ((parameters.getAccountCredit() >= parameters.getBookItem().getBookPrice())
				|| !parameters.isPaymentRequired()) {
			if (!parameters.isPaymentRequired()) {
				builder.setMessage(R.string.download_dialog_backup_message);
			}
			else{
				builder.setMessage(R.string.download_dialog_message);
			}
			builder.setPositiveButton(R.string.download_dialog_confirm_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (parameters.isPaymentRequired()){
						int finalCredit = (int) (parameters.getAccountCredit() - parameters.getBookItem().getBookPrice());
						MyPreference.setAccountCredit(getActivity().getApplicationContext(), finalCredit);
					}
					Bitmap bitmap = ClientDataController.getInstance().getExistedBitmap(parameters.getBookItem().getBookID());
					parameters.setBitmap(bitmap);
					delegate.startDownloadAction(parameters);
				}
			});
		}
		else{
			builder.setMessage(R.string.download_dialog_not_enough_credit_message);
			builder.setPositiveButton(R.string.download_dialog_not_enough_credit_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					delegate.startAccountActivity(parameters);
				}
			});
		}
		return builder.create();

	}
}
