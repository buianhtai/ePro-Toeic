package com.mZone.epro.dict.activity;

import com.mZone.epro.R;
import com.mZone.epro.client.task.DownloadManagerService;
import com.mZone.epro.dict.data.DictionaryItemServer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class DictionaryManagerActivity extends ActionBarActivity {

	private static final int POS_LOCAL_DICT_MANAGER_FRAGMENT = 0;
	private static final int POS_LANG_SELECTION_FRAGMENT = 1;
	private static final int POS_DICT_SELECTION_FRAGMENT = 2;

	private int fragmentPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dictionary_manager);
		initializeFragment();
	}
	
	@Override
	protected void onStart(){
		super.onStart();
        Intent intent = new Intent(this, DownloadManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		if (mIsBound) {
			unbindService(mConnection);
			mIsBound = false;
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dictionary_manager, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		MenuItem item = menu.findItem(R.id.action_add_dictionary);
		if (fragmentPosition != POS_LOCAL_DICT_MANAGER_FRAGMENT){
			item.setVisible(false);
		}
		else{
			item.setVisible(true);
		}
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_add_dictionary:
				moveToLanguageSelectionFragment();
				return true;
			case android.R.id.home:
				backPressed();
				return true;
			default:
        		return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		backPressed();
	}
	
	private void backPressed(){
		switch (fragmentPosition) {
			case POS_LOCAL_DICT_MANAGER_FRAGMENT:
				finish();
				break;
			case POS_LANG_SELECTION_FRAGMENT:
				initializeFragment();
				invalidateOptionsMenu();
				break;
			case POS_DICT_SELECTION_FRAGMENT:
				moveToLanguageSelectionFragment();
				break;
			default:
				break;
		}
	}
	
	private void initializeFragment(){
		fragmentPosition = POS_LOCAL_DICT_MANAGER_FRAGMENT;
		final FragmentManager fragmentManager = getSupportFragmentManager();
		LocalDictionaryFragment fragment = new LocalDictionaryFragment();
		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.container, fragment, "fragment").commit();
		getActionBar().setTitle(R.string.title_activity_dictionary_manager_manager);
	}
	
	public void moveToLanguageSelectionFragment(){
		invalidateOptionsMenu();
		final FragmentManager fragmentManager = getSupportFragmentManager();
		ServerDictionaryLanguageSelectionFragment fragment = new ServerDictionaryLanguageSelectionFragment();
		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
          .replace(R.id.container, fragment, "fragment").commit();
		fragmentPosition = POS_LANG_SELECTION_FRAGMENT;
		getActionBar().setTitle("Add new dictionary");
	}
	
	public void moveToDictionarySelectionFragment(Bundle args){
		final FragmentManager fragmentManager = getSupportFragmentManager();
		ServerDictionarySelectionFragment fragment = new ServerDictionarySelectionFragment();
		fragment.setArguments(args);
		fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        .replace(R.id.container, fragment, "fragment").commit();
		fragmentPosition = POS_DICT_SELECTION_FRAGMENT;
		getActionBar().setTitle("Add new dictionary");
	}
	
 	/************************** Donwload Service start **********************/
	/************************************************************************/
	/************************************************************************/
		
	private DownloadManagerService mBoundService;
	boolean mIsBound = false;
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
	    public void onServiceConnected(ComponentName className, IBinder service) {
	        mBoundService = ((DownloadManagerService.LocalBinder)service).getService();
//	        mBoundService.reprocessAllData();
	        mIsBound = true;
	    }

		@Override
	    public void onServiceDisconnected(ComponentName className) {
	        mIsBound = false; 
	    }
	};
	
	public void delegateStartDownloadDictionary(DictionaryItemServer dict){
		if (mIsBound){
			mBoundService.startDownloadDictionary(dict);
		}
	}
	
	/************************************************************************/
	/************************************************************************/
	/************************** Donwload Service stop ***********************/

}
