package com.kidscademy.atlas.model;

import java.util.List;

public class Image {
    static final String KEY_ICON = "icon";
    static final String KEY_COVER = "cover";
    static final String KEY_TRIVIA = "trivia";
    static final String KEY_FEATURED = "featured";
    static final String KEY_CONTEXTUAL = "contextual";

    private String path;
    private HTML caption;

    public String getPath() {
        return path;
    }

    public String getCaption() {
        if (caption == null) {
            return null;
        }

        List<HTML.Element> elements = caption.getElements();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < elements.size(); ++i) {
            if (i > 0 && elements.get(i) instanceof HTML.Paragraph) {
                text.append("\n");
            }
            text.append(elements.get(i).getText());
            text.append("\n");
        }

        return text.toString();
    }
}
