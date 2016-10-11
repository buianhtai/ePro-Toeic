package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.R;
import com.mZone.epro.toeic.customView.CustomWordClickableTextview;
import com.mZone.epro.toeic.parser.ToeicParser;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;


public class ToeicReadingElementParagraph extends ToeicReadingGroupElement{

	private int style;
	private int fontStyle;
	private int fontSize;
	private int fontGravity;
	private ToeicBasicSentence content;
	
	public static final String STYLE_HEADER_STR = "header";
	public static final String STYLE_NORMAL_STR = "normal";
	public static final int STYLE_HEADER = 1;
	public static final int STYLE_NORMAL = 0;
	
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
	
	
	public ToeicReadingElementParagraph() {
		// TODO Auto-generated constructor stub
		style = -1;
		fontStyle = -1;
		fontSize = -1;
		fontGravity = -1;
		content = null;
	}
	public ToeicReadingElementParagraph(String mStyle, String mFontStyle, String mFontSize, String mFontGravity, ToeicBasicSentence mContent) {
		// TODO Auto-generated constructor stub
		if (mStyle.equals(STYLE_HEADER_STR)){
			style = STYLE_HEADER;
		}
		else if (mStyle.equals(STYLE_NORMAL_STR)){
			style = STYLE_NORMAL;
		}
		
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
	public ToeicReadingElementParagraph(String mStyle, String mFontStyle, String mFontSize, String mFontGravity) {
		// TODO Auto-generated constructor stub
		if (mStyle.equals(STYLE_HEADER_STR)){
			style = STYLE_HEADER;
		}
		else if (mStyle.equals(STYLE_NORMAL_STR)){
			style = STYLE_NORMAL;
		}
		
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

	public void setContent(ToeicBasicSentence mContent){
		content = mContent;
	}
	@Override
	public int getTypeOfElement() {
		// TODO Auto-generated method stub
		return PARAGRAPH_TYPE;
	}
	public ToeicBasicSentence getContent(){
		return content;
	}
	
	public static final int PROPERTY_STYLE = 0;
	public static final int PROPERTY_FONT_STYLE = 1;
	public static final int PROPERTY_FONT_GRAVITY = 2;
	public static final int PROPERTY_FONT_SIZE = 3;
	public int getProperty(int key){
		switch(key){
			case PROPERTY_STYLE:
				return style;
			case PROPERTY_FONT_STYLE:
				return fontStyle;
			case PROPERTY_FONT_GRAVITY:
				return fontGravity;
			case PROPERTY_FONT_SIZE:
				return fontSize;
			default:
				return -1;
		}
	}
	@Override
	public View getView(LayoutInflater inflater, Context mContext) {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		Resources rs = mContext.getResources();
		int marginLR = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_layoutmargin_lr), 
		        rs.getDisplayMetrics()
		);
		int marginBottom = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_layoutmargin_bottom), 
		        rs.getDisplayMetrics()
		);
		layoutParam.setMargins(marginLR, marginBottom, marginLR, marginBottom);
		CustomWordClickableTextview textView = new CustomWordClickableTextview(mContext);
		textView.setLayoutParams(layoutParam);
		textView.setTextColor(rs.getColor(R.color.text_normal_color));
		textView.setText(content);
		//setting properties
		
		if (fontGravity == FONT_GRAVITY_LEFT){
			textView.setGravity(Gravity.LEFT);
		}
		else if (fontGravity == FONT_GRAVITY_CENTER){
			textView.setGravity(Gravity.CENTER);
		}
		else {
			textView.setGravity(Gravity.RIGHT);
		}
		
		if (fontStyle == FONT_STYLE_BOLD){
			textView.setTypeface(null, Typeface.BOLD);
		}
		else if (fontStyle == FONT_STYLE_ITALIC){
			textView.setTypeface(null, Typeface.ITALIC);
		}
		
		if (fontSize == FONT_SIZE_NORMAL_1){
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_fontsize_normal_1));
		}
		else if (fontSize == FONT_SIZE_NORMAL_2){
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_fontsize_normal_2));
		}
		else if (fontSize == FONT_SIZE_LARGE_1){
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_fontsize_large_1));
		}
		else if (fontSize == FONT_SIZE_LARGE_2){
			textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_paragraph_fontsize_large_2));
		}
		return textView;
	}
	@Override
	public void refresh(){
		
	}
	@Override
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState) {
		// TODO Auto-generated method stub
		try {
			xmlSerializer.startTag("", ToeicParser.PARAGRAPH_TAG);
			content.writeHighLightArrayToXML(xmlSerializer);
			xmlSerializer.endTag("", ToeicParser.PARAGRAPH_TAG);
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
