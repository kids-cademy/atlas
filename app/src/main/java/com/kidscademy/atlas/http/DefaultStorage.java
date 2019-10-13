package com.kidscademy.atlas.http;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DefaultStorage implements Storage {
    @Override
    public Resource getResource(String requestURI) throws IOException {
        throw new FileNotFoundException(requestURI);
    }

    @Override
    public String getDefaultResource() {
        return null;
    }
}
