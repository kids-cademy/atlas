package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Group;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Taxon;
import com.kidscademy.atlas.util.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import js.format.LongDateTime;

public class ReaderInfoBoxView extends ConstraintLayout implements ReaderSectionView {
    private static final DateFormat LAST_UPDATED_FORMAT = new SimpleDateFormat("MMMM d, yyyy");

    private TextView lastUpdatedText;

    private TextView classificationLabel;
    private TextView classificationText;

    private TextView progenitorLabel;
    private TextView progenitorText;

    private TaxonomyView taxonomyView;

    private Group aliasesGroup;
    private UnorderedListView aliasesListView;

    private Group spreadingGroup;
    private UnorderedListView spreadingListView;

    private TextView dateLabel;
    private TextView startDateText;
    private TextView endDateText;

    public ReaderInfoBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        lastUpdatedText = findViewById(R.id.infobox_last_updated);
        classificationLabel = findViewById(R.id.infobox_classification_label);
        classificationText = findViewById(R.id.infobox_classification);
        progenitorLabel = findViewById(R.id.infobox_progenitor_label);
        progenitorText = findViewById(R.id.infobox_progenitor);
        taxonomyView = findViewById(R.id.infobox_taxonomy);
        aliasesGroup = findViewById(R.id.infobox_aliases_group);
        aliasesListView = findViewById(R.id.infobox_aliases);
        spreadingGroup = findViewById(R.id.infobox_spreading_group);
        spreadingListView = findViewById(R.id.infobox_spreading);
        dateLabel = findViewById(R.id.infobox_date_label);
        startDateText = findViewById(R.id.infobox_start_date);
        endDateText = findViewById(R.id.infobox_end_date);
    }

    public void update(@NonNull AtlasObject atlasObject) {
        lastUpdatedText.setText(LAST_UPDATED_FORMAT.format(atlasObject.getLastUpdated()));

        if (atlasObject.hasTaxonomy()) {
            classificationLabel.setVisibility(View.VISIBLE);
            Taxon[] taxonomy = atlasObject.getTaxonomy();
            if (taxonomy.length == 1) {
                classificationLabel.setText(Strings.capitalize(taxonomy[0].getName()));
                classificationText.setText(atlasObject.getTaxonomy()[0].getValue());
                taxonomyView.setVisibility(View.GONE);
                classificationText.setVisibility(View.VISIBLE);
            } else {
                classificationLabel.setText(R.string.infobox_classification);
                taxonomyView.update(atlasObject);
                taxonomyView.setVisibility(View.VISIBLE);
                classificationText.setVisibility(View.GONE);
            }
        } else {
            classificationLabel.setVisibility(View.GONE);
            classificationText.setVisibility(View.GONE);
            taxonomyView.setVisibility(View.GONE);
        }

        if (atlasObject.hasProgenitor()) {
            this.progenitorLabel.setVisibility(View.VISIBLE);
            this.progenitorText.setVisibility(View.VISIBLE);
            this.progenitorText.setText(atlasObject.getProgenitor());
        } else {
            this.progenitorLabel.setVisibility(View.GONE);
            this.progenitorText.setVisibility(View.GONE);
        }

        setVisibility(aliasesGroup, atlasObject.hasAliases());
        if (atlasObject.hasAliases()) {
            aliasesListView.update(atlasObject.getAliases());
        }

        setVisibility(spreadingGroup, atlasObject.hasSpreading());
        if (atlasObject.hasSpreading()) {
            spreadingListView.update(Strings.getSpreadingDisplay(atlasObject.getSpreading()));
        }

        // display dates group if atlas object has at least start date
        if (atlasObject.hasStartDate()) {
            dateLabel.setVisibility(View.VISIBLE);
            startDateText.setVisibility(View.VISIBLE);

            if (atlasObject.hasEndDate()) {
                // if atlas object has end date we have a dates range and need update dates group label
                dateLabel.setText(R.string.infobox_period);
                endDateText.setText(atlasObject.getEndDate().toString());
                endDateText.setVisibility(View.VISIBLE);
            } else {
                // if end date is null takes care to restore default dates group label and hide end date view
                dateLabel.setText(R.string.infobox_since);
                endDateText.setVisibility(View.GONE);
            }

            startDateText.setText(atlasObject.getStartDate().toString());
        } else {
            dateLabel.setVisibility(View.GONE);
            startDateText.setVisibility(View.GONE);
            endDateText.setVisibility(View.GONE);
        }
    }

    private static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
