package com.kidscademy.atlas.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.util.RandomColor;

/**
 * Hexagonal icon.
 *
 * @author Iulian Rotaru
 */
public class HexaIcon extends View implements ValueAnimator.AnimatorUpdateListener, View.OnClickListener {
    private static final float DEF_ICON_RELATIVE_SIZE = 0.6F;
    private static final int ANIMATION_DURATION = 300;

    private int borderSize;
    private int iconColor;
    private Drawable iconDrawable;

    private String text;
    private int textColor;
    private Paint textPaint;

    private final Paint backgroundPaint;
    private final Paint borderPaint;
    private float iconRelativeSize = DEF_ICON_RELATIVE_SIZE;
    private ValueAnimator iconCollapseAnimator;
    private OnClickListener clickListener;

    public HexaIcon(Context context, AttributeSet attrs) {
        super(context, attrs);

        Integer backgroundColor = null;
        int backgroundAlpha, borderColor, iconDrawableId;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HexaIcon, 0, 0);
        try {
            if (a.hasValue(R.styleable.HexaIcon_backgroundColor)) {
                backgroundColor = a.getColor(R.styleable.HexaIcon_backgroundColor, 0);
            }
            backgroundAlpha = a.getInt(R.styleable.HexaIcon_backgroundAlpha, 128);
            borderSize = a.getInt(R.styleable.HexaIcon_borderSize, 0);
            borderColor = a.getColor(R.styleable.HexaIcon_borderColor, Color.WHITE);
            iconColor = a.getColor(R.styleable.HexaIcon_iconColor, Color.WHITE);
            iconDrawableId = a.getResourceId(R.styleable.HexaIcon_iconDrawable, 0);
            text = a.getString(R.styleable.HexaIcon_text);
            textColor = a.getColor(R.styleable.HexaIcon_textColor, Color.WHITE);
        } finally {
            a.recycle();
        }

        if (iconDrawableId != 0) {
            iconDrawable = VectorDrawableCompat.create(getResources(), iconDrawableId, null);
        }

        backgroundPaint = new Paint();
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setColor(backgroundColor != null ? backgroundColor : ContextCompat.getColor(context, RandomColor.getRandomColor()));
        backgroundPaint.setAlpha(backgroundAlpha);

        if (borderSize != 0) {
            borderPaint = new Paint();
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderSize);
            borderPaint.setColor(borderColor);
        } else {
            borderPaint = null;
        }

        iconCollapseAnimator = ValueAnimator.ofFloat(DEF_ICON_RELATIVE_SIZE, 0);
        iconCollapseAnimator.setDuration(ANIMATION_DURATION);
        iconCollapseAnimator.addUpdateListener(this);
        iconCollapseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                HexaIcon.this.iconRelativeSize = DEF_ICON_RELATIVE_SIZE;
                if (clickListener != null) {
                    clickListener.onClick(HexaIcon.this);
                }
            }
        });

        setText(text);
        setClickable(true);

        super.setOnClickListener(this);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        iconRelativeSize = (float) animator.getAnimatedValue();
        invalidate();
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View view) {
        iconCollapseAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int contentDimension = Math.min(contentWidth, contentHeight);

        float[][] hexaVertices = getHexaVertices(contentDimension);
        canvas.drawPath(path(hexaVertices), backgroundPaint);
        if (borderPaint != null) {
            float scaleFactor = (float) (contentDimension - borderSize) / contentDimension;
            canvas.drawPath(path(scale(hexaVertices, contentDimension, scaleFactor)), borderPaint);
        }

        int iconDimension = (int) (iconRelativeSize * contentDimension);
        int x1 = (contentWidth - iconDimension) / 2;
        int y1 = (contentHeight - iconDimension) / 2;
        int x2 = x1 + iconDimension;
        int y2 = y1 + iconDimension;

        if (text != null) {
            int fontSize = (Math.min(contentWidth, contentHeight) / 2);
            textPaint.setTextSize(fontSize);
            canvas.drawText(text, contentWidth / 2, contentHeight / 2 - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
            return;
        }

        if (iconDrawable != null) {
            iconDrawable.setBounds(x1, y1, x2, y2);
            DrawableCompat.setTint(iconDrawable, iconColor);
            iconDrawable.draw(canvas);
        }
    }

    private float[][] getHexaVertices(float dimension) {
        float[][] vertices = new float[6][2];

        float dx = 0.25F * dimension;
        float dy = 0.433F * dimension;

        vertices[0][0] = dx;
        vertices[0][1] = dimension / 2 - dy;
        vertices[1][0] = 0;
        vertices[1][1] = dimension / 2;
        vertices[2][0] = vertices[0][0];
        vertices[2][1] = dimension / 2 + dy;
        vertices[3][0] = dimension - dx;
        vertices[3][1] = vertices[2][1];
        vertices[4][0] = dimension;
        vertices[4][1] = dimension / 2;
        vertices[5][0] = vertices[3][0];
        vertices[5][1] = vertices[0][1];

        return vertices;
    }

    private static float[][] scale(float[][] vertices, float dimension, float scaleFactor) {
        float cx = dimension / 2;
        float cy = dimension / 2;
        for (int i = 0; i < vertices.length; ++i) {
            vertices[i][0] = cx + scaleFactor * (vertices[i][0] - cx);
            vertices[i][1] = cy + scaleFactor * (vertices[i][1] - cy);
        }
        return vertices;
    }

    private static Path path(float[][] vertices) {
        Path path = new Path();
        path.moveTo(vertices[0][0], vertices[0][1]);
        for (int i = 1; i < vertices.length; ++i) {
            path.lineTo(vertices[i][0], vertices[i][1]);
        }
        path.close();
        return path;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        // it seems alpha is not preserved when set color
        int alpha = backgroundPaint.getAlpha();
        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setAlpha(alpha);
        invalidate();
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
        invalidate();
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
        invalidate();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setColor(textColor);
            textPaint.setAntiAlias(true);
            textPaint.setFakeBoldText(false);
            textPaint.setStyle(Paint.Style.FILL);
            textPaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStrokeWidth(0);
        }
        invalidate();
    }
}
