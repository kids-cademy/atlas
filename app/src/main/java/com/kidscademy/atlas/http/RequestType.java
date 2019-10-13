package com.kidscademy.atlas.http;

enum RequestType {
    /**
     * Neutral value.
     */
    NONE,

    /**
     * Request for a generic file.
     */
    FILE,

    /**
     * HTTP-RMI request.
     */
    RMI,

    /**
     * Server-Sent Events.
     */
    EVENTS;

    public static RequestType valueOf(Request request) {
        if (request.hasHeader("X-Requested-With")) {
            return RMI;
        }
        if ("/events".equals(request.getRequestURI())) {
            return EVENTS;
        }
        return FILE;
    }
}
