package com.kidscademy.atlas.http;

import android.support.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import js.lang.BugError;

/**
 * HTTP request. Sample request format.
 *
 * <pre>
 * 	POST /net/dots/gpio/GpioService/acquireDigitalPin.rmi HTTP/1.1CRLF
 * 	Host: bbCRLF
 * 	Content-Type: application/json; charset=UTF-8CRLF
 * 	Content-Length: 3CRLF
 * 	CRLF
 * 	[1]
 * </pre>
 * <p>
 * Implementation note: request headers parser works on assumption that headers uses US-ASCII encoding. Tomcat
 * implementation uses the same assumption; see org.apache.coyote.http11.InternalInputBuffer#parseHeader
 *
 * @author Iulian Rotaru
 * @since 1.0
 */
final class Request {
    private static final String CRLF = System.getProperty("line.separator");
    private static final int CR = '\r';
    private static final int LF = '\n';
    private static final int EOF = -1;

    /**
     * Bytes stream containing entire HTTP request: start line, headers, empty line separator and body.
     */
    private final BufferedInputStream stream;

    private final String remoteAddr;
    /**
     * Flag true if bytes stream is used. Is not allowed to used both reader and stream to access request body.
     */
    private boolean streamUsed;

    private BufferedReader reader;
    /**
     * Flag true if body reader is used. Is not allowed to used both reader and stream to access request body.
     */
    private boolean readerUsed;

    private RequestType type;
    private String requestURI;
    private Map<String, String> headers = new HashMap<>();
    private boolean eof;

    Request(Socket socket) throws IOException {
        this.stream = new BufferedInputStream(socket.getInputStream());
        this.remoteAddr = socket.getRemoteSocketAddress().toString();
    }

    public void parse() throws IOException {
        // read start line and detect EOF or HTTP sync shutdown command
        String line = readLine();
        if (line == null) {
            eof = true;
            return;
        }

        int beginIndex = line.indexOf(' ') + 1;
        int endIndex = line.lastIndexOf(' ');
        requestURI = line.substring(beginIndex, endIndex);
        int separatorPosition = requestURI.lastIndexOf('?');
        if (separatorPosition != -1) {
            requestURI = requestURI.substring(0, separatorPosition);
        }

        // TODO: update headers parser to consider values continuing on next line (starts with white space)
        while ((line = readLine()) != null && !line.isEmpty()) {
            int separatorIndex = line.indexOf(':');
            headers.put(line.substring(0, separatorIndex).trim(), line.substring(separatorIndex + 1).trim());
        }

        type = RequestType.valueOf(this);
    }

    private String readLine() throws IOException {
        int c = stream.read();
        if (c == EOF) {
            return null;
        }
        StringBuilder line = new StringBuilder();
        boolean foundCR = false;

        LINE_LOOP:
        for (; ; ) {
            switch (c) {
                case EOF:
                    break LINE_LOOP;

                case CR:
                    foundCR = true;
                    break;

                case LF:
                    if (foundCR) {
                        break LINE_LOOP;
                    }
                    line.append(LF);
                    break;

                default:
                    if (foundCR) {
                        line.append(CR);
                        foundCR = false;
                    }
                    line.append((char) c);
            }
            c = stream.read();
        }

        return line.toString();
    }

    public RequestType getType() {
        return type;
    }

    @NonNull
    public String getRequestURI() {
        return requestURI;
    }

    boolean hasHeader(String key) {
        return headers.containsKey(key);
    }

    public InputStream getStream() {
        if (readerUsed) {
            throw new BugError("Attempt to use bytes stream while reader is active.");
        }
        streamUsed = true;
        return stream;
    }

    public BufferedReader getReader() throws UnsupportedEncodingException {
        if (reader != null) {
            return reader;
        }
        if (streamUsed) {
            throw new BugError("Attempt to use reader while bytes stream is active.");
        }
        readerUsed = true;
        reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        return reader;
    }

    boolean isEof() {
        return eof;
    }

    String getRemoteAddr() {
        return remoteAddr;
    }

    @NonNull
    String dump() {
        StringBuilder builder = new StringBuilder();

        addLine(builder, "Request-Type", type);
        addLine(builder, "Request-URI", requestURI);

        for (String key : headers.keySet()) {
            addLine(builder, key, headers.get(key));
        }
        return builder.toString();
    }

    private static void addLine(StringBuilder builder, String key, Object value) {
        builder.append(key);
        builder.append(": ");
        builder.append(value != null ? value.toString() : null);
        builder.append(CRLF);
    }
}