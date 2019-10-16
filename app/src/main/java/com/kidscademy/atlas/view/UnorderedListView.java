package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.R;

public class UnorderedListView extends LinearLayoutCompat {
    public UnorderedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.compo_unordered_list, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void update(String[] strings) {
        if (strings.length == 0) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        for (int i = getChildCount(); i < strings.length; ++i) {
            inflate(getContext(), R.layout.item_unordered_list, this);
        }

        for (int i = 0; i < strings.length; ++i) {
            TextView view = (TextView) getChildAt(i);
            view.setVisibility(View.VISIBLE);
            view.setText(strings[i]);
        }

        for (int i = strings.length; i < getChildCount(); ++i) {
            getChildAt(i).setVisibility(View.GONE);
        }
    }
}
