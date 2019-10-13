package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.adapter.AtlasIndexAdapter;
import com.kidscademy.atlas.app.Audit;

import js.log.Log;
import js.log.LogFactory;

public class IndexActivity extends AppActivity implements AtlasIndexAdapter.OnObjectSelected {
    private static final Log log = LogFactory.getLog(IndexActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, IndexActivity.class);
        activity.startActivity(intent);
    }

    private final Audit audit;

    private AtlasIndexAdapter adapter;

    public IndexActivity() {
        super(R.layout.activity_index);
        log.trace("IndexActivity()");
        this.audit = app.audit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if (isTablet()) {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
        RecyclerView listView = findViewById(R.id.index_list_view);
        listView.setLayoutManager(layoutManager);

        adapter = new AtlasIndexAdapter(this, app.repository());
        listView.setAdapter(adapter);

        if(isTablet()) {
            setClickListener(R.id.action_swap_horizontal);
            setClickListener(R.id.action_unfold_less);
            setClickListener(R.id.action_unfold_more);
            setClickListener(R.id.action_search);
            setClickListener(R.id.action_menu);
        }
        else {
            setClickListener(R.id.index_fab_menu);
        }
    }

    @Override
    public void onObjectSelected(int objectIndex) {
        // TODO fix index activity audit
        // audit.selectIndex(object.toString());
        ReaderActivity.start(this, objectIndex);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_unfold_less || id == R.id.index_fab_collapse_all) {
            audit.collapseIndex();
            adapter.collapseAll();
        } else if (id == R.id.action_unfold_more || id == R.id.index_fab_expand_all) {
            audit.expandIndex();
            adapter.expandAll();
        } else if (id == R.id.action_swap_horizontal || id == R.id.index_fab_swap_vertical) {
            audit.swapIndex();
            adapter.swapVertical();
        } else if (id == R.id.action_back) {
            onBackPressed();
        } else if (id == R.id.action_search || id == R.id.index_fab_search) {
            SearchActivity.start(this);
        } else if (id == R.id.action_menu || id == R.id.index_fab_main_menu) {
            MenuActivity.start(this);
        }
    }
}
