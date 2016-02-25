package com.altarrys.ultimatepixel.game;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.altarrys.ultimatepixel.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class FScoreScreen extends Fragment {

    public FScoreScreen() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_score_screen, container, false);
    }
}
