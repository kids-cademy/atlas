package com.kidscademy.atlas.util;

import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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
        populateListView(listView, Arrays.asList(items), builder);
    }

    public static <T> void populateListView(LinearLayout listView, Collection<T> items, ListViewBuilder<T> builder) {
        Iterator<T> iterator = items.iterator();
        int i = 0;

        for (; i < listView.getChildCount(); ++i) {
            View view = listView.getChildAt(i);
            if (iterator.hasNext()) {
                view.setVisibility(View.VISIBLE);
                builder.setObject(i, iterator.next());
            } else {
                view.setVisibility(View.GONE);
            }
        }

        while (iterator.hasNext()) {
            builder.createChild(listView);
            builder.setObject(i++, iterator.next());
        }
    }

    public interface ListViewBuilder<T> {
        /**
         * Create new child view and append it to given list view.
         *
         * @param listView parent list view.
         */
        void createChild(LinearLayout listView);

        void setObject(int index, T item);
    }
}
