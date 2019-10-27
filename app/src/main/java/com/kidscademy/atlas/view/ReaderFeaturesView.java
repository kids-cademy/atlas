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

public class ReaderFeaturesView extends LinearLayout implements Views.ListViewBuilder<Map.Entry<String, String>> {
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
        Views.populateListView(this, features.entrySet(), this);
    }

    @Override
    public void createChild(LinearLayout listView) {
        inflate(getContext(), R.layout.item_feature, this);
    }

    @Override
    public void setObject(int index, Map.Entry<String, String> feature) {
        FeatureItemView featureView = (FeatureItemView) getChildAt(index);
        featureView.setFeature(feature);
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
