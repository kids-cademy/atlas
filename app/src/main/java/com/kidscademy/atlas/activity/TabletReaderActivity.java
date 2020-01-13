package com.kidscademy.atlas.activity;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.ReaderAction;
import com.kidscademy.atlas.sync.PageLoadEvent;
import com.kidscademy.atlas.sync.PageScrollEvent;
import com.kidscademy.atlas.view.ActionIcon;
import com.kidscademy.atlas.view.HorizontalScrollViewEx;
import com.kidscademy.atlas.view.ReaderObjectView;

import js.log.Log;
import js.log.LogFactory;

public class TabletReaderActivity extends ReaderActivity implements View.OnClickListener, ReaderAction.Listener, HorizontalScrollViewEx.OnScrollChangedListener {
    private static final Log log = LogFactory.getLog(TabletReaderActivity.class);

    private TextView titleView;
    private HorizontalScrollViewEx scrollView;
    private ReaderObjectView objectView;
    private ActionIcon actionSync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        titleView = findViewById(R.id.reader_header_title);
        scrollView = findViewById(R.id.reader_page_scroll);
        scrollView.setOnScrollChangedListener(this);
        objectView = findViewById(R.id.reader_object_view);
        actionSync = findViewById(R.id.action_sync);

        setClickListener(R.id.action_back);
        setClickListener(R.id.action_search);
        setClickListener(R.id.action_index);
        setClickListener(R.id.action_sync);
        setClickListener(R.id.action_previous);
        setClickListener(R.id.action_next);
        setClickListener(R.id.action_menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        actionSync.setColor(R.color.text);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_search) {
            SearchActivity.start(this);
        } else if (id == R.id.action_index) {
            IndexActivity.start(this);
        } else if (id == R.id.action_sync) {
            SyncActivity.startFromReader(this);
        } else if (id == R.id.action_next) {
            if (objectIndex < repository.getObjectsCount() - 1) {
                objectIndex++;
            }
            updateUserInterface(objectIndex);
        } else if (id == R.id.action_previous) {
            if (objectIndex > 0) {
                objectIndex--;
            }
            updateUserInterface(objectIndex);
        } else if (id == R.id.action_menu) {
            MenuActivity.start(this);
        } else if (id == R.id.action_back) {
            onBackPressed();
        }
    }

    @Override
    protected void updateUserInterface(int objectIndex) {
        this.objectIndex = objectIndex;
        scrollView.smoothScrollTo(0, 0);

        AtlasObject atlasObject = repository.getObjectByIndex(objectIndex);
        pushEvent(new PageLoadEvent(atlasObject));

        titleView.setText(atlasObject.getDisplay());
        objectView.setAtlasObject(atlasObject);
    }

    @Override
    public void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        double scrollableWidth = scrollView.getChildAt(0).getMeasuredWidth() - scrollView.getMeasuredWidth();
        pushEvent(new PageScrollEvent(scrollX / scrollableWidth));
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        actionSync.setColor(R.color.text_accent);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        super.onServiceDisconnected(name);
        actionSync.setColor(R.color.text);
    }
}
