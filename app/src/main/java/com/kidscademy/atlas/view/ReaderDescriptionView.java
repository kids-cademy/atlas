package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.AppActivity;
import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.HTML;
import com.kidscademy.atlas.sync.ItemRevealEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import js.lang.BugError;

public class ReaderDescriptionView extends ConstraintLayout {
    private final ReaderActivity readerActivity;
    private final Handler handler;
    private LinearLayout container;

    public ReaderDescriptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        handler = ((AppActivity) context).isTablet() ? new TabletHandler() : new MobileHandler();
        inflate(context, R.layout.reader_description, this);
        if (!(context instanceof ReaderActivity)) {
            throw new BugError("Invalid context for object description. Not a reader activity.");
        }
        this.readerActivity = (ReaderActivity) context;
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        container = findViewById(R.id.reader_description_container);
    }

    public void update(AtlasObject atlasObject) {
        List<CharSequence> description = handler.getDescription(atlasObject);

        for (int i = container.getChildCount(); i < description.size(); ++i) {
            inflate(getContext(), R.layout.compo_paragraph, container);
        }

        for (int i = 0; i < description.size(); ++i) {
            handler.setText(i, description.get(i));
            container.getChildAt(i).setVisibility(View.VISIBLE);
        }

        for (int i = description.size(); i < container.getChildCount(); ++i) {
            container.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private interface Handler {
        List<CharSequence> getDescription(AtlasObject atlasObject);

        void setText(int childIndex, CharSequence description);
    }

    private class MobileHandler implements Handler {
        @Override
        public List<CharSequence> getDescription(AtlasObject atlasObject) {
            HTML content = atlasObject.getDescription();
            if(content == null) {
                return Collections.emptyList();
            }
            List<CharSequence> description = new ArrayList<>();
            for (HTML.Element element : content.getElements()) {
                if (element instanceof HTML.Paragraph) {
                    description.add(((HTML.Paragraph) element).getText());
                }
            }
            return description;
        }

        @Override
        public void setText(int childIndex, CharSequence description) {
            ConstraintLayout layout = (ConstraintLayout) container.getChildAt(childIndex);
            TextView textView = layout.findViewById(R.id.paragraph_text);
            textView.setText(description);
        }
    }

    private class TabletHandler implements Handler, View.OnClickListener {
        @Override
        public List<CharSequence> getDescription(AtlasObject atlasObject) {
            HTML content = atlasObject.getDescription();
            if(content == null) {
                return Collections.emptyList();
            }
            List<CharSequence> description = new ArrayList<>();
            for (HTML.Element element : content.getElements()) {
                if (element instanceof HTML.Paragraph) {
                    description.add(((HTML.Paragraph) element).getText());
                }
            }
            // there is a not understood behaviour when scrolling to description bottom: last line from last
            // paragraph is not displayed; workaround is to add an empty paragraph
            description.add("");
            return description;
        }

        @Override
        public void setText(int childIndex, CharSequence description) {
            TextView textView = (TextView) container.getChildAt(childIndex);
            textView.setText(description);
            textView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            // do not use parent.indexOfChild because it counts all views, including GONE

            int childIndex = 0;
            for (int i = 0, count = parent.getChildCount(); i < count; ++i) {
                View child = parent.getChildAt(i);
                if (child.getVisibility() != View.VISIBLE) {
                    continue;
                }
                if (child == view) {
                    break;
                }
                ++childIndex;
            }

            readerActivity.pushEvent(new ItemRevealEvent(ItemRevealEvent.Type.DESCRIPTION, childIndex));
        }
    }
}
