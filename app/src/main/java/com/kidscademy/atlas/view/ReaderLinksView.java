package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.app.Audit;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Link;
import com.kidscademy.atlas.util.RandomColor;

import js.util.BitmapLoader;

public class ReaderLinksView extends ConstraintLayout implements View.OnClickListener {
    private final Audit audit;

    private HexaIcon captionView;
    private ViewGroup listView;

    public ReaderLinksView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.audit = App.instance().audit();
        inflate(context, R.layout.reader_links, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        captionView = findViewById(R.id.reader_links_caption);
        listView = findViewById(R.id.reader_links_list);

        TextView titleView = findViewById(R.id.reader_links_title);
        String title = titleView.getText().toString();
        if (!title.isEmpty()) {
            captionView.setText(title.substring(0, 1));
        }
    }

    public void update(@NonNull AtlasObject object) {
        if (!object.hasLinks()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        captionView.setBackgroundColor(ContextCompat.getColor(getContext(), RandomColor.getRandomColor()));
        Link[] links = object.getLinks();

        for (int i = listView.getChildCount(); i < links.length; ++i) {
            inflate(getContext(), R.layout.item_link, listView);
        }

        for (int i = 0; i < links.length; ++i) {
            View view = listView.getChildAt(i);
            view.setOnClickListener(this);
            view.setVisibility(View.VISIBLE);
            setObject(listView.getChildAt(i), links[i]);
        }

        for (int i = links.length; i < listView.getChildCount(); ++i) {
            listView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void setObject(View view, Link link) {
        view.setTag(link);

        ImageView iconView = view.findViewById(R.id.link_icon);
        BitmapLoader loader = new BitmapLoader(getContext(), link.getIconPath(), iconView, 1);
        loader.start();

        TextView displayView = view.findViewById(R.id.link_display);
        displayView.setText(link.getDisplay());

        TextView descriptionView = view.findViewById(R.id.link_description);
        descriptionView.setText(link.getDescription());
    }

    @Override
    public void onClick(View view) {
        final Link link = (Link) view.getTag();
        audit.openLink(link.getUrl());
        final Intent browser = new Intent();
        browser.setAction(Intent.ACTION_VIEW);
        browser.setData(Uri.parse(link.getUrl().toExternalForm()));
        getContext().startActivity(browser);
    }
}
