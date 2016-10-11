package com.mZone.epro.client.dialog;

import com.mZone.epro.client.data.TransactionParameter;

public interface MyDialogInterface {
	public void showDownloadDialog(TransactionParameter parameters);
//	public void showPaymentMethodDialog(TransactionParameter parameters);
//	public void showPhoneCardPaymentDialog(TransactionParameter parameters);
//	public void showGooglePaymentDialog(TransactionParameter parameters);
	public void startDownloadAction(TransactionParameter parameters);
	public void startAccountActivity(TransactionParameter parameters);
}
