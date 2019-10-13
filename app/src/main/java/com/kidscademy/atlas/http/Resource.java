package com.kidscademy.atlas.http;

import java.io.Closeable;
import java.io.InputStream;

public interface Resource extends Closeable {
    ContentType getContentType();

    long getContentLength();

    InputStream getInputStream();
}
