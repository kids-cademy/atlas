package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.kidscademy.atlas.util.CollapseAnimator;

public class ActionTextView extends AppCompatTextView {
    private CollapseAnimator collapseAnimator;

    public ActionTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener clickListener) {
        assert collapseAnimator == null;
        // keep it simple; we are sure this setter is called only once
        collapseAnimator = new CollapseAnimator(this, clickListener);
        super.setOnClickListener(collapseAnimator);
    }
}
