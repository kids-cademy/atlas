package com.kidscademy.atlas.http;

import js.lang.BugError;
import js.lang.Event;
import js.log.Log;
import js.log.LogFactory;

/**
 * HTTP lite server.
 *
 * @author Iulian Rotaru
 */
public class HttpServer implements ServletFactory {
    private static final Log log = LogFactory.getLog(HttpServer.class);

    private final Connector connector;

    /**
     * Storage for file resources. This HTTP server just keep its reference but actual implementation is
     * created outside this server scope.
     */
    private final Storage storage;

    private final EventsManager eventsManager;

    public HttpServer(Storage storage, int port) {
        log.trace("HttpServer(Storage)");
        this.connector = new HttpConnector(this, port);
        this.storage = storage;
        this.eventsManager = new DefaultEventsManager();
    }

    public void start() {
        log.trace("start()");
        connector.start();
    }

    public void stop() {
        log.trace("stop()");
        connector.stop();
    }

    public void pushEvent(Event event) {
        eventsManager.pushEvent(event);
    }

    @Override
    public Servlet createServlet(RequestType requestType) {
        switch (requestType) {
            case FILE:
                return new FileServlet(storage);

            case RMI:
                return new RmiServlet();

            case EVENTS:
                return new EventsServlet(eventsManager);

            default:
                throw new BugError("Not supported request type |%s|.", requestType);
        }
    }
}
