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
        featuredImageView = findViewById(R.id.reader_featured_view);

        conservationView = findViewById(R.id.reader_conservation_view);

        infoBoxView = findViewById(R.id.reader_infobox_view);
        infoBoxView.setContainer(findViewById(R.id.reader_infobox));

        contextualView = findViewById(R.id.reader_contextual_view);

        factsView = findViewById(R.id.reader_facts_view);
        factsView.setContainer(findViewById(R.id.reader_facts));

        featuresView = findViewById(R.id.reader_features_view);
        featuresView.setContainer(findViewById(R.id.reader_features));

        relatedView = findViewById(R.id.reader_related_view);
        linksView = findViewById(R.id.reader_links_view);
    }

    public void setAtlasObject(@NonNull AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
        // this tag is used by espresso tests
        setTag(atlasObject.getTag());

        introView.update(atlasObject);
        descriptionView.update(atlasObject);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                featuredImageView.update(ReaderObjectView.this.atlasObject);
                conservationView.update(ReaderObjectView.this.atlasObject);
                infoBoxView.update(ReaderObjectView.this.atlasObject);
                contextualView.update(ReaderObjectView.this.atlasObject);
                factsView.update(ReaderObjectView.this.atlasObject);
                featuresView.update(ReaderObjectView.this.atlasObject);
                relatedView.update(ReaderObjectView.this.atlasObject);
                linksView.update(ReaderObjectView.this.atlasObject);
            }
        }, 50);
    }
}
