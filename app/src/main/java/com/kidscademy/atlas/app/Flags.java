package com.kidscademy.atlas.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Application global state and flags.
 *
 * @author Iulian Rotaru
 */
public final class Flags {
    private static final String FLAGS_FULL_SCREEN = "flags.reader.fullscreen";
    private static final String FLAGS_READER_POSITION = "flags.reader.position";

    private final Context context;

    Flags(Context context) {
        this.context = context;
        setFlagsFullScreen(true);
    }

    /**
     * Set adapter position for current explorer page.
     *
     * @param position adapter current position.
     */
    public void setExplorerPosition(int position) {
        putInt(FLAGS_READER_POSITION, position);
    }

    /**
     * Get adapter position for current instrument as saved by {@link #setExplorerPosition(int)}.
     *
     * @return saved adapter position.
     */
    public int getExplorerPosition() {
        return getInt(FLAGS_READER_POSITION);
    }

    public void setFlagsFullScreen(boolean fullScreen) {
        putInt(FLAGS_FULL_SCREEN, fullScreen ? 1 : 0);
    }

    public boolean isFullScreen() {
        return getInt(FLAGS_FULL_SCREEN) != 0;
    }

    // --------------------------------------------------------------------------------------------
    // UTILITY METHODS

    private void putInt(String key, int value) {
        editor().putInt(key, value).commit();
    }

    private int getInt(String key) {
        return store().getInt(key, 0);
    }

    private SharedPreferences store() {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences.Editor editor() {
        return store().edit();
    }
}
