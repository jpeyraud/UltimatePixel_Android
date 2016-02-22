package com.altarrys.ultimatepixel.game;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

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
    public void setColor(int color) {
        //if (this.getBackground() instanceof GradientDrawable)
            ((GradientDrawable)this.getBackground()).setColor(color);
        //else if (this.getBackground() instanceof ColorDrawable)
        //    ((ColorDrawable)this.getBackground()).setColor(color);
        this.mColor = color;
    }



}
