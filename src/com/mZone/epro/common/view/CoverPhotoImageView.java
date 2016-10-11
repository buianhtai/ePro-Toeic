package com.mZone.epro.common.view;

import java.io.FileNotFoundException;
import java.io.InputStream;

import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;
import com.mZone.epro.client.utility.AppLog;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 *
 */
public class CoverPhotoImageView extends ImageView implements LoaderCallbacks<Bitmap> {
	private static final String LOG_TAG = "CoverPhotoImageView";
	/**  */
	public int coverPhotoLoaderId = 0;
	/**  */
	public String coverPhotoUri = null;

	/**
	 *
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public CoverPhotoImageView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 *
	 * @param context
	 * @param attrs
	 */
	public CoverPhotoImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 *
	 * @param context
	 */
	public CoverPhotoImageView(final Context context) {
		super(context);
	}

	@Override
	public Loader<Bitmap> onCreateLoader(final int id, final Bundle args) {
		AppLog.in(LOG_TAG, "onCreateLoader() id=%d, args=%s", id, args);

		// 引数を取得
		String uri = null;
		if (args != null) {
			uri = args.getString(LocalCoverPhotoLoader.ARGS_KEY_COVER_PHOTO_URI, null);
		}
		AppLog.debug(LOG_TAG, "onCreateLoader() uri=%s", uri);

		final Context context = getContext();
		final LocalCoverPhotoLoader loader = new LocalCoverPhotoLoader(context, uri);

		AppLog.out(LOG_TAG, "onCreateLoader() loader=%s", loader);
		return loader;
	}

	@Override
	public void onLoadFinished(final Loader<Bitmap> loader, final Bitmap bitmap) {
		AppLog.in(LOG_TAG, "onLoadFinished() loader=%s, bitmap=%s", loader, bitmap);

		if (bitmap != null) {
			if (loader instanceof LocalCoverPhotoLoader) {
				final String viewUri = coverPhotoUri;
				final LocalCoverPhotoLoader photoLoader = (LocalCoverPhotoLoader) loader;
				final String loaderUri = photoLoader.contactsPhotoUri;
				AppLog.debug(LOG_TAG, "onLoadFinished() viewUri=%s, loaderUri=%s", viewUri, loaderUri);
				if (TextUtils.equals(viewUri, loaderUri)) {
					setImageBitmap(bitmap);
				}
			}
		}

		AppLog.out(LOG_TAG, "onLoadFinished()");
	}

	@Override
	public void onLoaderReset(final Loader<Bitmap> loader) {
		AppLog.in(LOG_TAG, "onLoaderReset() loader=%s", loader);
		AppLog.out(LOG_TAG, "onLoaderReset()");
	}

	/**
	 *
	 */
	public static class LocalCoverPhotoLoader extends AbstractCacheAsyncTaskLoader<Bitmap> {
		private static final String LOG_TAG = "ContactsPhotoLoader";

		/** */
		public static final String ARGS_KEY_COVER_PHOTO_URI = "loader_args_key_contacts_photo_uri";

		/**  */
		public String contactsPhotoUri = null;

		/**
		 *
		 * @param context
		 * @param contactsPhotoUri
		 */
		public LocalCoverPhotoLoader(final Context context, final String contactsPhotoUri) {
			super(context);
			this.contactsPhotoUri = contactsPhotoUri;
			AppLog.debug(LOG_TAG, "Constructor() this=%s", this);
		}

		@Override
		public Bitmap loadInBackground() {
			AppLog.in(LOG_TAG, "loadInBackground()");
			Bitmap bitmap = null;

			if (!TextUtils.isEmpty(contactsPhotoUri)) {
				final ContentResolver cr = getContext().getContentResolver();
				Uri myRowUri = Uri.parse(contactsPhotoUri);
				try {
				      InputStream inStream = cr.openInputStream(myRowUri);
				      bitmap = BitmapFactory.decodeStream(inStream);
				 }
				 catch (FileNotFoundException e) { 
				 }
			}

			AppLog.out(LOG_TAG, "loadInBackground() bitmap=%s", bitmap);
			return bitmap;
		}

		/**
		 *
		 * @param backgrounds
		 */
		@Override
		protected void onReleaseResources(final Bitmap bitmap) {
			AppLog.in(LOG_TAG, "onReleaseResources() bitmap=%s", bitmap);
			AppLog.out(LOG_TAG, "onReleaseResources()");
		}

		@Override
		public String toString() {
			return String.format("ContactsPhotoLoader [contactsPhotoUri=%s]", contactsPhotoUri);
		}
	}
}
