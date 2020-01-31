package com.kidscademy.atlas.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
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

    private int descriptionColumnsCount;

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
        infoBoxView = findViewById(R.id.reader_infobox_view);

        descriptionColumn1 = findViewById(R.id.reader_description_column1);
        featuresView = findViewById(R.id.reader_features);
        descriptionColumn2 = findViewById(R.id.reader_description_column2);
        featuredImageView = findViewById(R.id.reader_featured_view);
        descriptionColumn3 = findViewById(R.id.reader_description_column3);
        conservationView = findViewById(R.id.reader_conservation_view);
        contextualView = findViewById(R.id.reader_contextual_view);
        factsView = findViewById(R.id.reader_facts_view);
        factsView.setContainer(findViewById(R.id.reader_facts));
        relatedView = findViewById(R.id.reader_related_view);
        linksView = findViewById(R.id.reader_links_view);
    }

    public void setAtlasObject(@NonNull final AtlasObject atlasObject) {
        // this tag is used by espresso tests
        setTag(atlasObject.getTag());
        // since reader object view is reused
        // takes care to reset description paragraph offset and description column index
        atlasObject.updateDescriptionParagraphOffset(0);
        descriptionColumnsCount = 0;

        introView.update(atlasObject);
        infoBoxView.update(atlasObject);

        if (atlasObject.hasDescription()) {
            descriptionColumn1.update(atlasObject);
            descriptionColumn1.setVisibility(View.VISIBLE);
            descriptionColumnsCount++;
        } else {
            descriptionColumn1.setVisibility(View.GONE);
        }

        if (atlasObject.hasConservation()) {
            conservationView.update(atlasObject);
            conservationView.setVisibility(View.VISIBLE);
        } else {
            conservationView.setVisibility(View.GONE);
        }

        if (!atlasObject.hasFeatures()) {
            featuresView.setVisibility(View.GONE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setAtlasObjectEx(atlasObject);
                }
            }, 10);
            return;
        }

        // features view has a description column that fill the space not occupied by features table
        // in order to measure description column need to have features table drawn
        // features view invokes provided runnable after its completion
        featuresView.setVisibility(View.VISIBLE);
        featuresView.update(atlasObject, new Runnable() {
            @Override
            public void run() {
                setAtlasObjectEx(atlasObject);
            }
        });
    }

    private void setAtlasObjectEx(@NonNull AtlasObject atlasObject) {
        if (atlasObject.hasDescription()) {
            descriptionColumn2.update(atlasObject);
            descriptionColumn2.setVisibility(View.VISIBLE);
            descriptionColumnsCount++;
        } else {
            descriptionColumn2.setVisibility(View.GONE);
        }

        if (atlasObject.hasDescription()) {
            descriptionColumn3.update(atlasObject);
            descriptionColumn3.setVisibility(View.VISIBLE);
            descriptionColumnsCount++;
        } else {
            descriptionColumn3.setVisibility(View.GONE);
        }

        if (atlasObject.hasFeaturedImage()) {
            removeView(featuredImageView);
            switch (descriptionColumnsCount) {
                case 0:
                    addView(featuredImageView, indexOfChild(infoBoxView));
                    break;

                case 1:
                    addView(featuredImageView, indexOfChild((View) descriptionColumn1));
                    break;

                default:
                    addView(featuredImageView, indexOfChild((View) descriptionColumn2));
                    break;
            }

            featuredImageView.update(atlasObject);
            featuredImageView.setVisibility(View.VISIBLE);

        } else {
            featuredImageView.setVisibility(View.GONE);
        }

        if (atlasObject.hasContextualImage()) {
            contextualView.update(atlasObject);
            contextualView.setVisibility(View.VISIBLE);
        } else {
            contextualView.setVisibility(View.GONE);
        }

        if (atlasObject.hasFacts()) {
            factsView.update(atlasObject);
            factsView.setVisibility(View.VISIBLE);
        } else {
            factsView.setVisibility(View.GONE);
        }

        if (atlasObject.hasRelated()) {
            relatedView.update(atlasObject);
            relatedView.setVisibility(View.VISIBLE);
        } else {
            relatedView.setVisibility(View.GONE);
        }

        if (atlasObject.hasLinks()) {
            linksView.update(atlasObject);
            linksView.setVisibility(View.VISIBLE);
        } else {
            linksView.setVisibility(View.GONE);
        }
    }
}
