package com.mZone.epro.toeic.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.mZone.epro.R;
import com.mZone.epro.toeic.activity.ToeicTestActivity;
import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customInterface.ToeicMediaListenerObserver;
import com.mZone.epro.toeic.customInterface.ToeicSingleCheckResultListener;
import com.mZone.epro.toeic.customInterface.ToeicSingleCheckResultObserver;
import com.mZone.epro.toeic.customView.ToeicCustomListView;
import com.mZone.epro.toeic.customView.ToeicWrapContentHeightViewPager;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.data.ToeicUtil;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ToeicFragmentListview extends Fragment implements ToeicMediaListener, ToeicSingleCheckResultListener{

	public static final String ARGS_PART_INDEX = "ToeicFragmentListview.ARGS_PART_INDEX";
	
	private ToeicMediaListener mediaListenerContext;
	@SuppressWarnings("unused")
	private ToeicSingleCheckResultListener singleCheckResultListenerContext;
	private int partIndex;
	private ToeicDataController dataController = ToeicDataController.getInstance();
	private ToeicCustomListView listView;
	
	//for vitual header
	private RelativeLayout virtualHeader;
	private TextView virtualHeaderTextView;
	private ImageButton virtualHeaderReplayButton;
	private ImageButton virtualHeaderCheckButton;
	private int heightOfVirtualHeader;
	static final int CHECKING_BOTTOM_POSITION = 10;
	
	private boolean delayToScroll = false;
	private int delayToScrollRowID = 0; 
	
	//for audio control
	public static interface MediaControlListener{
		public void setTimeToGo(int time);
	}
	MediaControlListener mediaDelegate;
	
	public ToeicFragmentListview() {
		mediaListenerContext = this;
		singleCheckResultListenerContext = this;
	}
	
	@Override
	public void onAttach(Activity activity){
		 super.onAttach(activity);
		 mediaDelegate = (ToeicTestActivity)activity;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.partIndex = getArguments().getInt(ARGS_PART_INDEX);
		View rootView = inflater.inflate(R.layout.toeic_fragment_listview, container, false);
		listView = (ToeicCustomListView)rootView.findViewById(R.id.listView);
		CustomListViewAdapter adapter = new CustomListViewAdapter();
		listView.setAdapter(adapter);
		virtualHeader = (RelativeLayout)rootView.findViewById(R.id.virtualRowHeader);
		virtualHeaderTextView = (TextView)rootView.findViewById(R.id.virtualRowHeaderText);
		virtualHeaderReplayButton = (ImageButton)rootView.findViewById(R.id.virtualRowHeaderReplay);
		virtualHeaderCheckButton = (ImageButton)rootView.findViewById(R.id.virtualRowHeaderCheck);
		if (!ToeicUtil.isPracticeMode()){
			virtualHeaderReplayButton.setVisibility(View.INVISIBLE);
			virtualHeaderCheckButton.setVisibility(View.INVISIBLE);
		}
		if (partIndex == ToeicDataController.TOEIC_PART_5){
			virtualHeaderReplayButton.setVisibility(View.INVISIBLE);
		}
		ViewTreeObserver vto = rootView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				heightOfVirtualHeader = virtualHeader.getHeight();
				listView.setOnScrollListener(new OnScrollListener() {
					@Override
					public void onScrollStateChanged(AbsListView view, int scrollState) {
					}
					@Override
					public void onScroll(AbsListView view, final int firstVisibleItem,
							int visibleItemCount, int totalItemCount) {
						String textToSet = "";
						View topView = listView.getChildAt(0);
						if (topView == null) return;
						int bottom = topView.getBottom();
						int questionID = -1;
						int rowIDTemp = -1;
						if (bottom < CHECKING_BOTTOM_POSITION){
							rowIDTemp = firstVisibleItem + 1;
						}
						else{
							rowIDTemp = firstVisibleItem;
						}
						final int rowID = rowIDTemp;
						textToSet = dataController.getStringForRowHeaderInListview(partIndex, rowID);
						if (TextUtils.isEmpty(textToSet))
						{
							return;
						}
						if (rowID == scriptCurrentRowID){
							virtualHeaderReplayButton.setSelected(true);
						}
						else{
							virtualHeaderReplayButton.setSelected(false);
						}
						virtualHeaderReplayButton.setOnClickListener(new ImageButton.OnClickListener() {
							@Override
							public void onClick(View v) {
								int timeStart = dataController.getAudioTimeStart(partIndex, rowID);
								mediaDelegate.setTimeToGo(timeStart);
								v.setSelected(true);
								int firstVisible = listView.getFirstVisiblePosition()
						                - listView.getHeaderViewsCount();
						        int lastVisible = listView.getLastVisiblePosition()
						                - listView.getHeaderViewsCount();
						        for (int i = firstVisible; i <= lastVisible; i++){
						        	View rowView = listView.getChildAt(i - firstVisible);
						        	ImageButton replayBtn = (ImageButton)rowView.findViewById(R.id.rowHeaderReplay);
						        	if (rowID == i){
						        		replayBtn.setSelected(true);
						        	}
						        	else{
						        		replayBtn.setSelected(false);
						        	}
						        }
							}
						});
						questionID = dataController.getQuestionID(partIndex, rowID,  0) - 1;
						virtualHeaderTextView.setText(textToSet);
						if (dataController.getCheckResultState(questionID) != ToeicDataController.CHECK_RESULT_SELECTED_FIRST_STATE){
							virtualHeaderCheckButton.setEnabled(false);
						}
						else{
							virtualHeaderCheckButton.setEnabled(true);
							virtualHeaderCheckButton.setOnClickListener(new ImageButton.OnClickListener(){
								@Override
								public void onClick(View v) {
									dataController.setCheckResultState(partIndex, rowID);
									v.setEnabled(false);
									notifyObserverArray(rowID);
									int firstVisible = listView.getFirstVisiblePosition()
							                - listView.getHeaderViewsCount();
							        int lastVisible = listView.getLastVisiblePosition()
							                - listView.getHeaderViewsCount();
							        for (int i = firstVisible; i <= lastVisible; i++){
							        	View rowView = listView.getChildAt(i - firstVisible);
							        	ImageButton checkBtn = (ImageButton)rowView.findViewById(R.id.rowHeaderCheck);
							        	if (rowID == i){
							        		checkBtn.setEnabled(false);
							        	}
							        }
								}
							});
						}
						
						int y = 0;
						View wantedView = listView.getChildAt(1);
						if (wantedView != null){
							y = wantedView.getTop();
							
							if (y <= heightOfVirtualHeader){
								int delta = y - heightOfVirtualHeader;
								if (delta >= -heightOfVirtualHeader)
									virtualHeader.setY(delta);
								else
									virtualHeader.setY(0);
							}
							else{
								virtualHeader.setY(0);
							}
						}
						else{
							virtualHeader.setY(0);
						}
					}
				});
				listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		return rootView;
	}
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume (){
		super.onResume();
		if (delayToScroll){
			delayToScroll = false;
			listView.setSelection(delayToScrollRowID);
		}
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
	
	public class CustomListViewAdapter extends BaseAdapter{
		private LayoutInflater mLayoutInflater;
		public CustomListViewAdapter(){
			mLayoutInflater = LayoutInflater.from(getActivity());
		}
		private class Holder
		{
			TextView rowHeaderTextView;
			ToeicWrapContentHeightViewPager viewPager;
			ImageButton rowHeaderReplay;
			ImageButton rowHeaderCheck;
		}
		
		@Override
		public int getCount() {

			return dataController.getNumberOfRowInListview(partIndex);
		}

		@Override
		public Object getItem(int arg0) {

			return null;
		}

		@Override
		public long getItemId(int arg0) {

			return 0;
		}
		
		static final int PAGER_ID_OFFSET = 100;
	    static final int PAGER_PAGE_MARGIN = 20;
	    static final int PAGER_PAGE_LIMIT = 2;
	    
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			Holder holder = null;
			ToeicCustomListViewContentPagerAdapter adapter = null;
			if (convertView == null){
				int layout_id = R.layout.toeic_fragment_listview_row;
				if (partIndex <= ToeicDataController.TOEIC_PART_2)
					layout_id = R.layout.toeic_fragment_listview_row_with_virtual_background;
				else if (partIndex <= ToeicDataController.TOEIC_PART_4){
					layout_id = R.layout.toeic_fragment_listview_row_with_virtual_background;
				}
				else if (partIndex == ToeicDataController.TOEIC_PART_5){
					layout_id = R.layout.toeic_fragment_listview_row_with_virtual_background;
				}
				convertView = mLayoutInflater.inflate(layout_id, parent, false);
				holder = new Holder();
				holder.rowHeaderTextView = (TextView)convertView.findViewById(R.id.rowHeaderText);
				holder.rowHeaderCheck = (ImageButton)convertView.findViewById(R.id.rowHeaderCheck);
				holder.rowHeaderReplay = (ImageButton)convertView.findViewById(R.id.rowHeaderReplay);
				if (!ToeicUtil.isPracticeMode()){
					holder.rowHeaderReplay.setVisibility(View.INVISIBLE);
					holder.rowHeaderCheck.setVisibility(View.INVISIBLE);
				}
				if (partIndex == ToeicDataController.TOEIC_PART_5){
					holder.rowHeaderReplay.setVisibility(View.INVISIBLE);
				}
				holder.viewPager = (ToeicWrapContentHeightViewPager)convertView.findViewById(R.id.rowViewPager);
				adapter = new ToeicCustomListViewContentPagerAdapter(getChildFragmentManager(), position);
				holder.viewPager.setAdapter(adapter);
				holder.viewPager.setId(R.id.rowViewPager + PAGER_ID_OFFSET + position);
				holder.viewPager.setPageMargin(PAGER_PAGE_MARGIN);
				holder.viewPager.setOffscreenPageLimit(PAGER_PAGE_LIMIT);
				
				convertView.setTag(holder);
			}
			else{
				holder = (Holder)convertView.getTag();
				holder.viewPager.setId(R.id.rowViewPager + PAGER_ID_OFFSET + position);
				adapter = (ToeicCustomListViewContentPagerAdapter) holder.viewPager.getAdapter();
			}
			adapter.setRowID(position);
			holder.viewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int pagerIndex) {

						dataController.setRowPagerSelectedIndex(partIndex, position, pagerIndex);
				}
				
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

					
				}
				@Override
				public void onPageScrollStateChanged(int state) {

				}
			});
			holder.viewPager.setCurrentItem(dataController.getRowPagerSelectedIndex(partIndex, position));
			holder.rowHeaderTextView.setText(dataController.getStringForRowHeaderInListview(partIndex, position));
			holder.rowHeaderReplay.setOnClickListener(new ImageButton.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					int timeStart = dataController.getAudioTimeStart(partIndex, position);
					mediaDelegate.setTimeToGo(timeStart);
					v.setSelected(true);
				}
			});
			if (position == scriptCurrentRowID){
				holder.rowHeaderReplay.setSelected(true);
			}
			else{
				holder.rowHeaderReplay.setSelected(false);
			}
			int questionID = dataController.getQuestionID(partIndex, position, 0) - 1;
			if (dataController.getCheckResultState(questionID) != ToeicDataController.CHECK_RESULT_SELECTED_FIRST_STATE){
				holder.rowHeaderCheck.setEnabled(false);
			}
			else{
				holder.rowHeaderCheck.setEnabled(true);
				holder.rowHeaderCheck.setOnClickListener(new ImageButton.OnClickListener(){

					@Override
					public void onClick(View v) {
						dataController.setCheckResultState(partIndex, position);
						v.setEnabled(false);
						notifyObserverArray(position);
					}
				});
			}
			return convertView;
		}
	}
	
	public class ToeicCustomListViewContentPagerAdapter extends FragmentPagerAdapter{

		private int rowID;
		private ToeicFragmentRowAbstract contentPage;
		private ToeicFragmentRowAbstract scriptPage;
		public ToeicCustomListViewContentPagerAdapter(FragmentManager fragmentManager, int rowID) {
			super(fragmentManager);
			this.rowID = rowID;
		}

		@Override
		public Fragment getItem(int pageIndex) {
			if (pageIndex == 0){
				if (contentPage == null) {
					contentPage = ToeicFragmentRowAbstractFactory.getFragment(partIndex, pageIndex, mediaListenerContext);
					contentPage.rowChanged(rowID);
					registerObserver(contentPage);
				}
				return contentPage;
			}
			else if (pageIndex == 1){
				if (scriptPage == null) {
					scriptPage = ToeicFragmentRowAbstractFactory.getFragment(partIndex, pageIndex, mediaListenerContext);
					scriptPage.rowChanged(rowID);
				}
				return scriptPage;
			}
			return null;
		}

		@Override
		public int getCount() {
			if (!ToeicUtil.isPracticeMode()) return 1;
			if (partIndex != ToeicDataController.TOEIC_PART_5){
				return 2;
			}
			return 1;
		}
		
		public void setRowID(int rowID){
			this.rowID = rowID;
			if (contentPage != null) {
				contentPage.rowChanged(rowID);
			}
			if (scriptPage != null) {
				scriptPage.rowChanged(rowID);
			}
		}
	}

    //using for highlight listener
    ArrayList<ToeicMediaListenerObserver> observers = new ArrayList<ToeicMediaListenerObserver>() ;
    long currentVoiceStart = -1;
    long currentVoiceStop = -1;
    int scriptCurrentRowID = -1;
    int scriptCurrentSubID = -1;
    
	@Override
	public void registerObserver(ToeicMediaListenerObserver o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(ToeicMediaListenerObserver o) {
		for (int i = 0; i < observers.size(); i++){
			if (observers.get(i) == o) observers.remove(i);
		}
	}

	@Override
	public void notifyObserver() {
		for (int i = 0; i < observers.size(); i++){
			observers.get(i).setHighlight(scriptCurrentSubID);
		}
	}

	@Override
	public void onCurrentTimeChanged(long currentTime) {
		int currentTimeSec = (int) (currentTime/1000);
		if (currentTimeSec >= currentVoiceStart && currentTimeSec <= currentVoiceStop) return;
		scriptCurrentRowID = dataController.getAudioCurrentRowID(partIndex, currentTimeSec);
		if (scriptCurrentRowID < 0) return;
		scriptCurrentSubID = dataController.getAudioCurrentSubID(partIndex, currentTimeSec, scriptCurrentRowID);
		if (scriptCurrentSubID == -1){
			currentVoiceStop = dataController.getScriptTimeStop(partIndex, scriptCurrentRowID, scriptCurrentSubID); 
		}
		else if (scriptCurrentSubID == -2){
			currentVoiceStart = dataController.getScriptTimeStart(partIndex, scriptCurrentRowID, scriptCurrentSubID);
		}
		else{
			currentVoiceStart = dataController.getScriptTimeStart(partIndex, scriptCurrentRowID, scriptCurrentSubID);
			currentVoiceStop = dataController.getScriptTimeStop(partIndex, scriptCurrentRowID, scriptCurrentSubID);
		}
		int firstVisible = listView.getFirstVisiblePosition()
                - listView.getHeaderViewsCount();
        int lastVisible = listView.getLastVisiblePosition()
                - listView.getHeaderViewsCount();
        for (int i = firstVisible; i <= lastVisible; i++){
        	View rowView = listView.getChildAt(i - firstVisible);
        	ImageButton replayBtn = (ImageButton)rowView.findViewById(R.id.rowHeaderReplay);
        	if (scriptCurrentRowID == i){
        		replayBtn.setSelected(true);
        	}
        	else{
        		replayBtn.setSelected(false);
        	}
        }
        if (scriptCurrentRowID == firstVisible){
			virtualHeaderReplayButton.setSelected(true);
		}
        else{
        	virtualHeaderReplayButton.setSelected(false);
        }
		notifyObserver();
	}

	@Override
	public int getScriptCurrentRowID() {
		return scriptCurrentRowID;
	}

	@Override
	public int getScriptCurrentSubID() {
		return scriptCurrentSubID;
	}

	ArrayList<ToeicSingleCheckResultObserver> singleCheckResultObservers = new ArrayList<ToeicSingleCheckResultObserver>();
	
	@Override
	public void registerObserver(ToeicSingleCheckResultObserver o) {
		singleCheckResultObservers.add(o);
	}

	@Override
	public void removeObserver(ToeicSingleCheckResultObserver o) {
		for (int i = 0; i < singleCheckResultObservers.size(); i++){
			if (singleCheckResultObservers.get(i) == o) singleCheckResultObservers.remove(i);
		}
	}

	@Override
	public void notifyObserverArray(int rowID) {
		for (int i = 0; i < singleCheckResultObservers.size(); i++){
			singleCheckResultObservers.get(i).onCheckResultClicked(rowID);
		}
	}

	@Override
	public void scrollToRowID(int rowID) {
		listView.smoothScrollToPositionFromTop(rowID, 0);
	}

	@Override
	public void scrollToRowIDWithDelayUntilViewCreated(int rowID) {
		// TODO Auto-generated method stub
		delayToScroll = true;
		delayToScrollRowID = rowID;
	}
}
