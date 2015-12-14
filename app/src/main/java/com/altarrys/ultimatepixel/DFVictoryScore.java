package com.altarrys.ultimatepixel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;


public class DFVictoryScore extends DialogFragment implements android.content.DialogInterface.OnClickListener
{
	private int m_score;

	public DFVictoryScore(){}
	
	public DFVictoryScore(int sco)
	{
		m_score = sco;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(getString(R.string.dfvictoryscore)+" "+ m_score);
		builder.setPositiveButton(R.string.dfcontinue,  this);
		
		return builder.create();
	}
	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
		getActivity().finish();
	}
}
