package com.altarrys.ultimatepixel;

import java.util.ArrayList;

import android.graphics.drawable.ColorDrawable;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class Level 
{
	public static int NORMAL_MODE = 1;
	public static int HARD_MODE = 2;
	public static int EXTREME_MODE = 3;
	
	
	private int m_game_difficulty;
	private int m_targetPixel;
	private int m_nbTargetPixel;
	private ArrayList<Integer> m_pixelList;
	private ArrayList<Integer> m_allAdventureColors;
	private int m_nbPixelColorRemaining;
	private int m_nbPixelColorTotal;
	private ArrayList<Integer> m_pixelColorDeletedList;
	private int m_arcadePerCent = 50;

	public int score;

	//-----------------------------------------------------------------------------------------------------------------------------
	public Level(int gameDifficulty, int nbPixelColor, int nbPixelLine)
	{
		m_game_difficulty = gameDifficulty;

		m_nbPixelColorTotal = nbPixelColor;
		m_nbPixelColorRemaining = m_nbPixelColorTotal;

		
		m_pixelColorDeletedList = new ArrayList<Integer>();

		initAdventureColors();
		
		// Generate the level
		m_pixelList = new ArrayList<Integer>();

		for (int i = 0 ; i < nbPixelLine*5 ; i++)
		{
			m_pixelList.add(getRandomColor());
		}
		
		m_targetPixel = getRandomColor();
		m_nbTargetPixel = countTargetPixel();
		score = 0;
	}
	//-----------------------------------------------------------------------------------------------------------------------------
    public int getRandomColor()
    {
		if (m_game_difficulty == Level.HARD_MODE)
		{
			return getRandomColor_H();
		}
		else if (m_game_difficulty == Level.EXTREME_MODE)
		{
			return getRandomColor_E();
		}
		else //if (game_mode == Level.NORMAL_MODE)
		{
			return getRandomColorAdventure();
		}
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    private int getRandomColorAdventure()
    {
    	int choice = (int) (Math.random()*(double) m_nbPixelColorRemaining); // care to rand = 1.0

		return m_allAdventureColors.get(choice);
    }
	//-----------------------------------------------------------------------------------------------------------------------------
	private int getRandomColor_H()
	{
		int choice = R.color.MediumBlue;
		int rand = (int) (Math.random()*(double) m_nbPixelColorTotal);

		switch (rand)
		{
			case 0:
				choice = R.color.MediumBlue;
				break;
			case 1:
				choice = R.color.MediumRed;
				break;
			case 2:
				choice = R.color.MediumGreen;
				break;
			case 3:
				choice = R.color.DarkYellow;
				break;
			case 4:
				choice = R.color.DarkCyan;
				break;
			case 5:
				choice = R.color.DarkMagenta;
				break;
			default:
				choice = R.color.DarkBlue; // blue is life
				break;
		}

		if (m_pixelColorDeletedList.contains(choice))
		{
			return getRandomColor();
		}
		else
		{
			return choice;
		}
	}
    //-----------------------------------------------------------------------------------------------------------------------------
    private int getRandomColor_N()
    {
    	int choice = R.color.MediumBlue;
    	int rand = (int) (Math.random()*(double) m_nbPixelColorTotal);
  
    	switch (rand)
    	{
    	case 0:
    		choice = R.color.MediumBlue;
    		break;
    	case 1:
    		choice = R.color.MediumRed;
    		break;
    	case 2:
    		choice = R.color.MediumGreen;
    		break;
    	}
    	
    	if (m_pixelColorDeletedList.contains(choice))
    	{
    		return getRandomColor();
    	}
    	else
    	{
    		return choice;
    	}
    }
  //-----------------------------------------------------------------------------------------------------------------------------
    private int getRandomColor_E()
    {
    	int choice = R.color.DarkBlue;
    	int rand = (int) (Math.random()*(double) m_nbPixelColorTotal);
  
    	switch (rand)
    	{
    	case 0:
    		choice = R.color.Blue1;
    		break;
    	case 1:
    		choice = R.color.Blue2;
    		break;
    	case 2:
    		choice = R.color.Blue3;
    		break;
    	case 3:
    		choice = R.color.Blue4;
    		break;
    	case 4:
    		choice = R.color.Blue5;
    	}
    	
    	if (m_pixelColorDeletedList.contains(choice))
    	{
    		return getRandomColor();
    	}
    	else
    	{
    		return choice;
    	}
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int getM_targetPixel()
    {
    	return m_targetPixel;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int countTargetPixel()
    {
    	int cpt=0;
    	for (int i=0 ; i < m_pixelList.size() ; i++)
    	{
    		if(m_pixelList.get(i)== m_targetPixel)
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
    public void setTargetPixel(int targetPix)
    {
    	m_targetPixel = targetPix;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public boolean isTargetPixelRemaining()
    {
    	if (m_nbTargetPixel == 0)
    	{
    		m_nbPixelColorRemaining--;
    		if (m_nbPixelColorRemaining !=0)
    		{
				m_allAdventureColors.remove(m_allAdventureColors.indexOf(m_targetPixel));
    			m_targetPixel = getRandomColor();
        		m_nbTargetPixel = countTargetPixel();
    		}
    		return false;
    	}	
    	else
    	{
    		return true;
    	}	
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int pixelTouched()
    {
    	// 10% of chance to change target color
    	double rand =  Math.random();
    	if (rand < ((double) m_arcadePerCent)/100.0)
    	{
    		m_targetPixel = getRandomColor();
    	}
    	
    	return score++;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void deleteTarget()
    {
    	m_nbTargetPixel--;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public boolean isLevelFinished()
    {
    	if (m_nbPixelColorRemaining == 0)
    		return true;
    	else
    		return false;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public int getScore()
    {
    	return score;
    }
	//-----------------------------------------------------------------------------------------------------------------------------
	public void initAdventureColors()
	{
		m_allAdventureColors = new ArrayList<Integer>();

		m_allAdventureColors.add(R.color.MediumBlue);
		m_allAdventureColors.add(R.color.MediumRed);
		m_allAdventureColors.add(R.color.MediumGreen);
		m_allAdventureColors.add(R.color.Yellow);
		m_allAdventureColors.add(R.color.Cyan);
		m_allAdventureColors.add(R.color.Grey);
		m_allAdventureColors.add(R.color.DarkMagenta);
		m_allAdventureColors.add(R.color.Pink);
		m_allAdventureColors.add(R.color.Black);
		m_allAdventureColors.add(R.color.Orange);
		m_allAdventureColors.add(R.color.Purple);
		m_allAdventureColors.add(R.color.White);




	}
    //-----------------------------------------------------------------------------------------------------------------------------
}
