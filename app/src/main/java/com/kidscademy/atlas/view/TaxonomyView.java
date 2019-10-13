package com.kidscademy.atlas.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Taxon;

/**
 * Atlas object taxonomy view is a vertical linear layout that displays a fixed number of taxon item views. This custom view
 * has a single method used to initialize taxonomy, see {@link #update(AtlasObject)}.
 *
 * @author Iulian Rotaru
 */
public class TaxonomyView extends TableLayout {
    private View container;

    public TaxonomyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.compo_taxonomy, this);
    }

    public void setContainer(View container) {
        this.container = container;
    }

    /**
     * Set atlas object taxonomy, replacing existing taxon views, if any. A taxon has a name and a value.
     *
     * @param object atlas object.
     * @see FactItemView
     */
    public void update(@NonNull AtlasObject object) {
        Taxon[] taxonomy = object.getTaxonomy();

        int i = 0;
        for (; i < getChildCount(); ++i) {
            TableRow row = (TableRow) getChildAt(i);
            if (i < taxonomy.length) {
                row.setVisibility(View.VISIBLE);
                setTaxon(row, taxonomy[i]);
            } else {
                row.setVisibility(View.GONE);
            }
        }

        for (; i < taxonomy.length; ++i) {
            TableRow row = (TableRow) LayoutInflater.from(getContext()).inflate(R.layout.item_taxon, this, false);
            setTaxon(row, taxonomy[i]);
            addView(row);
        }
    }

    private void setTaxon(TableRow row, Taxon taxon) {
        TextView nameView = (TextView) row.getChildAt(0);
        nameView.setText(taxon.getName());

        TextView valueView = (TextView) row.getChildAt(2);
        valueView.setText(taxon.getValue());
    }

    public void setVisibility(int visibility) {
        if (container != null) {
            container.setVisibility(visibility);
        } else {
            super.setVisibility(visibility);
        }
    }
}
