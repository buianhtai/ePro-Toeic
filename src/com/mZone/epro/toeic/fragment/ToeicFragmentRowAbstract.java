package com.mZone.epro.toeic.fragment;

import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.customInterface.ToeicSingleCheckResultObserver;
import com.mZone.epro.toeic.data.ToeicDataController;

import android.support.v4.app.Fragment;

public abstract class ToeicFragmentRowAbstract extends Fragment implements ToeicSingleCheckResultObserver{
	protected int rowPosition = -1;
	protected int partIndex = -1;
	protected ToeicDataController dataController;
	int rowID;
	protected ToeicMediaListener mediaListener = null;
	public ToeicFragmentRowAbstract() {
		// TODO Auto-generated constructor stub
	}
	public ToeicFragmentRowAbstract(int partIndex) {
		// TODO Auto-generated constructor stub
		this.partIndex = partIndex;
		dataController = ToeicDataController.getInstance();
	}
	
	public ToeicFragmentRowAbstract(int partIndex, ToeicMediaListener listener) {
		// TODO Auto-generated constructor stub
		this.partIndex = partIndex;
		this.mediaListener = listener;
		dataController = ToeicDataController.getInstance();
	}
	
	abstract void rowChanged(int rowID);
}
