package com.kidscademy.atlas.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidscademy.atlas.app.App;
import com.kidscademy.atlas.model.Region;

import js.lang.BugError;

public final class Strings extends js.util.Strings {
    private Strings() {
        super();
    }

    public static String capitalize(String name) {
        if (name.length() < 1) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
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
}
