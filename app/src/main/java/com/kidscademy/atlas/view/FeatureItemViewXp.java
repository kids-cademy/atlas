package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.model.Feature;
import com.kidscademy.atlas.sync.ItemRevealEvent;
import com.kidscademy.atlas.util.RandomColor;

import js.lang.BugError;

public class FeatureItemViewXp extends TableRow implements View.OnClickListener {
    private final ReaderActivity readerActivity;
    private TextView nameView;
    private TextView valueView;
    private View separatorView;

    public FeatureItemViewXp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!(context instanceof ReaderActivity)) {
            throw new BugError("Invalid feature item context. Not a reader activity.");
        }
        this.readerActivity = (ReaderActivity) context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        nameView = findViewById(R.id.item_feature_name);
        valueView = findViewById(R.id.item_feature_value);
        separatorView = findViewById(R.id.item_feature_separator);
        setOnClickListener(this);
    }

    public void setFeature(@NonNull Feature feature) {
        nameView.setText(feature.getNameDisplay());
        valueView.setText(feature.getValueDisplay());
        separatorView.setBackgroundColor(RandomColor.getColor(getContext()));
    }

    @Override
    public void onClick(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        readerActivity.pushEvent(new ItemRevealEvent(ItemRevealEvent.Type.FEATURE, parent.indexOfChild(view)));
    }
}