package com.kidscademy.atlas.http;

import js.util.Files;

public enum ContentType {
    /**
     * Neutral value
     */
    NONE(""),

    /**
     *
     */
    TEXT_PLAIN("text/plain; charset=UTF-8"),

    /**
     *
     */
    TEXT_HTML("text/html; charset=UTF-8"),

    /**
     *
     */
    TEXT_CSS("text/css; charset=UTF-8"),

    /**
     *
     */
    TEXT_JS("text/javascript; charset=UTF-8"),

    /**
     *
     */
    TEXT_XML("text/xml; charset=UTF-8"),

    /**
     *
     */
    TEXT_CSV("text/csv; charset=UTF-8"),

    /**
     *
     */
    MULTIPART_FORM("multipart/form-data"),

    /**
     *
     */
    MULTIPART_MIXED("multipart/mixed"),

    /**
     *
     */
    URLENCODED_FORM("application/x-www-form-urlencoded; charset=UTF-8"),

    /**
     *
     */
    APPLICATION_JSON("application/json; charset=UTF-8"),

    /**
     *
     */
    APPLICATION_PDF("application/pdf"),

    /**
     *
     */
    APPLICATION_STREAM("application/octet-stream"),

    /**
     *
     */
    IMAGE_PNG("image/png"),

    /**
     *
     */
    IMAGE_JPEG("image/jpeg"),

    /**
     *
     */
    IMAGE_GIF("image/gif"),

    /**
     *
     */
    IMAGE_TIFF("image/tiff"),

    /**
     *
     */
    IMAGE_SVG("image/svg+xml"),

    TEXT_EVENT_STREAM("text/event-stream; charset=UTF-8");

    private String value;

    private ContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ContentType forExtension(String path) {
        String extension = Files.getExtension(path);
        if ("htm".equals(extension) || "html".equals(extension)) {
            return ContentType.TEXT_HTML;
        }
        if ("png".equals(extension)) {
            return ContentType.IMAGE_PNG;
        }
        if ("css".equals(extension)) {
            return ContentType.TEXT_CSS;
        }
        if ("js".equals(extension)) {
            return ContentType.TEXT_JS;
        }
        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return ContentType.IMAGE_PNG;
        }
        if ("gif".equals(extension)) {
            return ContentType.IMAGE_GIF;
        }
        return ContentType.NONE;
    }
}
