package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import js.lang.BugError;
import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

public class AtlasObject {
    private static final Log log = LogFactory.getLog(AtlasObject.class);

    private int index;
    private String name;
    private String display;
    private String definition;
    private String[] description;
    private int descriptionParagraphOffset;

    /**
     * Atlas object images are named to help identifying the context where they are used.
     * Supported names are:
     * <ul>
     * <li>{@link Image#KEY_ICON}</li>
     * <li>{@link Image#KEY_COVER}</li>
     * <li>{@link Image#KEY_TRIVIA}</li>
     * <li>{@link Image#KEY_FEATURED}</li>
     * <li>{@link Image#KEY_CONTEXTUAL}</li>
     * </ul>
     */
    private Map<String, Image> images;

    @NonNull
    private Date lastUpdated;
    private Taxon[] taxonomy;
    private String[] aliases;
    private Region[] spreading;
    private HDate startDate;
    private HDate endDate;
    private String progenitor;
    private ConservationStatus conservation;

    private String sampleTitle;
    private String samplePath;
    private String waveformPath;
    private String waveformSrc;

    private Fact[] facts;
    private Feature[] features;
    private RelatedObject[] related;
    private Link[] links;

    public int getIndex() {
        return index;
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

    public boolean hasDescription() {
        return description != null && descriptionParagraphOffset < description.length;
    }

    @NonNull
    public String[] getDescription() {
        if (description == null || descriptionParagraphOffset >= description.length) {
            throw new BugError("Attempt to retrieve not existing description for object |%s|.", name);
        }
        return description;
    }

    public int getDescriptionParagraphOffset() {
        return descriptionParagraphOffset;
    }

    public void updateDescriptionParagraphOffset(int descriptionParagraphOffset) {
        this.descriptionParagraphOffset = descriptionParagraphOffset;
    }

    // ---------------------------------------------------------------------------------------------

    public boolean hasIcon() {
        return images.containsKey(Image.KEY_ICON);
    }

    public String getIconPath() {
        return getImagePath(Image.KEY_ICON);
    }

    public boolean hasCoverImage() {
        return images.containsKey(Image.KEY_COVER);
    }

    public String getCoverPath() {
        return getImagePath(Image.KEY_COVER);
    }

    public boolean hasFeaturedImage() {
        return images.containsKey(Image.KEY_FEATURED);
    }

    public String getFeaturedPath() {
        return getImagePath(Image.KEY_FEATURED);
    }

    public String getFeaturedCaption() {
        return getImageCaption(Image.KEY_FEATURED);
    }

    public boolean hasTriviaImage() {
        return images.containsKey(Image.KEY_TRIVIA);
    }

    public String getTriviaPath() {
        return getImagePath(Image.KEY_TRIVIA);
    }

    public boolean hasTriviaCaption() {
        return getImageCaption(Image.KEY_TRIVIA) != null;
    }

    public String getTriviaCaption() {
        return getImageCaption(Image.KEY_TRIVIA);
    }

    public boolean hasContextualImage() {
        return images.containsKey(Image.KEY_CONTEXTUAL);
    }

    public String getContextualPath() {
        return getImagePath(Image.KEY_CONTEXTUAL);
    }

    public boolean hasContextualCaption() {
        return getImageCaption(Image.KEY_CONTEXTUAL) != null;
    }

    public String getContextualCaption() {
        return getImageCaption(Image.KEY_CONTEXTUAL);
    }

    private String getImagePath(String imageName) {
        Image image = images.get(imageName);
        return image != null ? image.getPath() : null;
    }

    private String getImageCaption(String imageName) {
        Image image = images.get(imageName);
        return image != null ? Strings.join(image.getCaption(), "\n\n") : null;
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

    @NonNull
    public String getWaveformPath() {
        return waveformPath;
    }

    // ---------------------------------------------------------------------------------------------

    public @NonNull
    Date getLastUpdated() {
        return lastUpdated;
    }

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

    public boolean hasProgenitor() {
        return progenitor != null;
    }

    public String getProgenitor() {
        return progenitor;
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
        return features.length > 0;
    }

    public Feature[] getFeatures() {
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
        return name;
    }

    public static AtlasObject getEmptyInstance() {
        // all next null values have predicate for null test
        AtlasObject object = new AtlasObject();
        object.index = 0;
        object.name = "empty";
        object.display = "Empty";
        object.definition = "";
        object.description = null;
        object.descriptionParagraphOffset = 0;
        object.images = Collections.emptyMap();
        object.lastUpdated = new Date();
        object.taxonomy = new Taxon[0];
        object.aliases = new String[0];
        object.spreading = new Region[0];
        object.startDate = null;
        object.endDate = null;
        object.progenitor = null;
        object.conservation = null;
        object.sampleTitle = null;
        object.samplePath = null;
        object.waveformPath = null;
        object.waveformSrc = null;
        object.facts = new Fact[0];
        object.features = new Feature[0];
        object.related = new RelatedObject[0];
        object.links = new Link[0];
        return object;
    }
}
