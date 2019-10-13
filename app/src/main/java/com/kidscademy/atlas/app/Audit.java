package com.kidscademy.atlas.app;

import android.support.annotation.NonNull;

import java.net.URL;

import js.converter.Converter;
import js.converter.ConverterRegistry;

public class Audit {
    private final String packageName;
    private final HubService hubService;
    private final Converter converter;

    /**
     * Timestamp for application activity duration.
     */
    private long timestamp;

    /**
     * Null audit instance created when WIFI is not connected in order to avoid using mobile data.
     */
    Audit() {
        this.packageName = null;
        this.hubService = null;
        this.converter = null;
    }

    Audit(@NonNull String packageName, @NonNull HubService hubService) {
        this.packageName = packageName;
        this.hubService = hubService;
        this.converter = ConverterRegistry.getConverter();
    }

    void openApplication() {
        timestamp = System.currentTimeMillis();
    }

    void closeApplication() {
        if (timestamp != 0) {
            send(Event.APP_ACTIVE, System.currentTimeMillis() - timestamp);
            timestamp = 0;
        }
    }

    public void openReader() {
        send(Event.OPEN_READER);
    }

    public void openArticle(@NonNull String atlasObjectName) {
        send(Event.OPEN_ARTICLE, atlasObjectName);
    }

    public void playSample(@NonNull String atlasObjectName) {
        send(Event.PLAY_SAMPLE, atlasObjectName);
    }

    public void expandFact(@NonNull String atlasObjectName, @NonNull String fact) {
        send(Event.EXPAND_FACT, atlasObjectName, fact);
    }

    public void openLink(@NonNull URL url) {
        send(Event.OPEN_LINK, url.toExternalForm());
    }

    public void scrollPage(@NonNull String atlasObjectName) {
        send(Event.SCROLL_PAGE, atlasObjectName);
    }

    public void readerSearch() {
        send(Event.READER_SEARCH);
    }

    public void readerIndex() {
        send(Event.READER_INDEX);
    }

    public void openSearch() {
        send(Event.OPEN_SEARCH);
    }

    public void searchKeyword(@NonNull String keyword) {
        send(Event.SEARCH_KEYWORD, keyword);
    }

    public void openIndex() {
        send(Event.OPEN_INDEX);
    }

    public void selectIndex(@NonNull String atlasObjectName) {
        send(Event.SELECT_INDEX, atlasObjectName);
    }

    public void collapseIndex() {
        send(Event.COLLAPSE_INDEX);
    }

    public void expandIndex() {
        send(Event.EXPAND_INDEX);
    }

    public void swapIndex() {
        send(Event.SWAP_INDEX);
    }

    public void openSync() {
        send(Event.OPEN_SYNC);
    }

    public void openAccessibility() {
        send(Event.OPEN_ACCESSIBILITY);
    }

    public void openAbout() {
        send(Event.OPEN_ABOUT);
    }

    public void openShare() {
        send(Event.OPEN_SHARE);
    }

    public void shareApp(String appName) {
        send(Event.SHARE_APP, appName);
    }

    public void openRate() {
        send(Event.OPEN_RATE);
    }

    public void openHelp() {
        send(Event.OPEN_HELP);
    }

    public void openSettings() {
        send(Event.OPEN_SETTINGS);
    }

    private void send(@NonNull Enum<?> event, Object... args) {
        if (hubService != null) {
            hubService.recordAuditEvent(packageName, event.name(), parameter(args, 0), parameter(args, 1));
        }
    }

    /**
     * Stringify argument identified by index.
     *
     * @param args  arguments list,
     * @param index desired argument index.
     * @return string representation of indexed argument or null if missing.
     */
    private String parameter(Object[] args, int index) {
        return args.length > index ? converter.asString(args[index]) : null;
    }

    private enum Event {
        APP_ACTIVE,
        OPEN_READER, OPEN_ARTICLE, PLAY_SAMPLE, EXPAND_FACT, OPEN_LINK, SCROLL_PAGE, READER_SEARCH, READER_INDEX,
        OPEN_SEARCH, SEARCH_KEYWORD,
        OPEN_INDEX, SELECT_INDEX, COLLAPSE_INDEX, EXPAND_INDEX, SWAP_INDEX,
        OPEN_SYNC, OPEN_ACCESSIBILITY,
        OPEN_ABOUT,
        OPEN_SHARE, SHARE_APP,
        OPEN_RATE,
        OPEN_HELP,
        OPEN_SETTINGS
    }
}
