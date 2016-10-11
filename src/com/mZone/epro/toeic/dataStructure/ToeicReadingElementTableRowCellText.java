package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import com.mZone.epro.toeic.parser.ToeicParser;

public class ToeicReadingElementTableRowCellText extends ToeicReadingElementTableRowCell {

	private float widthScale;
	private int fontStyle;
	private int fontSize;
	private int fontGravity;
	
	public static final String FONT_STYLE_NORMAL_STR = "normal";
	public static final String FONT_STYLE_BOLD_STR = "bold";
	public static final String FONT_STYLE_ITALIC_STR = "italic";
	
	public static final int FONT_STYLE_NORMAL = 0;
	public static final int FONT_STYLE_BOLD = 1;
	public static final int FONT_STYLE_ITALIC = 2;
	
	public static final String FONT_GRAVITY_CENTER_STR = "center";
	public static final String FONT_GRAVITY_LEFT_STR = "left";
	public static final String FONT_GRAVITY_RIGHT_STR = "right";
	public static final int FONT_GRAVITY_CENTER = 1;
	public static final int FONT_GRAVITY_LEFT = 0;
	public static final int FONT_GRAVITY_RIGHT = 2;
	
	public static final String FONT_SIZE_LARGE_1_STR = "large_1";
	public static final String FONT_SIZE_LARGE_2_STR = "large_2";
	public static final String FONT_SIZE_NORMAL_1_STR = "normal_1";
	public static final String FONT_SIZE_NORMAL_2_STR = "normal_2";
	public static final String FONT_SIZE_SMALL_1_STR = "small_1";
	public static final String FONT_SIZE_SMALL_2_STR = "small_2";
	public static final int FONT_SIZE_LARGE_1 = 2;
	public static final int FONT_SIZE_LARGE_2 = 3;
	public static final int FONT_SIZE_NORMAL_1 = 0;
	public static final int FONT_SIZE_NORMAL_2 = 1;
	public static final int FONT_SIZE_SMALL_1 = 4;
	public static final int FONT_SIZE_SMALL_2 = 5;
	
	private ToeicBasicSentence content;
	
	public ToeicReadingElementTableRowCellText() {
		// TODO Auto-generated constructor stub
		super();
		widthScale = -1;
		fontStyle = -1;
		fontSize = -1;
		fontGravity = -1;
	}
	public ToeicReadingElementTableRowCellText(String mStyle, float mWidthScale, String mFontStyle, String mFontSize, String mFontGravity) {
		// TODO Auto-generated constructor stub
		super(mStyle);
		widthScale = mWidthScale;
		if (mFontStyle.equals(FONT_STYLE_NORMAL_STR)){
			fontStyle = FONT_STYLE_NORMAL;
		}
		else if (mFontStyle.equals(FONT_STYLE_BOLD_STR)){
			fontStyle = FONT_STYLE_BOLD;
		}
		else if (mFontStyle.equals(FONT_STYLE_ITALIC_STR)){
			fontStyle = FONT_STYLE_ITALIC;
		}
		
		if (mFontGravity.equals(FONT_GRAVITY_LEFT_STR)){
			fontGravity = FONT_GRAVITY_LEFT;
		}
		else if (mFontGravity.equals(FONT_GRAVITY_RIGHT_STR)){
			fontGravity = FONT_GRAVITY_RIGHT;
		}
		else if (mFontGravity.equals(FONT_GRAVITY_CENTER_STR)){
			fontGravity = FONT_GRAVITY_CENTER;
		}
		
		if (mFontSize.equals(FONT_SIZE_LARGE_1_STR)){
			fontSize = FONT_SIZE_LARGE_1;
		}
		else if (mFontSize.equals(FONT_SIZE_LARGE_2_STR)){
			fontSize = FONT_SIZE_LARGE_2;
		}
		else if (mFontSize.equals(FONT_SIZE_NORMAL_1_STR)){
			fontSize = FONT_SIZE_NORMAL_1;
		}
		else if (mFontSize.equals(FONT_SIZE_NORMAL_2_STR)){
			fontSize = FONT_SIZE_NORMAL_2;
		}
		else if (mFontSize.equals(FONT_SIZE_SMALL_1_STR)){
			fontSize = FONT_SIZE_SMALL_1;
		}
		else if (mFontSize.equals(FONT_SIZE_SMALL_2_STR)){
			fontSize = FONT_SIZE_SMALL_2;
		}
		content = null;
	}
	public ToeicReadingElementTableRowCellText(String mStyle, float mWidthScale, String mFontStyle, String mFontSize, String mFontGravity, ToeicBasicSentence mContent) {
		// TODO Auto-generated constructor stub
		super(mStyle);
		widthScale = mWidthScale;
		if (mFontStyle.equals(FONT_STYLE_NORMAL_STR)){
			fontStyle = FONT_STYLE_NORMAL;
		}
		else if (mFontStyle.equals(FONT_STYLE_BOLD_STR)){
			fontStyle = FONT_STYLE_BOLD;
		}
		else if (mFontStyle.equals(FONT_STYLE_ITALIC_STR)){
			fontStyle = FONT_STYLE_ITALIC;
		}
		
		if (mFontGravity.equals(FONT_GRAVITY_LEFT_STR)){
			fontGravity = FONT_GRAVITY_LEFT;
		}
		else if (mFontGravity.equals(FONT_GRAVITY_RIGHT_STR)){
			fontGravity = FONT_GRAVITY_RIGHT;
		}
		else if (mFontGravity.equals(FONT_GRAVITY_CENTER_STR)){
			fontGravity = FONT_GRAVITY_CENTER;
		}
		
		if (mFontSize.equals(FONT_SIZE_LARGE_1_STR)){
			fontSize = FONT_SIZE_LARGE_1;
		}
		else if (mFontSize.equals(FONT_SIZE_LARGE_2_STR)){
			fontSize = FONT_SIZE_LARGE_2;
		}
		else if (mFontSize.equals(FONT_SIZE_NORMAL_1_STR)){
			fontSize = FONT_SIZE_NORMAL_1;
		}
		else if (mFontSize.equals(FONT_SIZE_NORMAL_2_STR)){
			fontSize = FONT_SIZE_NORMAL_2;
		}
		else if (mFontSize.equals(FONT_SIZE_SMALL_1_STR)){
			fontSize = FONT_SIZE_SMALL_1;
		}
		else if (mFontSize.equals(FONT_SIZE_SMALL_2_STR)){
			fontSize = FONT_SIZE_SMALL_2;
		}
		content = mContent;
	}
	//getter setter
	public float getWidthScale() {
		return widthScale;
	}
	public void setWidthScale(float widthScale) {
		this.widthScale = widthScale;
	}
	public int getFontStyle() {
		return fontStyle;
	}
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	public int getFontSize() {
		return fontSize;
	}
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}
	public int getFontGravity() {
		return fontGravity;
	}
	public void setFontGravity(int fontGravity) {
		this.fontGravity = fontGravity;
	}
	public ToeicBasicSentence getContent() {
		return content;
	}
	public void setContent(ToeicBasicSentence content) {
		this.content = content;
	}
	@Override
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer) {
		// TODO Auto-generated method stub
		try {
			xmlSerializer.startTag("", ToeicParser.TABLE_COLUM_TAG);
			xmlSerializer.startTag("", ToeicParser.TABLE_COLUM_CONTENT_TAG);
			content.writeHighLightArrayToXML(xmlSerializer);
			xmlSerializer.endTag("", ToeicParser.TABLE_COLUM_CONTENT_TAG);
			xmlSerializer.endTag("", ToeicParser.TABLE_COLUM_TAG);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
