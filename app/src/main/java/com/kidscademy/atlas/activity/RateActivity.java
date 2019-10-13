package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.kidscademy.atlas.R;

import js.log.Log;
import js.log.LogFactory;

public class RateActivity extends AppActivity {
    private static final Log log = LogFactory.getLog(RateActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, RateActivity.class);
        activity.startActivity(intent);
    }

    public RateActivity() {
        super(R.layout.activity_rate);
        log.trace("RateActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setClickListener(R.id.rate_button);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.rate_button) {
            try {
                startActivity(rate("market://details"));
            } catch (ActivityNotFoundException e) {
                startActivity(rate("http://play.google.com/store/apps/details"));
            }
        } else {
            super.onClick(view);
        }
    }

    private Intent rate(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }
}
