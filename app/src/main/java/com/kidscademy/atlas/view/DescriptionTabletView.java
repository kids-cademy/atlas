package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.util.Views;

/**
 * Description column view for tablet. This view has fixed dimensions, {@link #columnWidth} and {@link #columnHeight}
 * and a known divider height. It grabs as much description as it can accommodate in its real estate.
 * <p>
 * Does not display incomplete paragraphs so is possible that actual text height to be less that this
 * view available height.
 * <p>
 * In order to determine the amount of text to display this view needs to measure every paragraph height.
 * For that it uses {@link Views#getMeasuredHeight(TextView, int, String)} function.
 *
 * @author Iulian Rotaru
 */
public class DescriptionTabletView extends LinearLayout implements ReaderSectionView {
    private static final int UNSPECIFIED = -1;

    private final LayoutInflater inflater;

    /**
     * Description column width, in pixels.
     */
    @Dimension
    private int columnWidth = UNSPECIFIED;

    /**
     * Description column height, in pixels.
     */
    @Dimension
    private int columnHeight = UNSPECIFIED;

    /**
     * Paragraph vertical space, in pixels.
     */
    private int dividerHeight;

    @ColorRes
    private int textColor = R.color.text;

    private AtlasObject atlasObject;

    public DescriptionTabletView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dividerHeight = getDividerDrawable().getIntrinsicHeight();
    }

    public void setColumnWidth(@Dimension int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void setColumnHeight(@Dimension int columnHeight) {
        this.columnHeight = columnHeight;
    }

    public void setTextColor(@ColorRes int textColor) {
        this.textColor = textColor;
    }

    /**
     * View lifecycle handler invoked when size is changed. It is used to initialize this view dimensions.
     * Not very clear from APIDOC but dimensions seems to be pixels; this method count on that.
     * <p>
     * If atlas object was already provided by a previous call of {@link #update(AtlasObject)} this method
     * trigger text rendering, see {@link #render(AtlasObject)}.
     *
     * @param w    new width,
     * @param h    new height,
     * @param oldw old width,
     * @param oldh old height.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (columnWidth != w || columnHeight != h) {
            columnWidth = w;
            columnHeight = h;
            if (atlasObject != null) {
                render(atlasObject);
            }
        }
    }

    public void update(AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
        if (columnWidth != UNSPECIFIED) {
            render(atlasObject);
        }
    }

    /**
     * Render some paragraphs from object description into this view available space. Consider description
     * paragraph offset when select the first paragraph to display - see {@link AtlasObject#getDescriptionParagraphOffset()};
     * this method has side effects: it updates paragraph offset with the amount of paragraphs consumed.
     * <p>
     * Amount of paragraphs to display is determined by measuring the height necessary for each paragraph.
     * This view create child text view on the fly and reuse them.
     * <p>
     * In order for this method to work needs proper initialization of the {@link #columnWidth}, {@link #columnHeight}
     * and {@link #dividerHeight}. Also provided atlas object should have description.
     *
     * @param atlasObject object to display description. It state is altered.
     */
    private void render(AtlasObject atlasObject) {
        if (!atlasObject.hasDescription()) {
            return;
        }
        String[] description = atlasObject.getDescription();
        int paragraphIndex = atlasObject.getDescriptionParagraphOffset();
        int currentHeight = 0;
        int childIndex = 0;

        for (; paragraphIndex < description.length; ++childIndex, ++paragraphIndex) {
            String paragraph = description[paragraphIndex];
            boolean recycledChild = true;

            TextView paragraphView = (TextView) getChildAt(childIndex);
            if (paragraphView == null) {
                paragraphView = (TextView) inflater.inflate(R.layout.compo_paragraph, this, false);
                recycledChild = false;
            }
            paragraphView.setTextColor(ContextCompat.getColor(getContext(), textColor));

            int paragraphHeight = Views.getMeasuredHeight(paragraphView, columnWidth, paragraph);
            currentHeight += paragraphHeight;
            if (currentHeight > columnHeight) {
                break;
            }
            currentHeight += dividerHeight;

            paragraphView.setText(paragraph);
            if (recycledChild) {
                paragraphView.setVisibility(View.VISIBLE);
            } else {
                addView(paragraphView);
            }
        }

        for (; childIndex < getChildCount(); ++childIndex) {
            getChildAt(childIndex).setVisibility(View.GONE);
        }

        atlasObject.updateDescriptionParagraphOffset(paragraphIndex);
        // take care to reset atlas object cache since this view is reused
        this.atlasObject = null;
    }
}
