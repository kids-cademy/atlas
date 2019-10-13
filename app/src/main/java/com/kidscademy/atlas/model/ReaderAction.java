package com.kidscademy.atlas.model;

/**
 * User interface actions performed on atlas reader, both enumeration constants and listener interface.
 * Reader activity listen for actions fired by underlying reader object view.
 *
 * @author Iulian Rotaru
 */
public enum ReaderAction {
    /**
     * Start playing audio sample from contextual image view.
     */
    PLAY_SAMPLE,

    /**
     * Stop playing audio sample from contextual image view.
     */
    STOP_SAMPLE,

    /**
     * Loaded related object by clicking on an item from related objects list.
     */
    LOAD_OBJECT;

    /**
     * Listener for reader action.
     *
     * @author Iulian Rotaru
     */
    public interface Listener {
        /**
         * Handle specified user action, performed on atlas object identified by its repository index.
         *
         * @param action      reader action performed on user interface,
         * @param objectIndex repository index for atlas object on which action is performed.
         */
        void onReaderAction(ReaderAction action, int objectIndex);
    }
}
