package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.app.Audit;
import com.kidscademy.atlas.sync.SyncService;
import com.kidscademy.atlas.view.HexaIcon;

import js.log.Log;
import js.log.LogFactory;

public class MenuActivity extends AppActivity implements ServiceConnection {
    /**
     * Class logger.
     */
    private static final Log log = LogFactory.getLog(MenuActivity.class);

    static {
        // enable support for vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, MenuActivity.class);
        activity.startActivity(intent);
    }

    /**
     * Sync icon changes is tint to signal browser synchronization state.
     */
    private HexaIcon syncIcon;

    public MenuActivity() {
        super(R.layout.activity_menu);
        log.trace("MainActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        syncIcon = findViewById(R.id.menu_sync);

        setClickListener(R.id.menu_reader);
        setClickListener(R.id.menu_main_actions_reader_label);
        setClickListener(R.id.menu_search);
        setClickListener(R.id.menu_main_actions_search_label);
        setClickListener(R.id.menu_index);
        setClickListener(R.id.menu_main_actions_index_label);
        setClickListener(R.id.menu_sync);
        setClickListener(R.id.menu_main_actions_sync_label);
        setClickListener(R.id.menu_accessibility);
        setClickListener(R.id.menu_main_actions_accessibility_label);

        setClickListener(R.id.menu_user_guide);
        setClickListener(R.id.menu_user_guide_label);
        setClickListener(R.id.menu_about);
        setClickListener(R.id.menu_about_label);
        setClickListener(R.id.menu_share);
        setClickListener(R.id.menu_share_label);
        setClickListener(R.id.menu_rate);
        setClickListener(R.id.menu_rate_label);
        setClickListener(R.id.menu_close);
        setClickListener(R.id.menu_close_label);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, SyncService.class), this, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        resetSyncIcon();
    }

    @Override
    public void onClick(View view) {
        final Audit audit = app.audit();

        int id = view.getId();
        if (id == R.id.action_back) {
            MainActivity.start(this);
        } else if (id == R.id.menu_search || id == R.id.menu_main_actions_search_label) {
            audit.openSearch();
            SearchActivity.start(this);
        } else if (id == R.id.menu_reader || id == R.id.menu_main_actions_reader_label) {
            audit.openReader();
            ReaderActivity.start(this);
        } else if (id == R.id.menu_index || id == R.id.menu_main_actions_index_label) {
            audit.openIndex();
            IndexActivity.start(this);
        } else if (id == R.id.menu_sync || id == R.id.menu_main_actions_sync_label) {
            audit.openSync();
            SyncActivity.start(this);
        } else if (id == R.id.menu_accessibility || id == R.id.menu_main_actions_accessibility_label) {
            audit.openAccessibility();
            AccessibilityActivity.start(this);
        } else if (id == R.id.menu_about || id == R.id.menu_about_label) {
            audit.openAbout();
            AboutActivity.start(this);
        } else if (id == R.id.menu_user_guide || id == R.id.menu_user_guide_label) {
            audit.openHelp();
            HelpActivity.start(this);
        } else if (id == R.id.menu_share || id == R.id.menu_share_label) {
            audit.openShare();
            ShareActivity.start(this);
        } else if (id == R.id.menu_rate || id == R.id.menu_rate_label) {
            app.audit().openRate();
            RateActivity.start(this);
        } else if (id == R.id.menu_settings) {
            audit.openSettings();
        } else if (id == R.id.menu_close || id == R.id.menu_close_label) {
            ExitActivity.exit(this);
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        syncIcon.setIconColor(ContextCompat.getColor(this, R.color.text_accent));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        resetSyncIcon();
    }

    private void resetSyncIcon() {
        syncIcon.setIconColor(ContextCompat.getColor(this, R.color.text));
    }
}
