package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

import js.util.BitmapLoader;

public class ReaderFeaturedView extends ConstraintLayout {
    private ImageView imageView;
    private TextView captionView;

    public ReaderFeaturedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_featured, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        imageView = findViewById(R.id.reader_featured_image);
        captionView = findViewById(R.id.reader_featured_caption);
    }

    public void update(@NonNull AtlasObject atlasObject) {
        BitmapLoader loader = new BitmapLoader(getContext(), atlasObject.getFeaturedPath(), imageView);
        loader.start();
        captionView.setText(atlasObject.getFeaturedCaption());
    }
}
