package com.kidscademy.atlas.sync;

import js.lang.Event;

public class PageScrollEvent implements Event {
    private final double scrollPercent;

    public PageScrollEvent(double scrollPercent) {
        this.scrollPercent = scrollPercent;
    }
}
