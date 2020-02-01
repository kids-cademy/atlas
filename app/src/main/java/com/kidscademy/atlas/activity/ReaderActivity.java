package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.app.Flags;
import com.kidscademy.atlas.model.AtlasObject;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.model.EventsTree;
import com.kidscademy.atlas.model.ReaderAction;
import com.kidscademy.atlas.sync.PageLoadEvent;
import com.kidscademy.atlas.sync.SyncService;
import com.kidscademy.atlas.util.Params;
import com.kidscademy.atlas.util.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import js.lang.BugError;
import js.lang.Event;
import js.log.Log;
import js.log.LogFactory;

/**
 * Base class for reader activities, both mobile and tablet variants.
 *
 * @author Iulian Rotaru
 */
public abstract class ReaderActivity extends AppActivity implements ServiceConnection, ReaderAction.Listener, Player.StateListener, EventsTree {
    private final static Log log = LogFactory.getLog(ReaderActivity.class);

    protected static final String ARGS_OBJECT_INDEX = "reader.object.index";

    public static void start(Activity activity) {
        start(activity, 0);
    }

    /**
     * Open atlas reader activity and load requested object.
     *
     * @param activity    current activity,
     * @param objectIndex object repository index.
     */
    public static void start(Activity activity, int objectIndex) {
        log.trace("start(Activity, int)");

        if (!(activity instanceof AppActivity)) {
            throw new BugError("Not atlas context. Activity |%s| does not extends |%s|.", activity.getClass(), AppActivity.class);
        }
        boolean tablet = ((AppActivity) activity).isTablet();

        Intent intent = new Intent(activity, tablet ? TabletReaderActivity.class : MobileReaderActivity.class);
        intent.putExtra(ARGS_OBJECT_INDEX, objectIndex);
        activity.startActivity(intent);
    }

    /**
     * Descendants should provide specific logic for user interface update. It is guaranteed to be called
     * on user interface thread. Implementation should load requested atlas object from repository and
     * update its views.
     *
     * @param objectIndex repository index for loaded atlas object.
     */
    protected abstract void updateUserInterface(int objectIndex);

    protected final AtlasRepository repository;

    protected final Flags flags;

    protected final Player player;

    /**
     * Mutual exclusion on syncService reference initialization. A little of paranoia but does not harm.
     */
    private final Object syncMutex = new Object();

    /**
     * Reference to local synchronization service. It is initialized by onServiceConnected from a different
     * thread and tested for not null on user interface thread, when actually push events to client browser
     */
    private volatile SyncService syncService;

    /**
     * Repository index for atlas object currently displayed by reader.
     */
    protected int objectIndex;

    public ReaderActivity() {
        super(R.layout.activity_reader);
        log.trace("ReaderActivity");

        this.repository = app.repository();
        this.player = new Player(this);
        this.player.setStateListener(this);
        this.flags = app.flags();
    }

    // ---------------------------------------------------------------------------------------------
    // ACTIVITY LIFECYCLE

    @Override
    public void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log.trace("onNewIntent(Intent");
        objectIndex = intent.getIntExtra(ARGS_OBJECT_INDEX, 0);
        flags.setExplorerPosition(objectIndex);
    }

    @Override
    protected void onStart() {
        log.trace("onStart()");
        super.onStart();
        bindService(new Intent(this, SyncService.class), this, 0);
        player.create();
        updateUserInterface(objectIndex);
    }

    @Override
    protected void onStop() {
        log.trace("onStop()");
        flags.setExplorerPosition(objectIndex);
        player.destroy();
        unbindService(this);
        super.onStop();
    }

    // ---------------------------------------------------------------------------------------------
    // SYNC SERVICE

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        synchronized (syncMutex) {
            syncService = ((SyncService.LocalBinder) service).getService();
        }

        // at this point we face a race condition
        // - this activity user interface thread executes onStart -> updateUserInterface -> pushEvent
        // - pushEvent depende on syncService != null
        // - syncService is updated from a different thread at not predictable time

        // as a result, it is possible that page loading event to not be pushed, when activity start
        // in fact - at least on devices we tested, it was never sent because this callback was always call after onStart
        // to cope with this condition we send page loading from here
        // there is no attempt to check if already sent since from tests does not seems necessary and there is no penalty on user interface

        AtlasObject atlasObject = app.repository().getObjectByIndex(objectIndex);
        pushEvent(new PageLoadEvent(atlasObject));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // syncService is used by pushEvent in a not synchronized context
        // this is by design because it is not necessary
        // is callback is triggered by onDestroy -> unbindService and onDestroy is invoked after activity stopped with onStop
        // so is not possible to have concurrent pushEvent at this moment

        syncService = null;
    }

    public void pushEvent(Event event) {
        if (syncService == null) {
            synchronized (syncMutex) {
                if (syncService == null) {
                    return;
                }
                // if syncService is not null continue with next logic
            }
        }
        log.debug("Push event |%s|.", event.getClass());
        syncService.pushEvent(event);
    }

    // ---------------------------------------------------------------------------------------------
    // CALLBACKS

    @Override
    public void onPlayerStateChanged(Player.State state) {
        for (Player.StateListener listener : getListeners(Player.StateListener.class)) {
            listener.onPlayerStateChanged(state);
        }
    }

    @Override
    public void onReaderAction(ReaderAction action, int objectIndex) {
        switch (action) {
            case LOAD_OBJECT:
                updateUserInterface(objectIndex);
                break;

            case PLAY_SAMPLE:
                AtlasObject atlasObject = repository.getObjectByIndex(objectIndex);
                player.play(atlasObject.getAudioSamplePath());
                break;

            case STOP_SAMPLE:
                player.stop();
                break;

            default:
                throw new BugError("Reader action |%s| not handled.", action);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // EVENTS TREE IMPLEMENTATION

    /**
     * Interfaces recognized by current implementation of this events tree context.
     */
    private static final List<Class<?>> TYPES = new ArrayList<>();

    static {
        TYPES.add(Player.StateListener.class);
    }

    private Map<Class<?>, List<?>> listenersRegistry = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <I> void registerListener(Class<I> type, I listener) {
        Params.validType(TYPES, type);
        List<I> listeners = (List<I>) listenersRegistry.get(type);
        if (listeners == null) {
            listeners = new ArrayList<>();
            listenersRegistry.put(type, listeners);
        }
        listeners.add(listener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <I> Iterable<I> getListeners(Class<I> type) {
        Params.validType(TYPES, type);
        List listeners = listenersRegistry.get(type);
        return listeners != null ? listeners : Collections.emptyList();
    }
}
