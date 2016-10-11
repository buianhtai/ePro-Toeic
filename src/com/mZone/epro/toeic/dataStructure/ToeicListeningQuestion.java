package com.mZone.epro.toeic.dataStructure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ToeicListeningQuestion {
	private int id;
	private int ans;
	private int voiceStart;
	private int voiceStop;
	private String imgPath;
	private Bitmap imgBitmap;
	private ToeicBasicSentence question;
	private ArrayList<ToeicBasicSentence> responseArray;
	private ToeicListeningScript script = null;
	
	//constructor
	public ToeicListeningQuestion() {
		// TODO Auto-generated constructor stub
		id = -1;
		ans = -1;
		voiceStart = -1;
		voiceStop = -1;
		imgPath = null;
		imgBitmap = null;
		question = null;
		responseArray = null;
		script = null;
	}
	
	//constructor
	public ToeicListeningQuestion(int id, int ans, int voiceFrom, int voiceTo){
		this.id = id;
		this.ans = ans;
		voiceStart = voiceFrom;
		voiceStop = voiceTo;
		imgPath = null;
		imgBitmap = null;
		question = null;
		responseArray = null;
		script = null;
	}

	//getter setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAns() {
		return ans;
	}

	public void setAns(int ans) {
		this.ans = ans;
	}

	public int getVoiceStart() {
		return voiceStart;
	}

	public void setVoiceStart(int voiceStart) {
		this.voiceStart = voiceStart;
	}

	public int getVoiceStop() {
		return voiceStop;
	}

	public void setVoiceStop(int voiceStop) {
		this.voiceStop = voiceStop;
	}

	public Bitmap getImgBitmap() {
		if (imgBitmap == null || imgBitmap.isRecycled()){
			File file = new File(this.imgPath);
			InputStream bitmapStream = null;
			try {
				bitmapStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (bitmapStream != null) 
				imgBitmap = BitmapFactory.decodeStream(bitmapStream);
		}
		return imgBitmap;
	}

	public void setImgBitmap(Bitmap imgBitmap) {
		this.imgBitmap = imgBitmap;
	}

	public ToeicBasicSentence getQuestion() {
		return question;
	}

	public void setQuestion(ToeicBasicSentence question) {
		this.question = question;
	}

	public ArrayList<ToeicBasicSentence> getResponseArray() {
		return responseArray;
	}

	public void setResponseArray(ArrayList<ToeicBasicSentence> responseArray) {
		this.responseArray = new ArrayList<ToeicBasicSentence>(responseArray.size());
		for (int i = 0; i < responseArray.size(); i++){
			this.responseArray.add(responseArray.get(i));
		}
	}

	public ToeicListeningScript getScript() {
		return script;
	}

	public void setScript(ToeicListeningScript script) {
		this.script = script;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	public ToeicBasicSentence getResponseAtIndex(int index){
		return responseArray.get(index);
	}
}
