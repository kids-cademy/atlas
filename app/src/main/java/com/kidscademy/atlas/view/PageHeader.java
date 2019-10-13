package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.R;

public class PageHeader extends ConstraintLayout {
    private final boolean showBackAction;
    private final String pageCaption;

    public PageHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.compo_page_header, this);

        boolean showBackAction = true;
        String pageCaption = null;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageHeader, 0, 0);
        try {
            showBackAction = a.getBoolean(R.styleable.PageHeader_showBackAction, true);
            pageCaption = a.getString(R.styleable.PageHeader_pageCaption);
        } finally {
            a.recycle();
        }

        this.showBackAction = showBackAction;
        if (pageCaption == null) {
            pageCaption = context.getString(R.string.app_name);
        }
        this.pageCaption = pageCaption;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        if (!showBackAction) {
            View backAction = findViewById(R.id.action_back);
            backAction.setVisibility(View.INVISIBLE);
        }
        TextView captionText = findViewById(R.id.page_header_caption);
        captionText.setText(pageCaption);
    }
}
