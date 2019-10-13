package com.kidscademy.atlas.http;

public interface ServletFactory {
    Servlet createServlet(RequestType requestType);
}
