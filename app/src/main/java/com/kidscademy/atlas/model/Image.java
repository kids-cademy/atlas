package com.kidscademy.atlas.model;

public class Image {
    static final String KEY_ICON = "icon";
    static final String KEY_COVER = "cover";
    static final String KEY_TRIVIA = "trivia";
    static final String KEY_FEATURED = "featured";
    static final String KEY_CONTEXTUAL = "contextual";

    private String path;
    private String[] caption;

    public String getPath() {
        return path;
    }

    public String[] getCaption() {
        return caption;
    }
}
