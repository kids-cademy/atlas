package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RelatedObject {
    private int index;
    private String display;
    private String definition;
    private String iconPath;

    public int getIndex() {
        return index;
    }

    @NonNull
    public String getDisplay() {
        return display;
    }

    @Nullable
    public String getDefinition() {
        return definition;
    }

    @NonNull
    public String getIconPath() {
        return iconPath;
    }
}
