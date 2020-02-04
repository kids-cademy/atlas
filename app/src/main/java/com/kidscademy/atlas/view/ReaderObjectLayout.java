package com.kidscademy.atlas.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;

import js.log.Log;
import js.log.LogFactory;

/**
 * Reader object layout. Reader object view is a linear layout of reader sections. It is common for
 * all types of atlas objects and should be updated for every object instance, based on object
 * properties actually existing.
 *
 * @author Iulian Rotaru
 */
public class ReaderObjectLayout extends LinearLayout {
    private static final Log log = LogFactory.getLog(ReaderObjectLayout.class);

    private ReaderIntroView introView;
    private ReaderSectionView descriptionColumn1;
    private ReaderFeaturesView featuresView;
    private ReaderSectionView descriptionColumn2;
    private ReaderFeaturedView featuredImageView;
    private ReaderSectionView descriptionColumn3;
    private ReaderConservationView conservationView;
    private ReaderInfoBoxView infoBoxView;
    private ReaderContextualView contextualView;
    private ReaderFactsView factsView;
    private ReaderRelatedView relatedView;
    private ReaderLinksView linksView;

    /**
     * Runnable handlers for reader sections update. Reader object view is a linear layout of sections.
     * A sections group is one or more sections updated in a single step.
     */
    private Runnable[] sectionGroups = new Runnable[6];

    public ReaderObjectLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        log.trace("ReaderObjectView(Context context, @Nullable AttributeSet attrs)");
        inflate(context, R.layout.reader_object_view, this);
    }

    @Override
    public void onFinishInflate() {
        log.trace("onFinishInflate()");
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

    /**
     * Update reader object view. Reader view layout is common for all types of atlas objects and should
     * be updated for every object instance, based on object properties actually existing.
     * <p>
     * Reader view is a linear layout of section views. One or more section views are grouped together
     * and updated in a single step. Section groups are stored into {@link #sectionGroups} and updated
     * in sequence. It is critical to update next group after current group is measured - see {@link #updateSectionsGroup(int)}.
     *
     * @param atlasObject atlas object instance.
     */
    public void setAtlasObject(@NonNull final AtlasObject atlasObject) {
        log.trace("setAtlasObject(@NonNull final AtlasObject atlasObject)");

        // this tag is used by espresso tests
        setTag(atlasObject.getTag());
        // since reader object view is reused
        // takes care to reset description paragraph offset and description column index
        atlasObject.updateDescriptionParagraphOffset(0);

        sectionGroups[0] = new Runnable() {
            @Override
            public void run() {
                introView.update(atlasObject);
                infoBoxView.update(atlasObject);
                updateSection(descriptionColumn1, atlasObject, atlasObject.hasDescription());
                updateSection(conservationView, atlasObject, atlasObject.hasConservation());
            }
        };

        sectionGroups[1] = new Runnable() {
            @Override
            public void run() {
                updateSection(featuresView, atlasObject, atlasObject.hasFeatures());
            }
        };

        sectionGroups[2] = new Runnable() {
            @Override
            public void run() {
                updateSection(descriptionColumn2, atlasObject, atlasObject.hasDescription());
            }
        };

        sectionGroups[3] = new Runnable() {
            @Override
            public void run() {
                updateSection(descriptionColumn3, atlasObject, atlasObject.hasDescription());
            }
        };

        sectionGroups[4] = new Runnable() {
            @Override
            public void run() {
                if (!atlasObject.hasFeaturedImage()) {
                    featuredImageView.setVisibility(View.GONE);
                    return;
                }

                // find first visible section before contextual view and move featured image there
                // the point is to have a visible section between featured and contextual images to avoid having two images in sequence
                // if there is no visible section before contextual image do nothing, that is, leave featured image where it is

                removeView(featuredImageView);
                int index = indexOfChild(contextualView) - 1;
                for (; index >= 0; --index) {
                    if (getChildAt(index).getVisibility() == View.VISIBLE) {
                        break;
                    }
                }
                addView(featuredImageView, Math.max(index, 0));

                featuredImageView.update(atlasObject);
                featuredImageView.setVisibility(View.VISIBLE);
            }
        };

        sectionGroups[5] = new Runnable() {
            @Override
            public void run() {
                updateSection(contextualView, atlasObject, atlasObject.hasContextualImage());
                updateSection(factsView, atlasObject, atlasObject.hasFacts());
                updateSection(relatedView, atlasObject, atlasObject.hasRelated());
                updateSection(linksView, atlasObject, atlasObject.hasLinks());
            }
        };

        updateSectionsGroup(0);
    }

    /**
     * Recursively update sections group. One or more reader sections are grouped together and updated
     * in a single step.
     * <p>
     * After current group is updated the next one is recursively processed till index reach groups end.
     * This method uses view tree observer to detect when views are completely measured. It is critical
     * to update next group after current one is measured.
     * <p>
     * This method consumes section groups from {@link #sectionGroups}. It is started by {@link #setAtlasObject(AtlasObject)}
     * and invokes itself till index reaches groups length.
     *
     * @param groupIndex index of the current sections group to update.
     */
    private void updateSectionsGroup(final int groupIndex) {
        log.trace("updateSectionsGroup(final int groupIndex): %d", groupIndex);
        if (groupIndex == sectionGroups.length) {
            return;
        }
        if (groupIndex < sectionGroups.length - 1) {
            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            updateSectionsGroup(groupIndex + 1);
                        }
                    });
                    return true;
                }
            });
        }
        sectionGroups[groupIndex].run();
    }

    /**
     * Update reader section view.
     *
     * @param sectionView reader section view,
     * @param atlasObject atlas object instance,
     * @param visible     flag to display or hide reader section.
     */
    private static void updateSection(ReaderSectionView sectionView, AtlasObject atlasObject, boolean visible) {
        log.trace("updateSection(ReaderSectionView sectionView, AtlasObject atlasObject, boolean visible)");
        if (visible) {
            sectionView.update(atlasObject);
            sectionView.setVisibility(View.VISIBLE);
        } else {
            sectionView.setVisibility(View.GONE);
        }
    }
}
