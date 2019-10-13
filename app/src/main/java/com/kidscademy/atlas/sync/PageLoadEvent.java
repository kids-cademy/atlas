package com.kidscademy.atlas.sync;

import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.Link;
import com.kidscademy.atlas.model.Region;
import com.kidscademy.atlas.model.RelatedObject;
import com.kidscademy.atlas.model.Taxon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import js.lang.Event;

public class PageLoadEvent implements Event {
    private final AtlasObject atlasObject;

    public PageLoadEvent(AtlasObject atlasObject) {
        this.atlasObject = atlasObject;
    }
}
