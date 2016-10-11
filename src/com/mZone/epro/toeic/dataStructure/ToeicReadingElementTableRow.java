package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlSerializer;

import com.mZone.epro.toeic.parser.ToeicParser;

public class ToeicReadingElementTableRow {

	private int numberOfColum;
	private int backgroundStyle;
	private ArrayList<ToeicReadingElementTableRowCell> cellArray;
	
	public int getBackgroundStyle() {
		return backgroundStyle;
	}
	public void setBackgroundStyle(int backgroundStyle) {
		this.backgroundStyle = backgroundStyle;
	}
	public ArrayList<ToeicReadingElementTableRowCell> getCellArray() {
		return cellArray;
	}
	public void setCellArray(ArrayList<ToeicReadingElementTableRowCell> cellArray) {
		this.cellArray = cellArray;
	}
	public static final int ROW_BACKGROUND_STYLE_NORMAL = 0;
	public static final int ROW_BACKGROUND_STYLE_LIGHTGRAY = 1;
	
	public static final String ROW_BACKGROUND_STYLE_LIGHTGRAY_STR = "light_gray";
	public ToeicReadingElementTableRow() {
		// TODO Auto-generated constructor stub
		numberOfColum = -1;
		backgroundStyle = -1;
		cellArray = new ArrayList<ToeicReadingElementTableRowCell>();
	}
	public ToeicReadingElementTableRow(int mColum, String mStyle){
		numberOfColum = mColum;
		if (mStyle != null && mStyle.equals(ROW_BACKGROUND_STYLE_LIGHTGRAY_STR)){
			backgroundStyle = ROW_BACKGROUND_STYLE_LIGHTGRAY;
		}
		else{
			backgroundStyle = ROW_BACKGROUND_STYLE_NORMAL;
		}
		cellArray = new ArrayList<ToeicReadingElementTableRowCell>();
	}
	public int getNumberOfColum() {
		return numberOfColum;
	}
	public void setNumberOfColum(int numberOfColum) {
		this.numberOfColum = numberOfColum;
	}
	public void addCell(ToeicReadingElementTableRowCell cell){
		cellArray.add(cell);
	}
	public ToeicReadingElementTableRowCell getCell(int index){
		return cellArray.get(index);
	}
	
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer){
		try {
			xmlSerializer.startTag("", ToeicParser.TABLE_ROW_TAG);
			for (int i = 0; i < cellArray.size(); i++){
				cellArray.get(i).writeHighLightArrayToXML(xmlSerializer);
			}
			xmlSerializer.endTag("", ToeicParser.TABLE_ROW_TAG);
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
