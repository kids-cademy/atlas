package com.kidscademy.atlas.app;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.SearchIndex;
import com.kidscademy.atlas.util.AssetsBase;
import com.kidscademy.atlas.util.AsyncTask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

import js.lang.GType;
import js.log.Log;
import js.log.LogFactory;

/**
 * Atlas repository stored on application assets.
 *
 * @author Iulian Rotaru
 */
class AssetsRepository extends AssetsBase implements AtlasRepository {
    /**
     * Class logger.
     */
    private static final Log log = LogFactory.getLog(AssetsRepository.class);

    private final Resources resources;
    private final String[] names;
    private final AtlasObject[] objects;

    private final Object searchIndexLock = new Object();
    private List<SearchIndex> searchIndex;

    AssetsRepository(Context context) throws IOException {
        super(context);
        log.trace("AssetsRepository(Context context)");
        resources=context.getResources();

        names = loadObject(getAssetReader("atlas/objects-list.json"), String[].class);

        AsyncTask<Void> searchIndexLoader = new AsyncTask<Void>() {
            @Override
            protected Void execute() throws Throwable {
                searchIndex = loadRawObject("search-index", new GType(List.class, SearchIndex.class));
                synchronized (searchIndexLock) {
                    searchIndexLock.notify();
                }
                return null;
            }
        };
        searchIndexLoader.start();

        objects = new AtlasObject[names.length];
        AsyncTask<Void> objectsLoader = new AsyncTask<Void>() {
            @Override
            protected Void execute() throws Throwable {
                for (int index = 0; index < objects.length; ++index) {
                    if (objects[index] == null) {
                        synchronized (this) {
                            if (objects[index] == null) {
                                objects[index] = loadRawObject(names[index], AtlasObject.class);
                            }
                        }
                    }
                }
                return null;
            }
        };
        objectsLoader.start();
    }

    @Override
    public int getObjectsCount() {
        return names.length;
    }

    @NonNull
    @Override
    public AtlasObject getObjectByIndex(int index) {
        AtlasObject object = objects[index];
        if (object == null) {
            synchronized (this) {
                object = objects[index];
                if (object == null) {
                    object = loadRawObject(names[index], AtlasObject.class);
                    objects[index] = object;
                }
            }
        }
        return object;
    }

    @Override
    public List<SearchIndex> getSearchIndices() {
        if (searchIndex == null) {
            synchronized (searchIndexLock) {
                while (searchIndex == null) {
                    try {
                        searchIndexLock.wait();
                    } catch (InterruptedException e) {
                        log.debug(e);
                    }
                }
            }
        }
        return searchIndex;
    }

    private <T> T loadRawObject(String objectName, Type type) {
        // raw resources name convention uses underscore '_' instead of dash '-'
        objectName = objectName.replace('-', '_');
        int resourceId = resources.getIdentifier(objectName, "raw", context.getPackageName());
        return loadObject(new InputStreamReader(resources.openRawResource(resourceId)), type);
    }
}
