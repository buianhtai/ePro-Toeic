package com.mZone.epro.common.view;

import com.mZone.epro.client.utility.AppLog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

public class AlertDialogFragment extends DialogFragment {
	/** ログ用タグ */
	private static final String LOG_TAG = "AlertDialogFragment";

	/** Fragment Args Key DIALOG_ID */
	public static final String FRAGMENT_ARGS_DIALOG_ID = "fragment_args_dialog_id";
	/** Fragment Args Key Title */
	public static final String FRAGMENT_ARGS_TITLE = "fragment_args_title";
	/** Fragment Args Key Message */
	public static final String FRAGMENT_ARGS_MESSAGE = "fragment_args_message";
	/** Fragment Args Key Message */
	public static final String FRAGMENT_ARGS_POSITIVE_BUTTON = "fragment_args_positive_button";
	/** Fragment Args Key Message */
	public static final String FRAGMENT_ARGS_NEGATIVE_BUTTON = "fragment_args_negative_button";
	/** Fragment Args Key Message */
	public static final String FRAGMENT_ARGS_NEUTRAL_BUTTON = "fragment_args_neutral_button";
	/** Fragment Args Key Cancelable */
	public static final String FRAGMENT_ARGS_CANCELABLE = "fragment_args_cancelable";

	/** ダイアログID */
	private int mDialogId;
	/** ダイアログタイトル */
	private String mTitle;
	/** ダイアログメッセージ */
	private String mMessage;
	/** ポジティブボタン */
	private String mPositiveButton;
	/** ネガティブボタン */
	private String mNegativeButton;
	/** ニュートラルボタン */
	private String mNeutralButton;
	/** キャンセル可能フラグ */
	private boolean mCancelable;
	/** コールバック */
	private Callbacks mCallbacks;

