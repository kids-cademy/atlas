package com.kidscademy.atlas.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidscademy.atlas.R;

import js.log.Log;
import js.log.LogFactory;

public class UserGuideMobileAdapter extends PagerAdapter {
    private static final Log log = LogFactory.getLog(MobileReaderAdapter.class);

    private static final int[] layoutResIds = new int[]{
            R.layout.compo_help_overview,
            R.layout.compo_help_main_menu,
            R.layout.compo_help_atlas_reader,
            R.layout.compo_help_atlas_search,
            R.layout.compo_help_atlas_index,
            R.layout.compo_help_browser_sync
    };

    private static final int[] captionResIds = new int[]{
            R.string.help_overview,
            R.string.help_main_menu,
            R.string.menu_reader,
            R.string.menu_search,
            R.string.menu_index,
            R.string.menu_sync
    };

    private final LayoutInflater inflater;

    public UserGuideMobileAdapter(LayoutInflater inflater) {
        log.trace("UserGuideMobileAdapter(LayoutInflater)");
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup viewPager, int position) {
        log.trace("instantiateItem(ViewGroup, int)");
        View page = inflater.inflate(layoutResIds[position], viewPager, false);
        viewPager.addView(page);
        return page;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup viewPager, int position, @NonNull Object object) {
        viewPager.removeView((View) object);
    }

    @Override
    public int getCount() {
        return layoutResIds.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public int getCaption(int position) {
        return captionResIds[position];
    }
}
