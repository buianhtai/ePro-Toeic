package com.mZone.epro.account.dialog;

import com.mZone.epro.R;
import com.mZone.epro.account.AccountActivity;
import com.mZone.epro.client.utility.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class PhonecardPaymentDialog extends DialogFragment {

	public static interface PhonecardPaymentDialogDelegate{
		public void startPaymentTransaction(String cardType, String serialNumber, String pinNumber);
		public void alertByLackOfInformation(String cardType, String serialNumber, String pinNumber);
	}

	private PhonecardPaymentDialogDelegate delegate;
	
	//view
	private Spinner spinCardType;
	private EditText editSerial;
	private EditText editPin;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			if (activity instanceof AccountActivity){
				delegate = (AccountActivity)activity;
			}
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
		}
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.account_activity_phonecard_dialog_tittle);
		builder.setCancelable(false);
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		LinearLayout dialogView = (LinearLayout) inflater.inflate(R.layout.account_phonecard_payment_dialog, null);
		spinCardType = (Spinner)dialogView.findViewById(R.id.spinCardType);
		editSerial = (EditText)dialogView.findViewById(R.id.editSerial); 
		editPin = (EditText)dialogView.findViewById(R.id.editPin); 
		builder.setView(dialogView);
		builder.setNegativeButton(R.string.account_activity_phonecard_dialog_cancel, null);
		builder.setPositiveButton(R.string.account_activity_phonecard_dialog_confirm, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int cardTypeIndex = spinCardType.getSelectedItemPosition();
				String cardType = Constants.CARDTYPE_CODE[cardTypeIndex];
				String serialNumber = editSerial.getText().toString();
				String pinNumber = editPin.getText().toString();
				if (TextUtils.isEmpty(serialNumber) || TextUtils.isEmpty(pinNumber)){
					delegate.alertByLackOfInformation(cardType, serialNumber, pinNumber);
				}
				else{
					delegate.startPaymentTransaction(cardType, serialNumber, pinNumber);
				}
			}
		});
		return builder.create();
	}
}
