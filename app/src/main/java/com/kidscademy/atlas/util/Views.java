package com.kidscademy.atlas.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.kidscademy.atlas.R;

public class Views {
    /**
     * Check if given parent is a scroll view and move it to origin.
     *
     * @param view view.
     */
    public static void resetScrollParentView(View view) {
        ViewParent parentView = view.getParent();
        if (parentView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) parentView;
            scrollView.scrollTo(0, 0);
        }
    }

    public static <T> void populateListView(LinearLayout listView, T[] items, ListViewBuilder<T> builder) {
        for (int i = listView.getChildCount(); i < items.length; ++i) {
            builder.inflateChild(listView);
        }

        for (int i = 0; i < items.length; ++i) {
            View view = listView.getChildAt(i);
            view.setVisibility(View.VISIBLE);
            builder.setObject(i, items[i]);
        }

        for (int i = items.length; i < listView.getChildCount(); ++i) {
            listView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    public interface ListViewBuilder<T> {
        void inflateChild(LinearLayout listView);

        void setObject(int index, T item);
    }
}
