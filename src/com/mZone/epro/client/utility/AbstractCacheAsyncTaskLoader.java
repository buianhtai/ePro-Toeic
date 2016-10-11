package com.mZone.epro.client.utility;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

/**
 *
 * @param <D>
 */
public abstract class AbstractCacheAsyncTaskLoader<D> extends AsyncTaskLoader<D> {

	/** data cache */
	private D mResult = null;

	/**
	 * constructor
	 *
	 * @param context
	 */
	public AbstractCacheAsyncTaskLoader(final Context context) {
		super(context);
	}

	@Override
	public void deliverResult(final D result) {
		// release resource if reset
		if (isReset()) {
			if (result != null) {
				onReleaseResources(result);
			}
		}

		if (isStarted()) {
			super.deliverResult(result);
		}

		final D oldResult = mResult;
		final D newResult = result;
		final boolean isModifiedResult = (oldResult != newResult);

		if (isModifiedResult && (oldResult != null)) {
			onReleaseResources(oldResult);
		}

		mResult = newResult;
	}

	@Override
	protected void onStartLoading() {

		if (mResult != null) {
			deliverResult(mResult);
		}

		if (takeContentChanged() || (mResult == null)) {
			forceLoad();
		}
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	public void onCanceled(final D result) {		super.onCanceled(result);
		onReleaseResources(result);
	}

	@Override
	protected void onReset() {
		super.onReset();

		onStopLoading();

		if (mResult != null) {
			onReleaseResources(mResult);
			mResult = null;
		}
	}

	/**
	 *
	 * @return
	 */
	protected D getResult() {
		return mResult;
	}

	/**
	 * data release。
	 *
	 * @param result
	 */
	protected void onReleaseResources(final D result) {
	}
}
