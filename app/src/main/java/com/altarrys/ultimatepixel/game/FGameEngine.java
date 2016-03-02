package com.altarrys.ultimatepixel.game;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.altarrys.ultimatepixel.R;
import com.altarrys.ultimatepixel.opengl.GLBackground;

import java.util.List;


public class FGameEngine extends Fragment implements OnTouchListener
{
	private static final String TAG = "FGameEngine";

	private GridView m_pixelGridView;
	private Level m_levelManager;
	private boolean m_isStarted;
	private AGameEngine parent;
	private GLBackground mGlBackground;
	private RelativeLayout mTargetLayout;
	private int mTimerModifId;

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

		mTargetLayout = (RelativeLayout) rootView.findViewById(R.id.layout_targetlist);

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

		((TextView)rootView.findViewById(R.id.textview_timeelapsed)).setText("" + AGameEngine.TIME_TOT / 1000 + ":00");

		// set Up GLBackgound
		mGlBackground = new GLBackground(getActivity(), R.raw.fire_frag_shader);
		((FrameLayout) rootView.findViewById(R.id.gameengimelayout)).addView(mGlBackground, 0);

		mTimerModifId = View.generateViewId();

		return rootView;
	}

	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onPause() {
		super.onPause();
		mGlBackground.onPause();
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public void onResume() {
		super.onResume();
		mGlBackground.onResume();
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
			fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
			fadeOut.setDuration(1000);
			fadeOut.setAnimationListener(new ChangeColorAfterAnim(pixel));
			fadeOut.setFillAfter(false);

			// Animation to remove to current color and replace with a new random one
			pixel.startAnimation(fadeOut);

			// Update score
			score = m_levelManager.pixelTouched();

			// Modify pixel target color
			setUpTargetView(getActivity().getWindow().getDecorView());

			// Add time to timer
			parent.addTime(parent.ADD_TIME);

			// Show the timer modif in green to the user
			addTimerModifView(true);
		}
		else
		{
			m_levelManager.setLastFail();
			parent.removeTime(parent.REMOVE_TIME);

			// Show the timer modif in red to the user
			addTimerModifView(false);
		}

		// Set background shader progress
		mGlBackground.setProgress(m_levelManager.getStreakProgress());
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void addTimerModifView(boolean add) {

		View lastView = parent.findViewById(mTimerModifId);

		if(lastView != null)
			mTargetLayout.removeViewInLayout(lastView);

		TextView text = new TextView(parent);

		RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		param.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
		param.addRule(RelativeLayout.RIGHT_OF, R.id.textview_timeelapsed);
		text.setLayoutParams(param);
		text.setId(mTimerModifId);

		if (add) {
			text.setTextColor(getResources().getColor(R.color.Green));
			text.setText("+1");

		}
		else {
			text.setTextColor(getResources().getColor(R.color.Red));
			text.setText("-1");
		}

		mTargetLayout.addView(text);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public class ChangeColorAfterAnim implements Animation.AnimationListener
	{
		PixelTile mPixel;
		int nextColor;

		public ChangeColorAfterAnim(PixelTile pixel) {
			mPixel = pixel;
		}

		@Override
		public void onAnimationStart(Animation animation) {
			// set next random color at the start to avoid problem when click during the animation
			nextColor = getResources().getColor(m_levelManager.getRandomColor());
			mPixel.setColor(nextColor);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// Set color and update view
			mPixel.updateColor(nextColor);
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
				//((GradientDrawable)v.getBackground()).setColor(getResources().getColor(myPixel));
				((PixelTile)v).updateColor(getResources().getColor(myPixel));
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
		((PixelTile)root.findViewById(R.id.textview_askedcolorview)).updateColor(getResourceTargetPixel(0));
		((PixelTile)root.findViewById(R.id.textview_nextcolorview)).updateColor(getResourceTargetPixel(1));
		((PixelTile)root.findViewById(R.id.textview_nextnextcolorview)).updateColor(getResourceTargetPixel(2));
		((PixelTile)root.findViewById(R.id.textview_nextnextnextcolorview)).updateColor(getResourceTargetPixel(3));
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
}