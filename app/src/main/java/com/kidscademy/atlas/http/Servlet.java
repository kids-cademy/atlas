package com.kidscademy.atlas.http;

interface Servlet {
    void service(Request request, Response response) throws Exception;
}
