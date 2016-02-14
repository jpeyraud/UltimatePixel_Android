package com.altarrys.ultimatepixel.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.altarrys.ultimatepixel.R;


public class DFVictoryScore extends DialogFragment implements android.content.DialogInterface.OnClickListener
{
	private int m_score;

	public DFVictoryScore()
	{
		m_score = 0;
	}

	public void setScore(int score)
	{
		m_score = score;
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
