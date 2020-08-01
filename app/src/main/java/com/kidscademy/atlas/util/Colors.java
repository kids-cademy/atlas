package com.kidscademy.atlas.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.kidscademy.atlas.R;

public class Colors {
    private static final int[] COLORS = new int[]{
            R.color.red_500,
            R.color.pink_500,
            R.color.purple_500,
            R.color.deep_purple_500,
            R.color.indigo_500,
            R.color.blue_300,
            R.color.light_blue_500,
            R.color.cyan_500,
            R.color.teal_500,
            R.color.green_500,
            R.color.light_green_500,
            R.color.lime_500,
            R.color.yellow_500,
            R.color.amber_500,
            R.color.orange_500,
            R.color.deep_orange_500
    };
    private static int index = (int) ((System.currentTimeMillis() / 1000) % COLORS.length);

    public static int getColor() {
        return COLORS[index++ % COLORS.length];
    }

    public static int getColor(@NonNull Context context) {
        return ContextCompat.getColor(context, getColor());
    }
}
