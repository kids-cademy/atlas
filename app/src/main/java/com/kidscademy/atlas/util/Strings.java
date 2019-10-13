package com.kidscademy.atlas.util;

import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.model.Region;

import java.util.ArrayList;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;
import js.util.Paragraph;

public final class Strings extends js.util.Strings {
    private static final Log log = LogFactory.getLog(Strings.class);

    private Strings() {
        super();
    }

    public static List<String> getParagraphs(String text) {
        List<String> paragraphs = new ArrayList<>();
        // TODO: hack; replace paragraph parser with custom parser
        for (Paragraph paragraph : Paragraph.parseText(text)) {
            paragraphs.add(paragraph.getValue());
        }
        return paragraphs;
    }

    public static String getSpreadingDisplay(Region[] spreading) {
        if (spreading.length == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder(spreading[0].getDisplay(App.instance().getApplicationContext()));
        for (int i = 1; i < spreading.length; ++i) {
            builder.append('\n');
            builder.append(spreading[i].getDisplay(App.instance().getApplicationContext()));
        }
        return builder.toString();
    }

    public static String getAliasesDisplay(String[] aliases) {
        return Strings.join(aliases, "\n");
    }

    public static String capitalize(String text, Capitalize capitalize) {
        switch (capitalize) {
            case SENTENCES:
                break;

            case WORDS:
                break;

            case CHARACTERS:
                return text.toUpperCase();
        }
        return text;
    }

    /**
     * Capitalize operations for entire sentence, only words or all characters.
     *
     * @author Iulian Rotaru
     */
    public enum Capitalize {
        NONE, SENTENCES, WORDS, CHARACTERS
    }
}
