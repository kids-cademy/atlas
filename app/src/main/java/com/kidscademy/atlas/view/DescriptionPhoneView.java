package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

import java.util.List;

public class DescriptionPhoneView extends LinearLayout implements DescriptionColumnView {
    private final LayoutInflater inflater;

    private final int paragraphsCount;

    public DescriptionPhoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DescriptionPhoneView, 0, 0);
        try {
            paragraphsCount = a.getInt(R.styleable.DescriptionPhoneView_paragraphsCount, 100);
        } finally {
            a.recycle();
        }
    }

    public void update(AtlasObject atlasObject) {
        if (!atlasObject.hasDescription()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        List<String> description = atlasObject.getDescription().getText();
        int paragraphIndex = atlasObject.getDescriptionParagraphOffset();
        int paragraphsCount = Math.min(this.paragraphsCount, description.size() - paragraphIndex);

        int childIndex = 0;
        for (; childIndex < paragraphsCount; ++childIndex, ++paragraphIndex) {
            String paragraph = description.get(paragraphIndex);
            TextView paragraphView = (TextView) getChildAt(childIndex);
            if (paragraphView == null) {
                paragraphView = (TextView) inflater.inflate(R.layout.compo_paragraph, this, false);
                addView(paragraphView);
            }
            paragraphView.setVisibility(View.VISIBLE);
            paragraphView.setText(paragraph);
        }

        for (; childIndex < getChildCount(); ++childIndex) {
            getChildAt(childIndex).setVisibility(View.GONE);
        }

        atlasObject.updateDescriptionParagraphOffset(paragraphIndex);
    }
}
