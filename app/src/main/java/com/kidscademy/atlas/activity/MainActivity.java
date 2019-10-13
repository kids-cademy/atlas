package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kidscademy.atlas.R;

import js.log.Log;
import js.log.LogFactory;

public class MainActivity extends AppActivity {
    private static final Log log = LogFactory.getLog(MainActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public MainActivity() {
        super(R.layout.activity_main);
        log.trace("MainActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setClickListener(R.id.action_forward);
        setClickListener(R.id.main_picture);
        setClickListener(R.id.action_we_play);
        setClickListener(R.id.action_no_ads);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_forward || id == R.id.main_picture) {
            MenuActivity.start(this);
        } else if (id == R.id.action_we_play) {
            WePlayActivity.start(this);
        } else if (id == R.id.action_no_ads) {
            NoAdsActivity.start(this);
        } else {
            super.onClick(view);
        }
    }
}
