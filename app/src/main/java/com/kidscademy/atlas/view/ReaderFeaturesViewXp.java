package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Feature;
import com.kidscademy.atlas.util.Views;

public class ReaderFeaturesViewXp extends TableLayout implements Views.ListViewBuilder<Feature> {
    public ReaderFeaturesViewXp(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_features_xp, this);
    }

    private View container;

    public void update(@NonNull AtlasObject object) {
        if (!object.hasFeatures()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);
        Views.resetScrollParentView(this);
        Views.populateListView(this, object.getFeatures(), this);
    }

    @Override
    public void createChild(LinearLayout listView) {
        inflate(getContext(), R.layout.item_feature_xp, this);
    }

    @Override
    public void setObject(int index, Feature feature) {
        FeatureItemViewXp featureView = (FeatureItemViewXp) getChildAt(index);
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
