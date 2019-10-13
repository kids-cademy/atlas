package com.kidscademy.atlas.util;

import com.kidscademy.atlas.R;

import java.util.Random;

public class RandomColor {
    private static Random random = new Random();

    private static final int[] colors = new int[]{
            R.color.red_300,
            R.color.pink_300,
            R.color.purple_300,
            R.color.indigo_300,
            R.color.blue_300,
            R.color.cyan_300,
            R.color.teal_300,
            R.color.green_300,
            R.color.lime_300,
            R.color.amber_300,
            R.color.orange_300,
            R.color.brown_300
    };

    public static int getRandomColor() {
        return colors[random.nextInt(colors.length)];
    }
}
