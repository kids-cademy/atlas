package com.kidscademy.atlas.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.model.Region;

import java.util.ArrayList;
import java.util.List;

import js.lang.BugError;
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

    public static String[] getSpreadingDisplay(Region[] spreading) {
        if (spreading.length == 0) {
            return new String[0];
        }
        String[] strings = new String[spreading.length];
        for (int i = 0; i < spreading.length; ++i) {
            strings[i] = spreading[i].getDisplay(App.instance().getApplicationContext());
        }
        return strings;
    }

    public static String getString(@NonNull Context context, @NonNull String resNameFormat, Object... args) {
        String resName = String.format(resNameFormat, args);
        int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());
        if (resId == 0) {
            throw new BugError("Missing string resource |%s|.", resName);
        }
        return context.getString(resId);
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
