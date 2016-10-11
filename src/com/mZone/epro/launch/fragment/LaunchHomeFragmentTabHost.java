package com.mZone.epro.launch.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.mZone.epro.R;
import com.mZone.epro.common.view.SlidingTabLayout;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LaunchHomeFragmentTabHost extends Fragment {

	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;
	private List<TabPagerItem> mTabs = new ArrayList<TabPagerItem>();
	
	static final int PAGER_PAGE_MARGIN = 20;
	
	public LaunchHomeFragmentTabHost() {
	}
	
	@Override
	public void onAttach(Activity activity){
		 super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		mTabs.add(new TabPagerItem(getResources().getString(R.string.launch_home_fragment_tabhost_tabtittle_fulltest), getResources().getColor(R.color.launch_home_fragment_tabhost_tab_blue), Color.GRAY, 0));
		mTabs.add(new TabPagerItem(getResources().getString(R.string.launch_home_fragment_tabhost_tabtittle_parttest), getResources().getColor(R.color.launch_home_fragment_tabhost_tab_blue), Color.GRAY, 1));
		mTabs.add(new TabPagerItem(getResources().getString(R.string.launch_home_fragment_tabhost_tabtittle_download), getResources().getColor(R.color.launch_home_fragment_tabhost_tab_blue), Color.GRAY, 2));
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.launch_home_fragment_tabhost, container, false);
		return rootView;
	}
	
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
		mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.launch_home_fragment_tabhost_tabwidget, R.id.tabwidget_textview);
        mSlidingTabLayout.setCustomTabDelegate(new SlidingTabLayout.CustomTabInterface() {
			
			@Override
			public int getIconId(int position, int active) {
				// TODO Auto-generated method stub
				return mTabs.get(position).getIconId(active);
			}
		});
        mViewPager.setAdapter(new TabsAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mTabs.get(position).getIndicatorColor();
            }

            @Override
            public int getDividerColor(int position) {
                return mTabs.get(position).getDividerColor();
            }
        });
		mViewPager.setPageMargin(PAGER_PAGE_MARGIN);
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
	}
	@Override
	public void onPause (){
		super.onPause();
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
	
	/****************************************************************************************/
	/****************************************************************************************/
	/********************************** UI Support function *********************************/

	static class TabPagerItem {
        private final String mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;
        private final int mPosition;
        private Bundle args;

        public TabPagerItem(String title, int indicatorColor, int dividerColor, int position) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
            mPosition = position;
            args = new Bundle();
        }

        Fragment createFragment() {
        	switch (mPosition) {
        		case 0:
		        	LaunchHomeFragmentTabHostFullTestFragment fulltestFragment = new LaunchHomeFragmentTabHostFullTestFragment();
		        	fulltestFragment.setArguments(args);
				    return fulltestFragment;
        		case 1:
		        	LaunchHomeFragmentTabHostPartTestFragment parttestFragment = new LaunchHomeFragmentTabHostPartTestFragment();
		        	parttestFragment.setArguments(args);
				    return parttestFragment;
				default:
		        	LaunchHomeFragmentTabHostFullTestFragment fragment = new LaunchHomeFragmentTabHostFullTestFragment();
				    fragment.setArguments(args);
				    return fragment;
			}
        }

        CharSequence getTitle() {
            return mTitle;
        }

        int getIndicatorColor() {
            return mIndicatorColor;
        }

        int getDividerColor() {
            return mDividerColor;
        }
        
        int getIconId(int active){
        	if (active == 1){
        		switch (mPosition) {
        			case 0:
        				return R.drawable.launch_home_fragment_tabhost_tabwidget_fulltest_active;
        			case 1:
        				return R.drawable.launch_home_fragment_tabhost_tabwidget_parttest_active;
        			case 2:
        				return R.drawable.launch_home_fragment_tabhost_tabwidget_download_active;
        			default: 
    					return R.drawable.launch_home_fragment_tabhost_tabwidget_fulltest_active;
        		}
        	}
        	else{
        		switch (mPosition) {
    				case 0:
    					return R.drawable.launch_home_fragment_tabhost_tabwidget_fulltest_inactive;
        			case 1:
        				return R.drawable.launch_home_fragment_tabhost_tabwidget_parttest_inactive;
        			case 2:
        				return R.drawable.launch_home_fragment_tabhost_tabwidget_download_inactive;
    				default: 
    					return R.drawable.launch_home_fragment_tabhost_tabwidget_fulltest_inactive;
        		}

        	}

        }
    }
	
	class TabsAdapter extends FragmentPagerAdapter {

		TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mTabs.get(i).createFragment();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabs.get(position).getTitle();
        }
    }
	

}
