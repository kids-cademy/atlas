package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

import js.log.Log;
import js.log.LogFactory;
import js.util.BitmapLoader;

public class ReaderFeaturesView extends ConstraintLayout {
    private static final Log log = LogFactory.getLog(ReaderFactsView.class);

    private FeaturesTableView featuresTableView;
    private ImageView triviaImage;
    private TextView triviaText;
    private DescriptionTabletView descriptionView;
    private int descriptionMarginTop;

    public ReaderFeaturesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_features, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        log.trace("onFinishInflate()");
        featuresTableView = findViewById(R.id.features_table);
        triviaImage = findViewById(R.id.trivia_image);
        triviaText = findViewById(R.id.trivia_caption);

        descriptionView = findViewById(R.id.features_description);
        if (descriptionView != null) {
            ViewGroup.MarginLayoutParams params = (MarginLayoutParams) descriptionView.getLayoutParams();
            descriptionMarginTop = params.topMargin;
        }

        log.debug("on finish inflate: features view: table view height=%d", featuresTableView.getMeasuredHeight());
    }

    public void update(@NonNull final AtlasObject atlasObject, final Runnable drawCompleteListener) {
        featuresTableView.update(atlasObject, new Runnable() {
            @Override
            public void run() {
                if (!atlasObject.hasTriviaImage()) {
                    triviaImage.setVisibility(View.GONE);
                    triviaText.setVisibility(View.GONE);
                    if (descriptionView != null) {
                        descriptionView.setVisibility(View.VISIBLE);
                        descriptionView.setColumnWidth(getWidth());
                        descriptionView.setColumnHeight(getHeight() - featuresTableView.getHeight() - descriptionMarginTop);
                        descriptionView.setTextColor(R.color.text_accent);
                        descriptionView.update(atlasObject);
                    }
                } else {
                    if (descriptionView != null) {
                        descriptionView.setVisibility(View.GONE);
                    }

                    BitmapLoader loader = new BitmapLoader(getContext(), atlasObject.getTriviaPath(), triviaImage);
                    loader.start();
                    triviaImage.setVisibility(View.VISIBLE);

                    if (triviaText != null) {
                        if (atlasObject.hasTriviaCaption()) {
                            triviaText.setText(atlasObject.getTriviaCaption());
                            triviaText.setVisibility(View.VISIBLE);
                        } else {
                            triviaText.setVisibility(View.GONE);
                        }
                    }
                }
                drawCompleteListener.run();
            }
        });
    }
}
