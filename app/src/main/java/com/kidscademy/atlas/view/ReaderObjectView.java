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
    private ReaderDescriptionView descriptionViewEx;
    private ReaderConservationView conservationView;
    private ReaderInfoBoxView infoBoxView;
    private ReaderContextualView contextualView;
    private ReaderFactsView factsView;
    private ReaderRelatedView relatedView;
    private ReaderLinksView linksView;

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
        descriptionColumn1 = findViewById(R.id.reader_description_column1);
        descriptionColumn2 = findViewById(R.id.reader_description_column2);
        descriptionColumn3 = findViewById(R.id.reader_description_column3);

        descriptionViewEx = findViewById(R.id.reader_description_view_ex);
        featuredImageView = findViewById(R.id.reader_featured_view);

        conservationView = findViewById(R.id.reader_conservation_view);

        infoBoxView = findViewById(R.id.reader_infobox_view);

        contextualView = findViewById(R.id.reader_contextual_view);

        factsView = findViewById(R.id.reader_facts_view);
        factsView.setContainer(findViewById(R.id.reader_facts));

        featuresView = findViewById(R.id.reader_features);

        relatedView = findViewById(R.id.reader_related_view);
        linksView = findViewById(R.id.reader_links_view);
    }

    public void setAtlasObject(@NonNull final AtlasObject atlasObject) {
        // this tag is used by espresso tests
        setTag(atlasObject.getTag());
        atlasObject.updateDescriptionParagraphOffset(0);

        introView.update(atlasObject);
        descriptionColumn1.update(atlasObject);

        featuresView.update(atlasObject, new Runnable() {
            @Override
            public void run() {
                featuredImageView.update(atlasObject);
                if (descriptionViewEx != null) {
                    descriptionViewEx.update(atlasObject);
                }
                descriptionColumn2.update(atlasObject);
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
