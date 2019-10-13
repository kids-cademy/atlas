package com.kidscademy.atlas.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.app.App;

import js.log.Log;
import js.log.LogFactory;

/**
 * Application base activity.
 *
 * @author Iulian Rotaru
 */
public abstract class AppActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(AppActivity.class);

    protected final App app;
    private final int layout;

    protected AppActivity(int layout) {
        this.app = App.instance();
        this.layout = layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");

        if (isFullScreen()) {
            int options = getWindow().getDecorView().getSystemUiVisibility();
            options ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            options ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            options ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
            options ^= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            if (Build.VERSION.SDK_INT >= 19) {
                options ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }
            getWindow().getDecorView().setSystemUiVisibility(options);
        }

        super.onCreate(savedInstanceState);
        setContentView(layout);

        if (isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }
        setClickListener(R.id.action_back);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_back) {
            onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        log.trace("onStart()");
        super.onStart();
    }

    protected <V extends View> V setClickListener(int viewId) {
        V view = findViewById(viewId);
        if (view == null) {
            return null;
        }
        view.setOnClickListener(this);
        return view;
    }

    protected boolean isFullScreen() {
//        if (!isTablet()) {
//            return false;
//        }
//        return App.instance().flags().isFullScreen();
        return false;
    }

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
