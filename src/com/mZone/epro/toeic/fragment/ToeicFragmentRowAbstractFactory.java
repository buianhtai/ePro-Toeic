package com.mZone.epro.toeic.fragment;

import com.mZone.epro.toeic.customInterface.ToeicMediaListener;
import com.mZone.epro.toeic.data.ToeicDataController;

public class ToeicFragmentRowAbstractFactory {
	public static ToeicFragmentRowAbstract getFragment(int partIndex, int pagerIndex, ToeicMediaListener listener){
		if (partIndex == ToeicDataController.TOEIC_PART_1){
			if (pagerIndex == 0){
				return new ToeicFragmentRowContentPart_1(partIndex);
			}
			else{
				return new ToeicFragmentRowScriptPart_1(partIndex, listener);
			}
		}
		else if (partIndex == ToeicDataController.TOEIC_PART_2){
			if (pagerIndex == 0){
				return new ToeicFragmentRowContentPart_2(partIndex, listener);
			}
			else{
				return new ToeicFragmentRowScriptPart_2(partIndex, listener);
			}
		}
		else if (partIndex == ToeicDataController.TOEIC_PART_3){
			if (pagerIndex == 0){
				return new ToeicFragmentRowContentPart_3(partIndex);
			}
			else{
				return new ToeicFragmentRowScriptPart_3(partIndex, listener);
			}
		}
		else if (partIndex == ToeicDataController.TOEIC_PART_4){
			if (pagerIndex == 0){
				return new ToeicFragmentRowContentPart_4(partIndex);
			}
			else{
				return new ToeicFragmentRowScriptPart_4(partIndex, listener);
			}
		}
		else if (partIndex == ToeicDataController.TOEIC_PART_5){
			if (pagerIndex == 0){
				return new ToeicFragmentRowContentPart_5(partIndex);
			}
		}
		return null;
	}

}
