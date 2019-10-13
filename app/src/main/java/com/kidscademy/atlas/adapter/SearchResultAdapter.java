package com.kidscademy.atlas.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.activity.ReaderActivity;
import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.util.Strings;

import java.util.List;

import js.util.BitmapLoader;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.Holder> {
    private final Activity activity;
    private final LayoutInflater inflater;
    private final List<AtlasObject> objects;

    public SearchResultAdapter(Activity activity, List<AtlasObject> objects) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.objects = objects;
    }

    @Override
    @NonNull
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(activity, inflater.inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final Activity activity;
        private final ImageView iconView;
        private final TextView nameView;
        private final TextView alliassesView;
        private final TextView definitionView;

        private AtlasObject object;

        Holder(Activity activity, View itemView) {
            super(itemView);
            this.activity = activity;

            iconView = itemView.findViewById(R.id.search_result_icon);
            nameView = itemView.findViewById(R.id.search_result_name);
            alliassesView = itemView.findViewById(R.id.search_result_aliasses);
            definitionView = itemView.findViewById(R.id.search_result_definition);
            itemView.setOnClickListener(this);
        }

        void bindPosition(int position) {
            object = objects.get(position);

            BitmapLoader loader = new BitmapLoader(activity, object.getIconPath(), iconView, 1);
            loader.start();

            nameView.setText(object.getDisplay());
            definitionView.setText(object.getDefinition());
            if (alliassesView != null) {
                alliassesView.setText(Strings.getAliasesDisplay(object.getAliases()));
            }
        }

        @Override
        public void onClick(View view) {
            ReaderActivity.start(activity, object.getIndex());
        }
    }
}
