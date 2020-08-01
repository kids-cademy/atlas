package com.kidscademy.atlas.util;

import com.kidscademy.atlas.model.AtlasObject;

import java.lang.reflect.Type;

public class Types extends js.util.Types {
    public static Object getEmptyValue(Type t) {
        if (AtlasObject.class.equals(t)) {
            return AtlasObject.getEmptyInstance();
        }
        return js.util.Types.getEmptyValue(t);
    }
}
