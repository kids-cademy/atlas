package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.adapter.UserGuideMobileAdapter;

import js.log.Log;
import js.log.LogFactory;

public class HelpActivity extends AppActivity implements ViewPager.OnPageChangeListener {
    private static final Log log = LogFactory.getLog(HelpActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, HelpActivity.class);
        activity.startActivity(intent);
    }

    // mobile phone related fields

    private ViewPager pager;
    private TextView pageTitle;
    private UserGuideMobileAdapter adapter;

    // tablet fields

    private TextView currentLabelView;
    private View currentSlideView;

    public HelpActivity() {
        super(R.layout.activity_help);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        if (isTablet()) {
            currentLabelView = setClickListener(R.id.help_overview);
            currentSlideView = findViewById(R.id.help_overview_slide);

            setClickListener(R.id.help_main_menu);
            setClickListener(R.id.help_atlas_reader);
            setClickListener(R.id.help_atlas_search);
            setClickListener(R.id.help_atlas_index);
            setClickListener(R.id.help_browser_sync);
        } else {
            pageTitle = findViewById(R.id.activity_help_page_title);
            pager = findViewById(R.id.activity_help_pager);
            pager.addOnPageChangeListener(this);
            adapter = new UserGuideMobileAdapter(getLayoutInflater());
            pager.setAdapter(adapter);
            setClickListener(R.id.activity_help_next_slide);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.help_overview) {
            updateInterface(view, R.id.help_overview_slide);
        } else if (id == R.id.help_main_menu) {
            updateInterface(view, R.id.help_main_menu_slide);
        } else if (id == R.id.help_atlas_reader) {
            updateInterface(view, R.id.help_atlas_reader_slide);
        } else if (id == R.id.help_atlas_search) {
            updateInterface(view, R.id.help_atlas_search_slide);
        } else if (id == R.id.help_atlas_index) {
            updateInterface(view, R.id.help_atlas_index_slide);
        } else if (id == R.id.help_browser_sync) {
            updateInterface(view, R.id.help_browser_sync_slide);
        } else if (id == R.id.activity_help_next_slide) {
            int position = pager.getCurrentItem() + 1;
            if (position < adapter.getCount()) {
                pager.setCurrentItem(position, true);
            }
        } else {
            super.onClick(view);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {
        pageTitle.setText(adapter.getCaption(position));
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    private void updateInterface(View labelView, @IdRes int slideIdRes) {
        if (currentLabelView != null) {
            currentLabelView.setTextColor(ContextCompat.getColor(this, R.color.text));
        }
        if (currentSlideView != null) {
            currentSlideView.setVisibility(View.GONE);
        }

        currentLabelView = (TextView) labelView;
        currentLabelView.setTextColor(ContextCompat.getColor(this, R.color.text_accent));

        // instead of caching all slide views is acceptable to keep only current slide
        currentSlideView = findViewById(slideIdRes);
        currentSlideView.setVisibility(View.VISIBLE);
    }
}
