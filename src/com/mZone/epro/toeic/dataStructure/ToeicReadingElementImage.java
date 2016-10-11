package com.mZone.epro.toeic.dataStructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlSerializer;
import com.mZone.epro.R;
import com.mZone.epro.toeic.customView.ToeicCustomTouchListenerImageView;
import com.mZone.epro.toeic.parser.ToeicParser;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class ToeicReadingElementImage extends ToeicReadingGroupElement {

	private int widthStyle;
	private String imageName;
	private Bitmap imageBitmap;
	
	public int getWidthStyle() {
		return widthStyle;
	}
	public void setWidthStyle(int widthStyle) {
		this.widthStyle = widthStyle;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public Bitmap getImageBitmap() {
		return imageBitmap;
	}
	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public static final String WIDTH_SIZE_NORMAL_STR = "normal";
	public static final String WIDTH_SIZE_LARGE_STR = "large";
	public static final int WIDTH_SIZE_NORMAL = 0;
	public static final int WIDTH_SIZE_LARGE = 1;
	
	public ToeicReadingElementImage() {
		// TODO Auto-generated constructor stub
		widthStyle = -1;
		imageName = null;
		imageBitmap = null;
	}
	public ToeicReadingElementImage(String mWidthStyle, String mImageName) {
		// TODO Auto-generated constructor stub
		widthStyle = -1;
		if (mWidthStyle.equals(WIDTH_SIZE_NORMAL_STR)){
			widthStyle = WIDTH_SIZE_NORMAL;
		}
		else{
			widthStyle = WIDTH_SIZE_LARGE;
		}
		imageName = mImageName;
		imageBitmap = null;
	}

	@Override
	public int getTypeOfElement() {
		// TODO Auto-generated method stub
		return IMAGE_TYPE;
	}
	@Override
	public View getView(LayoutInflater inflater, Context mContext) {
		// TODO Auto-generated method stub
		ToeicCustomTouchListenerImageView imageView = new ToeicCustomTouchListenerImageView(mContext);
		LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		Resources rs = mContext.getResources();
		int marginLR = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_subview_imageview_layoutmargin_lr), 
		        rs.getDisplayMetrics()
		);
		int marginTB = (int) TypedValue.applyDimension(
		        TypedValue.COMPLEX_UNIT_PX,
		        rs.getDimension(R.dimen.toeic_tabview_subview_imageview_layoutmargin_bottom), 
		        rs.getDisplayMetrics()
		);
		layoutParam.setMargins(marginLR, marginTB/2, marginLR, marginTB);
		imageView.setLayoutParams(layoutParam);
		imageView.setScaleType(ScaleType.FIT_XY);
		if (imageBitmap == null){
			try {
				File file = new File(imageName);
				InputStream bitmapStream = new FileInputStream(file);
				imageBitmap = BitmapFactory.decodeStream(bitmapStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (imageBitmap != null){
			imageView.setImageBitmap(imageBitmap);
		}
		return imageView;
	}
		
	public Bitmap getBitmap(){
		if (imageBitmap == null){
			try {
				File file = new File(imageName);
				InputStream bitmapStream = new FileInputStream(file);
				imageBitmap = BitmapFactory.decodeStream(bitmapStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return imageBitmap;
	}
	
	@Override
	public void refresh(){
		
	}
	@Override
	public void writeHighLightArrayToXML(XmlSerializer xmlSerializer, int[] radioButtonSelectedState, int[] checkResultButtonSelectedState) {
		// TODO Auto-generated method stub
		//do no thing
		try {
			xmlSerializer.startTag("", ToeicParser.IMAGE_TAG);
			xmlSerializer.startTag("", ToeicBasicSentence.HIGHLIGHT_ARRAY_TAG);
			xmlSerializer.endTag("", ToeicBasicSentence.HIGHLIGHT_ARRAY_TAG);
			xmlSerializer.endTag("", ToeicParser.IMAGE_TAG);
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
