package com.kidscademy.atlas.http;

import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import js.lang.BugError;

/**
 * HTTP response. Sample response format.
 *
 * <pre>
 * HTTP/1.1 200 OKCRLF
 * Content-Type: application/json;charset=UTF-8CRLF
 * Content-Length: 618CRLF
 * Connection: close
 * CRLF
 * value
 * </pre>
 *
 * @author Iulian Rotaru
 */
public final class Response {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String LWS = " ";
    private static final String CRLF = "\r\n";

    private final BufferedOutputStream stream;
    private boolean streamUsed;

    private PrintWriter writer;
    private boolean writerUsed;

    private ResponseStatus status;
    private Map<String, String> headers = new HashMap<String, String>();
    private boolean commited;

    public Response(Socket socket) throws IOException {
        this.stream = new BufferedOutputStream(socket.getOutputStream());
    }

    public void setStatus(@NonNull ResponseStatus status) {
        this.status = status;
    }

    public void setContentType(ContentType contentType) {
        headers.put("Content-Type", contentType.value());
    }

    public void setContentLength(long length) {
        headers.put("Content-Length", Long.toString(length));
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public OutputStream getStream() throws IOException {
        if (writerUsed) {
            throw new BugError("Attempt to use bytes stream while reader is active.");
        }
        if (!commited) {
            commit();
        }
        streamUsed = true;
        return stream;
    }

    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        if (streamUsed) {
            throw new BugError("Attempt to use reader while bytes stream is active.");
        }
        if (!commited) {
            commit();
        }
        writerUsed = true;
        writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
        return writer;
    }

    public void flush() throws IOException {
        if (!commited) {
            commit();
        }
        if (writerUsed) {
            writer.flush();
        }
        stream.flush();
    }

    /**
     * For response status and headers serialization used bytes stream.
     *
     * @throws IOException
     */
    private void commit() throws IOException {
        commited = true;

        // write status line
        write(HTTP_VERSION);
        write(LWS);
        write(status.value());
        write(CRLF);

        // write headers
        for (String key : headers.keySet()) {
            write(key);
            write(": ");
            write(headers.get(key));
            write(CRLF);
        }

        // write empty line to mark headers end
        write(CRLF);

        stream.flush();
    }

    private void write(String string) throws IOException {
        stream.write(string.getBytes());
    }

    public boolean isCommitted() {
        return commited;
    }
}