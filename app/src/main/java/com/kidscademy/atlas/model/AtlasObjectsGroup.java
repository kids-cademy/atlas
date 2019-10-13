package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import java.util.List;

public class AtlasObjectsGroup implements Comparable<AtlasObjectsGroup> {
    private final String caption;
    private final List<AtlasObject> objects;
    private boolean collapsed;

    public AtlasObjectsGroup(String caption, List<AtlasObject> objects) {
        this.caption = caption;
        this.objects = objects;
    }

    public String getCaption() {
        return caption;
    }

    public List<AtlasObject> getObjects() {
        return objects;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    @Override
    public int compareTo(@NonNull AtlasObjectsGroup that) {
        return this.caption.compareTo(that.caption);
    }
}
