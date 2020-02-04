package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.util.BitmapLoader;

public class ReaderIntroView extends ConstraintLayout implements ReaderSectionView {
    private TextView titleView;
    private HexaIcon captionView;
    private TextView definitionView;
    private ImageView imageView;

    public ReaderIntroView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_intro, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        titleView = findViewById(R.id.reader_intro_title);
        captionView = findViewById(R.id.reader_intro_caption);
        definitionView = findViewById(R.id.reader_intro_definition);
        imageView = findViewById(R.id.reader_intro_image);
    }

    public void update(AtlasObject atlasObject) {
        if (titleView != null) {
            // title and caption views are not null on mobile phone
            titleView.setText(atlasObject.getDisplay());
            captionView.setText(atlasObject.getDisplay().substring(0, 1));
        }

        if (atlasObject.hasCoverImage()) {
            BitmapLoader loader = new BitmapLoader(getContext(), atlasObject.getCoverPath(), imageView, 1);
            loader.start();
            // this image view tag is used by espresso tests
            imageView.setTag(atlasObject.getCoverPath());
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        definitionView.setText(atlasObject.getDefinition());
    }
}
