package com.kidscademy.atlas.app;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.SearchIndex;
import com.kidscademy.atlas.util.AssetsBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import js.lang.AsyncTask;
import js.lang.BugError;
import js.lang.GType;
import js.log.Log;
import js.log.LogFactory;
import js.util.Strings;

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

    private final String[] names;
    private final AtlasObject[] objects;

    private final Object searchIndexLock = new Object();
    private List<SearchIndex> searchIndex;

    AssetsRepository(Context context) throws IOException {
        super(context);
        log.trace("AssetsRepository(Context context)");

        names = loadObject(getAssetReader("atlas/objects-list.json"), String[].class);

        AsyncTask<Void> searchIndexLoader = new AsyncTask<Void>() {
            @Override
            protected Void execute() throws Throwable {
                searchIndex = loadObject(getAssetReader("atlas/search-index.json"), new GType(List.class, SearchIndex.class));
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
                                objects[index] = loadObject(getAssetReader(getObjectPath(names[index])), AtlasObject.class);
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
                if (object == null) {
                    try {
                        object = loadObject(getAssetReader(getObjectPath(names[index])), AtlasObject.class);
                    } catch (IOException e) {
                        throw new BugError(e);
                    }
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

    // --------------------------------------------------------------------------------------------
    // UTILITY METHODS

    private static String getObjectPath(String objectName) {
        // TODO: hard coded language to English
        return Strings.concat("atlas/", objectName, "/object_en.json");
    }
}
