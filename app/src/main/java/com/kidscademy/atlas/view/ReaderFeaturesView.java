package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.util.Views;

import java.util.Iterator;
import java.util.Map;

public class ReaderFeaturesView extends LinearLayout {
    public ReaderFeaturesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_features, this);
    }

    private View container;

    public void update(@NonNull AtlasObject object) {
        Map<String, String> features = object.getFeatures();

        if (features.isEmpty()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        Views.resetScrollParentView(this);

        Iterator<Map.Entry<String, String>> featuresIterator = features.entrySet().iterator();

        for (int i = 0; i < getChildCount(); ++i) {
            FeatureItemView view = (FeatureItemView) getChildAt(i);
            if (featuresIterator.hasNext()) {
                view.setVisibility(View.VISIBLE);
                view.setFeature(featuresIterator.next());
            } else {
                view.setVisibility(View.GONE);
            }
        }

        while (featuresIterator.hasNext()) {
            FeatureItemView view = (FeatureItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_feature, this, false);
            view.setFeature(featuresIterator.next());
            addView(view);
        }
    }

    public void setContainer(View container) {
        this.container = container;
    }

    public void setVisibility(int visibility) {
        if (container != null) {
            container.setVisibility(visibility);
        } else {
            super.setVisibility(visibility);
        }
    }
}
