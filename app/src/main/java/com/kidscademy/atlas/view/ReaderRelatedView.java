package com.kidscademy.atlas.view;

import android.content.Context;
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
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.ReaderAction;
import com.kidscademy.atlas.model.RelatedObject;
import com.kidscademy.atlas.util.RandomColor;

import js.util.BitmapLoader;

public class ReaderRelatedView extends ConstraintLayout implements View.OnClickListener {
    private final ReaderAction.Listener listener;
    private HexaIcon captionView;
    protected ViewGroup listView;

    public ReaderRelatedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        listener = (ReaderAction.Listener) context;
        inflate(context, R.layout.reader_related, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        captionView = findViewById(R.id.reader_related_caption);
        listView = findViewById(R.id.reader_related_objects);

        TextView titleView = findViewById(R.id.reader_related_title);
        String title = titleView.getText().toString();
        if (!title.isEmpty()) {
            captionView.setText(title.substring(0, 1));
        }
    }

    public void update(@NonNull AtlasObject object) {
        if (!object.hasRelated()) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        captionView.setBackgroundColor(ContextCompat.getColor(getContext(), RandomColor.getRandomColor()));
        RelatedObject[] related = object.getRelated();

        for (int i = listView.getChildCount(); i < related.length; ++i) {
            inflate(getContext(), R.layout.item_related_object, listView);
        }

        for (int i = 0; i < related.length; ++i) {
            View view = listView.getChildAt(i);
            view.setOnClickListener(this);
            view.setVisibility(View.VISIBLE);
            setObject(listView.getChildAt(i), related[i]);
        }

        for (int i = related.length; i < listView.getChildCount(); ++i) {
            listView.getChildAt(i).setVisibility(View.GONE);
        }
    }

    private void setObject(View view, RelatedObject object) {
        view.setTag(object.getIndex());

        ImageView iconView = view.findViewById(R.id.related_object_icon);
        BitmapLoader loader = new BitmapLoader(getContext(), object.getIconPath(), iconView, 1);
        loader.start();

        TextView nameView = view.findViewById(R.id.related_object_name);
        nameView.setText(object.getDisplay());

        TextView definitionView = view.findViewById(R.id.related_object_definition);
        definitionView.setText(object.getDefinition());
    }

    @Override
    public void onClick(View view) {
        listener.onReaderAction(ReaderAction.LOAD_OBJECT, (Integer) view.getTag());
    }
}
