package com.altarrys.ultimatepixel;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


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
		String classicScoreNormal = load("classicnormal");
		String classicScoreHard = load("classichard");
		String classicScoreExtreme = load("classicextreme");
		String arcadeScoreNormal = load("arcadenormal");
		String arcadeScoreHard = load("arcadehard");
		String arcadeScoreExtreme = load("arcadeextreme");
		
		// Get all textviews to display score
		TextView scoreClassicNormal = ((TextView)rootView.findViewById(R.id.textview_classicscorenormal));
		TextView scoreClassicHard = ((TextView)rootView.findViewById(R.id.textview_classicscorehard));
		TextView scoreClassicExtreme = ((TextView)rootView.findViewById(R.id.textview_classicscoreextreme));
		TextView scoreArcadeNormal = ((TextView)rootView.findViewById(R.id.textview_arcadescorenormal));
		TextView scoreArcadeHard = ((TextView)rootView.findViewById(R.id.textview_arcadescorehard));
		TextView scoreArcadeExtreme = ((TextView)rootView.findViewById(R.id.textview_arcadescoreextreme));
		
		// Add scores to the textviews
		scoreClassicNormal.setText(scoreClassicNormal.getText()+classicScoreNormal);
		scoreClassicHard.setText(scoreClassicHard.getText()+classicScoreHard);
		scoreClassicExtreme.setText(scoreClassicExtreme.getText()+classicScoreExtreme);
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