package com.altarrys.ultimatepixel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


public class MainActivity extends Activity 
{
	
	public static String PREFERENCES_ID = "myPref";

	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) 
		{
			getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener 
	{
		public PlaceholderFragment() 
		{

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			
			// Set OnClickListener for all buttons
			((Button) rootView.findViewById(R.id.playButton)).setOnClickListener(this);
			((Button) rootView.findViewById(R.id.modeArcadeButton)).setOnClickListener(this);
			((Button) rootView.findViewById(R.id.ScoreButton)).setOnClickListener(this);
			
			return rootView;
		}

		@Override
		public void onClick(View v) 
		{
			if (v.getId() == R.id.playButton)
			{
				Intent intent = new Intent(getActivity(), ALevelChoice.class);

				// Add the index of the checked hardness to the intent
				//intent.putExtra("hardness", ((RadioGroup)getActivity().findViewById(R.id.Hardness)).getCheckedRadioButtonId());
				
				// Add the game mode to the intent
				//intent.putExtra("gamemode", "classic");
				
				startActivity(intent);
			}
			else if (v.getId() == R.id.modeArcadeButton)
			{
				Intent intent = new Intent(getActivity(), AGameEngine.class);
				
				// Add the index of the checked hardness to the intent
				intent.putExtra("hardness", "normal");//((RadioGroup)getActivity().findViewById(R.id.Hardness)).getCheckedRadioButtonId());
				
				// Add the game mode to the intent
				intent.putExtra("gamemode", "arcade");
				
				startActivity(intent);
			}
			else if (v.getId() == R.id.ScoreButton)
			{
				Intent intent = new Intent(getActivity(), AScore.class);
				
				startActivity(intent);
			}
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------

	//-----------------------------------------------------------------------------------------------------------------------------

}
