package com.mZone.epro;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mZone.epro.R;
import com.mZone.epro.account.AccountActivity;
import com.mZone.epro.client.data.BookItem;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookItemServer;
import com.mZone.epro.client.data.ClientDataController;
import com.mZone.epro.client.data.TransactionParameter;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.task.DownloadManagerService;
import com.mZone.epro.client.utility.AESHelper;
import com.mZone.epro.client.utility.AbstractCacheAsyncTaskLoader;
import com.mZone.epro.client.utility.Constants;
import com.mZone.epro.client.utility.DesSecurity;
import com.mZone.epro.client.utility.ExternalStorage;
import com.mZone.epro.client.utility.JSONParser;
import com.mZone.epro.client.utility.MyPreference;
import com.mZone.epro.client.utility.MyPreference.Account;
import com.mZone.epro.client.utility.Utils;
import com.mZone.epro.dict.activity.DictionaryManagerActivity;
import com.mZone.epro.dict.activity.ServerDictionaryLanguageSelectionFragment;
import com.mZone.epro.dict.data.DictionaryClientDataController;
import com.mZone.epro.dict.data.DictionaryItemServer;
import com.mZone.epro.launch.fragment.LaunchAboutFragment;
import com.mZone.epro.launch.fragment.LaunchDictionaryFragment;
import com.mZone.epro.launch.fragment.LaunchHistoryFragment;
import com.mZone.epro.launch.fragment.LaunchHomeFragment;
import com.mZone.epro.launch.fragment.LaunchHomeFragmentTabHost;
import com.mZone.epro.launch.fragment.LaunchStoreFragment;
import com.mZone.epro.testhisotry.data.TestHistoryItem;
import com.mZone.epro.toeic.activity.ToeicTestActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LaunchActivity extends EproBaseActivity implements View.OnClickListener, LoaderCallbacks<Cursor>{	
	
	private ClientDataController dataController = ClientDataController.getInstance();
	
	//side menu
	private DrawerLayout mDrawerLayout;
	private View mDrawerMenu;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerListView;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private View mAccountLayout;
	private TextView mAccountTextview;
	
	//loading bar
	private View mLoadingBar;
//	private ProgressBar loadingProgressBar;
//	private TextView loadingTextview;
	private int mLoadingBarHeight;
	private float mLoadingBarHidePosition;

	//fragment
	private int currentFragment;
	public static final int HOME_FRAGMENT = 0;
	public static final int STORE_FRAGMENT = 1;
	public static final int HISTORY_FRAGMENT = 2;
	public static final int DICTIONARY_FRAGMENT = 3;
	public static final int ABOUT_FRAGMENT = 4;
	
	//action bar item
	private MenuItem menuStoreHomeSwitch;
	private MenuItem menuRefresh;
	
	//double back to exit
	private boolean doubleBackToExitPressedOnce = false;
	private int doubleBackToExitPressedOnceTime = 2000;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction = intent.getAction();
			if (intentAction.equals(AccountActivity.ACCOUNT_CHANGE_BROADCAST_INTENT)){
				setAccountTextview();
				animateLoadingBarOnLoading();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
//		getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_launch);
//		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.argb(125, 0, 0, 0)));

		//load local book from database
		final LoaderManager loaderManager = getSupportLoaderManager();
		loaderManager.initLoader(1, null, this);
		
		//set activity context for ClientDataController
		ClientDataController.getInstance().setContext(getApplicationContext());
		
		//setup side menu
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mTitle = mDrawerTitle = getTitle();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) 
        {
            @Override
			public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
			public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerMenu = findViewById(R.id.left_drawer);
        mDrawerListView = (ListView)findViewById(R.id.left_drawer_lv);
        mDrawerListView.setAdapter(new SideMenuListviewAdapter(this));
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());
        if (savedInstanceState == null) {
            selectItem(0);
        }
        mAccountLayout = findViewById(R.id.left_drawer_account_layout);
        mAccountLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawers();
				startAccountActivity();
			}
		});
        mAccountTextview = (TextView)findViewById(R.id.left_drawer_account_tv);
        setAccountTextview();
        
        //loading bar
        mLoadingBar = findViewById(R.id.loadingBar);
        ViewTreeObserver vto = mLoadingBar.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				mLoadingBarHeight = mLoadingBar.getHeight();
				mLoadingBarHidePosition = mLoadingBarHeight + mLoadingBar.getY();
				mLoadingBar.setY(mLoadingBarHidePosition);
				mLoadingBar.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				animateLoadingBarOnLoading();
			}
		});

        //load the home fragment
        initFragment();
        
        //start download manager service
        if (!isMyServiceRunning(getApplicationContext())){
			startService(new Intent(this, DownloadManagerService.class));
		}
        
        Intent intent = new Intent(this, DownloadManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        
        //account change receiver register
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(AccountActivity.ACCOUNT_CHANGE_BROADCAST_INTENT));
	}
	
	/**
	 * Load home fragment on activity create
	 */
	private void initFragment(){
    	LaunchHomeFragment fragment = new LaunchHomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        currentFragment = HOME_FRAGMENT;
    }
	
	private void setAccountTextview(){
		String accountStr = null;
		Account account = MyPreference.getAccount(getApplicationContext());
		if (account != null){
			accountStr = account.account;
		}
		if (TextUtils.isEmpty(accountStr)){
			mAccountTextview.setText(R.string.sidemenu_no_account);
		}
		else{
			mAccountTextview.setText(accountStr);
		}
	}
	
	@Override
    public void onStart() {
        super.onStart();
        /*
        Intent intent = new Intent(this, DownloadManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        */
    }
	
	@Override
	public void onStop() {
		super.onStop();
		/*
		super.onStop();
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
	    }
	    */
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
	    }
	}
	
	@Override
	public void onBackPressed() {
		if (currentFragment != HOME_FRAGMENT){
			currentFragment = HOME_FRAGMENT;
			FragmentManager fragmentManager = getSupportFragmentManager();
			LaunchHomeFragment homeFragment = new LaunchHomeFragment();
		 	fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
			                    .replace(R.id.content_frame, homeFragment, "fragment").commit();
		 	mDrawerListView.setItemChecked(currentFragment, true);
		 	invalidateOptionsMenu();
			return;
		}
		
	    if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, getResources().getString(R.string.launch_activity_backpress_exit), Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, doubleBackToExitPressedOnceTime);
	} 
	 
	/************************** Loader Start ********************************/
	/************************************************************************/
	/************************************************************************/
	@Override
	public Loader<Cursor> onCreateLoader(int loaderID, Bundle bundle) {
		String[] projection = new String[]{
				String.valueOf(LocalBookTableMetaData._ID),
				String.valueOf(LocalBookTableMetaData.BOOK_ID),
				String.valueOf(LocalBookTableMetaData.BOOK_NAME),
				String.valueOf(LocalBookTableMetaData.BOOK_AUTHOR),
				String.valueOf(LocalBookTableMetaData.SECURITY_NAME),
				String.valueOf(LocalBookTableMetaData.COVER_IMAGE),
			};
		StringBuilder selection = new StringBuilder();
		selection.append(LocalBookTableMetaData.STATUS).append(" = ? ");
		
		String[] selectionArgs = new String[]{
			String.valueOf(BookItemLocal.STATUS_UNZIP_SUCESSFULL),
		};
		CursorLoader cursorLoader = new CursorLoader(this, LocalBookTableMetaData.CONTENT_URI, projection, selection.toString(), selectionArgs, LocalBookTableMetaData.DEFAULT_SORT_ORDER);
		return cursorLoader;
	}
		
	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
		HashMap<Integer, BookItemLocal> localBookMap = new HashMap<Integer, BookItemLocal>();
		ArrayList<Integer> localBookArray = new ArrayList<Integer>();
		
		if (cursor != null){
			int count = cursor.getCount();
			if (cursor.moveToFirst()){
				do{
					BookItemLocal item = new BookItemLocal(cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_ID)), 
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_NAME)), 
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.BOOK_AUTHOR)), 
							cursor.getInt(cursor.getColumnIndex(LocalBookTableMetaData._ID)),
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.COVER_IMAGE)),
							cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.SECURITY_NAME)));
					localBookMap.put(item.getBookID(), item);
					localBookArray.add(item.getBookID());
				}
				while (cursor.moveToNext());
			}
			if (dataController.getLocalBookArray().size() != count){
				dataController.swapCursor(localBookMap, localBookArray);
				if (currentFragment == HOME_FRAGMENT){
					final LaunchHomeFragment fragment = (LaunchHomeFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
					fragment.swapCursor();
				}
				else if (currentFragment == STORE_FRAGMENT){
					final LaunchStoreFragment fragment = (LaunchStoreFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
					fragment.swapCursor();
				}

			}	
		}
	}
		
	@Override
	public void onLoaderReset(Loader<Cursor> cursor) {
	}	
	
	/************************************************************************/
	/************************************************************************/
	/************************** Loader Finish *******************************/
	
	
	/************************** Action bar menu start ************************/
	/************************************************************************/
	/************************************************************************/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.launch, menu);
        menuStoreHomeSwitch = menu.findItem(R.id.launch_action_home_store_change);
        menuRefresh = menu.findItem(R.id.launch_action_refresh);
        return super.onCreateOptionsMenu(menu);
    }
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerMenu);
    	if (drawerOpen){
    		menuStoreHomeSwitch.setVisible(false);
    		menuRefresh.setVisible(false);
    	}
    	if (currentFragment != HOME_FRAGMENT && currentFragment != STORE_FRAGMENT){
    		menuRefresh.setVisible(false);
    		if (currentFragment != DICTIONARY_FRAGMENT){
    			menuStoreHomeSwitch.setVisible(false);
    		}
    	}
    	
    	switchActionBarItem();
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	//Action bar item is clicked
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        	case R.id.launch_action_home_store_change:
        		if (currentFragment == HOME_FRAGMENT || currentFragment == STORE_FRAGMENT){
        			switchHomeStore(0);
        		}
        		else{
	  		 		Intent intent = new Intent(this, DictionaryManagerActivity.class);
	  		 		startActivity(intent);
        		}
        		return true;
        	case R.id.launch_action_refresh:
        		animateLoadingBarOnLoading();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
    
	 public void switchActionBarItem(){
		 if (currentFragment == HOME_FRAGMENT){
			 menuStoreHomeSwitch.setIcon(R.drawable.ic_store);
			 menuStoreHomeSwitch.setTitle(getResources().getString(R.string.launch_menu_action_store));
		 }
		 else if (currentFragment == STORE_FRAGMENT){
			 menuStoreHomeSwitch.setIcon(R.drawable.ic_home);
			 menuStoreHomeSwitch.setTitle(getResources().getString(R.string.launch_menu_action_home));
		 }
		 else{
			 menuStoreHomeSwitch.setIcon(R.drawable.ic_action_settings);
			 menuStoreHomeSwitch.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT | MenuItem.SHOW_AS_ACTION_ALWAYS);
			 menuStoreHomeSwitch.setTitle(getResources().getString(R.string.launch_menu_action_setting_dictionary));
		 }
	 }
	 
	 public void switchHomeStore(int position){
		 FragmentManager fragmentManager = getSupportFragmentManager();
		 if (currentFragment == HOME_FRAGMENT){
			 currentFragment = STORE_FRAGMENT;
			 Bundle args = new Bundle();
			 args.putInt(LaunchStoreFragment.ARGS_START_INDEX, position);
			 LaunchStoreFragment storeFragment = new LaunchStoreFragment();
			 storeFragment.setArguments(args);
			 fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
			                    .replace(R.id.content_frame, storeFragment, "fragment").commit();
		 }
		 else{
			 currentFragment = HOME_FRAGMENT;
			 LaunchHomeFragment homeFragment = new LaunchHomeFragment();
			 fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
			                    .replace(R.id.content_frame, homeFragment, "fragment").commit();
		 }
		 mDrawerListView.setItemChecked(currentFragment, true);
		 switchActionBarItem();
	 }

    
    /************************************************************************/
	/************************************************************************/
	/************************** Actionbar menu Finish ***********************/
    
    /************************** Side menu start *****************************/
	/************************************************************************/
	/************************************************************************/
    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
  	private class DrawerItemClickListener implements ListView.OnItemClickListener {
  		@Override
  		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
  			selectItem(position);
  		}
  	}
  	private void selectItem(int position) {
  		mDrawerListView.setItemChecked(position, true);
  		mDrawerListView.setSelected(true);
  		switchFragment(position);
  		mDrawerLayout.closeDrawers();	 
 	} 
  	 
 	private void switchFragment(int position){
  		 if (currentFragment == position) return;
  		 currentFragment = position;
  		 FragmentManager fragmentManager = getSupportFragmentManager();
  		 switch(position){
  		 	case HOME_FRAGMENT:
  		 		LaunchHomeFragment homeFragment = new LaunchHomeFragment();
  		 		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
  			                    .replace(R.id.content_frame, homeFragment, "fragment").commit();
  		 		break;
  		 	case STORE_FRAGMENT:
  		 		LaunchStoreFragment storeFragment = new LaunchStoreFragment();
  			    fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
  			    			   .replace(R.id.content_frame, storeFragment, "fragment").commit();
  		 		break;
  		 	case HISTORY_FRAGMENT:
  		 		Fragment historyFragment = new LaunchHistoryFragment();
  		 		Bundle args = new Bundle();
  		 		historyFragment.setArguments(args);
  		 		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
  		 					   .replace(R.id.content_frame, historyFragment, "fragment").commit();
  		 		break;
  		 	case DICTIONARY_FRAGMENT:
  		 		//Load dictionary fragment instead
  		 		Fragment dictionaryFragment = new LaunchDictionaryFragment();
  		 		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
  		 					   .replace(R.id.content_frame, dictionaryFragment, "fragment").commit();
  		 		break;
  		 	case ABOUT_FRAGMENT:
//  		 		Fragment aboutFragment = new LaunchAboutFragment();
//  		 		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//  		 					   .replace(R.id.content_frame, aboutFragment, "fragment").commit();
  		 		Fragment tabhostFragment = new LaunchHomeFragmentTabHost();
  		 		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
  		 					   .replace(R.id.content_frame, tabhostFragment, "fragment").commit();

  		 		break;
  		 	default:
  		 		break;
  		 }
  	 }
 	
	 private void startAccountActivity(){
		 Intent intent = new Intent(this, AccountActivity.class);
		 startActivity(intent);
	 }


 	/************************************************************************/
	/************************************************************************/
	/************************** Side menu Finish ****************************/
 	
 	/************************** Donwload Service start **********************/
	/************************************************************************/
	/************************************************************************/
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
		mDrawerLayout.closeDrawers();
	}
	
	private boolean isMyServiceRunning(Context mContext) {
	    ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (DownloadManagerService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private DownloadManagerService mBoundService;
	boolean mIsBound = false;
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mBoundService = ((DownloadManagerService.LocalBinder)service).getService();
	        mBoundService.reprocessAllData();
	        mIsBound = true;
	    }

		@Override
	    public void onServiceDisconnected(ComponentName className) {
	        mIsBound = false; 
	    }
	};
	
	public void delegateStartDownloadTransaction(TransactionParameter parameters){
		if (mIsBound){
			mBoundService.startDownloadBookItem(parameters);
		}
	}
	
	/************************************************************************/
	/************************************************************************/
	/************************** Donwload Service stop ***********************/
	 
	 
	/************************************************************************/
	/************************************************************************/
	/***************************** Open test Start***************************/

	 /**
	  * User want to open test
	  * @param item: BookItem
	  */
	 public void openTest(BookItemLocal item){		 
		 
		 DesSecurity des = new DesSecurity();
		 String desBookID = null;
		 try {
			 desBookID = des.Decrypt(item.getSecurityName(), Utils.getDeviceImei(getApplicationContext()), "DESede");
		 } catch (Exception e) {
			 e.printStackTrace();
		 }	
		 if (desBookID != null){
			 if (item.getBookID() == Integer.valueOf(desBookID)){
				 //open the dialog for mode choosing here
				 TestOpenDialog testDialog = new TestOpenDialog(item, getApplicationContext());
				 testDialog.show(getSupportFragmentManager(), "TestOpenDialog");
			 }
			 else{
				//can not Decrypt the security name
			 }
		 }
		 else{
			 //can not Decrypt the security name
		 }
		 
	 }
	 
	 /**
	  * TestOpenDialog
	  * @author Tony Huynh
	  *
	  */
	 static public class TestOpenDialog extends DialogFragment {
		 private BookItemLocal item;
		 public TestOpenDialog(BookItemLocal item, Context c){
			 this.item = item;
		 }
		 @Override
		 public Dialog onCreateDialog(Bundle savedInstanceState) {
			 // Use the Builder class for convenient dialog construction
			 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			 builder.setTitle(R.string.test_open_dialog_tittle);
			 builder.setItems(R.array.test_open_dialog_items, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					switch (which) {
						case ToeicTestActivity.TEST_MODE_NORMAL:
							File openDir = ExternalStorage.getSDCacheDir(getActivity(), "unzipped/" + item.getBookID());
				 			Intent intent = new Intent(getActivity(), ToeicTestActivity.class);
							intent.putExtra(ToeicTestActivity.TEST_MODE_ARG, ToeicTestActivity.TEST_MODE_NORMAL);
							intent.putExtra(ToeicTestActivity.TEST_PATH_ARG, openDir.getAbsolutePath() + "/");
							intent.putExtra(ToeicTestActivity.BOOK_ID_ARG, item.getBookID());
							startActivity(intent);
							break;
						case ToeicTestActivity.TEST_MODE_PRACTICE:
							openDir = ExternalStorage.getSDCacheDir(getActivity(), "unzipped/" + item.getBookID());
				 			intent = new Intent(getActivity(), ToeicTestActivity.class);
							intent.putExtra(ToeicTestActivity.TEST_MODE_ARG, ToeicTestActivity.TEST_MODE_PRACTICE);
							intent.putExtra(ToeicTestActivity.TEST_PATH_ARG, openDir.getAbsolutePath() + "/");
							intent.putExtra(ToeicTestActivity.BOOK_ID_ARG, item.getBookID());
							startActivity(intent);
							break;
						default:
							break;
					}
				}
			});
			 return builder.create();
		 }
	 }
	 
	 
	 /**
	  * Open test from history
	  * @param historyItem
	  */
	 public void openTest(TestHistoryItem historyItem){
		 Uri uri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, historyItem.getBookRowID());
		 String[] projection = new String[]{
				 String.valueOf(LocalBookTableMetaData.SECURITY_NAME),
		 };
		 ContentResolver cr = getContentResolver();
		 Cursor cursor = cr.query(uri, projection, null, null, null);
		 if (cursor.moveToNext()){
			 String securityName = cursor.getString(cursor.getColumnIndex(LocalBookTableMetaData.SECURITY_NAME));
			 if (securityName != null){
				 DesSecurity des = new DesSecurity();
				 String desBookID = null;
				 try {
					 desBookID = des.Decrypt(securityName, Utils.getDeviceImei(getApplicationContext()), "DESede");
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
				 if (desBookID != null){
					 if (historyItem.getBookID() == Integer.valueOf(desBookID)){
						 //open the dialog for mode choosing here
						 File openDir = ExternalStorage.getSDCacheDir(getApplicationContext(), "unzipped/" + historyItem.getBookID());
				 		 Intent intent = new Intent(getApplicationContext(), ToeicTestActivity.class);
						 intent.putExtra(ToeicTestActivity.TEST_MODE_ARG, historyItem.getMode());
						 intent.putExtra(ToeicTestActivity.TEST_PATH_ARG, openDir.getAbsolutePath() + "/");
						 intent.putExtra(ToeicTestActivity.BOOK_ID_ARG, historyItem.getBookID());
						 intent.putExtra(ToeicTestActivity.TEST_HISTORY_PARCEL_ARG, historyItem);
						 startActivity(intent);
					 }
					 else{
						//can not Decrypt the security name
					 }
				 }
				 else{
					 //can not Decrypt the security name
				 }
			 }
		 }
	 }
	 
	 /***************************** Open test End***************************/
	 /************************************************************************/
	 /************************************************************************/

	 
	 
	 /*********************** Load book from server start ********************/
	 /************************************************************************/
	 /************************************************************************/
	 
	 /**
	  * animate bottom loading bar and start taskloader
	  */
	 private void animateLoadingBarOnLoading(){	
		 setupUIBeforeLoadingBookFromServer();
		 mLoadingBar.setY(mLoadingBarHidePosition);
		 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			 mLoadingBar.animate().setDuration(500).translationYBy(-mLoadingBarHeight).setListener(new AnimatorListenerAdapter(){
				 @Override
				 public void onAnimationEnd(Animator animation) {
					 startGetBookFromServerTaskLoader();
				 }
			 });
		 }
		 else{
			 mLoadingBar.animate().setDuration(500).translationYBy(-mLoadingBarHeight).withEndAction(new Runnable() {
				 @Override
				 public void run() {
					 startGetBookFromServerTaskLoader();
				 }
			 });
		 }
	 }
	 
	 /**
	  * setup fragment UI before loading
	  */
	 private void setupUIBeforeLoadingBookFromServer(){
		 if (currentFragment == HOME_FRAGMENT){
			 final LaunchHomeFragment fragment = (LaunchHomeFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
			 fragment.onPrepareLoadingBookFromServer();
		 }
	 }
	 
	 /**
	  * close loading bar and swap data
	  * @param result
	  */
	 private void animateLoadingBarOnFinish(final Integer result){	
		 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			 mLoadingBar.animate().setDuration(500).translationYBy(mLoadingBarHeight).setListener(new AnimatorListenerAdapter(){
				 @Override
				 public void onAnimationEnd(Animator animation) {
					 mLoadingBar.setY(mLoadingBarHidePosition);
					 setupUIAfterLoadingBookFromServer(result);
				 }
			 });
		 }
		 else{
			 mLoadingBar.animate().setDuration(500).translationYBy(mLoadingBarHeight).withEndAction(new Runnable() {
				 @Override
				 public void run() {
					 mLoadingBar.setY(mLoadingBarHidePosition);
					 setupUIAfterLoadingBookFromServer(result);
				 }
			 });
		 }
	 }
	 
	 /**
	  * refresh ui on fragment
	  * @param result
	  */
	 private void setupUIAfterLoadingBookFromServer(final Integer result){
		 if (result != null && result == GetBookFromServerTaskLoader.STATUS_SUCCESS){
			 FragmentManager fragmentManager = getSupportFragmentManager();
			 if (fragmentManager != null){
				 if (currentFragment == HOME_FRAGMENT){
					 final LaunchHomeFragment fragment = (LaunchHomeFragment) fragmentManager.findFragmentById(R.id.content_frame);
					 fragment.onLoadingBookFromServerFinish();
				 }
				 else if (currentFragment == STORE_FRAGMENT){
					 final LaunchStoreFragment fragment = (LaunchStoreFragment) fragmentManager.findFragmentById(R.id.content_frame);
					 fragment.onLoadingBookFromServerFinish();
				 }
				 showDictionaryRequestDialog();
			 }
		 }
		 else if (!Utils.isNetworkAvailable(this)){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.network_error_dialog_tittle);
				builder.setMessage(R.string.network_error_dialog_message)
					.setPositiveButton(R.string.network_error_dialog_position_btn, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							(LaunchActivity.this).startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
			            }
					})
			        .setNegativeButton(R.string.network_error_dialog_negative_btn, null);
				builder.create().show();
		 }
	 }
	 
	 /**
	  * Start TaskLoader
	  */
	 private void startGetBookFromServerTaskLoader(){
		 final LoaderManager loaderManager = getSupportLoaderManager();
		 final GetBookFromServerLoaderCallback callback = new GetBookFromServerLoaderCallback();
		 loaderManager.restartLoader(R.id.get_book_from_server_task_loader_id, null, callback);
	 }
	 
	 /**
	  * TaskLoader load book from database in background
	  * Save Result in ClientDataController instance
	  * Return the result of loading (Success or Error)
	  * @author Tony Huynh
	  *
	  */
	 public static class GetBookFromServerTaskLoader extends AbstractCacheAsyncTaskLoader<Integer>{
		
		public static final int STATUS_SUCCESS = 1;
		public static final int STATUS_ERROR = 0;
		
		private Context mAppContext;
		
		public GetBookFromServerTaskLoader(final Context context) {
			super(context);
			mAppContext = context.getApplicationContext();
		}

		@Override
		public Integer loadInBackground() {
			Integer searchResult = STATUS_ERROR;
			// create JSON parameters
			JSONParser jsonParser = new JSONParser();
			List<NameValuePair> paramsSearch = new ArrayList<NameValuePair>();
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_BOOKTYPE, "1"));
			String imei = Utils.getDeviceImei(mAppContext);
			paramsSearch.add(new BasicNameValuePair(Constants.PARAM_KEY_DEVICE, imei));
			String account = MyPreference.getCurrentAccount(mAppContext);
			if (!TextUtils.isEmpty(account)){
				paramsSearch.add(new BasicNameValuePair(Constants.PARAM_USER_ID, account));
			}
			//make http request
			JSONObject jsonSearch = jsonParser.makeHttpRequest(AESHelper.URLDescript(Constants.url_searchBook), "GET", paramsSearch);
			
			//parse result
			try {
	        	if (jsonSearch != null) {
	            	searchResult = jsonSearch.getInt(Constants.PARAM_SUCCESS_001);
					if (searchResult == 1) {
	    				// successfully search
	    				JSONArray searchResultObj = jsonSearch.getJSONArray(Constants.PARAM_TOEIC);// JSON Array
	    				int count = 0;
	    				HashMap<Integer, BookItemServer> itemMap = new HashMap<Integer, BookItemServer>();
	    				ArrayList<Integer> itemArray = new ArrayList<Integer>();
						for (int i=0; i<searchResultObj.length(); i++) {
							JSONObject arrElement = searchResultObj.getJSONObject(i);
							BookItemServer bookItem = new BookItemServer(arrElement.getInt(Constants.PARAM_BOOK_ID), 
									arrElement.getString(Constants.PARAM_BOOK_NAME) + "-" + arrElement.getString(Constants.PARAM_SUBNAME), 
									arrElement.getString(Constants.PARAM_AUTHOR), BookItem.BOOK_TYPE_TOEIC, 
									Float.valueOf(arrElement.getString(Constants.PARAM_PRICE_0)), 
									arrElement.getString(Constants.PARAM_IMAGE_LINK), 
									arrElement.getString(Constants.PARAM_FILE_LINK), 
									arrElement.getInt(Constants.PARAM_DOWNLOAD_REQUIRE));
							String updateDate = arrElement.getString(Constants.PARAM_UPDATE_TIME);
							bookItem.setUpdateDate(updateDate);
	    					count = count + 1;
	    					itemMap.put(bookItem.getBookID(), bookItem);
	    					itemArray.add(bookItem.getBookID());
						}
						if (count == searchResultObj.length()){
							ClientDataController dataController = ClientDataController.getInstance();
							dataController.addBookItemsFromServer(itemMap, itemArray);
						}
					}
				} 
			} 
			catch (JSONException e) {
		           e.printStackTrace();
		    }
			
			//dictionary data load
			paramsSearch = new ArrayList<NameValuePair>();
			jsonParser = new JSONParser();
			JSONArray dictionaryArray = jsonParser.makeHttpRequestArray(AESHelper.URLDescript(Constants.url_dictionary), "GET", paramsSearch);
			if (dictionaryArray != null){
				try {
					HashMap<String, ArrayList<String>> languageMap =  new HashMap<String, ArrayList<String>>();
					HashMap<String, DictionaryItemServer> dictionaryMap = new HashMap<String, DictionaryItemServer>();
					for (int i = 0; i < dictionaryArray.length(); i++){
						JSONObject dictionary = dictionaryArray.getJSONObject(i);
						String id = dictionary.getString(DictionaryItemServer.DICT_ID);
						String title = dictionary.getString(DictionaryItemServer.DICT_TITLE);
						String description = dictionary.getString(DictionaryItemServer.DICT_DES);
						JSONArray langs = dictionary.getJSONArray(DictionaryItemServer.DICT_LANG);
						String lang = langs.getString(langs.length() - 1);
						String url = dictionary.getString(DictionaryItemServer.DICT_URL);
						DictionaryItemServer item = new DictionaryItemServer(id, title, description, lang, url);
						dictionaryMap.put(id, item);
						ArrayList<String> ids = languageMap.get(lang);
						if (ids == null){
							ids = new ArrayList<String>();
						}
						ids.add(id);
						languageMap.put(lang, ids);
					}
					DictionaryClientDataController.getInstance().setDataFromServer(languageMap, dictionaryMap);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			return searchResult;
		}
	 }
	 
	 /**
	  * Callback from task loader
	  * @author Tony Huynh
	  *
	  */
	 private class GetBookFromServerLoaderCallback implements LoaderCallbacks<Integer>{

		@Override
		public Loader<Integer> onCreateLoader(final int id, final Bundle args) {
			//init loader
//			ClientDataController clientInstance = ClientDataController.getInstance();
//			clientInstance.resetImageLoader();
			final GetBookFromServerTaskLoader loader = new GetBookFromServerTaskLoader(LaunchActivity.this);
			return loader;
		}

		@Override
		public void onLoadFinished(final Loader<Integer> loader, Integer result) {
			animateLoadingBarOnFinish(result);			
		}

		@Override
		public void onLoaderReset(final Loader<Integer> loader) {			
		} 
	 }
	 
	 /************************************************************************/
	 /************************************************************************/
	 /*********************** Load book from server stop ********************/
	 
	private void showDictionaryRequestDialog(){
		if (MyPreference.getAppFirstLoadToken(getApplicationContext()) == 0 && DictionaryClientDataController.getInstance().getLanguagesArray().length != 0){
			String languageCode = getResources().getString(R.string.app_first_load_language_code);
			MyPreference.setAppFirstLoadToken(getApplicationContext());
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.app_first_load_dictionary_download_dialog_tittle);
			
			if (TextUtils.isEmpty(ServerDictionaryLanguageSelectionFragment.languageMap.get(languageCode))){
				//Choose Language
				final String[] languageArray = DictionaryClientDataController.getInstance().getLanguagesArray();
				ArrayList<String> languageTitles = new ArrayList<String>();
				for (int i = 0; i < languageArray.length; i++){
					languageTitles.add("English - " + ServerDictionaryLanguageSelectionFragment.languageMap.get(languageArray[i]));
				}
				String[] languageTitlesArray = languageTitles.toArray(new String[languageTitles.size()]);
				dialog.setItems(languageTitlesArray, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						// TODO Auto-generated method stub
						String languageCode = languageArray[which];
						DictionaryItemServer dict = DictionaryClientDataController.getInstance().appFirstLoadGetDictionary(languageCode);
						if (dict != null){
							startDownloadDictionary(dict);
						}
					}
				});
			}
			else{
				//Download Direct
				final DictionaryItemServer dict = DictionaryClientDataController.getInstance().appFirstLoadGetDictionary(languageCode);
				String message = getResources().getString(R.string.app_first_load_dictionary_download_dialog_message);
				message += dict.title;
				dialog.setMessage(message);
				dialog.setPositiveButton(R.string.app_first_load_dictionary_download_dialog_download_btn, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (dict != null){
							startDownloadDictionary(dict);
						}
					}
				});
			}
			dialog.setNegativeButton(R.string.app_first_load_dictionary_download_dialog_cancel_btn, null);
			dialog.create().show();
		}
	}
	
	public void startDownloadDictionary(DictionaryItemServer dict){
		if (mIsBound){
			mBoundService.startDownloadDictionary(dict);
			String toastMessage = getResources().getString(R.string.app_first_load_dictionary_download_start_download_toast_message);
			Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT).show();
		}
	}
}
