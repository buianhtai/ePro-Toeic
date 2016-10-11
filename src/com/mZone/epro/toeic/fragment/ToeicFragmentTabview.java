package com.mZone.epro.toeic.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import com.mZone.epro.R;
import com.mZone.epro.toeic.data.ToeicDataController;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class ToeicFragmentTabview extends Fragment{

	public static final String ARGS_PART_INDEX = "ToeicFragmentTabview.ARGS_PART_INDEX";
	
	private int partIndex;
	private ToeicDataController dataController = ToeicDataController.getInstance();
	
	//View variable
	private View rootView;
	private TabHost mTabHost;
	private ViewPager  mViewPager;
	private TabsAdapter mTabsAdapter;
	private HorizontalScrollView horizontalView;
	
	//other variable
	private int tabIndicatorPosition = -1;
	
	public ToeicFragmentTabview() {
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
		this.partIndex = getArguments().getInt(ARGS_PART_INDEX);
		rootView = inflater.inflate(R.layout.toeic_fragment_tabview, container, false);
		mTabHost = (TabHost)rootView.findViewById(android.R.id.tabhost);
		mTabHost.setup();
		horizontalView = (HorizontalScrollView)rootView.findViewById(R.id.horizontalTabView);
		mViewPager = (ViewPager)rootView.findViewById(R.id.viewPager);
		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		mViewPager.setOffscreenPageLimit(3);
		mTabsAdapter = new TabsAdapter(mTabHost, mViewPager, getChildFragmentManager(), horizontalView);	
		for (int i = 0; i < dataController.getReadingGroupQuestionArraySize(partIndex); i++){
        	Bundle args = new Bundle();
        	args.putInt(ToeicFragmentTabviewScrollableFragment.PART_INDEX_ARGUMENT, partIndex);
        	args.putInt(ToeicFragmentTabviewScrollableFragment.GROUP_QUESTION_INDEX_ARGUMENT, i);
        	String tabString = dataController.getReadingGroupQuestionTitle(partIndex, i);
        	View tabview = createTabView(mTabHost.getContext(), tabString);
        	mTabsAdapter.addTab(mTabHost.newTabSpec(tabString).setIndicator(tabview),
        			ToeicFragmentTabviewScrollableFragment.class, args);
        }
		
		rootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
			@Override
            public void onGlobalLayout() {
            	rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            	if (tabIndicatorPosition != -1){
               	 	mTabsAdapter.onPageSelected(tabIndicatorPosition);
               }
     
            }
        });
		return rootView;
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onPause (){
		super.onPause();
		
	}
	
	private View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.toeic_custom_tabview_tabwidget_layout, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
	
	//Child FragmentManager retained after detaching parent Fragment
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
	
	public class TabsAdapter extends FragmentPagerAdapter
		implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
//		private final Context mContext;
		private final TabHost mTabHost;
		private final ViewPager mViewPager;
		private HorizontalScrollView mHorizontalView;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
		
		public class TabInfo {
			private final String tag;
			private final Class<?> clss;
		    private final Bundle args;
		
		    TabInfo(String _tag, Class<?> _class, Bundle _args) {
		        tag = _tag;
		        clss = _class;
		        args = _args;
		    }
		}
		
		public class DummyTabFactory implements TabHost.TabContentFactory {
		    private final Context mContext;
		
		    public DummyTabFactory(Context context) {
		        mContext = context;
		    }
		
		    @Override
		    public View createTabContent(String tag) {
		        View v = new View(mContext);
		        v.setMinimumWidth(0);
		        v.setMinimumHeight(0);
		        return v;
		    }
		}
		
		public TabsAdapter(TabHost tabHost, ViewPager pager, FragmentManager fm, HorizontalScrollView horizontalView) {
		    super(fm);
		    mTabHost = tabHost;
		    mViewPager = pager;
		    mHorizontalView = horizontalView;
		    mTabHost.setOnTabChangedListener(this);
		    mViewPager.setAdapter(this);
		    mViewPager.setOnPageChangeListener(this);
		}
		public void addFragment(TabHost.TabSpec tabSpec, Fragment f, Bundle args)
		{
			tabSpec.setContent(new DummyTabFactory(getActivity()));
		}
		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
		    tabSpec.setContent(new DummyTabFactory(getActivity()));
		    String tag = tabSpec.getTag();
		
		    TabInfo info = new TabInfo(tag, clss, args);
		    mTabs.add(info);
		    mTabHost.addTab(tabSpec);
		    notifyDataSetChanged();
		}
		
		
		
		@Override
		public int getCount() {
		    return mTabs.size();
		}
		
		@Override
		public Fragment getItem(int position) {
		    TabInfo info = mTabs.get(position);
		    ToeicFragmentTabviewScrollableFragment fragment = new ToeicFragmentTabviewScrollableFragment();
		    fragment.setArguments(info.args);
		    return fragment;
		}
		
		@Override
		public void onTabChanged(String tabId) {
		    int position = mTabHost.getCurrentTab();
		    mViewPager.setCurrentItem(position);
		}
		@SuppressWarnings("deprecation")
		public void centerTabItem(int position) {
			mTabHost.setCurrentTab(position);
	        final TabWidget tabWidget = mTabHost.getTabWidget();
	        final int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
	        final int leftX = tabWidget.getChildAt(position).getLeft();
	        int newX = 0;
	
	        newX = leftX + (tabWidget.getChildAt(position).getWidth() / 2) - (screenWidth / 2);
	        if (newX < 0) {
	            newX = 0;
	        }
	        mHorizontalView.smoothScrollTo(newX, 0);
	    }
		
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}
		
		@Override
		public void onPageSelected(int position) {
		    TabWidget widget = mTabHost.getTabWidget();
		    int oldFocusability = widget.getDescendantFocusability();
		    widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		    centerTabItem(position);
		    widget.setDescendantFocusability(oldFocusability);
		    tabIndicatorPosition = position;
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
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
