package com.mZone.epro.toeic.dataStructure;

import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.R;
import com.mZone.epro.toeic.customView.CustomWordClickableTextview;
import com.mZone.epro.toeic.customView.ToeicCustomHorizontalScrollView;
import com.mZone.epro.toeic.parser.ToeicParser;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

public class ToeicReadingElementTable extends ToeicReadingGroupElement {

	private int size;
	private ArrayList<ToeicReadingElementTableRow> rowArray;
	private int border;
	
	public static final String SIZE_SUPER_LARGE_STR = "super_large";
	public static final String SIZE_LARGE_STR = "large";
	public static final String SIZE_NORMAL_STR = "normal";
	public static final String SIZE_SMALL_STR = "small";
	
	public static final int SIZE_SUPER_LARGE = 3;
	public static final int SIZE_LARGE = 2;
	public static final int SIZE_NORMAL = 1;
	public static final int SIZE_SMALL = 0;
	
	public static final int DEVICE_SUPER_LARGE_WIDTH = 960;
	public static final int DEVICE_LARGE_WIDTH = 720;
	public static final int DEVICE_NORMAL_WIDTH = 470;
	public static final int DEVICE_SMALL_WIDTH = 320;
	
	public ToeicReadingElementTable() {
		size = -1;
		rowArray = new ArrayList<ToeicReadingElementTableRow>();
	}
	public ToeicReadingElementTable(String mSize) {
		if (mSize.equals(SIZE_SUPER_LARGE_STR)){
			size = SIZE_SUPER_LARGE;
		}
		else if (mSize.equals(SIZE_LARGE_STR)){
			size = SIZE_LARGE;
		}
		else if (mSize.equals(SIZE_NORMAL_STR)){
			size = SIZE_NORMAL;
		}
		else{
			size = SIZE_SMALL;
		}
		rowArray = new ArrayList<ToeicReadingElementTableRow>();
	}

	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public ArrayList<ToeicReadingElementTableRow> getRowArray() {
		return rowArray;
	}
	public void setRowArray(ArrayList<ToeicReadingElementTableRow> rowArray) {
		this.rowArray = rowArray;
	}
	
	public void addRow(ToeicReadingElementTableRow row){
		rowArray.add(row);
	}

	public int getBorder() {
		return border;
	}
	
	public void setBorder(int border) {
		this.border = border;
	}
	
