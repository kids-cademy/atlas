package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.kidscademy.atlas.R;

public class FabMenu extends LinearLayout implements View.OnClickListener {
    private RandomColorFAB[] fabItems;
    private Animation[] animOpenFabMenuItem;
    private Animation[] animCloseFabMenuItem;

    private Integer backgroundColor;
    private boolean opened;
    private boolean visible;

    private OnClickListener listener;

    public FabMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FabMenu, 0, 0);
        try {
            if (a.hasValue(R.styleable.FabMenu_backgroundColor)) {
                backgroundColor = a.getColor(R.styleable.FabMenu_backgroundColor, 0);
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        fabItems = new RandomColorFAB[getChildCount() - 1];
        for (int i = 0, j = 0; i < getChildCount(); ++i) {
            RandomColorFAB fab = (RandomColorFAB) getChildAt(i);
            fab.setOnClickListener(this);
            if (!"control".equals(fab.getTag())) {
                fabItems[j++] = fab;
            }
        }

        animOpenFabMenuItem = new Animation[fabItems.length];
        animCloseFabMenuItem = new Animation[fabItems.length];

        int offset = getOrientation() == VERTICAL ? 100 : -100;
        for (int i = 0; i < animOpenFabMenuItem.length; ++i) {
            animOpenFabMenuItem[i] = AnimationUtils.loadAnimation(getContext(), R.anim.open_fab_menu_item);
            animOpenFabMenuItem[i].setStartOffset(i * offset);
        }
        for (int i = 0; i < animCloseFabMenuItem.length; ++i) {
            animCloseFabMenuItem[i] = AnimationUtils.loadAnimation(getContext(), R.anim.close_fab_menu_item);
            animCloseFabMenuItem[i].setStartOffset(animCloseFabMenuItem.length - i * offset);
        }

        opened = false;
        visible = true;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if ("control".equals(view.getTag())) {
            toggleFabMenu();
            return;
        }
        if (listener != null) {
            listener.onClick(view);
        }
    }

    public void show() {
        if (!visible) {
            visible = true;
            AlphaAnimation animation = new AlphaAnimation(0, 1);
            animation.setDuration(1000);
            animation.setFillAfter(true);
            startAnimation(animation);
        }
    }

    public void hide() {
        if (visible) {
            visible = false;
            AlphaAnimation animation = new AlphaAnimation(1, 0);
            animation.setDuration(600);
            animation.setFillAfter(true);
            startAnimation(animation);
        }
    }

    private void toggleFabMenu() {
        if (opened) {
            for (int i = 0; i < fabItems.length; ++i) {
                fabItems[i].startAnimation(animCloseFabMenuItem[i]);
                fabItems[i].hide();
                fabItems[i].setClickable(false);
            }
            if (backgroundColor != null) {
                setBackgroundResource(0);
            }
            opened = false;
        } else {
            for (int i = 0; i < fabItems.length; ++i) {
                fabItems[i].startAnimation(animOpenFabMenuItem[i]);
                fabItems[i].show();
                fabItems[i].setClickable(true);
            }
            if (backgroundColor != null) {
                setBackgroundColor(backgroundColor);
            }
            opened = true;
        }
    }

    // --------------------------------------------------------------------------------------------
    // FAB MENU BEHAVIOR

    public static class Behavior extends CoordinatorLayout.Behavior<FabMenu> {
        public Behavior(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull FabMenu child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull final FabMenu fabMenu, @NonNull View nestedScroll, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
            super.onNestedScroll(coordinatorLayout, fabMenu, nestedScroll, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
            if (dyConsumed < 0) {
                // move page down to reveal top content; show fab menu
                fabMenu.show();
            } else if (dyConsumed > 0) {
                // move page up to reveal bottom content; hide fab menu
                fabMenu.hide();
            }
        }
    }
}
