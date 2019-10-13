package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.adapter.SearchResultAdapter;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.SearchIndex;
import com.kidscademy.atlas.view.FabMenu;
import com.kidscademy.atlas.view.HexaIcon;
import com.kidscademy.atlas.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public class SearchResultActivity extends AppActivity {
    private static final Log log = LogFactory.getLog(SearchResultActivity.class);

    private static final String ARGS_KEYWORD = "search.result.keyword";
    private static final String ARGS_OBJECTS = "search.result.objects";

    public static void start(Activity activity, SearchIndex index) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, SearchResultActivity.class);
        intent.putExtra(ARGS_KEYWORD, index.getKeyword());
        intent.putExtra(ARGS_OBJECTS, index.getObjectIds());
        activity.startActivity(intent);
    }

    private final AtlasRepository repository;

    private String keyword;
    private int[] objectIds;

    private HexaIcon captionView;
    private TextView searchInput;

    private List<AtlasObject> results = new ArrayList<>();
    private SearchResultAdapter resultsAdapter;

    public SearchResultActivity() {
        super(R.layout.activity_search_result);
        log.trace("SearchResultActivity()");
        this.repository = app.repository();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        captionView = findViewById(R.id.search_result_caption);
        searchInput = findViewById(R.id.search_result_input);

        resultsAdapter = new SearchResultAdapter(this, results);
        RecyclerView resultsView = findViewById(R.id.search_result_objects);

        LinearLayoutManager resultsLayoutManager;
        if (isTablet()) {
            int rows = getResources().getInteger(R.integer.search_result_rows);
            resultsLayoutManager = new GridLayoutManager(this, rows, LinearLayoutManager.HORIZONTAL, false);

            int cardSpace = getResources().getDimensionPixelSize(R.dimen.card_space);
            resultsView.addItemDecoration(new SpacesItemDecoration(cardSpace));
        } else {
            resultsLayoutManager = new LinearLayoutManager(this);
            resultsLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        }
        resultsView.setLayoutManager(resultsLayoutManager);
        resultsView.setAdapter(resultsAdapter);

        onNewIntent(getIntent());
        if (isTablet()) {
            setClickListener(R.id.action_index);
            setClickListener(R.id.action_menu);
        } else {
            FabMenu fabMenu = findViewById(R.id.search_result_fab_menu);
            fabMenu.setOnClickListener(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log.trace("onNewIntent(Intent");
        keyword = intent.getStringExtra(ARGS_KEYWORD);
        objectIds = intent.getIntArrayExtra(ARGS_OBJECTS);
    }

    @Override
    public void onStart() {
        log.trace("onStart()");
        super.onStart();

        captionView.setText(Character.toString(keyword.charAt(0)).toUpperCase());
        searchInput.setText(keyword);

        results.clear();
        for (int id : objectIds) {
            results.add(repository.getObjectByIndex(id));
        }
        resultsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_index || id == R.id.search_result_fab_index) {
            IndexActivity.start(this);
        } else if (id == R.id.action_menu || id == R.id.search_result_fab_main_menu) {
            MenuActivity.start(this);
        } else if (id == R.id.search_result_fab_close) {
            finish();
        } else {
            super.onClick(view);
        }
    }
}