	/**
	 *
	 * @param targetFragment
	 * @param dialogId
	 * @param title
	 * @param message
	 * @param positiveButton
	 * @param negativeButton
	 * @param neutralButton
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment newInstance(final Fragment targetFragment, final int dialogId, final String title, final String message, final String positiveButton, final String negativeButton, final String neutralButton, final boolean cancelable) {
		AppLog.i(LOG_TAG, "[IN]newInstance()  targetFragment=%s, dialogId=%d, title=%s, message=%s, positiveButton=%s, negativeButton=%s, neutralButton=%s, cancelable=%s", targetFragment, dialogId, title, message, positiveButton, negativeButton, neutralButton, cancelable);

		// Fragmentを生成
		final AlertDialogFragment f = new AlertDialogFragment();

		// ターゲットフラグメントを設定
		if (targetFragment != null) {
			f.setTargetFragment(targetFragment, dialogId);
		}

		// 引数を設定
		final Bundle args = new Bundle();
		args.putInt(FRAGMENT_ARGS_DIALOG_ID, dialogId);
		args.putString(FRAGMENT_ARGS_TITLE, title);
		args.putString(FRAGMENT_ARGS_MESSAGE, message);
		args.putString(FRAGMENT_ARGS_POSITIVE_BUTTON, positiveButton);
		args.putString(FRAGMENT_ARGS_NEGATIVE_BUTTON, negativeButton);
		args.putString(FRAGMENT_ARGS_NEUTRAL_BUTTON, neutralButton);
		args.putBoolean(FRAGMENT_ARGS_CANCELABLE, cancelable);
		f.setArguments(args);

		AppLog.i(LOG_TAG, "[OUT]newInstance() f=%s, args=%s", f, args);
		return f;
	}

	/**
	 *
	 * @param targetFragment
	 * @param dialogId
	 * @param title
	 * @param message
	 * @param positiveButton
	 * @param negativeButton
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment newInstance(final Fragment targetFragment, final int dialogId, final String title, final String message, final String positiveButton, final String negativeButton, final boolean cancelable) {
		AppLog.i(LOG_TAG, "[IN]newInstance()  targetFragment=%s, dialogId=%d, title=%s, message=%s, positiveButton=%s, negativeButton=%s, cancelable=%s", targetFragment, dialogId, title, message, positiveButton, negativeButton, cancelable);

		final AlertDialogFragment f = AlertDialogFragment.newInstance(targetFragment, dialogId, title, message, positiveButton, negativeButton, null, cancelable);

		AppLog.i(LOG_TAG, "[OUT]newInstance() f=%s", f);
		return f;
	}

	/**
	 *
	 * @param targetFragment
	 * @param dialogId
	 * @param title
	 * @param message
	 * @param positiveButton
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment newInstance(final Fragment targetFragment, final int dialogId, final String title, final String message, final String positiveButton, final boolean cancelable) {
		AppLog.i(LOG_TAG, "[IN]newInstance() targetFragment=%s, dialogId=%d, title=%s, message=%s, positiveButton=%s, cancelable=%s", targetFragment, dialogId, title, message, positiveButton, cancelable);

		final AlertDialogFragment f = AlertDialogFragment.newInstance(targetFragment, dialogId, title, message, positiveButton, null, null, cancelable);

		AppLog.i(LOG_TAG, "[OUT]newInstance() f=%s", f);
		return f;
	}

	/**
	 *
	 * @param dialogId
	 * @param title
	 * @param message
	 * @param positiveButton
	 * @param negativeButton
	 * @param neutralButton
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment newInstance(final int dialogId, final String title, final String message, final String positiveButton, final String negativeButton, final String neutralButton, final boolean cancelable) {
		AppLog.i(LOG_TAG, "[IN]newInstance()  dialogId=%d, title=%s, message=%s, positiveButton=%s, negativeButton=%s, neutralButton=%s, cancelable=%s", dialogId, title, message, positiveButton, negativeButton, neutralButton, cancelable);

		final AlertDialogFragment f = AlertDialogFragment.newInstance(null, dialogId, title, message, positiveButton, negativeButton, null, cancelable);

		AppLog.i(LOG_TAG, "[OUT]newInstance() f=%s", f);
		return f;
	}

	/**
	 *
	 * @param dialogId
	 * @param title
	 * @param message
	 * @param positiveButton
	 * @param negativeButton
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment newInstance(final int dialogId, final String title, final String message, final String positiveButton, final String negativeButton, final boolean cancelable) {
		AppLog.i(LOG_TAG, "[IN]newInstance()  dialogId=%d, title=%s, message=%s, positiveButton=%s, negativeButton=%s, cancelable=%s", dialogId, title, message, positiveButton, negativeButton, cancelable);

		final AlertDialogFragment f = AlertDialogFragment.newInstance(null, dialogId, title, message, positiveButton, negativeButton, null, cancelable);

		AppLog.i(LOG_TAG, "[OUT]newInstance() f=%s", f);
		return f;
	}

	/**
	 *
	 * @param dialogId
	 * @param title
	 * @param message
	 * @param positiveButton
	 * @param cancelable
	 * @return
	 */
	public static AlertDialogFragment newInstance(final int dialogId, final String title, final String message, final String positiveButton, final boolean cancelable) {
		AppLog.i(LOG_TAG, "[IN]newInstance()  dialogId=%d, title=%s, message=%s, positiveButton=%s, cancelable=%s", dialogId, title, message, positiveButton, cancelable);

		final AlertDialogFragment f = AlertDialogFragment.newInstance(null, dialogId, title, message, positiveButton, null, null, cancelable);

		AppLog.i(LOG_TAG, "[OUT]newInstance() f=%s", f);
		return f;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		AppLog.i(LOG_TAG, "[IN]onCreateDialog() savedInstanceState=%s", savedInstanceState);

		// コールバックを取得
		// TargetFragment→Activityの順でチェック
		if (getTargetFragment() instanceof Callbacks) {
			mCallbacks = (Callbacks) getTargetFragment();
		} else if (getActivity() instanceof Callbacks) {
			mCallbacks = (Callbacks) getActivity();
		} else {
			mCallbacks = null;
		}

		// 引数を取得
		mDialogId = getArguments().getInt(FRAGMENT_ARGS_DIALOG_ID);
		mTitle = getArguments().getString(FRAGMENT_ARGS_TITLE);
		mMessage = getArguments().getString(FRAGMENT_ARGS_MESSAGE);
		mPositiveButton = getArguments().getString(FRAGMENT_ARGS_POSITIVE_BUTTON);
		mNegativeButton = getArguments().getString(FRAGMENT_ARGS_NEGATIVE_BUTTON);
		mNeutralButton = getArguments().getString(FRAGMENT_ARGS_NEUTRAL_BUTTON);
		mCancelable = getArguments().getBoolean(FRAGMENT_ARGS_CANCELABLE);

		// AlertDialogを作成
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// タイトルを設定
		if (!TextUtils.isEmpty(mTitle)) {
			builder.setTitle(mTitle);
		}

		// メッセージを設定
		if (!TextUtils.isEmpty(mMessage)) {
			builder.setMessage(mMessage);
		}

		// リスナー
		final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				AppLog.i(LOG_TAG, "[IN]onClick() dialog=%s, which=%d", dialog, which);
				if (mCallbacks != null) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							mCallbacks.onPositiveButtonClicked(getTag(), mDialogId);
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							mCallbacks.onNegativeButtonClicked(getTag(), mDialogId);
							break;
						case DialogInterface.BUTTON_NEUTRAL:
							mCallbacks.onNeutralButtonClicked(getTag(), mDialogId);
							break;
						default:
							break;
					}
				}
				AppLog.i(LOG_TAG, "[OUT]onClick()");
			}
		};
		if (!TextUtils.isEmpty(mPositiveButton)) {
			builder.setPositiveButton(mPositiveButton, listener);
		}
		if (!TextUtils.isEmpty(mNegativeButton)) {
			builder.setNegativeButton(mNegativeButton, listener);
		}
		if (!TextUtils.isEmpty(mNeutralButton)) {
			builder.setNeutralButton(mNeutralButton, listener);
		}
		setCancelable(mCancelable);

		AppLog.i(LOG_TAG, "[OUT]onCreateDialog() builder=%s", builder);
		return builder.create();
	}

	@Override
	public void onCancel(final DialogInterface dialog) {
		AppLog.i(LOG_TAG, "[IN]onCancel() dialog=%s", dialog);

		if (mCallbacks != null) {
			mCallbacks.onDialogCanceled(getTag(), mDialogId);
		}

		AppLog.i(LOG_TAG, "[OUT]onCancel()");
	}

	@Override
	public void onDismiss(final DialogInterface dialog) {
		AppLog.i(LOG_TAG, "[IN]onDismiss() dialog=%s", dialog);

		if (mCallbacks != null) {
			mCallbacks.onDialogDismissed(getTag(), mDialogId);
		}

		AppLog.i(LOG_TAG, "[OUT]onDismiss()");
	}

	public interface Callbacks {
		/**  */
		public void onPositiveButtonClicked(String tag, int dialogId);

		/**  */
		public void onNegativeButtonClicked(String tag, int dialogId);

		/**  */
		public void onNeutralButtonClicked(String tag, int dialogId);

		/**  */
		public void onDialogCanceled(String tag, int dialogId);

		/**  */
		public void onDialogDismissed(String tag, int dialogId);
	}

}
