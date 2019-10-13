package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.kidscademy.atlas.adapter.SearchKeywordsAdapter;
import com.kidscademy.atlas.app.Audit;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.CharTree;
import com.kidscademy.atlas.model.SearchIndex;
import com.kidscademy.atlas.view.FabMenu;
import com.kidscademy.atlas.view.HexaIcon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public class SearchActivity extends AppActivity implements SearchKeywordsAdapter.OnKeywordSelectListener {
    private static final Log log = LogFactory.getLog(SearchActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivity(intent);
    }

    private final AtlasRepository repository;
    private final Audit audit;

    private CharTree<SearchIndex> searchIndex;

    private HexaIcon captionView;
    private EditText searchInput;

    /**
     * Optional saved value for search input, default to null. This value is saved before going to search
     * results list: current search input value is saved into this field and it value replaced with entire
     * keyword. When back from search results list restore search input value.
     */
    private CharSequence currentSearchInput;

    private SearchKeywordsAdapter keywordsAdapter;
    private RecyclerView keywordsView;

    public SearchActivity() {
        super(R.layout.activity_search);
        log.trace("SearchActivity()");
        this.repository = app.repository();
        this.audit = app.audit();
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        searchIndex = new CharTree<>(repository.getSearchIndices());

        captionView = findViewById(R.id.search_caption);
        searchInput = findViewById(R.id.search_input);
        searchInput.setInputType(searchInput.getInputType()
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                | EditorInfo.TYPE_TEXT_VARIATION_FILTER);
        TextWatcher searchInputWatcher = new SearchTextWatcher();
        searchInput.addTextChangedListener(searchInputWatcher);

        keywordsAdapter = new SearchKeywordsAdapter(this);

        keywordsView = findViewById(R.id.search_keywords);
        LinearLayoutManager keywordsLayoutManager = new LinearLayoutManager(this);
        if (isTablet()) {
            keywordsLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        }
        keywordsView.setLayoutManager(keywordsLayoutManager);
        keywordsView.setAdapter(keywordsAdapter);
        keywordsView.setVisibility(View.VISIBLE);

        setClickListener(R.id.search_input);
        if (isTablet()) {
            setClickListener(R.id.action_index);
            setClickListener(R.id.action_menu);
        } else {
            FabMenu fabMenu = findViewById(R.id.search_fab_menu);
            fabMenu.setOnClickListener(this);
        }
    }

    private void onInputChanged(String text) {
        captionView.setText(text.isEmpty() ? null : Character.toString(text.charAt(0)).toUpperCase());
        currentSearchInput = null;

        List<SearchIndex> indices = searchIndex.find(text);
        Collections.sort(indices, new Comparator<SearchIndex>() {
            @Override
            public int compare(SearchIndex left, SearchIndex right) {
                if (left.getKeywordRelevance() == right.getKeywordRelevance()) {
                    return left.getKeyword().compareTo(right.getKeyword());
                }
                return ((Integer) right.getKeywordRelevance()).compareTo(left.getKeywordRelevance());
            }
        });
        keywordsAdapter.setIndices(indices);
        keywordsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onKeywordSelect(SearchIndex index) {
        log.debug("Search for |%s|.", index.getKeyword());
        audit.searchKeyword(index.getKeyword());
        SearchResultActivity.start(this, index);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.search_input) {
            searchInput.setCursorVisible(true);
            if (currentSearchInput != null) {
                searchInput.setText(currentSearchInput);
                searchInput.setSelection(searchInput.length());
            }
            keywordsView.setVisibility(View.VISIBLE);
        } else if (id == R.id.action_index || id == R.id.search_fab_index) {
            IndexActivity.start(this);
        } else if (id == R.id.action_menu || id == R.id.search_fab_main_menu) {
            MenuActivity.start(this);
        } else {
            super.onClick(view);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // INNER CLASSES

    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable searchInput) {
            onInputChanged(searchInput.toString());
        }
    }
}
