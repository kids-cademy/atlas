package com.kidscademy.atlas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasObjectsGroup;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.util.RandomColor;
import com.kidscademy.atlas.view.HexaIcon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;

public class AtlasIndexAdapter extends RecyclerView.Adapter<AtlasIndexAdapter.Holder> {
    private static final Log log = LogFactory.getLog(AtlasIndexAdapter.class);

    private final Context context;
    private final OnObjectSelected listener;
    private final LayoutInflater inflater;
    private final List<AtlasObjectsGroup> groups;

    private boolean sortAscending;

    public AtlasIndexAdapter(Context context, AtlasRepository repository) {
        super();
        log.trace("AtlasIndexAdapter(Context, List<AtlasObjectsGroup>)");
        this.context = context;
        this.listener = (OnObjectSelected) context;
        this.inflater = LayoutInflater.from(context);
        this.groups = getObjectsGroupByCaption(repository);
        this.sortAscending = true;
    }

    @Override
    @NonNull
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        log.trace("onCreateViewHolder(ViewGroup, int)");
        return new Holder(inflater.inflate(R.layout.compo_objects_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AtlasIndexAdapter.Holder holder, int position) {
        log.trace("onBindViewHolder(ViewHolder, int)");
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void collapseAll() {
        for (AtlasObjectsGroup group : groups) {
            group.setCollapsed(true);
        }
        notifyDataSetChanged();
    }

    public void expandAll() {
        for (AtlasObjectsGroup group : groups) {
            group.setCollapsed(false);
        }
        notifyDataSetChanged();
    }

    public void swapVertical() {
        sortAscending = !sortAscending;

        Collections.sort(groups, new Comparator<AtlasObjectsGroup>() {
            @Override
            public int compare(AtlasObjectsGroup left, AtlasObjectsGroup right) {
                return sortAscending ? left.compareTo(right) : right.compareTo(left);
            }
        });

        notifyDataSetChanged();
    }

    private static List<AtlasObjectsGroup> getObjectsGroupByCaption(AtlasRepository repository) {
        Map<String, List<AtlasObject>> atlasObjectByCaption = new TreeMap<>();
        for (int i = 0; i < repository.getObjectsCount(); ++i) {
            final AtlasObject atlasObject = repository.getObjectByIndex(i);
            String caption = Character.toString(atlasObject.getDisplay().charAt(0)).toUpperCase(Locale.getDefault());
            List<AtlasObject> atlasObjects = atlasObjectByCaption.get(caption);
            if (atlasObjects == null) {
                atlasObjects = new ArrayList<>();
                atlasObjectByCaption.put(caption, atlasObjects);
            }
            atlasObjects.add(atlasObject);
        }

        List<AtlasObjectsGroup> groups = new ArrayList<>();
        for (String caption : atlasObjectByCaption.keySet()) {
            groups.add(new AtlasObjectsGroup(caption, atlasObjectByCaption.get(caption)));
        }
        return groups;
    }

    // ---------------------------------------------------------------------------------------------

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final HexaIcon captionView;
        private final ViewGroup objectsView;

        private AtlasObjectsGroup group;

        Holder(View view) {
            super(view);

            captionView = view.findViewById(R.id.group_list_item_caption);
            captionView.setOnClickListener(this);

            objectsView = view.findViewById(R.id.group_list_item_objects);
            for (int i = 0; i < objectsView.getChildCount(); ++i) {
                objectsView.getChildAt(i).setOnClickListener(this);
            }
        }

        void bindPosition(int position) {
            group = groups.get(position);

            captionView.setBackgroundColor(ContextCompat.getColor(context, RandomColor.getRandomColor()));
            captionView.setText(group.getCaption());

            final List<AtlasObject> objects = group.getObjects();

            for (int i = objectsView.getChildCount(); i < objects.size(); ++i) {
                View instrumentView = inflater.inflate(R.layout.item_objects_group, objectsView, false);
                instrumentView.setOnClickListener(this);
                objectsView.addView(instrumentView);
            }

            for (int i = 0; i < objects.size(); ++i) {
                AtlasObject atlasObject = objects.get(i);

                View objectView = objectsView.getChildAt(i);
                objectView.setBackgroundColor(ContextCompat.getColor(context, R.color.black_T40));
                objectView.setVisibility(View.VISIBLE);
                objectView.setTag(atlasObject.getIndex());

                if(atlasObject.hasIcon()) {
                    ImageView iconView = objectView.findViewById(R.id.object_icon);
                    BitmapLoader loader = new BitmapLoader(context, atlasObject.getIconPath(), iconView, 1);
                    loader.start();
                }

                TextView nameView = objectView.findViewById(R.id.object_name);
                nameView.setText(atlasObject.getDisplay());

                TextView definitionView = objectView.findViewById(R.id.object_definition);
                if (definitionView != null) {
                    definitionView.setText(atlasObject.getDefinition());
                }
            }

            for (int i = objects.size(); i < objectsView.getChildCount(); ++i) {
                objectsView.getChildAt(i).setVisibility(View.GONE);
            }

            objectsView.setVisibility(group.isCollapsed() ? View.GONE : View.VISIBLE);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.group_list_item_caption) {
                group.setCollapsed(!group.isCollapsed());
                objectsView.setVisibility(group.isCollapsed() ? View.GONE : View.VISIBLE);
            } else {
                listener.onObjectSelected((Integer) view.getTag());
            }
        }
    }

    public interface OnObjectSelected {
        void onObjectSelected(int objectIndex);
    }
}
