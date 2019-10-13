package com.kidscademy.atlas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.SearchIndex;

import java.util.List;

import js.lang.BugError;

public class SearchKeywordsAdapter extends RecyclerView.Adapter<SearchKeywordsAdapter.Holder> {
    private final LayoutInflater inflater;
    private final OnKeywordSelectListener listener;

    private List<SearchIndex> indices;

    public SearchKeywordsAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
        if (!(context instanceof OnKeywordSelectListener)) {
            throw new BugError("Context |%s| does not implement |%s|.", context.getClass(), OnKeywordSelectListener.class);
        }
        this.listener = (OnKeywordSelectListener) context;
    }

    public void setIndices(List<SearchIndex> indices) {
        this.indices = indices;
    }

    @Override
    @NonNull
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(inflater.inflate(R.layout.item_search_keyword, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return indices != null ? indices.size() : 0;
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView keywordView;

        private SearchIndex index;

        Holder(View itemView) {
            super(itemView);
            keywordView = (TextView) itemView;
            keywordView.setOnClickListener(this);
        }

        void bindPosition(int position) {
            index = indices.get(position);
            keywordView.setText(index.getKeyword());
        }

        @Override
        public void onClick(View view) {
            listener.onKeywordSelect(index);
        }
    }

    public interface OnKeywordSelectListener {
        void onKeywordSelect(SearchIndex index);
    }
}

