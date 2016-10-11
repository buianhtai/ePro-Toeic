package com.mZone.epro.launch.fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.mZone.epro.LaunchActivity;
import com.mZone.epro.R;
import com.mZone.epro.client.data.BookItemLocal;
import com.mZone.epro.client.data.BookItemServer;
import com.mZone.epro.client.data.ClientDataController;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class LaunchHomeFragment extends Fragment {

	private Context mContext;
	
	private View rootView;
	ViewPager myBookPager;
	MyBookPagerAdapter myBookPagerAdapter;
	ClientDataController dataController = ClientDataController.getInstance();
		
	private TwoWayGridView newbookGridview;
	private GridViewAdapter newbookGridviewAdapter;
	
	public LaunchHomeFragment(){
		super();
	}
	
	@Override
	public void onAttach(Activity activity){
		 super.onAttach(activity);
		 mContext = activity;
	}
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.launch_home_fragment, container, false);
		myBookPager = (ViewPager)rootView.findViewById(R.id.viewPagerMyBook);
		myBookPagerAdapter = new MyBookPagerAdapter(getChildFragmentManager());
		myBookPager.setAdapter(myBookPagerAdapter);
		newbookGridview = (TwoWayGridView)rootView.findViewById(R.id.newbookGridview);
		
		newbookGridviewAdapter = new GridViewAdapter(mContext);
		newbookGridview.setAdapter(newbookGridviewAdapter);
		
		newbookGridview.setOnItemClickListener(new com.jess.ui.TwoWayAdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(TwoWayAdapterView<?> parent, View view,
					int position, long id) {
				((LaunchActivity)mContext).switchHomeStore(position);
			}
			
		});
		
		return rootView;
	}
	
	@Override
	public void onDestroyView(){
		super.onDestroy();
	}
	
	@Override
	public void onActivityCreated (Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		
	}
	@Override
	public void onPause (){
		super.onPause();
	}
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
	
	/**
	 * Local book fragment adapter
	 * @author Tony Huynh
	 *
	 */
	public class MyBookPagerAdapter extends FragmentPagerAdapter {

		private int maxItem;
		ArrayList<BookItemLocal> localBooks;
		boolean isDeleted = false;
		boolean isUpdated = false;
		public MyBookPagerAdapter(FragmentManager fm) {
			super(fm);
			maxItem = getResources().getInteger(R.integer.launch_home_fragment_mybook_fragment_item_number);
			localBooks = new ArrayList<BookItemLocal>();
			ArrayList<Integer> localBookArray = dataController.getLocalBookArray();
			for (int i = 0; i < localBookArray.size(); i++){
				localBooks.add(dataController.getBookItemFromLocal(localBookArray.get(i)));
			}
		}

		@Override
		public Fragment getItem(int position) {
			int cursorPosition = position*maxItem;
			ArrayList<BookItemLocal> itemArray = new ArrayList<BookItemLocal>();
			for (int i = cursorPosition; i < cursorPosition + maxItem && i < localBooks.size(); i++){
				itemArray.add(localBooks.get(i));
			}
			return new LaunchHomeMyBookPagerFragment(mContext, position, maxItem, itemArray);
		}

		@Override
		public int getCount() {
			if (localBooks.size() == 0)
				return 0;
			else{
				return ((localBooks.size() - 1)/maxItem + 1);
			}	 
		}
		
		@Override
		public int getItemPosition(Object object) {
			LaunchHomeMyBookPagerFragment updateFragment = (LaunchHomeMyBookPagerFragment)object;
			int position = updateFragment.getPosition();
			int cursorPosition = position*maxItem;
			if (localBooks.size() != 0){
				ArrayList<BookItemLocal> itemArray = new ArrayList<BookItemLocal>();
				for (int i = cursorPosition; i < cursorPosition + maxItem && i < localBooks.size(); i++){
					itemArray.add(localBooks.get(i));
				}
				if (!isDeleted){
					updateFragment.refresh(itemArray);
				}
				else{
					updateFragment.refreshByDeleted(itemArray);
				}
			}
			else{
				ArrayList<BookItemLocal> itemArray = new ArrayList<BookItemLocal>();
				updateFragment.refreshByDeleted(itemArray);
			}
			return super.getItemPosition(object);
		}
		
		public void swapCursor(ArrayList<BookItemLocal> localBooks){
			isDeleted = false;
			isUpdated = false;
			if (this.localBooks.size() > localBooks.size()){
				isDeleted = true;
			}
			else if (this.localBooks.size() < localBooks.size()){
				isUpdated = true;
			}
			if (isUpdated || isDeleted) {
				this.localBooks = localBooks;
				notifyDataSetChanged();
			}
		}
	}

	
	public void swapCursor(){
		ArrayList<Integer> localBookArray = dataController.getLocalBookArray();
		ArrayList<BookItemLocal> localBooks = new ArrayList<BookItemLocal>();
		for (int i = 0; i < localBookArray.size(); i++){
			localBooks.add(dataController.getBookItemFromLocal(localBookArray.get(i)));
		}
		myBookPagerAdapter.swapCursor(localBooks);
	}
	
	
	private class GridViewAdapter extends BaseAdapter {
		private Context mContext;
		private LayoutInflater mLayoutInflater;
		ClientDataController dataController = ClientDataController.getInstance();
		
		private class Holder{
			ImageView bookImageView;
		}
		public GridViewAdapter(Context c){
			mContext = c;
			mLayoutInflater = LayoutInflater.from(mContext);
		}
		@Override
		public int getCount() {
			if (dataController.isNewBookArrayReady()) return dataController.getNewBookArraySize();
			else return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null){
				convertView = mLayoutInflater.inflate(R.layout.launch_home_fragment_newbook_gridview_item, parent, false);
				holder = new Holder();
				holder.bookImageView = (ImageView)convertView.findViewById(R.id.newbookImageView);
				convertView.setTag(holder);
			}
			else{
				holder = (Holder)convertView.getTag();
			}
			BookItemServer bookItem = dataController.getBookItemFromNewArray(position);
			ImageView imageView = holder.bookImageView;
			dataController.setImage(imageView, bookItem.getBookID());
			convertView.setEnabled(false);
			return convertView;
		}
	}
	
	public void onPrepareLoadingBookFromServer(){
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			newbookGridview.animate().setDuration(200).alpha(0).setListener(new AnimatorListenerAdapter(){
				 @Override
				 public void onAnimationEnd(Animator animation) {
					 newbookGridview.setVisibility(View.GONE);
				 }
			});
		}
		else{
			newbookGridview.animate().setDuration(200).alpha(0).withEndAction(new Runnable() {
				
				@Override
				public void run() {
					newbookGridview.setVisibility(View.GONE);
				}
			});
		}
	}
	
	public void onLoadingBookFromServerFinish(){
		newbookGridviewAdapter.notifyDataSetChanged();
		newbookGridview.setVisibility(View.VISIBLE);
		newbookGridview.setAlpha(0);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			newbookGridview.animate().setDuration(200).alpha(1.0f).setListener(new AnimatorListenerAdapter(){
				 @Override
				 public void onAnimationEnd(Animator animation) {
					 newbookGridview.requestLayout();
				 }
			});
		}
		else{
			newbookGridview.animate().setDuration(200).alpha(1.0f).withEndAction(new Runnable() {
				@Override
				public void run() {
					newbookGridview.requestLayout();
				}
			});
		}
	}
}
