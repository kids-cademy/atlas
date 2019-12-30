package com.kidscademy.atlas.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

public class ReaderObjectView extends LinearLayout {
    private ReaderIntroView introView;
    private ReaderDescriptionView descriptionView;
    private ReaderFeaturedView featuredImageView;
    private ReaderDescriptionView descriptionViewEx;
    private ReaderConservationView conservationView;
    private ReaderInfoBoxView infoBoxView;
    private ReaderContextualView contextualView;
    private ReaderFactsView factsView;
    private ReaderFeaturesView featuresView;
    private ReaderRelatedView relatedView;
    private ReaderLinksView linksView;

    private AtlasObject atlasObject;

    public ReaderObjectView(Context context) {
        super(context);
        setOrientation(VERTICAL);
        inflate(context, R.layout.reader_object_view, this);
        onFinishInflate();
    }

    public ReaderObjectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_object_view, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        introView = findViewById(R.id.reader_intro_view);
        descriptionView = findViewById(R.id.reader_description_view);
        descriptionViewEx = findViewById(R.id.reader_description_view_ex);
        featuredImageView = findViewById(R.id.reader_featured_view);

        conservationView = findViewById(R.id.reader_conservation_view);

        infoBoxView = findViewById(R.id.reader_infobox_view);

        contextualView = findViewById(R.id.reader_contextual_view);

        factsView = findViewById(R.id.reader_facts_view);
        factsView.setContainer(findViewById(R.id.reader_facts));

        featuresView = findViewById(R.id.reader_features_view);
        featuresView.setContainer(findViewById(R.id.reader_features));

        relatedView = findViewById(R.id.reader_related_view);
        linksView = findViewById(R.id.reader_links_view);
    }

    public void setAtlasObject(@NonNull final AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
        // this tag is used by espresso tests
        setTag(atlasObject.getTag());

        introView.update(atlasObject);
        descriptionView.update(atlasObject);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                featuredImageView.update(atlasObject);
                if (descriptionViewEx != null) {
                    descriptionViewEx.update(atlasObject);
                }
                conservationView.update(atlasObject);
                infoBoxView.update(atlasObject);
                contextualView.update(atlasObject);
                factsView.update(atlasObject);
                featuresView.update(atlasObject);
                relatedView.update(atlasObject);
                linksView.update(atlasObject);
            }
        }, 50);
    }
}
