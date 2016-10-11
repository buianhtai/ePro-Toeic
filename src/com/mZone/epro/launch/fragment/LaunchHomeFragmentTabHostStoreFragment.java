package com.mZone.epro.launch.fragment;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.mZone.epro.EproBaseSectionListFragment;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;

public class LaunchHomeFragmentTabHostStoreFragment extends EproBaseSectionListFragment{

	public LaunchHomeFragmentTabHostStoreFragment() {
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}
	
	 public static class LoadingBookTaskLoader extends AbstractCacheAsyncTaskLoader<Integer>
	 {
		public static final int STATUS_SUCCESS = 1;
		public static final int STATUS_ERROR = 0;
		
		public static final int LOADING_TYPE_GENERAL = 0;
		public static final int LOADING_TYPE_DATABASE_ONLY = 1;
		public static final int LOADING_TYPE_SERVER_ONLY = 2;
				
		private Context mAppContext;
		private int mLoadingType;
		
		public LoadingBookTaskLoader(Context context, int loadingType) {
			super(context);
			mAppContext = context;
			mLoadingType = loadingType;
		}

		@Override
		public Integer loadInBackground() {
			return null;
		}
		 
	 }
}
