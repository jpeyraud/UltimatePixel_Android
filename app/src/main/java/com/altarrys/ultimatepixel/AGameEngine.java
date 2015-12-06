package com.altarrys.ultimatepixel;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AGameEngine extends Activity 
{
	public static long TIME_TOT = 30000;
	public static String GAME_MODE = "";
	public static String HARDNESS = "";
	
	private TextView m_timer;
	private Level m_levelManager;
	private Thread m_th;
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
		GAME_MODE = intent.getStringExtra("gamemode");
		Log.w("MyError", GAME_MODE);

		// Get the selected level name and Id
		String levelName = intent.getStringExtra("levelName");
		int levelId = intent.getIntExtra("levelId", -1);

		// Automatically generate level caracteristics
		generateLevel(levelId);

		// Get the hardness of the level and initialize the LevelManager
		HARDNESS = "normal";
		m_levelManager = new Level(Level.NORMAL_MODE, m_colorNumber, m_pixelLineNumber);

		if (savedInstanceState == null) 
		{
			getFragmentManager().beginTransaction().add(R.id.container,  new FGameEngine(m_levelManager)).commit();
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
	public void generateLevel(int levelId)
	{
		if (levelId > 5)
		{
			m_colorNumber = ((levelId-1)/5)+3;
			m_pixelLineNumber = (levelId % 5) + 4;
			m_maxVictoryTime = 0.0f;
		}
		else
		{
			m_colorNumber = 3;
			m_pixelLineNumber = 2;
			m_maxVictoryTime = 0.0f;
		}
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public void setM_timer(TextView m_timer) {
		this.m_timer = m_timer;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	public TextView getM_timer() {
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
		if (old > Float.parseFloat(val) && GAME_MODE.equals("classic"))
		{
			SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFERENCES_ID,Context.MODE_PRIVATE).edit();
			editor.putString(key,val).apply();
		}
		// If better
		if (old < Float.parseFloat(val) && GAME_MODE.equals("arcade"))
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
		m_th = new Thread(new ReverseTimerRunnable(this));
		m_th.start();
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
					GE.save(GAME_MODE+HARDNESS, ""+GE.getScore());

					DFVictoryScore dfVic = new DFVictoryScore(GE.getScore());
			    	dfVic.show(GE.getFragmentManager(), "id");

		    		// Stop Timer Thread
		    		GE.stopTimer();
				}
			}
		}
		
		
		
	}
}
