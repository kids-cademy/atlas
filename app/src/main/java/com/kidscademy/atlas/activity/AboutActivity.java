package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;

import com.kidscademy.atlas.R;

import js.log.Log;
import js.log.LogFactory;

/**
 * About activity.
 *
 * @author Iulian Rotaru
 */
public class AboutActivity extends AppActivity {
    private static final Log log = LogFactory.getLog(AboutActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, AboutActivity.class);
        activity.startActivity(intent);
    }

    public AboutActivity() {
        super(R.layout.activity_about);
        log.trace("AboutActivity()");
    }
}
