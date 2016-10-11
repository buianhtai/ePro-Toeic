package com.mZone.epro.toeic.dataStructure;

public class ToeicListeningSubScript {
	private int voiceStart;
	private int voiceStop;
	private String subject;
	private ToeicBasicSentence text;
	
	//constructor
	public ToeicListeningSubScript() {
		// TODO Auto-generated constructor stub
		voiceStart = -1;
		voiceStop = -1;
		subject = null;
		text = null;
	}
	
	//constructor
	public ToeicListeningSubScript(int voiceFrom, int voiceTo, String subject, ToeicBasicSentence sentence){
		voiceStart = voiceFrom;
		voiceStop = voiceTo;
		this.subject = subject;
		text = sentence;
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

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ToeicBasicSentence getText() {
		return text;
	}

	public void setText(ToeicBasicSentence text) {
		this.text = text;
	}
	
	//getter setter
	
}
