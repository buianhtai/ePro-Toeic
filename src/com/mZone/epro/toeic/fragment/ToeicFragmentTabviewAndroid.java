package com.mZone.epro.toeic.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.mZone.epro.R;
import com.mZone.epro.common.view.SlidingTabLayout;
import com.mZone.epro.toeic.data.ToeicDataController;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ToeicFragmentTabviewAndroid extends Fragment {

	static final int PAGER_PAGE_MARGIN = 20;
	
	private ToeicDataController dataController = ToeicDataController.getInstance();

	private int partIndex;
	private int tabIndicatorPosition = -1;
	
	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;
	private List<TabPagerItem> mTabs = new ArrayList<TabPagerItem>();
	
	public ToeicFragmentTabviewAndroid(int partIndex) {
		this.partIndex = partIndex;
	}
	
	public ToeicFragmentTabviewAndroid(int partIndex, int tabIndicatorPosition) {
		this.partIndex = partIndex;
		this.tabIndicatorPosition = tabIndicatorPosition;
	}
	
	static class TabPagerItem {
        private final String mTitle;
        private final int mIndicatorColor;
        private final int mDividerColor;
        private Bundle args;

        public TabPagerItem(String title, int indicatorColor, int dividerColor, int partIndex, int groupQuestionIndex) {
            mTitle = title;
            mIndicatorColor = indicatorColor;
            mDividerColor = dividerColor;
            args = new Bundle();
        	args.putInt(ToeicFragmentTabviewScrollableFragment.PART_INDEX_ARGUMENT, partIndex);
        	args.putInt(ToeicFragmentTabviewScrollableFragment.GROUP_QUESTION_INDEX_ARGUMENT, groupQuestionIndex);
        }

        Fragment createFragment() {
        	ToeicFragmentTabviewScrollableFragment fragment = new ToeicFragmentTabviewScrollableFragment();
		    fragment.setArguments(args);
		    return fragment;
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
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i = 0; i < dataController.getReadingGroupQuestionArraySize(partIndex); i++){
        	String tabString = dataController.getReadingGroupQuestionTitle(partIndex, i);
        	mTabs.add(new TabPagerItem(tabString, getResources().getColor(R.color.mint), Color.GRAY, partIndex, i));
        }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.toeic_fragment_tabview_android, container, false);
    }
	
	@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new TabsAdapter(getChildFragmentManager()));
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
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
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mViewPager.setPageMargin(PAGER_PAGE_MARGIN);
		if (tabIndicatorPosition != -1){
			mViewPager.setCurrentItem(tabIndicatorPosition);
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
	
	public class DepthPageTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.75f;

	    @Override
		public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 0) { // [-1,0]
	            // Use the default slide transition when moving to the left page
	            view.setAlpha(1);
	            view.setTranslationX(0);
	            view.setScaleX(1);
	            view.setScaleY(1);

	        } else if (position <= 1) { // (0,1]
	            // Fade the page out.
	            view.setAlpha(1 - position);

	            // Counteract the default slide transition
	            view.setTranslationX(pageWidth * -position);

	            // Scale the page down (between MIN_SCALE and 1)
	            float scaleFactor = MIN_SCALE
	                    + (1 - MIN_SCALE) * (1 - Math.abs(position));
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}
	
	public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
	    private static final float MIN_SCALE = 0.85f;
	    private static final float MIN_ALPHA = 0.5f;

	    @Override
		public void transformPage(View view, float position) {
	        int pageWidth = view.getWidth();
	        int pageHeight = view.getHeight();

	        if (position < -1) { // [-Infinity,-1)
	            // This page is way off-screen to the left.
	            view.setAlpha(0);

	        } else if (position <= 1) { // [-1,1]
	            // Modify the default slide transition to shrink the page as well
	            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
	            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
	            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
	            if (position < 0) {
	                view.setTranslationX(horzMargin - vertMargin / 2);
	            } else {
	                view.setTranslationX(-horzMargin + vertMargin / 2);
	            }

	            // Scale the page down (between MIN_SCALE and 1)
	            view.setScaleX(scaleFactor);
	            view.setScaleY(scaleFactor);

	            // Fade the page relative to its size.
	            view.setAlpha(MIN_ALPHA +
	                    (scaleFactor - MIN_SCALE) /
	                    (1 - MIN_SCALE) * (1 - MIN_ALPHA));

	        } else { // (1,+Infinity]
	            // This page is way off-screen to the right.
	            view.setAlpha(0);
	        }
	    }
	}	
}
