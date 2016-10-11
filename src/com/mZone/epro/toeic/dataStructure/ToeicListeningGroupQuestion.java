package com.mZone.epro.toeic.dataStructure;

import java.util.ArrayList;

public class ToeicListeningGroupQuestion {
	int voiceStart;
	int voiceStop;
	private ArrayList<ToeicListeningQuestion> questionArray;
	private ToeicListeningScript script;
	
	//constructor
	public ToeicListeningGroupQuestion() {
		// TODO Auto-generated constructor stub
		voiceStart = -1;
		voiceStop = -1;
		questionArray = new ArrayList<ToeicListeningQuestion>();
		script = null;
	}
	
	//constructor
	public ToeicListeningGroupQuestion(int voiceFrom, int voiceTo){
		voiceStart = voiceFrom;
		voiceStop = voiceTo;
		questionArray = new ArrayList<ToeicListeningQuestion>();
	}
	
	//constructor
	public ToeicListeningGroupQuestion(int voiceFrom, int voiceTo, int numberOfQuestion){
		voiceStart = voiceFrom;
		voiceStop = voiceTo;
		questionArray = new ArrayList<ToeicListeningQuestion>(numberOfQuestion);
	}
	
	//setter getter
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

	public ToeicListeningScript getScript() {
		return script;
	}

	public void setScript(ToeicListeningScript script) {
		this.script = script;
	}

	//private function
	public void addQuestion(ToeicListeningQuestion question){
		questionArray.add(question);
	}
	
	public ToeicListeningQuestion getQuestion(int index){
		return questionArray.get(index);
	}
	
	public String getStringForRowHeaderInListview(){
		String result = "";
		result = questionArray.get(0).getId() + " - " + questionArray.get(questionArray.size()-1).getId();
		return result;
	}
	
	public int getNumberOfQuestionInGroup(){
		return questionArray.size();
	}
	
}
