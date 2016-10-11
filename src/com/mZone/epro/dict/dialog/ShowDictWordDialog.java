package com.mZone.epro.dict.dialog;

import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Stack;

import com.mZone.epro.R;
import com.mZone.epro.dict.data.FavoriteWordPreference;
import com.mZone.epro.dict.dictLibrary.DictionaryManager;
import com.mZone.epro.dict.dictionary.MyWebView;
import com.mZone.epro.dict.dictionary.MyWebView.OnWordClickListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

public class ShowDictWordDialog extends DialogFragment implements View.OnClickListener, OnWordClickListener{

	public static final String ARGS_WORD = "ShowDictWordDialog.ARGS_WORD";
	
	public static interface ShowDictWordDialogDelegate {
		DictionaryManager getDictionaryManager();
	}
	
	WeakReference<ShowDictWordDialogDelegate> delegate;
	
	AlertDialog dialog;
	Stack<String> wordStack;
	Stack<String> resultStack;
		
	ImageButton backBtn;
	ImageButton favoriteBtn;
	MyWebView webView;
	
	ImageButton soundBtn;
	TextToSpeech textToSpeechObj;
	
	public ShowDictWordDialog(){
		wordStack = new Stack<String>();
		resultStack = new Stack<String>();
	}
	
	public void setDelegate(ShowDictWordDialogDelegate d){
		this.delegate = new WeakReference<ShowDictWordDialog.ShowDictWordDialogDelegate>(d);
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		wordStack.push(args.getString(ARGS_WORD));
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		builder.setTitle(wordStack.get(0));
		View rootView = inflater.inflate(R.layout.dict_show_word_dialog, null);
		builder.setView(rootView);
		webView = (MyWebView) rootView.findViewById(R.id.htmlWebView);
		webView.setOnWordClickListenerDelegate(this);
		String word = wordStack.get(0);
		backBtn = (ImageButton) rootView.findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		favoriteBtn = (ImageButton) rootView.findViewById(R.id.favoriteBtn);
		favoriteBtn.setOnClickListener(this);
		soundBtn = (ImageButton) rootView.findViewById(R.id.soundBtn);
		soundBtn.setOnClickListener(this);
		textToSpeechObj=new TextToSpeech(this.getActivity().getApplicationContext(), 
			      new TextToSpeech.OnInitListener() {
			      @Override
			      public void onInit(int status) {
			         if(status != TextToSpeech.ERROR){
			        	 textToSpeechObj.setLanguage(Locale.US);
			            }				
			         }
			      });
		dialog = builder.create();
		showWord(word, false);
		return dialog;
	}
	
	private void showWord(final String word, boolean onThreads){
		final DictionaryManager dictManager = delegate.get().getDictionaryManager();
		if (dictManager != null){
			if (!onThreads){
				String exp = dictManager.Smart_Find_Word(word);
				resultStack.push(exp);
				webView.loadData(exp, "text/html; charset=UTF-8", null);
				dialog.setTitle(word);
				Integer isFavorite = FavoriteWordPreference.getFavoriteWord(word, getActivity().getApplicationContext());
				if (isFavorite == FavoriteWordPreference.WORD_ACTIVE){
					favoriteBtn.setSelected(true);
				}
				else{
					favoriteBtn.setSelected(false);
				}
			}
			else{
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						String exp = "Cannot find this word";
						if (dictManager != null){
							exp = dictManager.Smart_Find_Word(word.toLowerCase(Locale.ENGLISH));
						}
						resultStack.push(exp);
						final String result = exp;
						webView.post(new Runnable() {
							@Override
							public void run() {
								dialog.setTitle(word);
								Integer isFavorite = FavoriteWordPreference.getFavoriteWord(word, getActivity().getApplicationContext());
								if (isFavorite == FavoriteWordPreference.WORD_ACTIVE){
									favoriteBtn.setSelected(true);
								}
								else{
									favoriteBtn.setSelected(false);
								}
								webView.loadDataWithBaseURL("fake-url", result, "text/html", "UTF-8", null);
							}
						});
					}
				}).start();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.backBtn:
				if (wordStack.size() > 1){
					wordStack.pop();
					resultStack.pop();
					String word = wordStack.get(wordStack.size() - 1);
					String result = resultStack.get(resultStack.size() - 1);
					webView.loadDataWithBaseURL("fake-url", result, "text/html", "UTF-8", null);
					dialog.setTitle(word);
				}
				break;
			case R.id.favoriteBtn:
				String word = wordStack.get(wordStack.size() - 1);
				boolean isFavorite = favoriteBtn.isSelected();
				favoriteBtn.setSelected(!isFavorite);
				if (isFavorite){
					FavoriteWordPreference.clearFavoriteWord(word, getActivity().getApplicationContext());
				}
				else{
					FavoriteWordPreference.putFavoriteWord(word, getActivity().getApplicationContext());
				}
				break;
			case R.id.soundBtn:
				String toSpeak  = wordStack.get(wordStack.size() - 1);
				textToSpeechObj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
				break;
			default:
				break;
		}
	}
	@Override
	public void showAnotherWord(String word) {
		word = word.toLowerCase(Locale.ENGLISH);
		String currentWord = wordStack.get(wordStack.size() - 1);
		if (currentWord.equals(word)) 
			return;
		wordStack.push(word);
		showWord(word, true);
	}
}
