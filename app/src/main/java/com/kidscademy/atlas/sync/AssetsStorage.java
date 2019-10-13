package com.kidscademy.atlas.sync;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import com.kidscademy.atlas.http.ContentType;
import com.kidscademy.atlas.http.Resource;
import com.kidscademy.atlas.http.Storage;
import com.kidscademy.atlas.util.AssetsBase;

import java.io.IOException;
import java.io.InputStream;

import js.log.Log;
import js.log.LogFactory;

final class AssetsStorage extends AssetsBase implements Storage {
    private static final Log log = LogFactory.getLog(AssetsStorage.class);

    private static final String DEFAULT_FILE = "sync/index.htm";

    AssetsStorage(Context context) {
        super(context);
        log.trace("AssetsStorage(Context)");
    }

    @Override
    public Resource getResource(String requestURI) throws IOException {
        String filePath = requestURI.substring(1);
        AssetManager assetManager = context.getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd(filePath);

        ContentType contentType = ContentType.forExtension(filePath);
        long contentLength = fileDescriptor.getLength();
        InputStream inputStream = assetManager.open(filePath);

        return new Asset(contentType, contentLength, inputStream);
    }

    private static class Asset implements Resource {
        private final ContentType contentType;
        private final long contentLength;
        private final InputStream inputStream;

        Asset(ContentType contentType, long contentLength, InputStream inputStream) {
            this.contentType = contentType;
            this.contentLength = contentLength;
            this.inputStream = inputStream;
        }

        @Override
        public ContentType getContentType() {
            return contentType;
        }

        @Override
        public long getContentLength() {
            return contentLength;
        }

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
        }
    }

    @Override
    public String getDefaultResource() {
        return DEFAULT_FILE;
    }
}
