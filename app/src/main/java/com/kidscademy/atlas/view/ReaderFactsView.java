package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Fact;

/**
 * Facts view is a vertical linear layout that displays a fixed number of fact item views. This custom view
 * has a single method used to initialize view facts, see {@link #update(AtlasObject)}.
 *
 * @author Iulian Rotaru
 */
public class ReaderFactsView extends LinearLayout {
    private View container;

    public ReaderFactsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_facts, this);
    }

    public void setContainer(View container) {
        this.container = container;
    }

    /**
     * Set facts, replacing existing fact item views, if any. A fact has a name and a value that is by default
     * collapsed.
     *
     * @param atlasObject atlas object to display facts for.
     * @see FactItemView
     */
    public void update(@NonNull AtlasObject atlasObject) {
        if (!atlasObject.hasFacts()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        Fact[] facts = atlasObject.getFacts();
        int index = 0;
        for (; index < getChildCount(); ++index) {
            FactItemView view = (FactItemView) getChildAt(index);
            if (index < facts.length) {
                view.setVisibility(View.VISIBLE);
                view.setFact(atlasObject.getName(), facts[index]);
            } else {
                view.setVisibility(View.GONE);
            }
        }

        for (; index < facts.length; ++index) {
            FactItemView view = (FactItemView) LayoutInflater.from(getContext()).inflate(R.layout.item_fact, this, false);
            view.setFact(atlasObject.getName(), facts[index]);
            addView(view);
        }
    }

    public void setVisibility(int visibility) {
        if (container != null) {
            container.setVisibility(visibility);
        } else {
            super.setVisibility(visibility);
        }
    }
}
