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

public class ReaderInfoBoxView extends ConstraintLayout {
    private TextView classificationLabel;
    private TextView classificationText;

    private TaxonomyView taxonomyView;

    private Group aliasesGroup;
    private TextView aliasesText;

    private Group spreadingGroup;
    private TextView spreadingText;

    private Group dateGroup;
    private TextView dateLabel;
    private TextView startDateText;
    private TextView endDateText;

    private View container;

    public ReaderInfoBoxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.reader_infobox, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        classificationLabel = findViewById(R.id.infobox_classification_label);
        classificationText = findViewById(R.id.infobox_classification);
        taxonomyView = findViewById(R.id.infobox_taxonomy);
        aliasesGroup = findViewById(R.id.infobox_aliases_group);
        aliasesText = findViewById(R.id.infobox_aliases);
        spreadingGroup = findViewById(R.id.infobox_spreading_group);
        spreadingText = findViewById(R.id.infobox_spreading);
        dateGroup = findViewById(R.id.infobox_date_group);
        dateLabel = findViewById(R.id.infobox_date_label);
        startDateText = findViewById(R.id.infobox_start_date);
        endDateText = findViewById(R.id.infobox_end_date);
    }

    public void update(@NonNull AtlasObject atlasObject) {
        if (!hasInfoBox(atlasObject)) {
            setVisibility(View.GONE);
            return;
        }
        setVisibility(View.VISIBLE);

        // in current reader layout info box comes after conservation status view
        // if conservation status view is displayed do not display info box vertical rule
        View rule = findViewById(R.id.infobox_vertical_rule);
        rule.setVisibility(atlasObject.hasConservation() ? View.GONE : View.VISIBLE);

        if (atlasObject.hasTaxonomy()) {
            Taxon[] taxonomy = atlasObject.getTaxonomy();
            if (taxonomy.length == 1) {
                classificationLabel.setText(taxonomy[0].getName());
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

        setVisibility(aliasesGroup, atlasObject.hasAliases());
        if (atlasObject.hasAliases()) {
            aliasesText.setText(Strings.getAliasesDisplay(atlasObject.getAliases()));
        }

        setVisibility(spreadingGroup, atlasObject.hasSpreading());
        if (atlasObject.hasSpreading()) {
            spreadingText.setText(Strings.getSpreadingDisplay(atlasObject.getSpreading()));
        }

        // display dates group if atlas object has at least start date
        setVisibility(dateGroup, atlasObject.hasStartDate());
        if (atlasObject.hasStartDate()) {
            if (atlasObject.hasEndDate()) {
                // if atlas object has end date we have a dates range and need update dates group label
                dateLabel.setText(R.string.infobox_period);
                endDateText.setText(atlasObject.getEndDate().toString());
                endDateText.setVisibility(View.VISIBLE);
            } else {
                // if end date is null takes care to restore default dates group label and remove end date view
                dateLabel.setText(R.string.infobox_since);
                // for unknown reasons is not enough to set GONE; need also to nullify text view content
                endDateText.setText(null);
                endDateText.setVisibility(View.GONE);
            }
            startDateText.setText(atlasObject.getStartDate().toString());
        }
    }

    public void setContainer(View container) {
        this.container = container;
    }

    public void setVisibility(int visibility) {
        if (container != null) {
            container.setVisibility(visibility);
        } else {
            super.setVisibility(visibility);
        }
    }

    private static boolean hasInfoBox(AtlasObject object) {
        return object.hasTaxonomy() || object.hasAliases() || object.hasStartDate() || object.hasSpreading();
    }

    private static void setVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}