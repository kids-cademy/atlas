package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

import java.util.List;

public class DescriptionColumnView extends LinearLayout {
    private final LayoutInflater inflater;

    /**
     * Description column width, in pixels.
     */
    private int columnWidth;
    /**
     * Description column height, in pixels.
     */
    private int columnHeight;
    /**
     * Paragraph vertical space, in pixels.
     */
    private int dividerHeight;

    private AtlasObject atlasObject;

    public DescriptionColumnView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
    }

    /**
     * Not very clear from APIDOC but dimensions seems to be pixels
     *
     * @param w    new width,
     * @param h    new height,
     * @param oldw old width,
     * @param oldh old height.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (columnWidth == 0) {
            columnWidth = w;
            columnHeight = h;
            dividerHeight = getDividerDrawable().getIntrinsicHeight();
            if (atlasObject != null) {
                update();
            }
        }
    }

    public void setWidth(int width) {
        this.columnWidth = width;
    }

    public void setHeight(int height) {
        this.columnHeight = height;
    }

    public void update(AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
        if (columnWidth != 0) {
            update();
        }
    }

    private void update() {
        if (atlasObject.getDescription() == null) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        List<String> description = atlasObject.getDescription().getText();
        int paragraphIndex = atlasObject.getDescriptionParagraphOffset();
        int currentHeight = 0;
        int childIndex = 0;

        for (; paragraphIndex < description.size(); ++childIndex, ++paragraphIndex) {
            String paragraph = description.get(paragraphIndex);
            boolean recycledChild = true;

            TextView paragraphView = (TextView) getChildAt(childIndex);
            if (paragraphView == null) {
                paragraphView = (TextView) inflater.inflate(R.layout.compo_paragraph, this, false);
                recycledChild = false;
            }

            float paragraphHeight = getMeasuredHeight(paragraphView, paragraph);
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
    }

    private float getMeasuredHeight(TextView paragraphView, String paragraph) {
        final boolean includePadding = true;
        Layout layout = new StaticLayout(paragraph,
                paragraphView.getPaint(),
                columnWidth,
                Layout.Alignment.ALIGN_NORMAL,
                paragraphView.getLineSpacingMultiplier(),
                paragraphView.getLineSpacingExtra(),
                includePadding);
        return layout.getHeight();
    }
}
