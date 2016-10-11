package com.mZone.epro.launch.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import com.mZone.epro.LaunchActivity;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookProvider;
import com.mZone.epro.client.data.BookProviderMetaData;
import com.mZone.epro.client.data.BookProviderMetaData.LocalBookTableMetaData;
import com.mZone.epro.client.task.BookDataProcessingIntentService;
import com.mZone.epro.client.utility.ExternalStorage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.TextView;

public class LaunchHomeMyBookPagerFragment extends Fragment implements View.OnTouchListener, ImageView.OnClickListener,
																	OnLongClickListener{

	private Context mContext;
	private View rootView;
	private LayoutInflater inflater;
	
	
	private int[] imgvID = {R.id.img1_1, R.id.img1_2, R.id.img1_3, R.id.img1_4, R.id.img1_5, R.id.img1_6};
	private int[] tvID = {R.id.tv1_1, R.id.tv1_2, R.id.tv1_3, R.id.tv1_4, R.id.tv1_5, R.id.tv1_6};
	private ArrayList<ImageView> imgvArray;
	private ArrayList<TextView> tvArray;
	private ArrayList<BookItemLocal> itemArray;
	private ArrayList<Bitmap> bitmapArray;
	private int maxItem;
	private int position;

	ViewTreeObserver vto;
	
	private float imgvTranslateXValue;
	private float imgvTranslateXValue_1_3;
	private float imgvTranslateYValue_1_3;
	private float tvTranslateXValue;
	private float tvTranslateXValue_1_3;
	private float tvTranslateYValue_1_3;
	
	private Typeface myTypeface;
	
	public LaunchHomeMyBookPagerFragment(){
		super();
	}
	
	public LaunchHomeMyBookPagerFragment(Context context, int position, int maxItem, ArrayList<BookItemLocal> itemArray) {
		this.mContext = context;
		this.position = position;
		this.maxItem = maxItem;
		this.itemArray = itemArray;
		myTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/HPHelven.ttf");
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mContext == null) this.mContext = getActivity();
		this.inflater = inflater;
		rootView = this.inflater.inflate(
				R.layout.launch_home_fragment_mybook_fragment, container, false);
		imgvArray = new ArrayList<ImageView>(maxItem);
		tvArray = new ArrayList<TextView>(maxItem);
		bitmapArray = new ArrayList<Bitmap>();
		for (int i = 0; i < maxItem; i++){
			ImageView imgv = (ImageView)rootView.findViewById(imgvID[i]);
			TextView tv = (TextView)rootView.findViewById(tvID[i]);
			tv.setTypeface(myTypeface);
			tv.setTextColor(getResources().getColor(R.color.launch_home_viewpager_textview_normal_color));
			if (i < itemArray.size()){
				BookItemLocal item = itemArray.get(i);
				if (item.getImageStatus().equals(BookItemLocal.IMAGE_HAVE)){
					 Uri myRowUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, item.getLocalID());
					 try {
					      InputStream inStream = mContext.getContentResolver().openInputStream(myRowUri);
					      Bitmap bitmap = BitmapFactory.decodeStream(inStream);
					      bitmapArray.add(bitmap);
					      imgv.setImageBitmap(bitmap);
					 }
					 catch (FileNotFoundException e) { 
					 } 
				}
				String[] parsingBookName = item.getBookName().split("-");
				String rebuildBookName = parsingBookName[0] + "\n" + parsingBookName[1];
				tv.setText(rebuildBookName);
			}
			else{
				imgv.setVisibility(View.INVISIBLE);
				tv.setVisibility(View.INVISIBLE);
			}
			imgv.setOnClickListener(this);
			imgv.setTag(String.valueOf(position * maxItem + i));
			imgv.setOnLongClickListener(this);
			imgv.setOnTouchListener(this);
			imgvArray.add(imgv);
			tvArray.add(tv);
			
		}
		vto = rootView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				int[] imgvLocation_1_1 = new int[2];
				int[] imgvLocation_1_2 = new int[2];
				int[] imgvLocation_1_3 = new int[2];
				int[] imgvLocation_1_4 = new int[2];
				imgvArray.get(0).getLocationOnScreen(imgvLocation_1_1);
				imgvArray.get(1).getLocationOnScreen(imgvLocation_1_2);
				imgvArray.get(2).getLocationOnScreen(imgvLocation_1_3);
				imgvArray.get(3).getLocationOnScreen(imgvLocation_1_4);
				imgvTranslateXValue = imgvLocation_1_2[0] - imgvLocation_1_1[0];
				imgvTranslateXValue_1_3 = imgvLocation_1_4[0] - imgvLocation_1_3[0];
				imgvTranslateYValue_1_3 = imgvLocation_1_4[1] - imgvLocation_1_3[1];
				
				int[] tvLocation_1_1 = new int[2];
				int[] tvLocation_1_2 = new int[2];
				int[] tvLocation_1_3 = new int[2];
				int[] tvLocation_1_4 = new int[2];
				tvArray.get(0).getLocationOnScreen(tvLocation_1_1);
				tvArray.get(1).getLocationOnScreen(tvLocation_1_2);
				tvArray.get(2).getLocationOnScreen(tvLocation_1_3);
				tvArray.get(3).getLocationOnScreen(tvLocation_1_4);
				tvTranslateXValue = tvLocation_1_2[0] - tvLocation_1_1[0];
				tvTranslateXValue_1_3 = tvLocation_1_4[0] - tvLocation_1_3[0];
				tvTranslateYValue_1_3 = tvLocation_1_4[1] - tvLocation_1_3[1];
				
				rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
			
			
		});
		rootView.setTag("rootview" + position);
		rootView.setOnDragListener(new ImageViewDragListener());
		return rootView;
	}
	
	@Override
	public void onPause (){
		super.onPause();
		
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState){
		super.onViewCreated(view, savedInstanceState);
		
	}
	
	@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
	}
	//Child FragmentManager retained after detaching parent Fragment
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} 
		catch (NoSuchFieldException e) {
		}
		sChildFragmentManagerField = f;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} 
			catch (Exception e) {
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		TextView tv = tvArray.get(imgvArray.indexOf(v));
		switch (event.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		v.setRotationY(getResources().getInteger(R.integer.launch_home_fragment_mybook_fragment_imageview_rotation));
        		tv.setTextColor(getResources().getColor(R.color.launch_home_viewpager_textview_hover_color));
        		return false;
        	case MotionEvent.ACTION_UP:
        		v.setRotationY(0);
        		tv.setTextColor(getResources().getColor(R.color.launch_home_viewpager_textview_normal_color));
        		return false;
        	case MotionEvent.ACTION_CANCEL:
        		v.setRotationY(0);
        		tv.setTextColor(getResources().getColor(R.color.launch_home_viewpager_textview_normal_color));
        		return false;
        	default:
        		break;
		}
		return true;
	}
	private boolean isAnimating = false;
	@Override
	public void onClick(View v) {
		if (isAnimating) return;
		int clickedIndex = 0;
		for (int i = 0; i < maxItem; i++){
			if (v.getId() == imgvID[i]){
				clickedIndex = i;
				break;
			}
		}
		BookItemLocal clickedItem = itemArray.get(clickedIndex);
		((LaunchActivity)mContext).openTest(clickedItem);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public ArrayList<BookItemLocal> getItemArray() {
		return itemArray;
		
	}

	public void setItemArray(ArrayList<BookItemLocal> itemArray) {
		this.itemArray = itemArray;
		
	}
	
	public void refresh(ArrayList<BookItemLocal> itemArray) {
		if (isAnimating == true) {
			return;
		}
		isAnimating = true;
		if (bitmapArray.size() == maxItem)
			bitmapArray.remove(bitmapArray.size() - 1);
		int countDif = itemArray.size() - this.itemArray.size();
		if (itemArray.size() == this.itemArray.size()){
			countDif = 1;
		}
		for (int i = countDif - 1; i>=0; i-- ){
			BookItemLocal item = itemArray.get(i);
			if (item.getImageStatus().equals(BookItemLocal.IMAGE_HAVE)){
				 Uri myRowUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, item.getLocalID());
				 try {
				      InputStream inStream = mContext.getContentResolver().openInputStream(myRowUri);
				      Bitmap bitmap = BitmapFactory.decodeStream(inStream);
				      bitmapArray.add(0, bitmap);
				 }
				 catch (FileNotFoundException e) { 
				 } 
			}
		}
		this.itemArray = itemArray;
		animationView();
	}
	public void refreshByDeleted(ArrayList<BookItemLocal> itemArray) {
		this.itemArray = itemArray;
		for (int i = 0; i < maxItem; i++){
			ImageView imgv = imgvArray.get(i);
			TextView tv = tvArray.get(i);
			if (i < itemArray.size()){
				BookItemLocal item = itemArray.get(i);
				if (item.getImageStatus().equals(BookItemLocal.IMAGE_HAVE)){
					 Uri myRowUri = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, item.getLocalID());
					 try {
						 
					      InputStream inStream = mContext.getContentResolver().openInputStream(myRowUri);
					      Bitmap bitmap = BitmapFactory.decodeStream(inStream);
					      bitmapArray.add(bitmap);
					      imgv.setImageBitmap(bitmap);
					 }
					 catch (FileNotFoundException e) { 
					 } 
				}
				String[] parsingBookName = item.getBookName().split("-");
				String rebuildBookName = parsingBookName[0] + "\n" + parsingBookName[1];
				tv.setText(rebuildBookName);
			}
			else{
				imgv.setVisibility(View.INVISIBLE);
				tv.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private class ImgvTranslationRunnable implements Runnable{
		private int index;
		
		public ImgvTranslationRunnable(int index){
			this.index = index;
		}
		@Override
		public void run() {
			if (index >= itemArray.size()){
				imgvArray.get(index).setVisibility(View.INVISIBLE);
			}
			else{
				imgvArray.get(index).setImageBitmap(bitmapArray.get(index));
				imgvArray.get(index).setVisibility(View.VISIBLE);
			}
			if (index == 0){
				imgvArray.get(index).setX(imgvArray.get(index).getX() - 2*imgvTranslateXValue);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					imgvArray.get(index).animate().translationXBy(imgvTranslateXValue).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 isAnimating = false;
						 }
					});
				}
				else{
					imgvArray.get(index).animate().translationXBy(imgvTranslateXValue).withEndAction(new Runnable() {
						
						@Override
						public void run() {
							isAnimating = false;
						}
					});
				}
			}
			else if (index == 2){
				imgvArray.get(index).setX(imgvArray.get(index).getX() - imgvTranslateXValue_1_3);
				imgvArray.get(index).setY(imgvArray.get(index).getY() - imgvTranslateYValue_1_3);
			}
			else{
				imgvArray.get(index).setX(imgvArray.get(index).getX() - imgvTranslateXValue);
			}
		}
	}
	
	private class TvTranslationRunnable implements Runnable{
		private int index;
		
		public TvTranslationRunnable(int index){
			this.index = index;
		}
		@Override
		public void run() {
			if (index >= itemArray.size()){
				tvArray.get(index).setVisibility(View.INVISIBLE);
			}
			else{
				String[] parsingBookName = itemArray.get(index).getBookName().split("-");
				String rebuildBookName = parsingBookName[0] + "\n" + parsingBookName[1];
				tvArray.get(index).setText(rebuildBookName);
				tvArray.get(index).setVisibility(View.VISIBLE);
			}
			if (index == 0){
				tvArray.get(index).setX(tvArray.get(index).getX() - 2*tvTranslateXValue);
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					tvArray.get(index).animate().translationXBy(tvTranslateXValue).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 isAnimating = false;
						 }
					});
				}
				else{
					tvArray.get(index).animate().translationXBy(tvTranslateXValue).withEndAction(new Runnable() {
						
						@Override
						public void run() {
							isAnimating = false;
						}
					});
				}
			}
			else if (index == 2){
				tvArray.get(index).setX(tvArray.get(index).getX() - tvTranslateXValue_1_3);
				tvArray.get(index).setY(tvArray.get(index).getY() - tvTranslateYValue_1_3);
			}
			else{
				tvArray.get(index).setX(tvArray.get(index).getX() - tvTranslateXValue);
			}
		}
	}
	
	private void animationView(){
		for (int i = 0; i < maxItem; i++){
			ImageView imgv = imgvArray.get(i);
			TextView tv = tvArray.get(i);
			if (i != 2){
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					final int tempI = i;
					imgv.animate().translationXBy(imgvTranslateXValue).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 new ImgvTranslationRunnable(tempI).run();
						 }
					});
					tv.animate().translationXBy(tvTranslateXValue).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 new TvTranslationRunnable(tempI).run();
						 }
					});
				}
				else{
					imgv.animate().translationXBy(imgvTranslateXValue).withEndAction(new ImgvTranslationRunnable(i));
					tv.animate().translationXBy(tvTranslateXValue).withEndAction(new TvTranslationRunnable(i));
				}
			}
			else {
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					final int tempI = i;
					imgv.animate().translationXBy(imgvTranslateXValue_1_3).translationYBy(imgvTranslateYValue_1_3).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 new ImgvTranslationRunnable(tempI).run();
						 }
					});
					tv.animate().translationXBy(tvTranslateXValue_1_3).translationYBy(tvTranslateYValue_1_3).setListener(new AnimatorListenerAdapter(){
						 @Override
						 public void onAnimationEnd(Animator animation) {
							 new TvTranslationRunnable(tempI).run();
						 }
					});
				}
				else{
					imgv.animate().translationXBy(imgvTranslateXValue_1_3).translationYBy(imgvTranslateYValue_1_3).withEndAction(new ImgvTranslationRunnable(i));
					tv.animate().translationXBy(tvTranslateXValue_1_3).translationYBy(tvTranslateYValue_1_3).withEndAction(new TvTranslationRunnable(i));
				}
			}
		}
	}
	
	//using for drag to delete
	private int currentDrag = -1;
	private boolean viewIsDeleted;
	@Override
	public boolean onLongClick(View v) {
		if (currentDrag > 0) return false;
		currentDrag = Integer.valueOf((String) v.getTag());
		ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
		String[] type = {ClipDescription.MIMETYPE_TEXT_PLAIN};
		ClipData dragData = new ClipData((CharSequence) v.getTag(), type, item);
		v.startDrag(dragData,  // the data to be dragged
				new View.DragShadowBuilder(v),  // the drag shadow builder
                v,      // no need to use local data
                0          // flags (not currently used, set to 0)
				);
		return true;
	}
	protected class ImageViewDragListener implements OnDragListener {
		@Override
		public boolean onDrag(View v, DragEvent event) {
			final int action = event.getAction();
			switch(action) {
				case DragEvent.ACTION_DRAG_STARTED:
					View view = (View) event.getLocalState();
					int viewTag = Integer.valueOf((String) view.getTag());
					if (viewTag/maxItem != position){
						return false;
					}
					view.setVisibility(View.INVISIBLE);
					view.invalidate();
					break;
				case DragEvent.ACTION_DRAG_ENTERED:
					viewIsDeleted = false;
					v.setBackgroundColor(mContext.getResources().getColor(R.color.delete_book_inside_background));
					v.setAlpha(0.5f);
					break;
				case DragEvent.ACTION_DRAG_EXITED:
					viewIsDeleted = true;
					v.setBackgroundColor(mContext.getResources().getColor(R.color.delete_book_outside_background));
					break;
				case DragEvent.ACTION_DROP:
					break;
				case DragEvent.ACTION_DRAG_ENDED:

					view = (View) event.getLocalState();
					viewTag = Integer.valueOf((String) view.getTag());
					if (viewTag/maxItem != position){
						return false;
					}
					v.setBackgroundColor(mContext.getResources().getColor(R.color.delete_book_drag_end_background));
					v.setAlpha(1.0f);
					if (viewIsDeleted){
						deleteItem(currentDrag);
					}
					currentDrag = -1;
					view.setVisibility(View.VISIBLE);
					view.invalidate();
					break;
				default:
                    break;
			}
			return true;
		}
	}
	
	private void deleteItem(int itemID){
		if (itemID/maxItem != position){
			return;
		}
		final int realItemID = itemID%maxItem;
		final BookItemLocal item = itemArray.get(realItemID);
		final int rowID = item.getLocalID();
		final int bookID = item.getBookID();
		final String bookName = item.getBookName();
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(bookName);
		dialog.setMessage(R.string.launch_home_fragment_delete_dialog_message);
		dialog.setNegativeButton(R.string.launch_home_fragment_delete_dialog_cancel_btn, null);
		dialog.setPositiveButton(R.string.launch_home_fragment_delete_dialog_confirm_btn, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				Thread deleteThreads = new Thread(new Runnable() {
					
					@Override
					public void run() {
						Uri deleteURI = ContentUris.withAppendedId(LocalBookTableMetaData.CONTENT_URI, rowID);
						ContentResolver cr = mContext.getContentResolver();
						cr.delete(deleteURI, null, null);
						String selection = LocalBookTableMetaData.BOOK_ID + " = ?";
						String[] selectionArgs = {String.valueOf(bookID)};
						cr.delete(BookProviderMetaData.TestHistoryTableMetaData.CONTENT_URI, selection, selectionArgs);
						final String sourceFile = ExternalStorage.getSDCacheDir(getActivity(), BookDataProcessingIntentService.UNZIP_FOLDER).toString() + "/" + bookID;
						File deleteFile = new File(sourceFile);
						deleteRecursive(deleteFile);
						final String imageFilePath = ExternalStorage.getSDCacheDir(getActivity(), BookProvider.IMAGE_FOLDER).toString() + "/" + rowID;
						File deleteImageFile = new File(imageFilePath);
						deleteImageFile.delete();
					}
				});
				deleteThreads.start();
			}
		});
		dialog.create().show();
	}
	
	
	void deleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory()){
	    	for (File child : fileOrDirectory.listFiles()){
	        	 deleteRecursive(child);
	        }
	    }
	    fileOrDirectory.delete();
	}
}
