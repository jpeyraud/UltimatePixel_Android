package com.altarrys.ultimatepixel.game;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.altarrys.ultimatepixel.R;
import com.altarrys.ultimatepixel.opengl.GLBackground;

/**
 * Created by jpeyraux on 20/02/2016.
 */
public class MainFragment extends Fragment implements View.OnTouchListener
{
    private static final String TAG = "MainFragment";

    private GLBackground mGlMenuBackground;

    private DFChangeGLBackground mDFChangeBackground;

    private FpsCallback mFpsCheck = null;

    public MainFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container,	false);

        // Init DF change Background
        mDFChangeBackground = new DFChangeGLBackground();
        mDFChangeBackground.setFragment(this);

        // Add opengl background
        mGlMenuBackground = new GLBackground(this.getActivity(), R.raw.cubes_frag_shader);
        ((FrameLayout)rootView.findViewById(R.id.main_fragment_framelayout)).addView(mGlMenuBackground,0);

        // Set OnClickListener for all buttons
        Button ButtonPlay = (Button) rootView.findViewById(R.id.playButton);
        Button ButtonScore = (Button) rootView.findViewById(R.id.ScoreButton);
        ButtonPlay.setOnTouchListener(this);
        ButtonScore.setOnTouchListener(this);

        return rootView;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onResume() {
        super.onResume();
        mGlMenuBackground.onResume();
        startFpsCallback();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onPause() {
        super.onPause();
        mGlMenuBackground.onPause();
        mFpsCheck.kill();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void startFpsCallback() {
        mFpsCheck = new FpsCallback(this);
        Thread thFps = new Thread(mFpsCheck);
        thFps.start();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public double getFps() {
       return mGlMenuBackground.getFps();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void changeFragShader() {
        mGlMenuBackground.useHighFpsShader();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        Log.d(TAG, "on press");
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (v.getId() == R.id.playButton)
            {
                v.setBackgroundResource(R.drawable.buttonshapeplayclicked_shadow);
            }
            else if (v.getId() == R.id.ScoreButton)
            {
                v.setBackgroundResource(R.drawable.buttonshapeplayclicked_shadow);
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            if (v.getId() == R.id.playButton)
            {
                v.setBackgroundResource(R.drawable.buttonshapeplay_shadow);

                Intent intent = new Intent(getActivity(), AGameEngine.class);
                startActivity(intent);
                return true;
            }
            else if (v.getId() == R.id.ScoreButton)
            {
                Log.v(TAG, "FRAG CHANGED");
                v.setBackgroundResource(R.drawable.buttonshapeplay_shadow);
                //GoogleApiClient googleApi = ((MainActivity)getActivity()).getGoogleApiClient();

                //if (googleApi.isConnected())
                //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(googleApi,getString(R.string.leaderboard_id)), 1224);
            }
        }

        return true;
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public class FpsCallback implements Runnable {

        private MainFragment mFrag;
        private double mStart;
        private boolean mKill = false;

        public FpsCallback(MainFragment frag) {
            mFrag = frag;
        }
        public void run() {
            mStart = System.currentTimeMillis();
            while (System.currentTimeMillis() - mStart < 3000 && !mKill) {
            }
            if (mFrag.getFps() < 20 && !mKill)
                mDFChangeBackground.show(getFragmentManager(), "changeBackground");
        }

        public void kill(){
            mKill = true;
        }

    }
}