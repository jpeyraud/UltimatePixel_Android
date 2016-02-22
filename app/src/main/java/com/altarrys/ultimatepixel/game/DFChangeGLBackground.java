package com.altarrys.ultimatepixel.game;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.altarrys.ultimatepixel.R;


public class DFChangeGLBackground extends DialogFragment implements DialogInterface.OnClickListener
{
	private MainFragment mFrag;

	public void setFragment(MainFragment frag){
		mFrag = frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		builder.setTitle(R.string.dfglbackground);
		builder.setPositiveButton(R.string.dfchangebackground, this);
		builder.setNegativeButton(R.string.dfkeepbackground, this);

		return builder.create();
	}
	@Override
	public void onClick(DialogInterface dialog, int which) 
	{
		if (which == DialogInterface.BUTTON_POSITIVE)
			mFrag.changeFragShader();
	}
}
