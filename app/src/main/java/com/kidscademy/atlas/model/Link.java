package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import java.net.URL;

public class Link {
    private URL url;
    private String display;
    private String description;
    private String iconPath;

    @NonNull
    public URL getUrl() {
        return url;
    }

    @NonNull
    public String getDisplay() {
        return display;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getIconPath() {
        return iconPath;
    }
}
