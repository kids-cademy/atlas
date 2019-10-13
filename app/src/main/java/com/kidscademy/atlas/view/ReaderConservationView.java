package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.ConservationStatus;

import java.util.HashMap;
import java.util.Map;

import js.lang.BugError;

public class ReaderConservationView extends ConstraintLayout {
    private final Map<ConservationStatus, View[]> views = new HashMap<>();

    private ConservationStatus conservation;

    public ReaderConservationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_conservation, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        views.put(ConservationStatus.LC, views(R.id.reader_conservation_label_LC, R.id.reader_conservation_bullet_LC));
        views.put(ConservationStatus.NT, views(R.id.reader_conservation_label_NT, R.id.reader_conservation_bullet_NT));
        views.put(ConservationStatus.VU, views(R.id.reader_conservation_label_VU, R.id.reader_conservation_bullet_VU));
        views.put(ConservationStatus.EN, views(R.id.reader_conservation_label_EN, R.id.reader_conservation_bullet_EN));
        views.put(ConservationStatus.CR, views(R.id.reader_conservation_label_CR, R.id.reader_conservation_bullet_CR));
        views.put(ConservationStatus.EW, views(R.id.reader_conservation_label_EW, R.id.reader_conservation_bullet_EW));
        views.put(ConservationStatus.EX, views(R.id.reader_conservation_label_EX, R.id.reader_conservation_bullet_EX));
        views.put(ConservationStatus.NA, views(R.id.reader_conservation_label_NA, R.id.reader_conservation_bullet_NA));
    }

    /**
     * Update current atlas object conservation status. Conservation view displays all available states but dimmed, that is,
     * with alpha set to 0.5F. Current value is emphasized by setting alpha to 1.0F, i.e. full opacity.
     *
     * @param atlasObject atlas object.
     */
    public void update(AtlasObject atlasObject) {
        if (!atlasObject.hasConservation()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        if (conservation != null) {
            setConservationAlpha(conservation, 0.5F);
        }
        conservation = atlasObject.getConservation();
        setConservationAlpha(conservation, 1.0F);
    }

    /**
     * Set color alpha value for label and bullet views attached to given conservation status. Color alpha is
     * used to emphasized current conservation status.
     *
     * @param conservation conservation status.
     * @param alpha        color alpha value.
     */
    private void setConservationAlpha(ConservationStatus conservation, float alpha) {
        View[] views = this.views.get(conservation);
        if (views == null) {
            throw new BugError("Missing views for conservation status |%s|.", conservation);
        }
        for (View view : views) {
            view.setAlpha(alpha);
        }
    }

    /**
     * Get conservation status label and bullet views.
     *
     * @param labelId  label view ID,
     * @param bulletId bullet view ID.
     * @return array of two views, respective label and bullet views.
     */
    private View[] views(int labelId, int bulletId) {
        return new View[]{
                findViewById(labelId),
                findViewById(bulletId)
        };
    }
}