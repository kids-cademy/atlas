package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.util.RandomColor;

/**
 * Floating button with random background color.
 *
 * @author Iulian Rotaru
 */
public class RandomColorFAB extends android.support.design.widget.FloatingActionButton {
    public RandomColorFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, RandomColor.getRandomColor())));
        setRippleColor(ContextCompat.getColor(context, R.color.white_T40));
    }
}