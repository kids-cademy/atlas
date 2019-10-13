package com.kidscademy.atlas.model;

public class Image {
    public static final String KEY_ICON = "icon";
    public static final String KEY_COVER = "cover";
    public static final String KEY_FEATURED = "featured";
    public static final String KEY_CONTEXTUAL = "contextual";

    private String path;
    private String caption;

    public String getPath() {
        return path;
    }

    public String getCaption() {
        return caption;
    }
}
