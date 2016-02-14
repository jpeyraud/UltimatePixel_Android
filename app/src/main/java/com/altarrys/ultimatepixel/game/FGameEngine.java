package com.altarrys.ultimatepixel.game;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.altarrys.ultimatepixel.R;

import java.util.List;


public class FGameEngine extends Fragment implements OnTouchListener
{
	private GridView m_pixelGridView;
	private Level m_levelManager;
	private boolean m_isStarted;
	private AGameEngine parent;

	//-----------------------------------------------------------------------------------------------------------------------------
	public FGameEngine()
	{
		m_isStarted = false;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void setLevelManager(Level level)
	{
		m_levelManager = level;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		View rootView = inflater.inflate(R.layout.fragment_game_engine,	container, false);
		
		// Put the parent activity in Attribute
		parent = ((AGameEngine)getActivity());
		
		// Get the GridView and set the adapter to display more than one Button dynamically
		m_pixelGridView = (GridView) rootView.findViewById(R.id.imagegridview);
		PixelArrayAdapter adapter = new PixelArrayAdapter(getActivity(), R.id.Pixel , m_levelManager.getPixelList());
		m_pixelGridView.setAdapter(adapter);
		m_pixelGridView.setOnTouchListener(this);
		
		// Display targets color
		rootView.findViewById(R.id.textview_askedcolorview).setBackgroundColor(getResourceTargetPixel(0));
		rootView.findViewById(R.id.textview_nextcolorview).setBackgroundColor(getResourceTargetPixel(1));
		rootView.findViewById(R.id.textview_nextnextcolorview).setBackgroundColor(getResourceTargetPixel(2));

		// Set timer textview in activity attributes to set the timer of the game in a thread later
		parent.setTimer((TextView) rootView.findViewById(R.id.textview_timeelapsedview));

		((TextView)rootView.findViewById(R.id.textview_timeelapsed)).setText(R.string.timeremaining);
		((TextView)rootView.findViewById(R.id.textview_timeelapsedview)).setText(""+AGameEngine.TIME_TOT/1000);

		return rootView;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onTouch(View v, MotionEvent me) 
	{
		// Start timer in activity
		if (m_isStarted == false)
		{
			m_isStarted = true;
			parent.startReverseTimerThread();
		}
		
		// Hand made pixel detection
		int action = me.getActionMasked(); 
        float currentXPosition = me.getX();
        float currentYPosition = me.getY();
        int position = m_pixelGridView.pointToPosition((int) currentXPosition, (int) currentYPosition);

        TextView tv = (TextView) m_pixelGridView.getChildAt(position);
		
        if (me.getActionMasked() == MotionEvent.ACTION_DOWN && tv !=null)
        {
        	ColorDrawable draw = (ColorDrawable) tv.getBackground();
			handleTouch(draw, tv);
        }
		return false;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void handleTouch(ColorDrawable draw, TextView tv) {
		int score;

		// if color of touched pixel  is the same as the target color, delete it
		if (draw.getColor() == getResourceTargetPixel()) {
			// Replaced the touched pixel by a random color
			tv.setBackgroundColor(getResources().getColor(m_levelManager.getRandomColor()));
			score = m_levelManager.pixelTouched();

			// Modify pixel target color
			getActivity().findViewById(R.id.textview_askedcolorview).setBackgroundColor(getResourceTargetPixel(0));
			getActivity().findViewById(R.id.textview_nextcolorview).setBackgroundColor(getResourceTargetPixel(1));
			getActivity().findViewById(R.id.textview_nextnextcolorview).setBackgroundColor(getResourceTargetPixel(2));

			//parent.setTimerColor(R.color.Green);
			parent.addTime(parent.ADD_TIME - (100 * (score / 20)));
			Log.d("TAG", ""+(parent.ADD_TIME - (100 * (score / 20))));

		}
		else
		{
			//parent.setTimerColor(R.color.Red);
			parent.removeTime(parent.REMOVE_TIME);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------	
	// Inherit from "ArrayAdapter" to adapt the view to display many Buttons
    public class PixelArrayAdapter extends ArrayAdapter<Integer>
    {
		public PixelArrayAdapter(Context context, int resource, List<Integer> objects) 
		{
			super(context, resource, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			// Get the Button at the provided position
			Integer myPixel = getItem(position);
			
			View v;
			
			// Load the view only if one was not already loaded
			if (convertView == null) 
			{
				v = LayoutInflater.from(getContext()).inflate(R.layout.pixel, null);
				v.setBackground(new ColorDrawable(getResources().getColor(myPixel)));
			}
			else	
			{
				v=convertView;
			}
			return v;
		}
    }
	//-----------------------------------------------------------------------------------------------------------------------------
    public int getResourceTargetPixel()
    {
    	// Get the target color value
    	return getResources().getColor(m_levelManager.getCurrentTargetPixel());
    }
	//-----------------------------------------------------------------------------------------------------------------------------
	public int getResourceTargetPixel(int index)
	{
		// Get the target color value
		return getResources().getColor(m_levelManager.getAllTargetPixel().get(index));
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
}