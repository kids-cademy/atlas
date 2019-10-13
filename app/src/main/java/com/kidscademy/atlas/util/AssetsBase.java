package com.kidscademy.atlas.util;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;

import js.core.Factory;
import js.json.Json;
import js.lang.GType;
import js.util.Files;
import js.util.Types;

public abstract class AssetsBase {
    protected final Context context;

    public AssetsBase(Context context) {
        this.context = context;
    }

    protected Reader getAssetReader(String assetPath) throws IOException {
        return new InputStreamReader(context.getAssets().open(assetPath));
    }

    @SuppressWarnings("unchecked")
    protected <T> T loadObject(Reader reader, Type... types) {
        Type type = types.length > 1 ? new GType(types[0], Arrays.copyOfRange(types, 1, types.length)) : types[0];
        try {
            Json json = Factory.getInstance(Json.class);
            return json.parse(reader, type);
        } catch (Throwable t) {
            return (T) Types.getEmptyValue(type);
        } finally {
            Files.close(reader);
        }
    }
}
