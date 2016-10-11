package com.mZone.epro.client.data;

import java.util.ArrayList;
import java.util.HashMap;
import com.mZone.epro.client.utility.ImageLoader;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;


public class ClientDataController {

	static private ClientDataController singleton;
	private ImageLoader imageLoader;
	private HashMap<Integer, BookItemLocal> localBookMap;
	private ArrayList<Integer> localBookArray;
	
	private HashMap<Integer, BookItemServer> serverBookMap;
	private ArrayList<Integer> newBookArray;
		
	private boolean isNewBookArrayReady;
	
	public ClientDataController() {
		// TODO Auto-generated constructor stub
		serverBookMap = new HashMap<Integer, BookItemServer>();
		newBookArray = new ArrayList<Integer>();
		isNewBookArrayReady = false;
		localBookMap = new HashMap<Integer, BookItemLocal>();
		localBookArray = new ArrayList<Integer>();		
	}
	public static ClientDataController getInstance()
	{
		if (singleton == null){
			singleton = new ClientDataController();
		}
		return singleton;
	}
	
	public void setContext(Context mContext){
		if (imageLoader == null) 
			imageLoader = new ImageLoader(mContext);
	}
	
	public void resetImageLoader(){
		if (imageLoader != null){
			imageLoader.clearCache();
		}
	}
	
	public void setImage(ImageView imageView, int bookId){
		if (imageLoader != null){
			imageLoader.DisplayImage(bookId, imageView);
		}
	}
	
	public void addBookItemsFromServer(HashMap<Integer, BookItemServer> itemMap, ArrayList<Integer> itemArray){
		serverBookMap = itemMap;
		newBookArray = itemArray;
		isNewBookArrayReady = true;
	}
	
	public BookItemServer getBookItemFromServer(int id){
		return serverBookMap.get(id);
	}
	
	public BookItemServer getBookItemFromNewArray(int position){
		if (position >= 0 && position < newBookArray.size()){
			int id = newBookArray.get(position);
			return serverBookMap.get(id);
		}
		return null;
	}
	public boolean isNewBookArrayReady() {
		return isNewBookArrayReady;
	}
	
	public int getNewBookArraySize(){
		return newBookArray.size();
	}
	
	public Bitmap getExistedBitmap(int bookID){
		return imageLoader.getExistedBitmap(bookID);
	}

	public void swapCursor(HashMap<Integer, BookItemLocal> localBookMap, ArrayList<Integer> localBookArray){
		this.localBookMap = localBookMap;
		this.localBookArray = localBookArray;
	}
	public ArrayList<Integer> getLocalBookArray() {
		return localBookArray;
	}
	public void setLocalBookArray(ArrayList<Integer> localBookArray) {
		this.localBookArray = localBookArray;
	}
	
	public BookItemLocal getBookItemFromLocal(int bookID){
		return localBookMap.get(bookID);
	}
	
	public boolean checkIfBookExistedInLocal(int bookID){
		return localBookArray.contains(bookID);
	}
	
}
