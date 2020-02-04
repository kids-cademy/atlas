package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.sync.PageScrollEvent;

import js.lang.BugError;

public class ReaderPage extends NestedScrollView implements NestedScrollView.OnScrollChangeListener {
    private final ReaderActivity readerActivity;
    private ReaderObjectLayout objectLayout;

    public ReaderPage(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!(context instanceof ReaderActivity)) {
            throw new BugError("Invalid reader page context. Not a reader activity.");
        }
        this.readerActivity = (ReaderActivity) context;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        objectLayout = findViewById(R.id.reader_page_object_view);
        setOnScrollChangeListener(this);
    }

    public void setAtlasObject(AtlasObject atlasObject) {
        scrollTo(0, 0);
        objectLayout.setAtlasObject(atlasObject);
    }

    @Override
    public void onScrollChange(NestedScrollView scrollView, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        double scrollableHeight = getChildAt(0).getMeasuredHeight() - getMeasuredHeight();
        readerActivity.pushEvent(new PageScrollEvent(scrollY / scrollableHeight));
    }
}
