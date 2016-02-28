package com.altarrys.ultimatepixel.game;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import com.altarrys.ultimatepixel.R;

/**
 * Created by jpeyraux on 21/02/2016.
 */
public class PixelTile extends TextView {

    private int mColor;

    public PixelTile(Context context, AttributeSet attrs){
       super(context, attrs);
    }

    public int getColor() {
        return mColor;
    }

    public void updateColor(int color) {
        setColor(color);
        setBackground(color);
    }

    public void setBackground(int color) {
        LayerDrawable layers = (LayerDrawable) getBackground();
        GradientDrawable shape = (GradientDrawable) (layers.findDrawableByLayerId(R.id.pixelTileItem));
        shape.setColor(color);
    }

    public void setColor(int color) {
        this.mColor = color;
    }




}
