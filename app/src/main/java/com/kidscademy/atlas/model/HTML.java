package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.converter.Converter;
import js.converter.ConverterException;
import js.lang.BugError;
import js.util.Strings;

public class HTML implements Converter {
    private List<Element> elements;

    /**
     * Default constructor used by JSON deserializer.
     */
    @SuppressWarnings("unused")
    public HTML() {
    }

    public HTML(String html) {
        this.elements = parse(html);
    }

    public List<Element> getElements() {
        return elements;
    }

    public List<String> getText() {
        List<String> text = new ArrayList<>();
        for (Element element : elements) {
            if (element instanceof Paragraph) {
                text.add(((Paragraph) element).text);
            }
        }
        return text;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T asObject(String value, Class<T> type) throws IllegalArgumentException, ConverterException {
        return (T) new HTML(value);
    }

    @Override
    public String asString(Object object) throws ConverterException {
        HTML html = (HTML) object;
        StringBuilder builder = new StringBuilder();
        for (Element element : html.elements) {
            builder.append(element);
        }
        return builder.toString();
    }

    public interface Element {

    }

    public static class Paragraph implements Element {
        private final String text;

        public Paragraph(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        @Override
        @NonNull
        public String toString() {
            // <p>text</p>
            return Strings.concat("<p>", text, "</p>");
        }
    }

    public static class Image implements Element {
        private final String path;

        Image(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }

        @Override
        @NonNull
        public String toString() {
            // <img src='path' />
            return Strings.concat("<img src='", path, "' />");
        }
    }

    /**
     * Parse HTML content.
     * <p>
     * Known limitation:
     * - does not support nested elements,
     * - supports only paragraphs and images,
     * - assume tag name, attribute value, attribute assignment and text content does not start or end with white spaces,
     * - does not process escaping sequence.
     *
     * @param html HTML formatted text.
     * @return list of elements.
     */
    private static List<Element> parse(String html) {
        List<Element> elements = new ArrayList<>();
        StringBuilder tagBuilder = new StringBuilder();
        AttributesBuilder attributesBuilder = new AttributesBuilder();
        StringBuilder textBuilder = new StringBuilder();

        State state = State.SYN;
        for (int i = 0; i < html.length(); ++i) {
            char c = html.charAt(i);
            switch (state) {
                case SYN:
                    if (c == '<') {
                        state = State.TAG;
                    }
                    break;

                case TAG:
                    if (c == '/') {
                        state = State.END_TAG;
                        break;
                    } else {
                        state = State.START_TAG;
                        tagBuilder.setLength(0);
                        attributesBuilder.setLength(0);
                        textBuilder.setLength(0);
                    }
                    // fall through START_TAG case

                case START_TAG:
                    if (c == ' ') {
                        state = State.ATTR;
                        break;
                    } else if (c == '>') {
                        state = State.TEXT;
                        break;
                    } else {
                        tagBuilder.append(c);
                    }
                    break;

                case END_TAG:
                    if (c == '>') {
                        elements.add(getElementForTag(tagBuilder.toString(), attributesBuilder.getAttributes(), textBuilder.toString()));
                        state = State.SYN;
                    }
                    break;

                case ATTR:
                    if (c == '>') {
                        state = State.TEXT;
                        break;
                    }
                    if (!attributesBuilder.append(c)) {
                        state = State.END_TAG;
                    }
                    break;

                case TEXT:
                    if (c == '<') {
                        state = State.TAG;
                    } else {
                        textBuilder.append(c);
                    }
                    break;
            }
        }

        return elements;
    }

    private enum State {
        SYN,
        TAG,
        START_TAG,
        END_TAG,
        ATTR,
        TEXT
    }

    private static class AttributesBuilder {
        private final Map<String, String> attributes = new HashMap<>();
        private final StringBuilder nameBuilder = new StringBuilder();
        private final StringBuilder valueBuilder = new StringBuilder();

        private State state = State.SYN;
        private char quoteChar;

        public void setLength(@SuppressWarnings("unused") int length) {
            // this method signature is chosen for consistency with StringBuilder but it ignores length parameter
            attributes.clear();
        }

        boolean append(char c) {
            switch (state) {
                case SYN:
                    if (c == '/') {
                        return false;
                    }
                    if (c == ' ') {
                        break;
                    }
                    state = State.NAME;
                    nameBuilder.setLength(0);
                    valueBuilder.setLength(0);
                    // fall through NAME case

                case NAME:
                    if (c == '=') {
                        state = State.QUOTE;
                    } else {
                        nameBuilder.append(c);
                    }
                    break;

                case QUOTE:
                    if (c == '\'' || c == '"') {
                        state = State.VALUE;
                        quoteChar = c;
                    }
                    break;

                case VALUE:
                    if (c == quoteChar) {
                        state = State.SYN;
                        attributes.put(nameBuilder.toString(), valueBuilder.toString());
                    } else {
                        valueBuilder.append(c);
                    }
                    break;
            }
            return true;
        }

        Map<String, String> getAttributes() {
            return attributes;
        }

        private enum State {
            SYN, NAME, QUOTE, VALUE
        }
    }

    private static Element getElementForTag(String tag, Map<String, String> attributes, String text) {
        switch (tag) {
            case "p":
                return new Paragraph(text);
            case "img":
                return new Image(attributes.get("src"));
            default:
                throw new BugError("Not handled tag |%s|.", tag);
        }
    }
}
