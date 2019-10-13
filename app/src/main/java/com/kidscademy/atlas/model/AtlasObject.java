package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import com.kidscademy.atlas.app.App;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import js.util.Strings;

public class AtlasObject {
    private int index;
    private int rank;
    private String name;
    private String display;
    private String definition;
    private HTML description;

    /**
     * keys: icon, cover, featured, contextual
     */
    private Map<String, Image> images;

    private Taxon[] taxonomy;
    private String[] aliases;
    private Region[] spreading;
    private HDate startDate;
    private HDate endDate;
    private ConservationStatus conservation;

    private String sampleTitle;
    private String samplePath;

    private Fact[] facts;
    private Map<String, String> features;
    private RelatedObject[] related;
    private Link[] links;

    public int getIndex() {
        return index;
    }

    public int getRank() {
        return rank;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getDisplay() {
        return display;
    }

    @NonNull
    public String getDefinition() {
        return definition;
    }

    @NonNull
    public HTML getDescription() {
        return description;
    }

    // ---------------------------------------------------------------------------------------------

    public String getIconPath() {
        return getImagePath(Image.KEY_ICON);
    }

    public String getCoverPath() {
        return getImagePath(Image.KEY_COVER);
    }

    public boolean hasFeaturedImage() {
        return images.get(Image.KEY_FEATURED) != null;
    }

    public String getFeaturedPath() {
        return getImagePath(Image.KEY_FEATURED);
    }

    public String getFeaturedCaption() {
        return getImageCaption(Image.KEY_FEATURED);
    }

    public boolean hasContextualImage() {
        return images.get(Image.KEY_CONTEXTUAL) != null;
    }

    public String getContextualPath() {
        return getImagePath(Image.KEY_CONTEXTUAL);
    }

    public boolean hasContextualText() {
        return getImageCaption(Image.KEY_CONTEXTUAL) != null;
    }

    public String getContextualText() {
        return getImageCaption(Image.KEY_CONTEXTUAL);
    }

    private String getImagePath(String imageName) {
        Image image = images.get(imageName);
        return image != null ? image.getPath() : null;
    }

    private String getImageCaption(String imageName) {
        Image image = images.get(imageName);
        return image != null ? image.getCaption() : null;
    }

    public boolean hasAudioSample() {
        return samplePath != null;
    }

    @NonNull
    public String getAudioSamplePath() {
        return samplePath;
    }

    @NonNull
    public String getAudioSampleTitle() {
        return sampleTitle;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean hasTaxonomy() {
        return taxonomy != null && taxonomy.length > 0;
    }

    public Taxon[] getTaxonomy() {
        return taxonomy;
    }

    public boolean hasAliases() {
        return aliases.length > 0;
    }

    public String[] getAliases() {
        return aliases;
    }

    public boolean hasSpreading() {
        return spreading.length > 0;
    }

    public Region[] getSpreading() {
        return spreading;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }

    public HDate getStartDate() {
        return startDate;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public HDate getEndDate() {
        return endDate;
    }

    public boolean hasConservation() {
        return conservation != null;
    }

    public ConservationStatus getConservation() {
        return conservation;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean hasFacts() {
        return facts.length > 0;
    }

    public Fact[] getFacts() {
        return facts;
    }

    public boolean hasFeatures() {
        return features != null;
    }

    public Map<String, String> getFeatures() {
        return features;
    }

    public boolean hasRelated() {
        return related.length > 0;
    }

    public RelatedObject[] getRelated() {
        return related;
    }

    public boolean hasLinks() {
        return links.length > 0;
    }

    public Link[] getLinks() {
        return links;
    }

    public String getTag() {
        return Strings.toString(index, name);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AtlasObject that = (AtlasObject) o;
        if (this.name == null) {
            return that.name == null;
        }
        return name.equals(that.name);
    }

    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @NonNull
    public String toString() {
        // toString is used by Audit to print object; leave it as it is
        return name;
    }
}