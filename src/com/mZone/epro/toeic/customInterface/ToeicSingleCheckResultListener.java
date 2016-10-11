package com.mZone.epro.toeic.customInterface;

public interface ToeicSingleCheckResultListener {
	public void registerObserver(ToeicSingleCheckResultObserver o);
	public void removeObserver(ToeicSingleCheckResultObserver o);
	public void notifyObserverArray(int rowID);
}
