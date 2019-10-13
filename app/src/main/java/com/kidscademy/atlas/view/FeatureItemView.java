package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.sync.ItemRevealEvent;

import java.util.Map;

import js.lang.BugError;

public class FeatureItemView extends LinearLayout implements View.OnClickListener {
    private final ReaderActivity readerActivity;
    private TextView keyView;
    private TextView valueView;

    public FeatureItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!(context instanceof ReaderActivity)) {
            throw new BugError("Invalid feature item context. Not a reader activity.");
        }
        this.readerActivity = (ReaderActivity) context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        keyView = findViewById(R.id.item_feature_name);
        valueView = findViewById(R.id.item_feature_value);
        setOnClickListener(this);
    }

    public void setFeature(@NonNull Map.Entry<String, String> feature) {
        keyView.setText(feature.getKey());
        valueView.setText(feature.getValue());
    }

    @Override
    public void onClick(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        readerActivity.pushEvent(new ItemRevealEvent(ItemRevealEvent.Type.FEATURE, parent.indexOfChild(view)));
    }
}