package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;

import com.kidscademy.atlas.R;

import js.log.Log;
import js.log.LogFactory;

public class WePlayActivity extends AppActivity {
    private static final Log log = LogFactory.getLog(WePlayActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, WePlayActivity.class);
        activity.startActivity(intent);
    }

    public WePlayActivity() {
        super(R.layout.activity_we_play);
        log.trace("WePlayActivity()");
    }
}

