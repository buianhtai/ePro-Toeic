package com.mZone.epro;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SideMenuListviewAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private Typeface myTypeface;
	private Typeface myTypefaceB;
	private int[] iconIDs = {R.drawable.ic_home, R.drawable.ic_store, R.drawable.ic_history, R.drawable.ic_dictionary, R.drawable.ic_setting};
	private int[] titleIDs = {R.string.sidemenu_title_home, R.string.sidemenu_title_store, R.string.sidemenu_title_history
								,R.string.sidemenu_title_dictionary, R.string.sidemenu_title_setting};
	public SideMenuListviewAdapter(Context c) {
		mContext = c;
		mLayoutInflater = LayoutInflater.from(mContext);
		myTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/HPHelven.ttf");
		myTypefaceB = Typeface.createFromAsset(mContext.getAssets(), "fonts/HPHelveb.ttf");
	}

	@Override
	public int getCount() {
		return 5;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mLayoutInflater.inflate(R.layout.launch_side_menu_listview_item, parent, false);
		ImageView icon = (ImageView) convertView.findViewById(R.id.side_menu_item_icon);
		TextView title = (TextView) convertView.findViewById(R.id.side_menu_item_title);
		icon.setImageResource(iconIDs[position]);
		title.setText(mContext.getResources().getString(titleIDs[position]));
		
		if (position < 2){ //home and store
			title.setTextColor(mContext.getResources().getColor(R.color.sidemenu_home_store_text_color));
			title.setTypeface(myTypefaceB);
		}
		else{
			title.setTextColor(mContext.getResources().getColor(R.color.sidemenu_normal_text_color));
			title.setTypeface(myTypeface);
		}
		return convertView;
	}

}
