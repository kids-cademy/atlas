package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;

public class SyncDevicesView extends ConstraintLayout {
    private ImageView tvImage;
    private View connectionView;
    private TextView labelText;

    private boolean inSync;

    public SyncDevicesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.compo_sync_devices, this);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SyncDevicesView, 0, 0);
        try {
            inSync = a.getBoolean(R.styleable.SyncDevicesView_inSync, false);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        tvImage = findViewById(R.id.sync_tv);
        connectionView = findViewById(R.id.sync_connection);
        labelText = findViewById(R.id.sync_label);
        setInSyncState(inSync);
    }

    public void setInSyncState(boolean inSync) {
        if (inSync) {
            tvImage.setImageResource(R.drawable.tv_reader);
            labelText.setVisibility(View.VISIBLE);
            connectionView.setAlpha(1F);
        } else {
            tvImage.setImageResource(R.drawable.tv_empty);
            labelText.setVisibility(View.GONE);
            connectionView.setAlpha(0.2F);
        }
    }
}
