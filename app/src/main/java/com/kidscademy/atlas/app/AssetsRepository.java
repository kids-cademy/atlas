package com.kidscademy.atlas.app;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.SearchIndex;
import com.kidscademy.atlas.util.AssetsBase;

import java.util.ArrayList;
import java.util.List;

import js.lang.AsyncTask;
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

    private List<AtlasObject> objects;

    private List<SearchIndex> searchIndex;

    AssetsRepository(Context context) {
        super(context);
        log.trace("AssetsRepository(Context context)");

        AsyncTask<Void> task = new AsyncTask<Void>() {
            @Override
            protected Void execute() throws Throwable {
                String[] names = loadObject(getAssetReader("atlas/objects-list.json"), String[].class);
                objects = new ArrayList<>();
                for (String name : names) {
                    final AtlasObject atlasObject = loadObject(getAssetReader(getObjectPath(name)), AtlasObject.class);
                    objects.add(atlasObject);
                }
                searchIndex = loadObject(getAssetReader("atlas/search-index.json"), new GType(List.class, SearchIndex.class));
                return null;
            }
        };
        task.start();
    }

    @Override
    public int getObjectsCount() {
        return objects.size();
    }

    @NonNull
    @Override
    public AtlasObject getObjectByIndex(int index) {
        return objects.get(index);
    }

    @Override
    public List<SearchIndex> getSearchIndices() {
        return searchIndex;
    }

    // --------------------------------------------------------------------------------------------
    // UTILITY METHODS

    private static String getObjectPath(String objectName) {
        // TODO: hard coded language to English
        return Strings.concat("atlas/", objectName, "/object_en.json");
    }
}
