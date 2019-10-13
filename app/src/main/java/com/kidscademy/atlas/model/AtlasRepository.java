package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import java.util.List;

import js.lang.BugError;

/**
 * Atlas repository holds atlas objects and search indices. It allows for random access by object name
 * or by repository index. Repository index behaves like implementation has and indexed storage and objects
 * are sorted ascendant by name.
 *
 * @author Iulian Rotaru
 */
public interface AtlasRepository {
    /**
     * Returns the number of atlas objects contained in this repository.
     *
     * @return atlas objects count.
     */
    int getObjectsCount();

    /**
     * Get atlas object by its repository index. Implementation should treat this index value as if atlas
     * objects are sorted ascendant by name.
     *
     * @param index repository index for requested atlas object.
     * @return non null atlas object.
     * @throws IndexOutOfBoundsException if index is not in range.
     */
    @NonNull
    AtlasObject getObjectByIndex(int index) throws IndexOutOfBoundsException;

    /**
     * Get search indices for this atlas repository. Search indices allows searching repository objects
     * by keywords.
     *
     * @return repository search indices.
     */
    List<SearchIndex> getSearchIndices();
}