	@Override
	public int getTypeOfElement() {
		return TABLE_TYPE;
	}
	
	
	@Override
	public View getView(LayoutInflater inflater, Context mContext) {		
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics outMetrics = new DisplayMetrics ();
	    display.getMetrics(outMetrics);
	    float density  = mContext.getResources().getDisplayMetrics().density;
	    float dpWidth  = outMetrics.widthPixels / density;
		int width = outMetrics.widthPixels;
		int largeSize = width;
		switch (size) {
			case SIZE_SUPER_LARGE:
				if (dpWidth < DEVICE_NORMAL_WIDTH){
					largeSize = (int) (largeSize*2.5);
				}
				else if (dpWidth < DEVICE_LARGE_WIDTH){
					largeSize = (int) (largeSize*2.0);
				}
				else{
					largeSize = (int) (largeSize*1.5);
				}
				break;
			case SIZE_LARGE:
				if (dpWidth < DEVICE_NORMAL_WIDTH){
					largeSize = (int) (largeSize*2.0);
				}
				else if (dpWidth < DEVICE_LARGE_WIDTH){
					largeSize = (int) (largeSize*1.5);
				}
				break;
			case SIZE_NORMAL:
				if (dpWidth < DEVICE_NORMAL_WIDTH){
					largeSize = (int) (largeSize*1.5);
				}
				break;
			default:
				break;
		}
		Resources rs = mContext.getResources();
		int marginLR = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_subview_table_layoutmargin_lr), 
		        rs.getDisplayMetrics()
		);
		marginLR += 1;
		if (largeSize == width){
			largeSize -= 2*marginLR;
		}
		
		ToeicCustomHorizontalScrollView scrollView = (ToeicCustomHorizontalScrollView)inflater.inflate(R.layout.toeic_custom_tabview_horizontal_scrollview, null);
		LinearLayout horizontalLayout = (LinearLayout)scrollView.findViewById(R.id.horizontalLinearLayout);

		TableLayout tableLayout = new TableLayout(mContext);
		tableLayout.setStretchAllColumns(true);
		LinearLayout.LayoutParams tableParams;
		tableParams = new LinearLayout.LayoutParams(largeSize, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		tableLayout.setLayoutParams(tableParams);
		int count = 0;
		for (int i = 0; i < rowArray.size(); i++){	
			TableRow row = getRowView(inflater, mContext, i, largeSize);
			if (row != null){
				tableLayout.addView(row, count);
				count++;
			}
				
		}
		horizontalLayout.addView(tableLayout);
		return scrollView;
	}

	@SuppressWarnings({ "deprecation" })
	private TableRow getRowView(LayoutInflater inflater, Context mContext, int index, int largeSize){
		
		ToeicReadingElementTableRow elementTableRow = rowArray.get(index);
		TableRow tableRow = new TableRow(mContext);
		tableRow.setOrientation(LinearLayout.HORIZONTAL);
		TableRow.LayoutParams rowParam;
		rowParam = new TableRow.LayoutParams(largeSize, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		tableRow.setLayoutParams(rowParam);
		LinearLayout tableRowLinear = new LinearLayout(mContext);
		tableRowLinear.setBackgroundColor(0x00000000);
		tableRowLinear.setOrientation(LinearLayout.HORIZONTAL);
		for (int i = 0; i < elementTableRow.getNumberOfColum(); i++){
			ToeicReadingElementTableRowCell cell = elementTableRow.getCell(i);
			if (cell instanceof ToeicReadingElementTableRowCellText){
				ToeicReadingElementTableRowCellText textCell = (ToeicReadingElementTableRowCellText) elementTableRow.getCell(i);
				CustomWordClickableTextview textView = new CustomWordClickableTextview(mContext);
				float widthScale = textCell.getWidthScale();
				LinearLayout.LayoutParams textViewLayoutParam;
				textViewLayoutParam = new LinearLayout.LayoutParams((int) (largeSize*widthScale),android.view.ViewGroup.LayoutParams.FILL_PARENT);
				textView.setLayoutParams(textViewLayoutParam);
				textView.setText(textCell.getContent());
				textView.setPadding(5, 5, 5, 5);
				
				//setting properties
				int fontGravity = textCell.getFontGravity();
				int fontStyle = textCell.getFontStyle();
				int fontSize = textCell.getFontSize();
				if (fontGravity == ToeicReadingElementTableRowCellText.FONT_GRAVITY_LEFT){
					textView.setGravity(Gravity.LEFT);
				}
				else if (fontGravity == ToeicReadingElementTableRowCellText.FONT_GRAVITY_CENTER){
					textView.setGravity(Gravity.CENTER);
				}
				else {
					textView.setGravity(Gravity.RIGHT);
				}
				
				if (fontStyle == ToeicReadingElementTableRowCellText.FONT_STYLE_BOLD){
					textView.setTypeface(null, Typeface.BOLD);
				}
				
				Resources rs = mContext.getResources();
				if (fontSize == ToeicReadingElementTableRowCellText.FONT_SIZE_NORMAL_1){
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_table_fontsize_normal_1));
				}
				else if (fontSize == ToeicReadingElementTableRowCellText.FONT_SIZE_NORMAL_2){
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_table_fontsize_normal_2));
				}
				else if (fontSize == ToeicReadingElementTableRowCellText.FONT_SIZE_LARGE_1){
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_table_fontsize_large_1));
				}
				else if (fontSize == ToeicReadingElementTableRowCellText.FONT_SIZE_LARGE_2){
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rs.getDimension(R.dimen.toeic_tabview_subview_table_fontsize_large_2));
				}
				if (border == 1) {
					textView.setBackgroundResource(R.drawable.tabview_table_row_border_background);
				}
				else{
					textView.setBackgroundColor(Color.TRANSPARENT);
				}
				tableRowLinear.addView(textView);
			}
		}
		if (elementTableRow.getBackgroundStyle() == ToeicReadingElementTableRow.ROW_BACKGROUND_STYLE_LIGHTGRAY){
			tableRowLinear.setBackgroundColor(Color.TRANSPARENT);
		}
		else{
			tableRowLinear.setBackgroundColor(Color.TRANSPARENT);
		}
		tableRow.addView(tableRowLinear);
		return tableRow;
	}
	
	@Override
	public void refresh(){
		
	}
	
	@Override
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer,
			int[] radioButtonSelectedState, int[] checkResultButtonSelectedState) {
		try {
			xmlSerializer.startTag("", ToeicParser.TABLE_TAG);
			for (int i = 0; i < rowArray.size(); i++){
				rowArray.get(i).writeHighLightArrayToXML(xmlSerializer);
			}
			xmlSerializer.endTag("", ToeicParser.TABLE_TAG);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
}
