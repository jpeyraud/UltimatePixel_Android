package com.altarrys.ultimatepixel.game;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.altarrys.ultimatepixel.R;
import com.altarrys.ultimatepixel.opengl.GLBackground;

/**
 * A placeholder fragment containing a simple view.
 */
public class FScoreScreen extends Fragment {

    GLBackground mGlScoreBackground;

    public FScoreScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_score_screen, container, false);

        // Add opengl background
        mGlScoreBackground = new GLBackground(this.getActivity(), R.raw.timer_frag_shader);
        ((FrameLayout)rootView.findViewById(R.id.score_fragment_framelayout)).addView(mGlScoreBackground,0);

        return rootView;
    }
}
