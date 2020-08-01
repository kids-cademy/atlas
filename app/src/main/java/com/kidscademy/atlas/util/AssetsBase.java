package com.kidscademy.atlas.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;

import js.json.Json;
import js.lang.GType;
import js.log.Log;
import js.log.LogFactory;
import js.util.Classes;
import js.util.Files;

public abstract class AssetsBase {
    protected static final Log log = LogFactory.getLog(AssetsBase.class);

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
            Json json = Classes.loadService(Json.class);
            return json.parse(reader, type);
        } catch (Throwable t) {
            log.error(t);
            return (T) Types.getEmptyValue(type);
        } finally {
            Files.close(reader);
        }
    }
}
