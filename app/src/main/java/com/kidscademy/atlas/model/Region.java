package com.kidscademy.atlas.model;

import android.content.Context;

import com.kidscademy.atlas.util.Strings;

import js.lang.BugError;

@SuppressWarnings("unused")
public class Region {
    private String name;
    private Area area;
    private String less;
    private Area lessArea;

    public String getName() {
        return name;
    }

    public Area getArea() {
        return area;
    }

    public String getLess() {
        return less;
    }

    public Area getLessArea() {
        return lessArea;
    }

    public String getDisplay(Context context) {
        if (less == null) {
            return String.format(string(context, area), name);
        }
        return String.format(Strings.concat(string(context, area), ' ', string(context, "region_less"), ' ', string(context, lessArea)), name, less);
    }

    private static String string(Context context, Area area) {
        int resId = context.getResources().getIdentifier("region_" + area.name(), "string", context.getPackageName());
        if (resId == 0) {
            throw new BugError("Missing string resource for region area |%s|", area);
        }
        return context.getResources().getString(resId);
    }

    private static String string(Context context, String resName) {
        int resId = context.getResources().getIdentifier(resName, "string", context.getPackageName());
        if (resId == 0) {
            throw new BugError("Missing string resource |%s|", resName);
        }
        return context.getResources().getString(resId);
    }

    public enum Area {
        ALL, CENTRAL, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
    }
}
