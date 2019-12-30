package com.kidscademy.atlas.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.model.Fact;
import com.kidscademy.atlas.sync.ItemRevealEvent;
import com.kidscademy.atlas.util.RandomColor;

import js.log.Log;
import js.log.LogFactory;

/**
 * Facts item view displays fact name and related value. By default fact value is hidden, that is, fact item view is collapsed.
 * Collapsing state is changed by clicking on item.
 *
 * @author Iulian Rotaru
 */
public class FactItemView extends ConstraintLayout implements View.OnClickListener {
    private static final Log log = LogFactory.getLog(FactItemView.class);

    private ReaderActivity readerActivity;
    private TextView valueView;
    private int valueViewHeight;
    private ImageView expandButton;

    private boolean collapsed;

    public FactItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof ReaderActivity) {
            this.readerActivity = (ReaderActivity) context;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        valueView = findViewById(R.id.item_fact_value);
        expandButton = findViewById(R.id.item_fact_expand);

        setOnClickListener(this);
        if (expandButton != null) {
            expandButton.setOnClickListener(this);
        }
    }

    public void setFact(String objectName, Fact fact) {
        ImageView bulletView = findViewById(R.id.item_fact_bullet);
        bulletView.setColorFilter(RandomColor.getColor(getContext()));

        TextView keyView = findViewById(R.id.item_fact_name);
        keyView.setText(fact.getName());
        keyView.setTag(objectName);

        // hides value on fact initialization
        // takes care to reset value view height and expand button rotation, every time a new fact is set
        ViewGroup.LayoutParams layoutParams = valueView.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        valueView.setLayoutParams(layoutParams);
        if (expandButton != null) {
            expandButton.setRotation(0);
        }

        // prepare observer for value view layout update to retrieve value view height
        // after measurement, takes care to hide value view by setting layout parameters height to zero
        valueView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                valueView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                valueViewHeight = valueView.getMeasuredHeight();
                log.debug("valueViewHeight=%d", valueViewHeight);

                if (expandButton != null) {
                    ViewGroup.LayoutParams layoutParams = valueView.getLayoutParams();
                    layoutParams.height = 0;
                    valueView.setLayoutParams(layoutParams);
                    collapsed = true;
                }
            }
        });

        valueView.setText(fact.getValue());
    }

    @Override
    public void onClick(View view) {
        // click on fact expand toggle collapsed state
        // click on anything else push item reveal event

        if (view.getId() != R.id.item_fact_expand) {
            ViewGroup parent = (ViewGroup) view.getParent();
            readerActivity.pushEvent(new ItemRevealEvent(ItemRevealEvent.Type.FACT, parent.indexOfChild(view)));
            return;
        }

        collapsed = !collapsed;
        int start = collapsed ? valueViewHeight : 0;
        int end = !collapsed ? valueViewHeight : 0;

        ValueAnimator anim = ValueAnimator.ofInt(start, end);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = valueView.getLayoutParams();
                layoutParams.height = (int) valueAnimator.getAnimatedValue();
                valueView.setLayoutParams(layoutParams);
            }
        });
        anim.setDuration(400);
        anim.start();

        expandButton.animate().setDuration(400).rotation(collapsed ? 0 : 180);
    }
}
