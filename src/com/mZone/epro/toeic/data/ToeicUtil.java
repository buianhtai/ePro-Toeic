package com.mZone.epro.toeic.data;

import android.graphics.Color;

public class ToeicUtil {
	static boolean isHighlight = false;
	static boolean practiceMode = true;
	static boolean isClickable = false;
	public static int highlightColor = Color.YELLOW;
	public static int transparentColor = 0x00000000;
	public static int wordClickColor = 0xFF9FD0FF;	
	
	public ToeicUtil() {
		// TODO Auto-generated constructor stub
	}
	public static boolean isHighlight() {
		return isHighlight;
	}
	public static void setHighlight(boolean isHighlight) {
		ToeicUtil.isHighlight = isHighlight;
	}
	public static boolean isPracticeMode() {
		return practiceMode;
	}
	public static void setPracticeMode(boolean practiceMode) {
		ToeicUtil.practiceMode = practiceMode;
	}
	public static boolean isClickable() {
		return isClickable;
	}
	public static void setClickable(boolean isClickable) {
		ToeicUtil.isClickable = isClickable;
	}
		
//	public static void showDialog(String word){
//		if (!practiceMode) return;
//		ShowDictWordDialog dialog = new ShowDictWordDialog(mContext, word);
//		dialog.show(((ToeicTestActivity)mContext).getSupportFragmentManager(), "ShowDictWordDialog");
//		return;
//	}
	
}
