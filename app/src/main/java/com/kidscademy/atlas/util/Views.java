package com.kidscademy.atlas.util;

import android.support.annotation.NonNull;
import android.text.Layout;
import android.text.StaticLayout;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

    /**
     * Measure the height of a multi-lines text view required to accommodate a given text. Text view
     * instance is not yet measured and has no dimensions set. For this reason desired width is
     * provided as separated parameter.
     * <p>
     * Text alignment is always normal, that is left to right and text view padding is included.
     *
     * @param textView text view instance,
     * @param width    desired width,
     * @param text     string to fit into text view.
     * @return text view height necessary to display given text.
     */
    public static int getMeasuredHeight(TextView textView, int width, String text) {
        final boolean includePadding = true;
        Layout layout = new StaticLayout(text,
                textView.getPaint(),
                width,
                Layout.Alignment.ALIGN_NORMAL,
                textView.getLineSpacingMultiplier(),
                textView.getLineSpacingExtra(),
                includePadding);
        return layout.getHeight();
    }

    public interface ListViewBuilder<T> {
        /**
         * Create new child view and append it to given list view.
         *
         * @param listView parent list view.
         */
        void createChild(@NonNull LinearLayout listView);

        void setObject(int index, @NonNull T item);
    }
}
