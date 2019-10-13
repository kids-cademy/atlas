package com.kidscademy.atlas.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import js.log.Log;
import js.log.LogFactory;

public class FileServlet implements Servlet {
    private static final Log log = LogFactory.getLog(FileServlet.class);

    /**
     * The size of buffer used by copy operations.
     */
    private static final int BUFFER_SIZE = 4 * 1024;

    private final Storage storage;

    public FileServlet(Storage storage) {
        log.trace("FileServlet(Storage)");
        this.storage = storage;
    }

    @Override
    public void service(Request request, Response response) throws IOException {
        log.trace("service(Request,Response)");
        String requestURI = request.getRequestURI();
        if ("/".equals(requestURI)) {
            redirect(response, storage.getDefaultResource());
            return;
        }
        Resource resource = storage.getResource(requestURI);

        response.setStatus(ResponseStatus.OK);
        response.setContentType(resource.getContentType());
        response.setContentLength(resource.getContentLength());

        InputStream inputStream = new BufferedInputStream(resource.getInputStream());
        OutputStream outputStream = response.getStream();

        byte[] buffer = new byte[BUFFER_SIZE];
        int length;
        try {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            inputStream.close();
            // do not close output stream; request dispatcher takes care
        }

        response.flush();
    }

    private void redirect(Response response, String location) throws IOException {
        response.setStatus(ResponseStatus.MOVED_PERMANENTLY);
        response.setHeader("Location", location);
        response.flush();
    }
}
