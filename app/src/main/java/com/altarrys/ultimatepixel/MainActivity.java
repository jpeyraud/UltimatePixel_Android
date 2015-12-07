package com.altarrys.ultimatepixel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
	public static class PlaceholderFragment extends Fragment implements View.OnTouchListener
	{
		public PlaceholderFragment() 
		{

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container,	false);
			
			// Set OnClickListener for all buttons
			((Button) rootView.findViewById(R.id.playButton)).setOnTouchListener(this);
			((Button) rootView.findViewById(R.id.modeArcadeButton)).setOnTouchListener(this);
			((Button) rootView.findViewById(R.id.ScoreButton)).setOnTouchListener(this);
			
			return rootView;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			Log.d("TAG", "on press");
			if(event.getAction() == MotionEvent.ACTION_DOWN)
			{
				// put on pressed background on press
				if (v.getId() == R.id.playButton)
					((Button) getActivity().findViewById(R.id.playButton)).setBackgroundResource(R.drawable.blue_pressed_button);
				else if (v.getId() == R.id.modeArcadeButton)
					((Button) getActivity().findViewById(R.id.modeArcadeButton)).setBackgroundResource(R.drawable.red_pressed_button);
				else if (v.getId() == R.id.ScoreButton)
					((Button) getActivity().findViewById(R.id.ScoreButton)).setBackgroundResource(R.drawable.green_pressed_button);
			}
			else if (event.getAction() == MotionEvent.ACTION_UP)
			{
				// come back to original background on release
				((Button) getActivity().findViewById(R.id.playButton)).setBackgroundResource(R.drawable.blue_button);
				((Button) getActivity().findViewById(R.id.modeArcadeButton)).setBackgroundResource(R.drawable.red_button);
				((Button) getActivity().findViewById(R.id.ScoreButton)).setBackgroundResource(R.drawable.green_button);

				if (v.getId() == R.id.playButton)
				{
					Intent intent = new Intent(getActivity(), ALevelChoice.class);

					// Add the index of the checked hardness to the intent
					//intent.putExtra("hardness", ((RadioGroup)getActivity().findViewById(R.id.Hardness)).getCheckedRadioButtonId());

					// Add the game mode to the intent
					//intent.putExtra("gamemode", "classic");

					startActivity(intent);
					return true;
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




			return true;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------

	//-----------------------------------------------------------------------------------------------------------------------------

}
