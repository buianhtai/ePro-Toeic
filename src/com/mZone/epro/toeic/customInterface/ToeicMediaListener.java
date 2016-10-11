package com.mZone.epro.toeic.customInterface;

public interface ToeicMediaListener {
	public void registerObserver(ToeicMediaListenerObserver o);
	public void removeObserver(ToeicMediaListenerObserver o);
	public void notifyObserver();
	public void onCurrentTimeChanged(long currentTime);
	public int getScriptCurrentRowID();
	public int getScriptCurrentSubID();
	public void scrollToRowID(int rowID);
	public void scrollToRowIDWithDelayUntilViewCreated(int rowID);
}
