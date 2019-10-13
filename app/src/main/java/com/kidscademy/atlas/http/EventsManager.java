package com.kidscademy.atlas.http;

import java.util.concurrent.BlockingQueue;

import js.lang.Event;

public interface EventsManager {
    BlockingQueue<Event> acquireQueue(Integer eventsServletID);

    void releaseQueue(Integer eventsServletID);

    void pushEvent(Event event);
}
