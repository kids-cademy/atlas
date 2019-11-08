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
import com.kidscademy.atlas.util.Views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import js.lang.BugError;

public class ReaderDescriptionView extends LinearLayout implements Views.ListViewBuilder<CharSequence> {
    /**
     * On tablet description is divided in two parts. First is of fixed height and contains 4 paragraphs.
     * The second part is displayed after featured image view and contains remaining atlas object description paragraphs.
     * This flag is true if this view is for teh second part, named extension. It is detected based on  view ID.
     */
    private final boolean descriptionExtension;
    private final ReaderActivity readerActivity;
    private final Handler handler;

    public ReaderDescriptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        descriptionExtension = this.getId() == R.id.reader_description_view_ex;
        handler = ((AppActivity) context).isTablet() ? new TabletHandler() : new MobileHandler();
        inflate(context, R.layout.reader_description, this);
        if (!(context instanceof ReaderActivity)) {
            throw new BugError("Invalid context for object description. Not a reader activity.");
        }
        this.readerActivity = (ReaderActivity) context;
    }

    public void update(AtlasObject atlasObject) {
        Views.resetScrollParentView(this);
        Views.populateListView(this, handler.getDescription(atlasObject), this);
    }

    @Override
    public void createChild(LinearLayout listView) {
        inflate(getContext(), R.layout.compo_paragraph, this);
    }

    @Override
    public void setObject(int index, CharSequence description) {
        handler.setText(index, description);
    }

    public interface Handler {
        List<CharSequence> getDescription(AtlasObject atlasObject);

        void setText(int childIndex, CharSequence description);
    }

    private class MobileHandler implements Handler {
        @Override
        public List<CharSequence> getDescription(AtlasObject atlasObject) {
            HTML content = atlasObject.getDescription();
            if (content == null) {
                return Collections.emptyList();
            }
            List<CharSequence> description = new ArrayList<>();

            // atlas object description on mobile phone is divided into two parts
            // first part always contains 4 paragraphs
            // extension part contains the rest of them
            int skip = 0;
            int count = 4;
            int index = -1;
            if (descriptionExtension) {
                skip = 4;
                count = Integer.MAX_VALUE;
            }

            for (HTML.Element element : content.getElements()) {
                if (element instanceof HTML.Paragraph) {
                    index++;
                    if (index < skip) {
                        continue;
                    }
                    if (index == count) {
                        break;
                    }
                    description.add(((HTML.Paragraph) element).getText());
                }
            }
            return description;
        }

        @Override
        public void setText(int childIndex, CharSequence description) {
            ConstraintLayout layout = (ConstraintLayout) getChildAt(childIndex);
            TextView textView = layout.findViewById(R.id.paragraph_text);
            textView.setText(description);
        }
    }

    private class TabletHandler implements Handler, View.OnClickListener {
        @Override
        public List<CharSequence> getDescription(AtlasObject atlasObject) {
            HTML content = atlasObject.getDescription();
            if (content == null) {
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
            TextView textView = (TextView) getChildAt(childIndex);
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
