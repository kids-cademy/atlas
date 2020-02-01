package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Link;
import com.kidscademy.atlas.util.BitmapLoader;
import com.kidscademy.atlas.util.Colors;
import com.kidscademy.atlas.util.Views;

public class ReaderLinksView extends ConstraintLayout implements Views.ListViewBuilder<Link>, View.OnClickListener {
    private HexaIcon captionView;
    private LinearLayout listView;

    public ReaderLinksView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
        Views.resetScrollParentView(this);
        captionView.setBackgroundColor(Colors.getColor(getContext()));
        Views.populateListView(listView, object.getLinks(), this);
    }

    @Override
    public void createChild(@NonNull LinearLayout listView) {
        inflate(getContext(), R.layout.item_link, listView);
    }

    @Override
    public void setObject(int index, @NonNull Link link) {
        View view = listView.getChildAt(index);
        view.setTag(link);
        view.setOnClickListener(this);

        ImageView iconView = view.findViewById(R.id.link_icon);
        BitmapLoader loader = new BitmapLoader(getContext(), link.getIconPath(), iconView);
        loader.start();

        TextView displayView = view.findViewById(R.id.link_display);
        displayView.setText(link.getDisplay());

        TextView descriptionView = view.findViewById(R.id.link_description);
        descriptionView.setText(link.getDescription());
    }

    @Override
    public void onClick(View view) {
        final Link link = (Link) view.getTag();
        final Intent browser = new Intent();
        browser.setAction(Intent.ACTION_VIEW);
        browser.setData(Uri.parse(link.getUrl().toExternalForm()));
        getContext().startActivity(browser);
    }
}
