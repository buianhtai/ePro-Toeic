package com.mZone.epro.toeic.activity;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.mZone.epro.EproBaseActivity;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookProviderMetaData.TestHistoryTableMetaData;
import com.mZone.epro.client.task.BookDataProcessingIntentService;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;
import com.mZone.epro.client.utility.ExternalStorage;
import com.mZone.epro.dict.data.DictionaryItem;
import com.mZone.epro.dict.dialog.DictionarySearchDialogFragment;
import com.mZone.epro.dict.dialog.DictionarySearchDialogFragment.DictionarySearchDialogFragmentInterface;
import com.mZone.epro.dict.dialog.ShowDictWordDialog;
import com.mZone.epro.dict.dialog.ShowDictWordDialog.ShowDictWordDialogDelegate;
import com.mZone.epro.dict.dictLibrary.DictionaryManager;
import com.mZone.epro.testhisotry.data.TestHistoryItem;
import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customView.CustomWordClickableTextview;
import com.mZone.epro.toeic.customView.ToeicSlidingUpPanelLayout;
import com.mZone.epro.toeic.customView.ToeicSlidingUpPanelLayout.PanelSlideListener;
import com.mZone.epro.toeic.data.ToeicDataController;
import com.mZone.epro.toeic.data.ToeicUtil;
import com.mZone.epro.toeic.dataStructure.ToeicBasicSentence;
import com.mZone.epro.toeic.fragment.ToeicFragmentListview;
import com.mZone.epro.toeic.fragment.ToeicFragmentTabviewAndroid;
import com.mZone.epro.toeic.fragment.ToeicFragmentListview.MediaControlListener;
import com.mZone.epro.toeic.result.ToeicResultDialog;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ToeicTestActivity extends EproBaseActivity implements
					android.support.v7.app.ActionBar.OnNavigationListener, MediaControlListener,
					ShowDictWordDialogDelegate, DictionarySearchDialogFragmentInterface{

	//argument name constant
	public static final String TEST_MODE_ARG = "TEST_MODE_ARG";
	public static final String TEST_PATH_ARG = "TEST_PATH_ARG";
	public static final String BOOK_ID_ARG = "BOOK_ID_ARG";
	public static final String TEST_HISTORY_PARCEL_ARG = "TEST_HISTORY_PARCEL_ARG";
	
	//test mode constant
	public static final int TEST_MODE_NORMAL = 0;
	public static final int TEST_MODE_PRACTICE = 1;
	
	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	private ToeicDataController toeicDataController;
	
	//intent variable
	private int mTestMode;
	private String mDataPath;
	TestHistoryItem historyItem = null;
	int bookID = -1;	
	private int testStatus = TestHistoryItem.STATUS_DOING;
	//in case of test mode, if first load from history, with this flag, activity could set the section fragment
	boolean firstLoadFromHistory = false; 
	
	//activity
	private Context mContext;
	private View containerView;
	
	//audio panel
	private ToeicSlidingUpPanelLayout audioBarPanel;
	private ImageButton dragView;
	private int heightOffset;	//offset from dragView
	
	//audio control view
	TextView forwardIndicatorTextview;
	TextView backwardIndicatorTextview;
	SeekBar voiceSeekbar;
	ImageButton voiceBackwardButton;
	ImageButton voiceForwardButton;
	ImageButton voicePlayButton;
	ImageButton voicePlusButton;
	ImageButton voiceMinusButton;
	boolean isVoiceBackAndForwardEnable;
	
    //Listening observer
    ToeicMediaListener fragmentMediaListener;
    
    //Dictionary
//    private StarDict stardict;
    DictionaryManager dictManager;
	
	//audio control variable
	static final int HOUR = 60*60*1000;
	static final int MINUTE = 60*1000;
	static final int SECOND = 1000;
	private int activePart = ToeicDataController.TOEIC_PART_1;
	private long currentTime = 0;
	private long totalTime = 0;
	private boolean isPlaying = true;
	private MediaPlayer mediaPlayer;
	private Handler mediaHandler = new Handler();
	private Runnable mediaUpdateTimeTask = new Runnable() {
        @Override
		public void run() {
        	updateSeekBar();
            updateScriptHighlight();
            mediaHandler.postDelayed(this, 1000);
        }
     };
     
     //Menu timer
     MenuItem timerItem;
     int timerCurrentTime = 0;
     private Handler timerHandler = new Handler();
     private Runnable timerUpdateActionBar = new Runnable() {
		@Override
		public void run() {
			timerCurrentTime++;
			if (timerItem != null){
				timerItem.setTitle(convertFromCurrentTimeToString());
			}
			if (mTestMode == TEST_MODE_NORMAL && activePart >= ToeicDataController.TOEIC_PART_5){
				timerCurrentReadingTimer++;
				if (timerCurrentReadingTimer == READING_MAX_TIME - READING_ALARM_TIME){
					//make alarm toast here
					Toast.makeText(getApplicationContext(), R.string.quit_test_auto_finish_toast_message, Toast.LENGTH_LONG).show();
				}
				if (timerCurrentReadingTimer == READING_MAX_TIME){
					//finish the test
					showAutoFinishTestDialog();
					return;
				}
			}
			timerHandler.postDelayed(this, 1000);
		}
	};
	
	//timer for reading
	private static final int READING_MAX_TIME = 75*60; //75 minutes
	private static final int READING_ALARM_TIME = 70*60; //75 minutes
	private int timerCurrentReadingTimer = 0;
	
	//Progress Bar
	ProgressBar loadingProgressBar;
	
	//using for test life cycle
	boolean isDataLoading = false;
	boolean autoChangePart = true; //for listening part auto change
	
	//if being loading in background, return if activity is destroyed
	boolean activityIsDestroy = false;
	
	//side menu
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	
	//using for show result
	private int[] resultArray = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		toeicDataController = ToeicDataController.getInstance();
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		
		//loading dict
		final LoaderManager loaderManager = getSupportLoaderManager();
		final DictionaryLoadingTaskLoaderCallbacks callback = new DictionaryLoadingTaskLoaderCallbacks();
		loaderManager.initLoader(R.id.toeic_test_activity_dictionary_loading_task_loader_id, null, callback);
		
		//set context
		mContext = this;
		setContentView(R.layout.activity_toeic_test);
		containerView = findViewById(R.id.container);
		heightOffset = ((RelativeLayout.LayoutParams)containerView.getLayoutParams()).bottomMargin;
				
		//setup from intent
		Intent intent = getIntent();
		mTestMode = intent.getExtras().getInt(TEST_MODE_ARG);
		mDataPath = intent.getExtras().getString(TEST_PATH_ARG);
		bookID = intent.getExtras().getInt(BOOK_ID_ARG);
		historyItem = intent.getExtras().getParcelable(TEST_HISTORY_PARCEL_ARG);
		
		//if run from history
		if (historyItem != null){
			toeicDataController.setTestHistoryFile(historyItem.getFilePath());
			timerCurrentTime = historyItem.getCurrentTime();
			timerCurrentReadingTimer = historyItem.getCurrentReadingTime();
			testStatus = historyItem.getStatus();
			activePart = historyItem.getActivePart();
			currentTime = historyItem.getMediaCurrentTime();
			firstLoadFromHistory = true;
			if (testStatus == TestHistoryItem.STATUS_FINISH){
				resultArray = historyItem.getScore();
				mTestMode = TEST_MODE_PRACTICE;
				activePart = 0;
				currentTime = 0;
			}
		}
		else{
			toeicDataController.setTestHistoryFile(null);
		}
		
		if (mTestMode == TEST_MODE_NORMAL){
			ToeicUtil.setPracticeMode(false);
		}	
		else{
			ToeicUtil.setPracticeMode(true);
		}
		
		//loading progress
		loadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
		setupMediaPlayerController();
		
		//set data for toeic data controller
		if (savedInstanceState != null) {
			toeicDataController.dataInitForRestoreInstance(mDataPath, mTestMode, testStatus);
		}
		else{
			toeicDataController.dataInit(mDataPath, mTestMode, testStatus);
		}
		
		
		//undo list for highlight
		undoList = new ArrayList<ToeicBasicSentence>();
		undoListView = new ArrayList<CustomWordClickableTextview>();
		
		
		// Set up the action bar to show a dropdown list.
		final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		actionBar.setListNavigationCallbacks(
				new ArrayAdapter<String>(getSupportActionBarThemedContextCompat(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.toeic_title_section1),
								getString(R.string.toeic_title_section2),
								getString(R.string.toeic_title_section3),
								getString(R.string.toeic_title_section4),
								getString(R.string.toeic_title_section5),
								getString(R.string.toeic_title_section6),
								getString(R.string.toeic_title_section7),}), this);
		actionBar.setSelectedNavigationItem(activePart);
	}
	
	private void setupMediaPlayerController(){
		audioBarPanel = (ToeicSlidingUpPanelLayout) findViewById(R.id.audioBar);
//		audioBarPanel.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
		audioBarPanel.setAnchorPoint(1.0f);
		audioBarPanel.setPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (slideOffset < 0.2) {
                	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                	heightOffset = panel.getHeight();
                	layoutParams.setMargins(0, 0, 0, heightOffset);
//                	containerView.setLayoutParams(layoutParams);
                	containerView.invalidate();
                	
                } else {
                	RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                	heightOffset = dragView.getHeight();
                	layoutParams.setMargins(0, 0, 0, heightOffset);
//                	containerView.setLayoutParams(layoutParams);
                	containerView.invalidate();
                	
                }
            }

            @Override
            public void onPanelExpanded(View panel) {
            	dragView.setImageResource(R.drawable.slide_down);
            }

            @Override
            public void onPanelCollapsed(View panel) {
            	dragView.setImageResource(R.drawable.slide_up);
            }

            @Override
            public void onPanelAnchored(View panel) {
            }
        });
		float panelHeight = this.getResources().getDimension(R.dimen.toeic_audiobar_panel_height);
		audioBarPanel.setPanelHeight((int)panelHeight);
		dragView = (ImageButton)findViewById(R.id.dragButton);
		audioBarPanel.setDragView(dragView);
		
		forwardIndicatorTextview = (TextView)findViewById(R.id.forwardingIndicatorText);
		backwardIndicatorTextview = (TextView)findViewById(R.id.backingIndicatorText);
		voiceSeekbar = (SeekBar)findViewById(R.id.voiceSeekBar);
		voiceBackwardButton = (ImageButton)findViewById(R.id.audioBackwardButton);
		voiceForwardButton = (ImageButton)findViewById(R.id.audioForwardButton);
		voicePlusButton = (ImageButton)findViewById(R.id.audioPlusButton);
		voiceMinusButton = (ImageButton)findViewById(R.id.audioMinusButton);
		voicePlayButton = (ImageButton)findViewById(R.id.audioPlayButton);
		voicePlayButton.setSelected(true);
		voiceSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				updateTimeFromUser(seekBar.getProgress());
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (fromUser){
					updateTimeFromUser(progress);
				}
			}
		});
		voicePlayButton.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!v.isSelected()){
					v.setSelected(true);
				}
				else{
					v.setSelected(false);
				}
				changePlayingState();
			}
			
		});
		voiceForwardButton.setOnClickListener(new ImageButton.OnClickListener(){

			@Override
			public void onClick(View v) {
				if (isVoiceBackAndForwardEnable) nextPlay();
			}
			
		});
		voiceBackwardButton.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (isVoiceBackAndForwardEnable) backPlay();
			}
		});
		voicePlusButton.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (isPlaying){
					mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 5000);
				}
			}
		});
		voiceMinusButton.setOnClickListener(new ImageButton.OnClickListener(){
			@Override
			public void onClick(View v) {
				if (isPlaying){
					mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 5000);
				}
			}
		});
		if (!ToeicUtil.isPracticeMode()){
			disableMediaPlayerController();
		}
		isVoiceBackAndForwardEnable = true;
	}
		
	private void disableMediaPlayerController(){
		voiceBackwardButton.setEnabled(false);
		voiceForwardButton.setEnabled(false);
		voiceMinusButton.setEnabled(false);
		voicePlusButton.setEnabled(false);
		voicePlayButton.setEnabled(false);
		voiceSeekbar.setEnabled(false);
	}
	
	private void enableMediaPlayerController(){
		voiceBackwardButton.setEnabled(true);
		voiceForwardButton.setEnabled(true);
		voiceMinusButton.setEnabled(true);
		voicePlusButton.setEnabled(true);
		voicePlayButton.setEnabled(true);
		voiceSeekbar.setEnabled(true);
	}
	
	public void showProgressBar(){
		loadingProgressBar.setVisibility(View.VISIBLE);
	}
	public void hideProgressBar(){
		loadingProgressBar.setVisibility(View.GONE);
	}

	/***********************************************************************************/
	/***********************************************************************************/
	/************************ activity life cycle start*********************************/
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getSupportActionBarThemedContextCompat() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return getSupportActionBar().getThemedContext();
		} else {
			return this;
		}
	}
	
	@Override
	public void onStart(){
		super.onStart();
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
//		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
//		super.onSaveInstanceState(outState);

	}

	@Override
	public void onResume() {
	    super.onResume();  // Always call the superclass method first
	    if (mediaPlayer != null && isPlaying){
	    	mediaPlayer.start();
	    	mediaHandler.postDelayed(mediaUpdateTimeTask, 100);
	    }
	    if (testStatus == TestHistoryItem.STATUS_DOING) {
	    	timerHandler.postDelayed(timerUpdateActionBar, 1000);
	    }
	}
	@Override
	public void onPause() {
	    super.onPause();  // Always call the superclass method first

	    // Release the Camera because we don't need it when paused
	    // and other activities might need to use it.
	    if (mediaPlayer != null && mediaPlayer.isPlaying()){
	    	mediaPlayer.pause();
	    	mediaHandler.removeCallbacks(mediaUpdateTimeTask);
	    }
	    if (testStatus == TestHistoryItem.STATUS_DOING) {
	    	timerHandler.removeCallbacks(timerUpdateActionBar);
	    }
	}
	
	@Override
	public void onStop(){
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();  // Always call the superclass method first
	    this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    if (mediaPlayer != null && mediaPlayer.isPlaying()){
	    	mediaPlayer.stop();
	    	mediaHandler.removeCallbacks(mediaUpdateTimeTask);
	    }
	    toeicDataController.releaseData();
	    if (dictManager != null) {
	    	dictManager.releaseDict();
	    }
	    activityIsDestroy = true;
	}
	
	@Override
	public void onBackPressed() {
	   onQuitTest();
	}
	
	/************************ activity life cycle end***********************************/
	/***********************************************************************************/
	/***********************************************************************************/
	
	/***********************************************************************************/
	/***********************************************************************************/
	/***************************** navigation bar start*********************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.toeic_test, menu);
		timerItem = menu.findItem(R.id.action_timer);
		
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (testStatus == TestHistoryItem.STATUS_FINISH) {
			timerItem.setTitle(getResources().getString(R.string.toeic_activity_actionbar_review));
			MenuItem finishTestItem = menu.findItem(R.id.action_finish_test);
			finishTestItem.setTitle(R.string.toeic_test_menu_action_review_score);
	    }
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_undo:
	        	undo();
	            return true;
	        case R.id.action_finish_test:
	        	//finish test here
	        	if (testStatus == TestHistoryItem.STATUS_DOING){ 
	        		showFinishTestDialog();
	        	}
	        	else if (testStatus == TestHistoryItem.STATUS_FINISH){
	        		showReviewResultDialog();
	        	}
	        	return true;
	        case R.id.action_dictionary:
	        	showDictionarySearchDialog();
	        	return true;
	        case android.R.id.home:
	        	onQuitTest();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		if (isDataLoading){
			getSupportActionBar().setSelectedNavigationItem(activePart);
			return false;
		}
		if (mTestMode == TEST_MODE_NORMAL){
			if (autoChangePart){	//change part automatically (for media player finish)
				autoChangePart = false;	//toggle and user cannot change anymore
			}
			else{	//change part when user click droplist
				if (timerCurrentReadingTimer <= 0){	//while not pass to reading part
					getSupportActionBar().setSelectedNavigationItem(activePart);
					return false;
				}
				else{
					//user can check every part without play media
				}
			}
		}
		else{
			
		}
		showProgressBar();
		activePart = position;
		resetAudio();
		if (position <= ToeicDataController.TOEIC_PART_4){
        	audioBarPanel.setVisibility(View.VISIBLE);
		}
		else{
        	audioBarPanel.setVisibility(View.GONE);
		}
		isDataLoading = true;
		new LoadingDataFromXML().execute();
		return true;
	}
	
	private class LoadingDataFromXML extends AsyncTask<Integer, String, Boolean>{
		
		@Override
		protected Boolean doInBackground(Integer... params) {
			ToeicDataController.getInstance().getDataFromXML(activePart);
			if (activityIsDestroy) return false;
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (activityIsDestroy || result == false){
				return;
			}
			hideProgressBar();
			if (activePart == ToeicDataController.TOEIC_PART_1 || activePart == ToeicDataController.TOEIC_PART_2 || activePart == ToeicDataController.TOEIC_PART_3
					|| activePart == ToeicDataController.TOEIC_PART_4 || activePart == ToeicDataController.TOEIC_PART_5){
				Bundle args = new Bundle();
				args.putInt(ToeicFragmentListview.ARGS_PART_INDEX, activePart);
				ToeicFragmentListview fragment = new ToeicFragmentListview();
				fragment.setArguments(args);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
				ft.replace(R.id.container, fragment, "fragment");
				ft.commit();
				fragmentMediaListener = fragment;
			}
			else if (activePart == ToeicDataController.TOEIC_PART_6 || activePart == ToeicDataController.TOEIC_PART_7){
				fragmentMediaListener = null;
				ToeicFragmentTabviewAndroid fragment = new ToeicFragmentTabviewAndroid(activePart);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
				ft.replace(R.id.container, fragment, "fragment");
				ft.commit();
			}
			
			if (activePart <= ToeicDataController.TOEIC_PART_4){
				if (mTestMode == TEST_MODE_PRACTICE || timerCurrentReadingTimer <= 0){
					startMedia();
					if (firstLoadFromHistory){
						setTimeToGoFromHistory();
					}
				}
			}
			firstLoadFromHistory = false;
			isDataLoading = false;
			isVoiceBackAndForwardEnable = true;
		}
	}

	/***************************** navigation bar end***********************************/
	/***********************************************************************************/
	/***********************************************************************************/


	/***********************************************************************************/
	/***********************************************************************************/
	/****************************** audio control start*********************************/

	private void resetAudio(){
		if (mediaPlayer == null){
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mediaHandler.removeCallbacks(mediaUpdateTimeTask);
					mp.stop();
					//after finish audio, auto change
					final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
					if (activePart < ToeicDataController.TOEIC_PART_5) {
						autoChangePart = true;
						actionBar.setSelectedNavigationItem(activePart + 1);
					}	
				}
			});
		}
		mediaPlayer.reset();
		mediaHandler.removeCallbacks(mediaUpdateTimeTask);
		if (activePart < ToeicDataController.TOEIC_PART_5){
			ToeicDataController dataController = ToeicDataController.getInstance();
			String musicFile = dataController.getAudioPath(activePart);
			try {
				mediaPlayer.setDataSource(musicFile);
				mediaPlayer.prepare();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (mediaPlayer != null){
				totalTime =  mediaPlayer.getDuration();
			}
			else{
				totalTime = 0;
			}
			if (!firstLoadFromHistory){
				currentTime = 0;
			}
		}
	}

	private void startMedia(){
		if (mediaPlayer != null){
			mediaPlayer.seekTo((int) currentTime);
			if (isPlaying){
				mediaPlayer.start();
				mediaHandler.postDelayed(mediaUpdateTimeTask, 100);
			}
		}
	}

	private void updateSeekBar(){
		currentTime = mediaPlayer.getCurrentPosition();
		double percent = ((double)currentTime/totalTime)*100;
		long remainTime = totalTime - currentTime;
		forwardIndicatorTextview.setText(stringFromTime(currentTime));
		backwardIndicatorTextview.setText(stringFromTime(remainTime));	
		voiceSeekbar.setProgress((int) Math.round(percent));
	}
	
	public void updateTimeFromUser(int progress){
		double doubleTimeToGo = (double)progress/100;
		doubleTimeToGo *= totalTime;
		currentTime = (int) Math.round(doubleTimeToGo);
		long remainTime = totalTime - currentTime;
		forwardIndicatorTextview.setText(stringFromTime(currentTime));
		backwardIndicatorTextview.setText(stringFromTime(remainTime));	
		mediaHandler.removeCallbacks(mediaUpdateTimeTask);
		mediaPlayer.seekTo((int)currentTime);
		mediaHandler.postDelayed(mediaUpdateTimeTask, 100);
	}

	@Override
	public void setTimeToGo(int time) {
		currentTime = time*SECOND;
		mediaPlayer.seekTo((int) currentTime);
	}

	public void setTimeToGoFromHistory(){
		mediaPlayer.seekTo((int) currentTime);
		int currentT = (int) (currentTime/1000);
		int[] nextQuestion = toeicDataController.getForwardPlayTime(activePart, currentT);
		int[] previous = toeicDataController.getBackwardPlayTime(activePart, currentT);
		if (nextQuestion[0] == ToeicDataController.NEXT_TIME_STATE_ERROR || nextQuestion[0] == ToeicDataController.NEXT_TIME_STATE_NEXT_PART){
			fragmentMediaListener.scrollToRowIDWithDelayUntilViewCreated(previous[0] + 1);
		}
		else if (previous[0] == ToeicDataController.NEXT_TIME_STATE_ERROR || nextQuestion[0] == ToeicDataController.NEXT_TIME_STATE_PREVIOUS_PART){
			fragmentMediaListener.scrollToRowIDWithDelayUntilViewCreated(nextQuestion[0] - 1);
		}
		else{
			fragmentMediaListener.scrollToRowIDWithDelayUntilViewCreated(nextQuestion[0] - 1);
		}
	}
	
	public void updateScriptHighlight(){
		if (fragmentMediaListener != null)
			fragmentMediaListener.onCurrentTimeChanged(currentTime);
	}
	
	private void changePlayingState(){
		if (isPlaying){
			isPlaying = false;
			if (mediaPlayer != null && mediaPlayer.isPlaying()){
				mediaPlayer.pause();
				mediaHandler.removeCallbacks(mediaUpdateTimeTask);
			}
		}
		else{
			isPlaying = true;
			if (mediaPlayer != null && !mediaPlayer.isPlaying()){
				mediaPlayer.start();
				mediaHandler.postDelayed(mediaUpdateTimeTask, 100);
			}
		}
	}
	
	private void nextPlay(){
		if (isPlaying){
			int currentT = mediaPlayer.getCurrentPosition()/1000;
			int[] returnResult = toeicDataController.getForwardPlayTime(activePart, currentT);
			if (returnResult[0] != ToeicDataController.NEXT_TIME_STATE_ERROR && returnResult[0] != ToeicDataController.NEXT_TIME_STATE_NEXT_PART){
				mediaPlayer.seekTo(returnResult[1]*1000);
				fragmentMediaListener.scrollToRowID(returnResult[0]);
			}
			else if (returnResult[0] != ToeicDataController.NEXT_TIME_STATE_ERROR){
				isVoiceBackAndForwardEnable = false;
				getSupportActionBar().setSelectedNavigationItem(activePart + 1);
			}
		}
	}
	
	private void backPlay(){
		if (isPlaying){
			int currentT = mediaPlayer.getCurrentPosition()/1000;
			int[] returnResult = toeicDataController.getBackwardPlayTime(activePart, currentT);
			if (returnResult[0] != ToeicDataController.NEXT_TIME_STATE_ERROR && returnResult[0] != ToeicDataController.NEXT_TIME_STATE_PREVIOUS_PART){
				mediaPlayer.seekTo(returnResult[1]*1000);
				fragmentMediaListener.scrollToRowID(returnResult[0]);
			}
			else if (returnResult[0] != ToeicDataController.NEXT_TIME_STATE_ERROR){
				isVoiceBackAndForwardEnable = false;
				getSupportActionBar().setSelectedNavigationItem(activePart - 1);
			}
		}
	}

	/****************************** audio control end*********************************/
	/***********************************************************************************/
	/***********************************************************************************/
	
	
	/***********************************************************************************/
	/***********************************************************************************/
	/****************************** undo process start**********************************/
	
	private static ArrayList<ToeicBasicSentence> undoList = new ArrayList<ToeicBasicSentence>();
	private static ArrayList<CustomWordClickableTextview> undoListView = new ArrayList<CustomWordClickableTextview>();
	
	public static void insertToUndoList(CustomWordClickableTextview view, ToeicBasicSentence sentence){
		undoListView.add(view);
		undoList.add(sentence);
	}
	
	public void undo(){
		if (undoList.size() > 0){
			ToeicBasicSentence sentence = undoList.get(undoList.size() - 1);
			CustomWordClickableTextview view = undoListView.get(undoListView.size() - 1);
			sentence.removeHighlightSpace();
			if (view != null){
				view.restoreText();
			}
			undoList.remove(undoList.size() - 1);
			undoListView.remove(undoListView.size() - 1);
		}
		
	}
	
	public static void removeViewInUndoList(CustomWordClickableTextview view){
		int id = undoListView.indexOf(view);
		if (id >= 0){
			undoList.remove(id);
			undoListView.remove(id);
		}
	}
	
	/********************************* undo process end*********************************/
	/***********************************************************************************/
	/***********************************************************************************/

	
	/***********************************************************************************/
	/***********************************************************************************/
	/****************************** support function start *****************************/

	private String stringFromTime(long duration){
		long durationMint = (duration%HOUR)/MINUTE;
		long durationSec = (duration%MINUTE)/SECOND;
		return String.format("%02d:%02d", durationMint, durationSec);
	}

	private String convertFromCurrentTimeToString(){
		int h = timerCurrentTime/3600;
		int m = (timerCurrentTime%3600)/60;
		int s = (timerCurrentTime%3600)%60;
		String result = "";
		if (h >= 10){
			result += String.valueOf(h) + ":";
		}
		else{
			result += "0" + String.valueOf(h) + ":";
		}
		if (m >= 10){
			result += String.valueOf(m) + ":";
		}
		else{
			result += "0" + String.valueOf(m) + ":";
		}
		if (s >= 10){
			result += String.valueOf(s);
		}
		else{
			result += "0" + String.valueOf(s);
		}
		return result;
	}

	
	/****************************** support function end *******************************/
	/***********************************************************************************/
	/***********************************************************************************/

	
	/***********************************************************************************/
	/***********************************************************************************/
	/****************************** quit or finish test start **************************/

	private void onQuitTest(){
		if (testStatus == TestHistoryItem.STATUS_DOING){	//save the test if doing
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setTitle(R.string.quit_test_dialog_title);
			builder.setMessage(R.string.quit_test_dialog_message);
			builder.setNegativeButton(R.string.quit_test_dialog_cancel_btn, null);
			builder.setNeutralButton(R.string.quit_test_dialog_quit_not_save_btn, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					(ToeicTestActivity.this).finish();
				}
			});
			builder.setPositiveButton(R.string.quit_test_dialog_quit_and_save_btn, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					saveAndQuitTest(dialog);
		        }
		    });
			builder.create().show();
		}
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);
			builder.setTitle(R.string.quit_test_review_dialog_title);
			builder.setMessage(R.string.quit_test_review_dialog_message);
			builder.setNegativeButton(R.string.quit_test_review_dialog_cancel_btn, null);
			builder.setPositiveButton(R.string.quit_test_review_dialog_quit_btn, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					(ToeicTestActivity.this).finish();
				}
			});
			builder.create().show();
		}
	}
	
	private void saveAndQuitTest(final DialogInterface dialog){
		showProgressBar();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Date currentDate = new Date();
				String currentDateStr = sdf.format(currentDate);
				String filePath = currentDateStr + ".xml";
				toeicDataController.saveCurrentTest(mDataPath + filePath);
				if (historyItem == null){ //insert to history
					TestHistoryItem insertItem = new TestHistoryItem(bookID, testStatus, currentDate, filePath, mTestMode, timerCurrentTime, timerCurrentReadingTimer,activePart, currentTime);
					ContentResolver cr = mContext.getContentResolver();
					cr.insert(TestHistoryTableMetaData.CONTENT_URI, insertItem.getContentValues());
				}
				else {	//update to history
					ContentResolver cr = mContext.getContentResolver();
					Uri updateUri = ContentUris.withAppendedId(TestHistoryTableMetaData.CONTENT_URI, historyItem.get_id());
					int bookID = historyItem.getBookID();
					String fileName = historyItem.getFilePath();
					final String sourceFile = ExternalStorage.getSDCacheDir(getApplicationContext(), BookDataProcessingIntentService.UNZIP_FOLDER).toString() + "/" + bookID + "/" + fileName;
					historyItem.setStatus(testStatus);
					historyItem.setModifiedDate(currentDate);
					historyItem.setFilePath(filePath);
					historyItem.setCurrentTime(timerCurrentTime);
					historyItem.setActivePart(activePart);
					historyItem.setMediaCurrentTime(currentTime);
					historyItem.setCurrentReadingTime(timerCurrentReadingTimer);
					ContentValues updatedValues = historyItem.getContentValues();
					cr.update(updateUri, updatedValues, null, null);
					File oldFile = new File(sourceFile);
					oldFile.delete();
				}
				loadingProgressBar.post(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
			        	(ToeicTestActivity.this).finish();
					}
				});
			}
	   }).start();	
	}
	
	public void showFinishTestDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.quit_test_finish_dialog_title);
		builder.setMessage(R.string.quit_test_finish_dialog_message);
		builder.setNegativeButton(R.string.quit_test_finish_dialog_cancel_btn, null);
		builder.setPositiveButton(R.string.quit_test_finish_dialog_quit_btn, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int id) {
				checkFinishTest();
			}
		});
		builder.create().show();
	}
	
	private void showAutoFinishTestDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.quit_test_auto_finish_dialog_tittle);
		builder.setMessage(R.string.quit_test_auto_finish_dialog_message);
		builder.setPositiveButton(R.string.quit_test_auto_finish_dialog_confirm_btn, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				checkFinishTest();
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}

	private void checkFinishTest(){
		showProgressBar();
		mTestMode = TEST_MODE_PRACTICE;
		testStatus = TestHistoryItem.STATUS_FINISH;
		ToeicUtil.setPracticeMode(true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				resultArray = toeicDataController.onFinishTestListener();
				Date currentDate = new Date();
				String currentDateStr = sdf.format(currentDate);
				String filePath = currentDateStr + ".xml";
				toeicDataController.saveCurrentTest(mDataPath + filePath);
				if (historyItem == null){ //insert to history
					TestHistoryItem insertItem = new TestHistoryItem(bookID, testStatus, currentDate, filePath, mTestMode, timerCurrentTime, timerCurrentReadingTimer,activePart, currentTime);
					insertItem.setScore(resultArray);
					ContentResolver cr = mContext.getContentResolver();
					cr.insert(TestHistoryTableMetaData.CONTENT_URI, insertItem.getContentValues());
				}
				else {	//update to history
					ContentResolver cr = mContext.getContentResolver();
					Uri updateUri = ContentUris.withAppendedId(TestHistoryTableMetaData.CONTENT_URI, historyItem.get_id());
					int bookID = historyItem.getBookID();
					String fileName = historyItem.getFilePath();
					final String sourceFile = ExternalStorage.getSDCacheDir(getApplicationContext(), BookDataProcessingIntentService.UNZIP_FOLDER).toString() + "/" + bookID + "/" + fileName;
					historyItem.setStatus(testStatus);
					historyItem.setModifiedDate(currentDate);
					historyItem.setFilePath(filePath);
					historyItem.setCurrentTime(timerCurrentTime);
					historyItem.setActivePart(activePart);
					historyItem.setMediaCurrentTime(currentTime);
					historyItem.setCurrentReadingTime(timerCurrentReadingTimer);
					historyItem.setScore(resultArray);
					ContentValues updatedValues = historyItem.getContentValues();
					cr.update(updateUri, updatedValues, null, null);
					File oldFile = new File(sourceFile);
					oldFile.delete();
				}
				loadingProgressBar.post(new Runnable() {
					@Override
					public void run() {
						resetAudio();
						enableMediaPlayerController();
						hideProgressBar();
						timerHandler.removeCallbacks(timerUpdateActionBar);
						invalidateOptionsMenu();
						showResultDialog();
					}
				});
			}
	   }).start();
	}
	
	private void showResultDialog(){
		Bundle args = getResultBundle();
		args.putInt(ToeicResultDialog.ARGS_TYPE, ToeicResultDialog.ARGS_TYPE_REVIEWABLE);
		ToeicResultDialog dialog = new ToeicResultDialog();
		dialog.setCancelable(false);
		dialog.setArguments(args);
		dialog.show(getSupportFragmentManager(), "ToeicResultDialog");
	}
	
	private void showReviewResultDialog(){
		Bundle args = getResultBundle();
		args.putInt(ToeicResultDialog.ARGS_TYPE, ToeicResultDialog.ARGS_TYPE_NORMAL);
		ToeicResultDialog dialog = new ToeicResultDialog();
		dialog.setCancelable(false);
		dialog.setArguments(args);
		dialog.show(getSupportFragmentManager(), "ToeicResultDialog");
	}
	
	private Bundle getResultBundle(){
		int[] listeningResult = {resultArray[ToeicDataController.TOEIC_PART_1], resultArray[ToeicDataController.TOEIC_PART_2], 
				resultArray[ToeicDataController.TOEIC_PART_3], resultArray[ToeicDataController.TOEIC_PART_4]};
		int[] readingResult = {resultArray[ToeicDataController.TOEIC_PART_5], resultArray[ToeicDataController.TOEIC_PART_6], 
				resultArray[ToeicDataController.TOEIC_PART_7]};
		Bundle args = new Bundle();
		args.putIntArray(ToeicResultDialog.ARGS_LISTENING_RESULT, listeningResult);
		args.putIntArray(ToeicResultDialog.ARGS_READING_RESULT, readingResult);
		return args;
	}
	
	public void onShowResultDialogFinish(){
		finish();
	}
	
	public void onShowResultDialogReview(){
		if (activePart != ToeicDataController.TOEIC_PART_1){
			activePart = ToeicDataController.TOEIC_PART_1;
			getSupportActionBar().setSelectedNavigationItem(ToeicDataController.TOEIC_PART_1);
		}
		else{
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			Bundle args = new Bundle();
			args.putInt(ToeicFragmentListview.ARGS_PART_INDEX, ToeicDataController.TOEIC_PART_1);
			ToeicFragmentListview fragment =  new ToeicFragmentListview();
			fragment.setArguments(args);
			ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
			ft.replace(R.id.container, fragment, "fragment");
			ft.commit();
			fragmentMediaListener = fragment;
			startMedia();
		}
	}
		
	/****************************** quit or finish test end ****************************/
	/***********************************************************************************/
	/***********************************************************************************/
	
	/***********************************************************************************/
	/***********************************************************************************/
	/***************************** Dictionary loading  *********************************/
	
	private static class DictionaryLoadingTaskLoader extends AbstractCacheAsyncTaskLoader<DictionaryManager>{

		Context mContext; 
		
		public DictionaryLoadingTaskLoader(Context context) {
			super(context);
			mContext = context;
		}

		@Override
		public DictionaryManager loadInBackground() {
			ArrayList<String> listDictNames = new ArrayList<String>();
			ArrayList<String> listDictRealNames = new ArrayList<String>();
			String[] projection = new String[]{
												DictionaryItem.Columns.DICT_ID,
												DictionaryItem.Columns.TITLE,
											};
			StringBuilder selection = new StringBuilder();
			selection.append(DictionaryItem.Columns.STATUS).append(" = ? ");
			selection.append(" AND ").append(DictionaryItem.Columns.ACTIVE).append(" = ? ");
			
			String[] selectionArgs = new String[]{
				String.valueOf(DictionaryItem.STATUS_UNZIP_SUCESSFULL),
				String.valueOf(1),
			};
			
			ContentResolver cr = mContext.getContentResolver();
			Cursor cursor = cr.query(DictionaryItem.CONTENT_URI, projection, selection.toString(), selectionArgs, DictionaryItem.DEFAULT_SORT_ORDER);
			if (cursor != null){
				while(cursor.moveToNext()){
					String dictID = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.DICT_ID));
					String dictTitle = cursor.getString(cursor.getColumnIndex(DictionaryItem.Columns.TITLE));
					listDictNames.add(dictID);
					listDictRealNames.add(dictTitle);
				}
			}
			DictionaryManager dictionaryManager = new DictionaryManager(null, listDictNames, listDictRealNames, new WeakReference<Context>(mContext));
			return dictionaryManager;
		}
	}
	
	private class DictionaryLoadingTaskLoaderCallbacks implements LoaderCallbacks<DictionaryManager>{

		@Override
		public Loader<DictionaryManager> onCreateLoader(int id, Bundle bundle) {
			final DictionaryLoadingTaskLoader loader = new DictionaryLoadingTaskLoader(getApplicationContext());
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<DictionaryManager> loader, DictionaryManager dictionaryManager) {
			dictManager = dictionaryManager;
		}

		@Override
		public void onLoaderReset(Loader<DictionaryManager> loader) {
			dictManager = null;
		}
		
	}
		
	public void showDictDialog(String word){
		if (ToeicUtil.isPracticeMode() && dictManager != null){
			word = word.toLowerCase(Locale.ENGLISH);
			Bundle args = new Bundle();
			args.putString(ShowDictWordDialog.ARGS_WORD, word);
			ShowDictWordDialog dialog = new ShowDictWordDialog();
			dialog.setDelegate(this);
			dialog.setArguments(args);
			dialog.show(getSupportFragmentManager(), "ShowDictWordDialog");
		}
	}

	@Override
	public DictionaryManager getDictionaryManager() {
		return dictManager;
	}
	
	/***************************** Dictionary loading  *********************************/
	/***********************************************************************************/
	/***********************************************************************************/
	
	private void showDictionarySearchDialog(){
		if (ToeicUtil.isPracticeMode() && dictManager != null){
			DictionarySearchDialogFragment dialog = new DictionarySearchDialogFragment();
			dialog.setDelegate(this);
			dialog.setCancelable(false);
			dialog.show(getSupportFragmentManager(), "Show Dictionary Search Dialog");
		}
		else if (dictManager != null){
			showDictionaryUnavailableDialog();
		}
	}

	@Override
	public DictionaryManager getDictManager() {
		return dictManager;
	}
	
	private void showDictionaryUnavailableDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(R.string.toeic_dictionary_fragment_unvailable_title);
		builder.setMessage(R.string.toeic_dictionary_fragment_unvailable_message);
		builder.setNegativeButton(R.string.toeic_dictionary_fragment_unvailable_btn, null);
		builder.create().show();
	}
}
