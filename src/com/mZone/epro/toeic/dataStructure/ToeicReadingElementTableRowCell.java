package com.mZone.epro.toeic.dataStructure;

import org.xmlpull.v1.XmlSerializer;

public abstract class ToeicReadingElementTableRowCell {
	private int style;
	
	public static final String STYLE_NORMAL_STR = "normal";
	public static final String STYLE_TEXT_STR = "text";
	public static final int STYLE_TEXT = 0;
	
	public ToeicReadingElementTableRowCell(){
		style = -1;
	}
	public ToeicReadingElementTableRowCell(String mStyle){
		if (mStyle.equals(STYLE_TEXT_STR)){
			style = STYLE_TEXT;
		}
	}
	public int getStyle() {
		return style;
	}
	public void setStyle(int style) {
		this.style = style;
	}
	
	abstract public void writeHighLightArrayToXML(XmlSerializer xmlSerializer);

}
