package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

public class ReaderObjectView extends LinearLayout {
    private ReaderIntroView introView;
    private DescriptionColumnView descriptionColumn1;
    private ReaderFeaturesView featuresView;
    private DescriptionColumnView descriptionColumn2;
    private ReaderFeaturedView featuredImageView;
    private DescriptionColumnView descriptionColumn3;
    private ReaderConservationView conservationView;
    private ReaderInfoBoxView infoBoxView;
    private ReaderContextualView contextualView;
    private ReaderFactsView factsView;
    private ReaderRelatedView relatedView;
    private ReaderLinksView linksView;

    public ReaderObjectView(Context context) {
        this(context, null);
        setOrientation(VERTICAL);
    }

    public ReaderObjectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_object_view, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        introView = findViewById(R.id.reader_intro_view);
        descriptionColumn1 = findViewById(R.id.reader_description_column1);
        featuresView = findViewById(R.id.reader_features);
        descriptionColumn2 = findViewById(R.id.reader_description_column2);
        featuredImageView = findViewById(R.id.reader_featured_view);
        descriptionColumn3 = findViewById(R.id.reader_description_column3);
        conservationView = findViewById(R.id.reader_conservation_view);
        infoBoxView = findViewById(R.id.reader_infobox_view);
        contextualView = findViewById(R.id.reader_contextual_view);
        factsView = findViewById(R.id.reader_facts_view);
        factsView.setContainer(findViewById(R.id.reader_facts));
        relatedView = findViewById(R.id.reader_related_view);
        linksView = findViewById(R.id.reader_links_view);
    }

    public void setAtlasObject(@NonNull final AtlasObject atlasObject) {
        // this tag is used by espresso tests
        setTag(atlasObject.getTag());

        introView.update(atlasObject);
        // takes care to reset description paragraph offset since reader object view is reused
        atlasObject.updateDescriptionParagraphOffset(0);
        descriptionColumn1.update(atlasObject);

        // features view has a description column that fill the space not occupied by features table
        // in order to measure description column need to have features table drawn
        // features view invokes provided runnable after its completion

        featuresView.update(atlasObject, new Runnable() {
            @Override
            public void run() {
                descriptionColumn2.update(atlasObject);
                featuredImageView.update(atlasObject);
                descriptionColumn3.update(atlasObject);
                conservationView.update(atlasObject);
                infoBoxView.update(atlasObject);
                contextualView.update(atlasObject);
                factsView.update(atlasObject);
                relatedView.update(atlasObject);
                linksView.update(atlasObject);
            }
        });
    }
}
