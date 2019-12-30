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
import com.kidscademy.atlas.model.Feature;
import com.kidscademy.atlas.sync.ItemRevealEvent;
import com.kidscademy.atlas.util.Strings;

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

    public void setFeature(@NonNull Feature feature) {
        keyView.setText(feature.getNameDisplay());
        valueView.setText(feature.getValueDisplay());
    }

    @Override
    public void onClick(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        readerActivity.pushEvent(new ItemRevealEvent(ItemRevealEvent.Type.FEATURE, parent.indexOfChild(view)));
    }

    private static FeatureValue getFeatureValue(Feature feature) {
        switch (feature.getQuantity()) {
            case MASS:
                return new MassValue(feature);

            case TIME:
                return new TimeValue(feature);

            case LENGTH:
                return new LengthValue(feature);

            case SPEED:
                return new SpeedValue(feature);
        }
        throw new BugError("Not implemented feature value for quantity |%s|.", feature.getQuantity());
    }

    private static abstract class FeatureValue {
        protected final Feature feature;

        protected FeatureValue(Feature feature) {
            this.feature = feature;
        }

        public abstract String getText();
    }

    private static class MassValue extends FeatureValue {
        public MassValue(Feature feature) {
            super(feature);
        }

        @Override
        public String getText() {
            return Double.toString(feature.getValue());
        }
    }

    private static class TimeValue extends FeatureValue {
        public TimeValue(Feature feature) {
            super(feature);
        }

        @Override
        public String getText() {
            return Double.toString(feature.getValue());
        }
    }

    private static class LengthValue extends FeatureValue {
        public LengthValue(Feature feature) {
            super(feature);
        }

        @Override
        public String getText() {
            return Double.toString(feature.getValue());
        }
    }

    private static class SpeedValue extends FeatureValue {
        public SpeedValue(Feature feature) {
            super(feature);
        }

        @Override
        public String getText() {
            if (feature.hasMaximum()) {
                return String.format("%.2f %s to %.2f %s", feature.getValue(), units(feature.getValue()), feature.getMaximum(), units(feature.getMaximum()));
            }
            return String.format("%.2f %s", feature.getValue(), units(feature.getValue()));
        }

        private static String units(double value) {
            return "km/h";
        }
    }
}