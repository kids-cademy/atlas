package com.kidscademy.atlas.http;

import java.io.IOException;

public interface Storage {
    Resource getResource(String requestURI) throws IOException;

    String getDefaultResource();
}
