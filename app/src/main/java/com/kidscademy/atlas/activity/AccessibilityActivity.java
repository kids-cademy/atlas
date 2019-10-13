package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.util.Strings;

import js.log.Log;
import js.log.LogFactory;
import js.util.Player;

public class AccessibilityActivity extends AppActivity implements Player.StateListener {
    private static final Log log = LogFactory.getLog(AccessibilityActivity.class);

    private static final String PROJECT_NAME = "accessibility";

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, AccessibilityActivity.class);
        activity.startActivity(intent);
    }

    private final Player player;
    private ImageView sampleButton;
    private TextView voteValueText;

    public AccessibilityActivity() {
        super(R.layout.accessibility_activity);
        log.trace("AccessibilityActivity");
        this.player = new Player(this);
        this.player.setStateListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sampleButton = findViewById(R.id.accessibility_play_sample);
        sampleButton.setOnClickListener(this);

        voteValueText = findViewById(R.id.accessibility_vote_value);
        setClickListener(R.id.accessibility_vote_plus);
        setClickListener(R.id.accessibility_vote_minus);
    }

    @Override
    protected void onStart() {
        log.trace("onStart()");
        super.onStart();
        player.create();
        voteValueText.setText(Strings.format("%04d", 1));
    }

    @Override
    protected void onStop() {
        log.trace("onStop()");
        player.destroy();
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        log.trace("onClick(View)");
        int id = view.getId();
        if (id == R.id.accessibility_play_sample) {
            if (player.isPlaying()) {
                player.stop();
            } else {
                sampleButton.setImageResource(android.R.color.transparent);
                player.play(this, Uri.parse("http://kids-cademy.com/sample/accordion.mp3"));
            }
        } else if (id == R.id.accessibility_vote_plus) {
            app.hubService().votePlus(app.getPackageName(), PROJECT_NAME);
        } else if (id == R.id.accessibility_vote_minus) {
            app.hubService().voteMinus(app.getPackageName(), PROJECT_NAME);
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void onPlayerStateChanged(Player.State state) {
        switch (state) {
            case PLAYING:
                sampleButton.setImageResource(R.drawable.action_stop_sample);
                break;

            case PAUSED:
            case IDLE:
                sampleButton.setImageResource(R.drawable.action_play_sample);
                break;
        }
    }
}
