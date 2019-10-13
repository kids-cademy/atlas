package com.kidscademy.atlas.sync;

import js.lang.Event;

public class ItemRevealEvent implements Event {
    private final Type type;
    private final int index;

    public ItemRevealEvent(Type type, int index) {
        this.type = type;
        this.index = index;
    }

    public enum Type {
        DESCRIPTION, FACT, FEATURE, RELATED, LINK
    }
}
