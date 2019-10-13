package com.kidscademy.atlas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.SharingApp;

import java.util.List;

import js.log.Log;
import js.log.LogFactory;

/**
 * Adapter for list of {@link SharingApp} displayed by app share activity.
 *
 * @author Iulian Rotaru
 */
public class SharingAdapter extends RecyclerView.Adapter<SharingAdapter.Holder> {
    private static final Log log = LogFactory.getLog(SharingAdapter.class);

    private final LayoutInflater inflater;
    private final Listener listener;
    private final List<SharingApp> apps;

    public SharingAdapter(Context context, List<SharingApp> apps) {
        super();
        this.inflater = LayoutInflater.from(context);
        this.listener = (Listener) context;
        this.apps = apps;
    }

    @Override
    @NonNull
    public SharingAdapter.Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        log.trace("onCreateViewHolder(ViewGroup, int)");
        View view = inflater.inflate(R.layout.item_sharing, viewGroup, false);
        return new SharingAdapter.Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharingAdapter.Holder holder, int position) {
        log.trace("onBindViewHolder(ViewHolder, int)");
        holder.bindPosition(position);
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView nameText;
        private final TextView packageNameText;
        private final TextView versionText;
        private final ImageView iconView;

        private SharingApp app;

        Holder(View view) {
            super(view);

            nameText = view.findViewById(R.id.item_sharing_name);
            packageNameText = view.findViewById(R.id.item_sharing_package);
            versionText = view.findViewById(R.id.item_sharing_version);
            iconView = view.findViewById(R.id.item_sharing_icon);

            view.setOnClickListener(this);
        }

        void bindPosition(int position) {
            app = apps.get(position);

            nameText.setText(app.getAppName());
            packageNameText.setText(app.getPackageName());
            versionText.setText(app.getVersion());
            iconView.setImageDrawable(app.getIcon());
        }

        @Override
        public void onClick(View v) {
            listener.onSharingAppSelected(app);
        }
    }

    public interface Listener {
        void onSharingAppSelected(SharingApp app);
    }
}
