package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;

import com.kidscademy.atlas.R;

import js.log.Log;
import js.log.LogFactory;

public class NoAdsActivity extends AppActivity {
    private static final Log log = LogFactory.getLog(NoAdsActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, NoAdsActivity.class);
        activity.startActivity(intent);
    }

    public NoAdsActivity() {
        super(R.layout.activity_no_ads);
        log.trace("NoAdsActivity()");
    }
}
