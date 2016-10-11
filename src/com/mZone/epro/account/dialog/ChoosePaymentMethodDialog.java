package com.mZone.epro.account.dialog;

import com.mZone.epro.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ChoosePaymentMethodDialog extends DialogFragment {

	public static final int PAYMENT_METHOD_GOOGLE = 1;
	public static final int PAYMENT_METHOD_TELEPHONE_CARD = 0;
	
	private Context mContext;
	private ChoosePaymentMethodDialogDelegate delegate;
	private int paymentOption;
	
	public static interface ChoosePaymentMethodDialogDelegate{
		/**
		 * 
		 * @param method: PAYMENT_METHOD_GOOGLE or PAYMENT_METHOD_TELEPHONE_CARD
		 * @param paymentOption: 0.99 1.99 or 4.99
		 */
		public void startPayment(int method, int paymentOption);
	}

	/**
	 * 
	 * @param delegate
	 * @param paymentOption: 0.99 1.99 or 4.99
	 */
	public ChoosePaymentMethodDialog(ChoosePaymentMethodDialogDelegate delegate, int paymentOption) {
		this.delegate = delegate;
		this.paymentOption = paymentOption;
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
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.choose_payment_method_dialog_tittle);
		builder.setItems(R.array.choose_payment_method_dialog_item_array, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (delegate != null){
					delegate.startPayment(which, paymentOption);
				}
			}
		});
		return builder.create();
	}

}
