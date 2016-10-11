package com.mZone.epro.launch.fragment;

import java.lang.reflect.Field;

import com.mZone.epro.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

public class LaunchAboutFragment extends Fragment implements View.OnClickListener{
	
	private static final String FRAGMENT_TAG_DIALOG_OPEN_SOURCE_LICENSE = "FRAGMENT_TAG_DIALOG_OPEN_SOURCE_LICENSE";
	private static final String USER_GUIDE_LINK = "https://www.youtube.com/watch?v=AKOKd02OuJc&feature=youtu.be";
	private static final String DEVELOPER_HOMEPAGE = "http://mzone.com.vn";

	public LaunchAboutFragment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onAttach(Activity activity){
		 super.onAttach(activity);		 
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.launch_about_fragment, container, false);
		Button developerHomepage = (Button) rootView.findViewById(R.id.launch_about_developer_homepage);
		developerHomepage.setOnClickListener(this);
		Button useGuide = (Button) rootView.findViewById(R.id.launch_about_user_guide);
		useGuide.setOnClickListener(this);
		Button ratingUs = (Button) rootView.findViewById(R.id.launch_about_rating_us);
		ratingUs.setOnClickListener(this);
		Button softwareLicense = (Button) rootView.findViewById(R.id.launch_about_about_opensource);
		softwareLicense.setOnClickListener(this);
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	private static final Field sChildFragmentManagerField;
	
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} 
		catch (NoSuchFieldException e) {
		}
		sChildFragmentManagerField = f;
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} 
			catch (Exception e) {
			}
		}
	}
	
	/**
	 *
	 */
	
	public static class OpenSourceLicenseDialogFragment extends DialogFragment {
		private static final String LOG_TAG = "OpenSourceLicenseDialogFragment";

		@Override
		public Dialog onCreateDialog(final Bundle savedInstanceState) {

			// ダイアログを作成
			final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.open_source_license_dialog_fragment_title);
			builder.setPositiveButton(R.string.open_source_license_dialog_fragment_positive_button, null);

			// WebViewの準備
			final WebView webView = new WebView(getActivity());
			builder.setView(webView);

			webView.loadUrl("file:///android_asset/OpenSourceLicense/OpenSourceLicense.html");

			// ダイアログを作成
			final Dialog dialog = builder.create();

			return dialog;
		}
	}

	@Override
	public void onClick(View v) {
		final FragmentManager fragmentManager = getChildFragmentManager();
		Fragment fragment = null;
		DialogFragment dialogFragment = null;

		int viewID = v.getId();
		switch (viewID) {
		case R.id.launch_about_developer_homepage:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(DEVELOPER_HOMEPAGE)));
			break;
		case R.id.launch_about_user_guide:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(USER_GUIDE_LINK)));
			break;
		case R.id.launch_about_rating_us:
			final String appPackageName = getActivity().getPackageName();
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
			}
			break;
		case R.id.launch_about_about_opensource:
			fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_DIALOG_OPEN_SOURCE_LICENSE);
			if (fragment == null) {
				dialogFragment = new OpenSourceLicenseDialogFragment();
				dialogFragment.show(fragmentManager, FRAGMENT_TAG_DIALOG_OPEN_SOURCE_LICENSE);
			}
			break;

		default:
			break;
		}
	}

}
