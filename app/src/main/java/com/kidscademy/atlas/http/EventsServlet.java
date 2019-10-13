package com.kidscademy.atlas.http;

import com.kidscademy.atlas.sync.KeepAliveEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import js.json.Json;
import js.lang.Event;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;

public class EventsServlet implements Servlet {
    private static final Log log = LogFactory.getLog(EventsServlet.class);

    private static final int KEEP_ALIVE_PERIOD = 60 * 1000;

    private static int ID_SEED;

    private final EventsManager eventsManager;

    private final Json json;

    public EventsServlet(EventsManager eventsManager) {
        log.trace("EventsServlet()");
        this.eventsManager = eventsManager;
        this.json = Classes.loadService(Json.class);
    }

    @Override
    public void service(Request request, Response response) throws IOException {
        log.trace("service(Request,Response)");

        response.setStatus(ResponseStatus.OK);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Content-Type", ContentType.TEXT_EVENT_STREAM.value());

        OutputStream stream = response.getStream();

        Integer id = ++ID_SEED;
        log.debug("Open event stream |%d| from |%s|. ", id, request.getRemoteAddr());
        BlockingQueue<Event> queue = eventsManager.acquireQueue(id);

        try {
            for (; ; ) {
                Event event = null;
                try {
                    event = queue.poll(KEEP_ALIVE_PERIOD, TimeUnit.MILLISECONDS);
                } catch (InterruptedException unused) {
                }
                if (event == null) {
                    event = new KeepAliveEvent();
                }
                log.debug("Send event |%s| on stream |%d|.", event.getClass(), id);

                try {
                    // event: counterCRLF
                    stream.write(bytes("event:"));
                    stream.write(bytes(event.getClass().getSimpleName()));
                    stream.write(bytes("\r\n"));

                    // data: { json }CRLF
                    stream.write(bytes("data:"));
                    stream.write(bytes(json.stringify(event)));
                    stream.write(bytes("\r\n"));

                    // empty line for event end mark
                    stream.write(bytes("\r\n"));
                    stream.flush();
                } catch (SocketException e) {
                    log.debug("Client closes event stream. Break server-sent events loop.");
                    break;
                }
            }
        } finally {
            eventsManager.releaseQueue(id);
            log.debug("Close event stream |%d| with |%s|.", id, request.getRemoteAddr());
        }
    }

    private static byte[] bytes(String string) throws UnsupportedEncodingException {
        return string.getBytes("UTF-8");
    }
}
