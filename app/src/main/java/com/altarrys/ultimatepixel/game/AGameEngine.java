package com.altarrys.ultimatepixel.game;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.altarrys.ultimatepixel.R;

public class AGameEngine extends Activity 
{
	public static long TIME_TOT = 10000;
	public static int ADD_TIME = 1000;
	public static int REMOVE_TIME = 1000;
	public static String HARDNESS = "";
	
	private TextView m_timer;
	private Level m_levelManager;
	private Thread m_th;
	private ReverseTimerRunnable m_thRun;
	private boolean m_isRunning;
	private int m_colorNumber;
	private float m_maxVictoryTime;
	private int m_pixelLineNumber;

	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_engine);
		
		m_isRunning = true;

		// get the hardness selected before
		Intent intent = getIntent();
		int hardnessId = intent.getIntExtra("hardness", -2);

		// Get the hardness of the level and initialize the LevelManager
		HARDNESS = "hard";
		m_colorNumber = 6;
		m_pixelLineNumber = 6;
		m_levelManager = new Level(Level.NORMAL_MODE, m_colorNumber, m_pixelLineNumber);

		if (savedInstanceState == null) 
		{
			FGameEngine fGE = new FGameEngine();
			fGE.setLevelManager(m_levelManager);
			getFragmentManager().beginTransaction().add(R.id.container, fGE).commit();
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.agame_engine, menu);
		return true;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int id = item.getItemId();
		if (id == R.id.action_settings) 
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void setTimer(TextView m_timer) {
		this.m_timer = m_timer;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void setTimerColor(int color) {
		m_timer.setTextColor(color);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public TextView getTimer() {
		return m_timer;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void stopTimer()
	{
		m_isRunning = false;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public int getScore()
	{
		return m_levelManager.getScore();
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void save(String key, String val)
	{
		SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENCES_ID, Context.MODE_PRIVATE);
		float old = Float.parseFloat(prefs.getString(key, "0.0"));

		// If better
		if (old < Float.parseFloat(val))
		{
			SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFERENCES_ID,Context.MODE_PRIVATE).edit();
			editor.putString(key,val).apply();
		}
		
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void startTimerThread() 
	{ 
		m_th = new Thread(new Runnable()
		{
			private long startTime = System.currentTimeMillis();
			public void run() 
			{     
				while(m_isRunning)
				{
					runOnUiThread(new Runnable() 
					{
						@Override
						public void run() 
						{
							m_timer.setText("" + ((float) (((int) ((System.currentTimeMillis() - startTime) * 100.0)) / 1000)) / 100.0);
						}
					});
					try {
						Thread.sleep(10);
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		m_th.start();
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void startReverseTimerThread() 
	{
		m_thRun = new ReverseTimerRunnable(this);
		m_th = new Thread(m_thRun);
		m_th.start();
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void addTime(int ms)
	{
		if (ms < 100)
			m_thRun.increaseTimer(100);
		else
			m_thRun.increaseTimer(ms);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void removeTime(int ms)
	{
		m_thRun.decreaseTimer(ms);
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public class ReverseTimerRunnable implements Runnable
	{
		private long startTime = System.currentTimeMillis();
		private long totTime = AGameEngine.TIME_TOT;
		private AGameEngine GE;
		
		public ReverseTimerRunnable(AGameEngine GE) 
		{
			this.GE = GE;
		}
		public void run() 
		{     
			while(m_isRunning)
			{
				runOnUiThread(new RevTimeRunnable(GE));
				try {
					Thread.sleep(100);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		public class RevTimeRunnable implements Runnable
		{
			AGameEngine GE;
			
			public RevTimeRunnable(AGameEngine GE)
			{
				this.GE = GE;
			}
			
			@Override
			public void run() 
			{
				double res = ((float)(((int)(((totTime - (System.currentTimeMillis()-startTime)))*10.0))/1000))/10.0;
				m_timer.setText("" + res);
				
				// if it doesn't remain time stop the level
				if (res < 0.1)
				{
					// save score
					//GE.save(HARDNESS, ""+GE.getScore());

					DFVictoryScore dfVic = new DFVictoryScore();
					dfVic.setScore(GE.getScore());
			    	dfVic.show(GE.getFragmentManager(), "id");

		    		// Stop Timer Thread
		    		GE.stopTimer();
				}
			}
		}

		public void increaseTimer(int ms)
		{
			startTime += ms;
		}


		public void decreaseTimer(int ms)
		{
			startTime -= ms;
		}
	}
}