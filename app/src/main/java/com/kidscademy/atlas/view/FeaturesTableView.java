package com.kidscademy.atlas.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
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

public class FeaturesTableView extends TableLayout implements Views.ListViewBuilder<Feature> {
    private final Handler handler;
    private Runnable drawCompleteListener;

    public FeaturesTableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.compo_features_table, this);
        handler = new Handler();
    }

    public void update(@NonNull AtlasObject object, Runnable drawCompleteListener) {
        this.drawCompleteListener = drawCompleteListener;

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
        inflate(getContext(), R.layout.item_feature, this);
    }

    @Override
    public void setObject(int index, Feature feature) {
        FeatureItemView featureView = (FeatureItemView) getChildAt(index);
        featureView.setFeature(feature);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        handler.post(drawCompleteListener);
    }
}
