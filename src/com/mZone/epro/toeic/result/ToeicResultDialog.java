package com.mZone.epro.toeic.result;

import com.mZone.epro.R;
import com.mZone.epro.toeic.activity.ToeicTestActivity;
import com.mZone.epro.toeic.data.ToeicDataController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ToeicResultDialog extends DialogFragment {

	public static final String ARGS_LISTENING_RESULT = "com.mZone.epro.toeic.result.ToeicResultDialog.ARGS_LISTENING_RESULT";
	public static final String ARGS_READING_RESULT = "com.mZone.epro.toeic.result.ToeicResultDialog.ARGS_READING_RESULT";
	public static final String ARGS_TYPE = "com.mZone.epro.toeic.result.ToeicResultDialog.ARGS_TYPE";
	public static final int ARGS_TYPE_NORMAL = 0;
	public static final int ARGS_TYPE_REVIEWABLE = 1;
	
	private static final int[] PART_TEXTVIEW_ID = {R.id.toeic_result_dialog_part_1,
		R.id.toeic_result_dialog_part_2, 
		R.id.toeic_result_dialog_part_3, 
		R.id.toeic_result_dialog_part_4, 
		R.id.toeic_result_dialog_part_5, 
		R.id.toeic_result_dialog_part_6, 
		R.id.toeic_result_dialog_part_7, };
	
	private CustomResultPieChart listeningPie;
	private CustomResultPieChart readingPie;
	private float[] listeningResultScale;
	private float[] readingResultScale;
	private int[] listeningResult;
	private int[] readingResult;
	
	
	private int type = 0;
	
	public ToeicResultDialog() {
		// TODO Auto-generated constructor stub
	}

	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.finish_test_result_dialog_tittle);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View bodyView = inflater.inflate(R.layout.toeic_result_dialog, null);
		listeningPie = (CustomResultPieChart) bodyView.findViewById(R.id.toeic_result_listening_pie);
		readingPie = (CustomResultPieChart) bodyView.findViewById(R.id.toeic_result_reading_pie);
		builder.setView(bodyView);
		
		listeningResult = getArguments().getIntArray(ARGS_LISTENING_RESULT);
		readingResult = getArguments().getIntArray(ARGS_READING_RESULT);
		listeningResultScale = new float[listeningResult.length];
		readingResultScale = new float[readingResult.length];
		for (int i = 0; i < listeningResult.length; i++){
			listeningResultScale[i] = ((float)listeningResult[i])/ToeicDataController.LISTENING_QUESTION_NUMBER;
		}
		for (int i = 0; i < readingResult.length; i++){
			readingResultScale[i] = ((float)readingResult[i])/ToeicDataController.LISTENING_QUESTION_NUMBER;
		}
		
		type = getArguments().getInt(ARGS_TYPE);
		if (type == ARGS_TYPE_NORMAL){
			builder.setNegativeButton("OK", null);
		}
		else {
			builder.setNegativeButton(R.string.finish_test_result_dialog_quit_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Activity activity = getActivity();
					if (activity instanceof ToeicTestActivity){
						((ToeicTestActivity)activity).onShowResultDialogFinish();
					}
				}
			});
			builder.setPositiveButton(R.string.finish_test_result_dialog_review_btn, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Activity activity = getActivity();
					if (activity instanceof ToeicTestActivity){
						((ToeicTestActivity)activity).onShowResultDialogReview();
					}
				}
			});
		}
		
		for (int i = 0; i < PART_TEXTVIEW_ID.length; i++){
			TextView partTextview = (TextView) bodyView.findViewById(PART_TEXTVIEW_ID[i]);
			if (i < ToeicDataController.TOEIC_PART_5){
				partTextview.setText(getEachPartResultString(getActivity(), i, listeningResult[i]));
			}
			else{
				partTextview.setText(getEachPartResultString(getActivity(), i, readingResult[i - ToeicDataController.TOEIC_PART_5]));
			}
		}
		TextView listeningTv = (TextView)bodyView.findViewById(R.id.toeic_result_dialog_listening_score);
		listeningTv.setText(getListeningPartScoreString(getActivity(), listeningResult));
		TextView readingTv = (TextView)bodyView.findViewById(R.id.toeic_result_dialog_reading_score);
		readingTv.setText(getReadingPartScoreString(getActivity(), readingResult));
		TextView totalTv = (TextView)bodyView.findViewById(R.id.toeic_result_dialog_total_score);
		totalTv.setText(getTotalScoreString(getActivity(), listeningResult, readingResult));
		return builder.create();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		listeningPie.setPieScale(listeningResultScale);
		readingPie.setPieScale(readingResultScale);
	}
	
	public static String getEachPartResultString(final Context context, int part, int score){
		String label = context.getResources().getStringArray(R.array.toeic_result_dialog_toeic_part)[part];
		int numberOfQuestion = ToeicDataController.PART_QUESTION_NUMBER[part];
		return String.format("%s   %d/%d", label, score, numberOfQuestion).toString();
	}
	
	public static String getListeningPartScoreString(final Context context, int[] scores){
		String label = context.getResources().getString(R.string.toeic_result_dialog_listening_score);
		int listeningScore = context.getResources().getInteger(R.integer.listening_total_score);
		int score = 0;
		for (int i = 0; i < scores.length; i++){
			score += scores[i];
		}
		int revertScore = context.getResources().getIntArray(R.array.listening_score_table)[score];
		return String.format("%s   %d/%d", label, revertScore, listeningScore).toString();
	}
	
	public static String getReadingPartScoreString(final Context context, int[] scores){
		String label = context.getResources().getString(R.string.toeic_result_dialog_reading_score);
		int listeningScore = context.getResources().getInteger(R.integer.reading_total_score);
		int score = 0;
		for (int i = 0; i < scores.length; i++){
			score += scores[i];
		}
		int revertScore = context.getResources().getIntArray(R.array.reading_score_table)[score];
		return String.format("%s   %d/%d", label, revertScore, listeningScore).toString();
	}
	
	public static String getTotalScoreString(final Context context, int[] listeningScores, int[] readingScores){
		String label = context.getResources().getString(R.string.toeic_result_dialog_total_score);
		int totalScore = context.getResources().getInteger(R.integer.toeic_total_score);
		int listeningScore = 0;
		for (int i = 0; i < listeningScores.length; i++){
			listeningScore += listeningScores[i];
		}
		int readingScore = 0;
		for (int i = 0; i < readingScores.length; i++){
			readingScore += readingScores[i];
		}
		int revertTotalScore = context.getResources().getIntArray(R.array.listening_score_table)[listeningScore] + 
				context.getResources().getIntArray(R.array.reading_score_table)[readingScore];
		return String.format("%s   %d/%d", label, revertTotalScore, totalScore).toString();
	}

}
