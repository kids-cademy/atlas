package com.kidscademy.atlas.model;

import android.content.Context;

import js.lang.BugError;

@SuppressWarnings("unused")
public class Region {
    private String name;
    private Area area;

    public String getName() {
        return name;
    }

    public Area getArea() {
        return area;
    }

    public String getDisplay(Context context) {
        if (area == Area.ALL) {
            return name;
        }
        int resId = context.getResources().getIdentifier("region_" + area.name(), "string", context.getPackageName());
        if (resId == 0) {
            throw new BugError("Missing string resource for region area |%s|", area);
        }
        return String.format(context.getResources().getString(resId), name);
    }

    public enum Area {
        ALL, CENTRAL, NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
    }
}
