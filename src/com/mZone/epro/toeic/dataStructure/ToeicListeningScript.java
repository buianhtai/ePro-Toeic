package com.mZone.epro.toeic.dataStructure;

import java.util.ArrayList;

public class ToeicListeningScript {
	private ArrayList<ToeicListeningSubScript> subScriptArray = null;
	
	//constructor
	public ToeicListeningScript() {
		// TODO Auto-generated constructor stub
		
	}
	
	//constructor
	public void setSubScriptArray(ArrayList<ToeicListeningSubScript> array){
		if (array != null && array.size() > 0){
			subScriptArray = new ArrayList<ToeicListeningSubScript>(array.size());
			for (int i = 0; i < array.size(); i++){
				subScriptArray.add(array.get(i));
			}
		}
	}
	
	//get size of getSubScriptArraySize
	public int getSubScriptArraySize(){
		return subScriptArray.size();
	}
	
	//get Subcript at index
	public ToeicListeningSubScript getSubScript(int index){
		return subScriptArray.get(index);
	}
	
	public ArrayList<ToeicListeningSubScript> getSubScriptArray(){
		return subScriptArray;
	}
}
