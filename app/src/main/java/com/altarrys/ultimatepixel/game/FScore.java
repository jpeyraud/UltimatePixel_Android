package com.altarrys.ultimatepixel.game;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.altarrys.ultimatepixel.R;


public class FScore extends Fragment
{
	private AScore m_parent;

	//-----------------------------------------------------------------------------------------------------------------------------
	public FScore() 
	{

	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_score,	container, false);
		
		// Put the parent activity in Attribute
		m_parent = ((AScore)getActivity());

		// Load scores
		String arcadeScoreNormal = load("arcadenormal");
		String arcadeScoreHard = load("arcadehard");
		String arcadeScoreExtreme = load("arcadeextreme");
		
		// Get all textviews to display score
		TextView scoreArcadeNormal = ((TextView)rootView.findViewById(R.id.textview_arcadescorenormal));
		TextView scoreArcadeHard = ((TextView)rootView.findViewById(R.id.textview_arcadescorehard));
		TextView scoreArcadeExtreme = ((TextView)rootView.findViewById(R.id.textview_arcadescoreextreme));
		
		// Add scores to the textviews
		scoreArcadeNormal.setText(scoreArcadeNormal.getText()+arcadeScoreNormal);
		scoreArcadeHard.setText(scoreArcadeHard.getText()+arcadeScoreHard);
		scoreArcadeExtreme.setText(scoreArcadeExtreme.getText()+arcadeScoreExtreme);
		
		return rootView;
	}
    //-----------------------------------------------------------------------------------------------------------------------------
	public String load(String key)
	{
		SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFERENCES_ID, Context.MODE_PRIVATE);
		return prefs.getString(key, "0");
	}
	//-----------------------------------------------------------------------------------------------------------------------------
}