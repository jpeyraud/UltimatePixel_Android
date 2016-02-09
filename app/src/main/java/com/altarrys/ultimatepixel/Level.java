package com.altarrys.ultimatepixel;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Level 
{
	public static int NORMAL_MODE = 1;
	public static int HARD_MODE = 2;
	public static int EXTREME_MODE = 3;
	
	
	private int m_game_difficulty;
	private ArrayList<Integer> m_targetPixels;
	private int m_nbTargetPixel;
	private ArrayList<Integer> m_pixelList;
	private ArrayList<Integer> m_allColors;
	private int m_nbPixelColorTotal;
	private HashMap<Integer,Integer> m_pixelColorNumberMap;
	private int m_arcadePerCent = 50;

	public int score;

	//-----------------------------------------------------------------------------------------------------------------------------
	public Level(int gameDifficulty, int nbPixelColor, int nbPixelLine)
	{
		m_game_difficulty = gameDifficulty;

		m_nbPixelColorTotal = nbPixelColor;

		m_nbTargetPixel = 3;

		// number of each solor
		m_pixelColorNumberMap = new HashMap<Integer,Integer>();

		initColors();
		
		// Generate the level
		m_pixelList = new ArrayList<Integer>();

		for (int i = 0 ; i < nbPixelLine*5 ; i++)
		{
			int color = getRandomColor();
			m_pixelList.add(color);
		}

		// Init targets
		m_targetPixels = new ArrayList<Integer>();
		for (int i = 0; i<m_nbTargetPixel; i++)
			m_targetPixels.add(getTargetRandomColor());

		score = 0;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	private int getTargetRandomColor()
	{
		int choice = (int) (Math.random()*(double) m_nbPixelColorTotal); // care to rand = 1.0

		Log.d("TAG", "target remaining : " + m_pixelColorNumberMap.get(m_allColors.get(choice)));

		if (m_pixelColorNumberMap.get(m_allColors.get(choice)) == 0)
			return getTargetRandomColor();

		return m_allColors.get(choice);
	}
    //-----------------------------------------------------------------------------------------------------------------------------
    public int getRandomColor()
    {
    	int choice = (int) (Math.random()*(double) m_nbPixelColorTotal); // care to rand = 1.0

		int color =  m_allColors.get(choice);

		// Increment the number of the touched pixel
		m_pixelColorNumberMap.put(color, m_pixelColorNumberMap.get(color) + 1);

		return color;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int getCurrentTargetPixel()
    {
    	return m_targetPixels.get(0);
    }
	//-----------------------------------------------------------------------------------------------------------------------------
	public ArrayList<Integer> getAllTargetPixel()
	{
		return m_targetPixels;
	}
    //-----------------------------------------------------------------------------------------------------------------------------
    public int countTargetPixel()
    {
    	int cpt=0;
    	for (int i=0 ; i < m_pixelList.size() ; i++)
    	{
    		if(m_pixelList.get(i)== getCurrentTargetPixel())
    			cpt++;
    	}
    	return cpt;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public ArrayList<Integer> getPixelList()
    {
    	return m_pixelList;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int pixelTouched()
    {
		// Decrement the number of the touched pixel
		m_pixelColorNumberMap.put(getCurrentTargetPixel(), m_pixelColorNumberMap.get(getCurrentTargetPixel())-1);

		// Get a new random target
		m_targetPixels.remove(0);
		m_targetPixels.add(getTargetRandomColor());

    	return score++;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int getScore()
    {
    	return score;
    }
	//-----------------------------------------------------------------------------------------------------------------------------
	public void initColors()
	{
		m_allColors = new ArrayList<Integer>();

		m_allColors.add(R.color.MediumBlue);
		m_allColors.add(R.color.MediumRed);
		m_allColors.add(R.color.MediumGreen);
		m_allColors.add(R.color.Yellow);
		m_allColors.add(R.color.Cyan);
		m_allColors.add(R.color.DarkMagenta);
		m_allColors.add(R.color.Grey);
		m_allColors.add(R.color.Pink);
		m_allColors.add(R.color.Black);
		m_allColors.add(R.color.Orange);
		m_allColors.add(R.color.Purple);
		m_allColors.add(R.color.White);

		m_pixelColorNumberMap.put(R.color.MediumBlue, 0);
		m_pixelColorNumberMap.put(R.color.MediumRed, 0);
		m_pixelColorNumberMap.put(R.color.MediumGreen, 0);
		m_pixelColorNumberMap.put(R.color.Yellow, 0);
		m_pixelColorNumberMap.put(R.color.Cyan, 0);
		m_pixelColorNumberMap.put(R.color.Grey, 0);
		m_pixelColorNumberMap.put(R.color.DarkMagenta, 0);
		m_pixelColorNumberMap.put(R.color.Pink, 0);
		m_pixelColorNumberMap.put(R.color.Black, 0);
		m_pixelColorNumberMap.put(R.color.Orange,0);
		m_pixelColorNumberMap.put(R.color.Purple,0);
		m_pixelColorNumberMap.put(R.color.White,0);
	}
    //-----------------------------------------------------------------------------------------------------------------------------
}
