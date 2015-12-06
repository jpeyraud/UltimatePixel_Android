package com.altarrys.ultimatepixel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class FLevelChoice extends Fragment implements AdapterView.OnItemClickListener {

    public FLevelChoice() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_level_choice, container, false);

        // Create level list
        ArrayList<String> levelList = new ArrayList<String>();
        for (int i = 1; i <=20; i++)
            levelList.add(String.valueOf(i));

        // Get the GridView and set the adapter to display more than one Button dynamically
        ListView levelListView = (ListView) rootView.findViewById(R.id.levellistview);
        LevelArrayAdapter adapter = new LevelArrayAdapter(getActivity(), R.id.level , levelList);
        levelListView.setAdapter(adapter);
        levelListView.setOnItemClickListener(this);

        return rootView;

    }
    //-----------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Intent intent = new Intent(getActivity(), AGameEngine.class);

        // Add the index of the checked hardness to the intent
        intent.putExtra("hardness", "normal");

        // Add the game mode to the intent
        intent.putExtra("gamemode", "classic");

        // Send the level name to display it
        intent.putExtra("levelName", ((TextView) view.findViewById(R.id.levelName)).getText());

        // Send LevelId +1 because position start to 0
        intent.putExtra("levelId", position+1);

        startActivity(intent);
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    public void load(String key, String val)
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.PREFERENCES_ID, Context.MODE_PRIVATE);
        float old = Float.parseFloat(prefs.getString(key, "0.0"));

        // If better
        //SharedPreferences.Editor editor = getSharedPreferences(MainActivity.PREFERENCES_ID, Context.MODE_PRIVATE).edit();
        //editor.putString(key, val).apply();
    }
    //-----------------------------------------------------------------------------------------------------------------------------
    // Inherit from "ArrayAdapter" to adapt the view to display many Buttons
    public class LevelArrayAdapter extends ArrayAdapter<String>
    {
        public LevelArrayAdapter(Context context, int resource, List<String> objects)
        {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // Get the Button at the provided position
            String level = getItem(position);

            Log.w("PixelDebug", ""+position);
            Log.w("PixelDebug", ""+level);

            View v;

            // Load the view only if one was not already loaded
            if (convertView == null)
            {
                v = LayoutInflater.from(getContext()).inflate(R.layout.level, null);
                ((TextView)v.findViewById(R.id.levelName)).setText("level "+level);
                ((ImageView)v.findViewById(R.id.medal)).setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            }
            else
            {
                v=convertView;
            }
            return v;
        }
    }
}
