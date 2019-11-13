package com.kidscademy.atlas.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.adapter.MobileReaderAdapter;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.sync.PageLoadEvent;
import com.kidscademy.atlas.view.FabMenu;

import js.log.Log;
import js.log.LogFactory;
import js.util.Player;

public class MobileReaderActivity extends ReaderActivity implements View.OnClickListener, Player.StateListener, ViewPager.OnPageChangeListener {
    private static final Log log = LogFactory.getLog(MobileReaderActivity.class);

    /**
     * The number of view pager pages pre-loaded on both sides of currently visible page.
     */
    public static final int OFFSCREEN_PAGE_LIMIT = 1;

    private ViewPager pager;

    private FabMenu fabMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        pager = findViewById(R.id.activity_atlas_reader_pager);
        pager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
        pager.addOnPageChangeListener(this);

        MobileReaderAdapter adapter = new MobileReaderAdapter(getLayoutInflater(), app.repository());
        pager.setAdapter(adapter);

        fabMenu = findViewById(R.id.activity_reader_fab_menu);
        fabMenu.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.activity_reader_fab_search) {
            SearchActivity.start(this);
        } else if (id == R.id.activity_reader_fab_index) {
            IndexActivity.start(this);
        } else if (id == R.id.activity_reader_fab_sync) {
            SyncActivity.start(this);
        } else if (id == R.id.activity_reader_fab_main_menu) {
            MenuActivity.start(this);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        fabMenu.show();
        player.stop();
        updateUserInterface(position);
    }

    @Override
    protected void updateUserInterface(int objectIndex) {
        log.trace("updateUserInterface(int)");
        pager.setCurrentItem(objectIndex, false);
        AtlasObject atlasObject = app.repository().getObjectByIndex(objectIndex);
        pushEvent(new PageLoadEvent(atlasObject));
    }
}