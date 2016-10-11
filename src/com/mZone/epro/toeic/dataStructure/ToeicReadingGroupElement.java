package com.mZone.epro.toeic.dataStructure;

import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public abstract class ToeicReadingGroupElement {
	private int type;
	public static final int QUESTION_TYPE = 1;
	public static final int TABLE_TYPE = 2;
	public static final int IMAGE_TYPE = 3;
	public static final int PARAGRAPH_TYPE = 4;
	public ToeicReadingGroupElement() {
		// TODO Auto-generated constructor stub
		type = -1;
	}
	public ToeicReadingGroupElement(int mType) {
		// TODO Auto-generated constructor stub
		type = mType;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	abstract public int getTypeOfElement();
	abstract public View getView(LayoutInflater inflater, Context mContext);
	abstract public void refresh();
	abstract public void writeHighLightArrayToXML(XmlSerializer xmlSerializer, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState);
}
