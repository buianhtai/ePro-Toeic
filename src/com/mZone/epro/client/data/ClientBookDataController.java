package com.mZone.epro.client.data;

import java.util.ArrayList;

import android.content.Context;
import android.util.SparseArray;
import android.widget.ImageView;

import com.mZone.epro.client.utility.ImageLoader;

public class ClientBookDataController {

	private static ClientBookDataController singleton;
	
	private ImageLoader imageLoader;
	SparseArray<BookItemServer> serverBookData;
	SparseArray<BookItemLocal> localBookData;
	SparseArray<ArrayList<Integer>> serverBookDataTypeMap;
	
	private boolean isDataReady;

	
	public ClientBookDataController(Context context) {
		imageLoader = new ImageLoader(context);
		serverBookData = new SparseArray<BookItemServer>();
		localBookData = new SparseArray<BookItemLocal>();
		serverBookDataTypeMap = new SparseArray<ArrayList<Integer>>();
		isDataReady = false;
	}
	
	public static ClientBookDataController getSingletonInstance(Context context){
		if (singleton == null){
			singleton = new ClientBookDataController(context);
		}
		return singleton;
	}
	
	private void setNewServerBookData(ArrayList<BookItemServer> newBookData){
		serverBookData = new SparseArray<BookItemServer>();
		serverBookDataTypeMap = new SparseArray<ArrayList<Integer>>();
		for (BookItemServer item : newBookData){
			int bookID = item.getBookID();
			int bookType = item.getBookType();
			serverBookData.setValueAt(bookID, item);
			ArrayList<Integer> typeArray = serverBookDataTypeMap.get(bookType);
			if (typeArray == null){
				typeArray = new ArrayList<Integer>();
			}
			typeArray.add(bookID);
			serverBookDataTypeMap.setValueAt(bookType, typeArray);
		}
	}
	
	private void setLocalBookData(ArrayList<BookItemLocal> newLocalBookData){
		localBookData = new SparseArray<BookItemLocal>();
		for (BookItemLocal item : newLocalBookData){
			int bookID = item.getBookID();
			localBookData.setValueAt(bookID, item);
			
		}
	}
	
	public synchronized void setNewBookData(ArrayList<BookItemServer> newBookData, ArrayList<BookItemLocal> newLocalBookData)
	{
		if (newBookData != null){
			setNewServerBookData(newBookData);
		}
		if (newLocalBookData != null){
			setLocalBookData(newLocalBookData);
		}
	}

	public boolean isDataReady() {
		return isDataReady;
	}

	public void setDataReady(boolean isDataReady) {
		this.isDataReady = isDataReady;
	}
	
	public void setImage(ImageView imageView, int bookId){
		if (!isDataReady){
			return;
		}
		BookItemServer item = serverBookData.get(bookId);
		if (imageLoader != null){
			imageLoader.DisplayImage(bookId, imageView, item.getUrlImage());
		}
	}
}
