package com.kidscademy.atlas.adapter;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kidscademy.atlas.activity.MobileReaderActivity;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.view.ReaderPage;

import js.log.Log;
import js.log.LogFactory;

/**
 * View pager adapter for atlas reader activity - mobile variant. {@link MobileReaderActivity} creates
 * an instance of this adapter and bind it to view pager. Main purpose of this adapter is to create page
 * views; it uses a cache to avoid inflating {@link ReaderPage} for every single page - see {@link #pages}.
 *
 * @author Iulian Rotaru
 * @see MobileReaderActivity
 */
public class MobileReaderAdapter extends PagerAdapter {
    private static final Log log = LogFactory.getLog(MobileReaderAdapter.class);

    private final LayoutInflater inflater;
    private final AtlasRepository repository;

    /**
     * Cache for view pager pages. Contains currently displayed page and offscreen pages, both sides.
     * It is populated on the fly by {@link #instantiateItem(ViewGroup, int)} then reused to avoid inflating
     * the same layout for every single page. Size of this cache is kept synchronized with parent view
     * pager offscreen page limit.
     */
    private final ReaderPage[] pages;

    public MobileReaderAdapter(LayoutInflater inflater, AtlasRepository repository) {
        super();
        log.trace("MobileReaderAdapter(LayoutInflater, AtlasRepository)");
        this.inflater = inflater;
        this.repository = repository;
        // off screen limit is the number of views on both sides of currently visible one
        // so we need to cache currently visible plus both side off screen views
        this.pages = new ReaderPage[2 * MobileReaderActivity.OFFSCREEN_PAGE_LIMIT + 1];
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup viewPager, int position) {
        log.trace("instantiateItem(ViewGroup, int)");
        ReaderPage page = pages[position % pages.length];
        if (page == null) {
            log.debug("Inflate explorer page");
            page = (ReaderPage) inflater.inflate(R.layout.item_reader_pager, viewPager, false);
            pages[position % pages.length] = page;
            viewPager.addView(page);
        }
        // by convention page position is used as object repository index
        page.setAtlasObject(repository.getObjectByIndex(position));
        return page;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup viewPager, int position, @NonNull Object object) {
        // current implementation uses cache for explorer pages and does not remove object view
        // viewPager.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        // this interface is not very intuitive; it actually expose an implementation detail
        // used by pager view to detect which child view is the object returned by instantiateItem
        return view == object;
    }

    @Override
    public int getCount() {
        return repository.getObjectsCount();
    }
}