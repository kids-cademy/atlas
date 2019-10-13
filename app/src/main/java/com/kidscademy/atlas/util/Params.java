package com.kidscademy.atlas.util;

import java.util.List;

public final class Params extends js.util.Params {
    public static void validType(List<Class<?>> types, Class<?> type) {
        if (!types.contains(type)) {
            throw new IllegalArgumentException(type.getName() + " is not recognized by events tree context.");
        }
    }
}
