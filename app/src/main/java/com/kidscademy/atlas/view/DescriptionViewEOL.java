package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

import java.util.Arrays;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public class DescriptionViewEOL extends LinearLayout {
    private static final Log log = LogFactory.getLog(DescriptionViewEOL.class);

    private final LayoutInflater inflater;

    /**
     * Description view width, in pixels.
     */
    private float availableWidth;
    /**
     * Description view height, in pixels.
     */
    private float availableHeight;
    /**
     * Paragraph vertical space, in pixels.
     */
    private float dividerHeight;

    private int paragraphsCount;

    private AtlasObject atlasObject;

    public DescriptionViewEOL(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * Not very clear from APIDOC but dimensions seems to be pixels
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (availableWidth == 0) {
            availableWidth = w;
            availableHeight = h;
            dividerHeight = dp2px(16);
            log.debug("onSizeChanged: availableWidth=%f availableHeight=%f dividerHeight=%f", availableWidth, availableHeight, dividerHeight);
            update();
        }
    }

    //                                                                          16
    // 10 - availableWidth=900.000000 availableHeight=1198.000000 dividerHeight=32.000000 dm.density=2.000000, dm.densityDpi=320, dm.scaledDensity=2.000000 font 20sp 40.00 46.87 62.34
    // 7  - availableWidth=466.000000 availableHeight=554.000000  dividerHeight=21.300001 dm.density=1.331250, dm.densityDpi=213, dm.scaledDensity=1.331250 font 14sp 20.00 22.26 29.61
    // X  - availableWidth=450.000000 availableHeight=598.000000  dividerHeight=16.000000 dm.density=1.000000, dm.densityDpi=160, dm.scaledDensity=1.000000 font xxsp 21.00 23.43 31.17

    public static float convertDpToPixelx(float dp, Context context) {
//      return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_HIGH);
        log.debug("density=%f", context.getResources().getDisplayMetrics().density);
        return dp * context.getResources().getDisplayMetrics().density;
//        return dp * 1.33F;
    }

    public static float px2dp(float px) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                px,
                Resources.getSystem().getDisplayMetrics()
        );
    }

    public static float dp2px(float dp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        log.debug("dm.density=%f, dm.densityDpi=%d, dm.scaledDensity=%f", dm.density, dm.densityDpi, dm.scaledDensity);

//        return TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dp,
//                Resources.getSystem().getDisplayMetrics()
//        );

        return dp * 1.33F;
    }

    public void update(AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
        if (availableWidth != 0) {
            update();
        }
    }

    private void update() {
        removeAllViews();

        if (atlasObject.getDescription() == null) {
            return;
        }

        List<String> description = atlasObject.getDescription().getText();
        int index = atlasObject.getDescriptionParagraphOffset();
        int currentHeight = 0;

        int paragraphsCount = 0;
        for (; index < description.size(); ++index) {
            String paragraph = description.get(index);

            TextView paragraphView = (TextView) inflater.inflate(R.layout.compo_paragraph, this, false);
            float paragraphHeight = getMeasuredHeight(paragraphView, paragraph);
            log.debug("Current height=%d paragraph height=%f", currentHeight, paragraphHeight);

            currentHeight += paragraphHeight;
            if (currentHeight > availableHeight) {
                break;
            }
            if (paragraphsCount > 0) {
                currentHeight += dividerHeight;
            }

            paragraphView.setText(paragraph);
            addView(paragraphView);

            ++paragraphsCount;
        }

        atlasObject.updateDescriptionParagraphOffset(index);
        log.debug("Total paragraphs=%d view paragraphs=%d", description.size(), paragraphsCount);
    }

    private float getMeasuredHeight(TextView paragraphView, String paragraph) {
        // paint is extracted from view and seems to already have typeface and text size
        Paint paint = paragraphView.getPaint();
//        paint.setTypeface(paragraphView.getTypeface());
//        paint.setTextSize(paragraphView.getTextSize()/ getResources().getDisplayMetrics().density);

        int index = 0;
        int rowsCount = 0;
        for (; ; ) {
            ++rowsCount;
            int charsCount = paint.breakText(paragraph.substring(index), true, availableWidth, null);
            if (index + charsCount >= paragraph.length()) {
                break;
            }
            index = roundToWord(paragraph, index + charsCount);
        }

        Rect bounds = new Rect();
        // TODO: there is no need to measure all paragraph since we are interested only on height
        paint.getTextBounds(paragraph, 0, paragraph.length(), bounds);
        // it seems text bounds are on device independent pixels
        // log.debug("rows count=%d bounds height=%d", rowsCount, bounds.height());
        //return convertDpToPixel(rowsCount * bounds.height(), getContext());
        //return rowsCount * bounds.height();

        Paint.FontMetrics fm = paint.getFontMetrics();
        float height = fm.descent - fm.ascent + fm.leading;
        height *= paragraphView.getLineSpacingMultiplier();
        height += paragraphView.getLineSpacingExtra();
        log.debug("rows count=%d bounds height=%d line height=%f dp2px height=%f", rowsCount, bounds.height(), height, dp2px(height));
        // it seems units are device independent and need to be converted to pixels
        return dp2px(rowsCount * height);
        //return rowsCount * height;
    }

    private static final List<Character> WORD_SEPARATORS = Arrays.asList(' ', '.', '\r', '\t', '\n', ':', ';', '?', '!', '(', '[', ')', ']');

    private int roundToWord(String paragraph, int index) {
        if (index == paragraph.length()) {
            return index;
        }
        if (WORD_SEPARATORS.contains(paragraph.charAt(index))) {
            return index;
        }
        while (index > 0 && !WORD_SEPARATORS.contains(paragraph.charAt(index))) {
            --index;
        }
        return index;
    }
}
