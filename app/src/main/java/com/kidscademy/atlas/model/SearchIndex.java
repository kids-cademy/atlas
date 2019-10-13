package com.kidscademy.atlas.model;

/**
 * A single record from objects atlas search indices. Basically it is a map of keywords to object IDs; given
 * a keyword this record stores all objects related to that keyword.
 * <p>
 * Instances of this class are loaded from <code>search-index.json</code>.
 *
 * @author Iulian Rotaru
 */
public class SearchIndex implements Keyword {
    /**
     * A magic string used to identify objects, used as search criterion.
     */
    private String keyword;
    /**
     * Keyword relevance is used to sort keyword in a context where more keywords are present.
     */
    private int keywordRelevance;
    /**
     * Objects related to keyword.
     */
    private int[] objectIds;

    public SearchIndex() {
    }

    /**
     * Test constructor.
     *
     * @param keyword          keyword is used as search criterion,
     * @param keywordRelevance keyword relevance,
     * @param objectIds        variable number of objects related to keyword.
     */
    public SearchIndex(String keyword, int keywordRelevance, int... objectIds) {
        this.keyword = keyword;
        this.keywordRelevance = keywordRelevance;
        this.objectIds = objectIds;
    }

    @Override
    public String getKeyword() {
        return keyword;
    }

    public int getKeywordRelevance() {
        return keywordRelevance;
    }

    public int[] getObjectIds() {
        return objectIds;
    }

    @Override
    public String toString() {
        return keyword;
    }
}
