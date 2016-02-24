package com.altarrys.ultimatepixel.game;

import android.animation.Animator;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.altarrys.ultimatepixel.R;
import com.altarrys.ultimatepixel.opengl.MenuGLSurface;

import java.util.List;


public class FGameEngine extends Fragment implements OnTouchListener
{
	private static final String TAG = "FGameEngine";

	private GridView m_pixelGridView;
	private Level m_levelManager;
	private boolean m_isStarted;
	private AGameEngine parent;
	private MenuGLSurface mGlMenuBackground;

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
		PixelArrayAdapter adapter = new PixelArrayAdapter(getActivity(), R.id.Pixel , m_levelManager.getPixelList(), m_pixelGridView);
		m_pixelGridView.setAdapter(adapter);
		m_pixelGridView.setOnTouchListener(this);

		setUpTargetView(rootView);

		// Set timer textview in activity attributes to set the timer of the game in a thread later
		parent.setTimer((TextView) rootView.findViewById(R.id.textview_timeelapsed));

		((TextView)rootView.findViewById(R.id.textview_timeelapsed)).setText(""+AGameEngine.TIME_TOT/1000+":00");

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
        float currentXPosition = me.getX();
        float currentYPosition = me.getY();
        int position = m_pixelGridView.pointToPosition((int) currentXPosition, (int) currentYPosition);

		PixelTile tv = (PixelTile) m_pixelGridView.getChildAt(position);
		
        if (me.getActionMasked() == MotionEvent.ACTION_DOWN && tv !=null)
        {
			handleTouch(tv);
        }
		return false;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void handleTouch(PixelTile pixel) {
		int score;

		// if color of touched pixel  is the same as the target color, delete it
		if (pixel.getColor() == getResourceTargetPixel()) {

			Animation fadeOut = new AlphaAnimation(1, 0);
			fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
			fadeOut.setDuration(1000);
			fadeOut.setAnimationListener(new ChangeColorAfterAnim(pixel));

			// Animation to remove to current color and replace with a new random one
			pixel.startAnimation(fadeOut);

			// Update score
			score = m_levelManager.pixelTouched();

			// Modify pixel target color
			setUpTargetView(getActivity().getWindow().getDecorView());

			// Add time to timer
			parent.addTime(parent.ADD_TIME);
		}
		else
		{
			parent.removeTime(parent.REMOVE_TIME);
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public class ChangeColorAfterAnim implements Animation.AnimationListener
	{
		PixelTile mPixel;

		public ChangeColorAfterAnim(PixelTile pixel) {
			mPixel = pixel;
		}

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// Replaced the touched pixel by a random color
			mPixel.setColor(getResources().getColor(m_levelManager.getRandomColor()));
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	// Inherit from "ArrayAdapter" to adapt the view to display many Buttons
    public class PixelArrayAdapter extends ArrayAdapter<Integer>
    {
		GridView mParent;

		public PixelArrayAdapter(Context context, int resource, List<Integer> objects, GridView parent)
		{
			super(context, resource, objects);
			mParent = parent;
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
				((GradientDrawable)v.getBackground()).setColor(getResources().getColor(myPixel));
				((PixelTile)v).setColor(getResources().getColor(myPixel));
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
	public void setUpTargetView(View root){
		// Display targets color
		((PixelTile)root.findViewById(R.id.textview_askedcolorview)).setColor(getResourceTargetPixel(0));
		((PixelTile)root.findViewById(R.id.textview_nextcolorview)).setColor(getResourceTargetPixel(1));
		((PixelTile)root.findViewById(R.id.textview_nextnextcolorview)).setColor(getResourceTargetPixel(2));
		((PixelTile)root.findViewById(R.id.textview_nextnextnextcolorview)).setColor(getResourceTargetPixel(3));
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
}