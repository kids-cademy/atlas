package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.Feature;
import com.kidscademy.atlas.util.Colors;

public class FeatureItemView extends TableRow {
    private TextView nameView;
    private TextView valueView;
    private View separatorView;

    public FeatureItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        nameView = findViewById(R.id.item_feature_name);
        valueView = findViewById(R.id.item_feature_value);
        separatorView = findViewById(R.id.item_feature_separator);
    }

    public void setFeature(@NonNull Feature feature) {
        nameView.setText(feature.getNameDisplay());
        valueView.setText(feature.getValueDisplay());
        if (separatorView != null) {
            separatorView.setBackgroundColor(Colors.getColor(getContext()));
        }
    }
}