package com.kidscademy.atlas.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.activity.ErrorActivity;
import com.kidscademy.atlas.model.AtlasRepository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;

import js.log.Log;
import js.log.LogFactory;
import js.log.LogLevel;
import js.log.LogManager;

/**
 * Application singleton holds global state, generates crash report and implements application active detection logic.
 * <p>
 * <h5>Android Process Creation</h5>
 * <p>
 * An Android process is created when an activity should be activated; a process is just a run-time container for an
 * activity instance. The ultimate goal is to start an activity but before that Android creates application singleton
 * and invoke {@link #onCreate()}. After onCreate method returns Android continue with activity creation. This is
 * true even if onCreate starts another activity; Android will still create activity requested by platform then will
 * create that requested by onCreate.
 * <p>
 * Now, which activity is created when application starts depends on Android platform and external applications. For
 * example, if application is started from home launcher, main activity will be created; if application is recreated from
 * recent applications, platform will restore last active activity. Also an activity could be explicitly requested by a
 * third party application. This behavior can be adjusted by <code>launchMode</code> attribute from manifest or intent
 * flags.
 * <p>
 * To sum-up, there is no way to route application start-up to different activity. Platform chosen activity will be
 * created anyway, even if onCreate method requests another activity start.
 *
 * @author Iulian Rotaru
 */
public class App extends Application implements Thread.UncaughtExceptionHandler, Application.ActivityLifecycleCallbacks {
    /**
     * Class logger.
     */
    private static Log log = LogFactory.getLog(App.class);

    /**
     * Application single instance. System guarantee there is only one single instance of this class, per process.
     */
    private static App instance;

    private AtlasRepository repository;
    private Flags flags;

    @Override
    public void onCreate() {
        boolean debug = false;
        try {
            String versionName = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            debug = versionName.endsWith("debug");
        } catch (PackageManager.NameNotFoundException ignored) {
        }

        LogManager.activateInAppLogging(this, debug ? LogLevel.TRACE : LogLevel.OFF, debug);
        log = LogFactory.getLog(App.class);
        log.trace("onCreate()");

        Thread.setDefaultUncaughtExceptionHandler(this);

        try {
            instance = this;
            // register activity life cycle here in order to catch first MainActivity start
            registerActivityLifecycleCallbacks(this);
            super.onCreate();

            repository = new AssetsRepository(getApplicationContext());
            flags = new Flags(getApplicationContext());

        } catch (Throwable throwable) {
            log.dump("App start fatal error: ", throwable);
            dumpStackTrace(throwable);
            ErrorActivity.start(getApplicationContext(), R.string.error_message, throwable);
        }
    }

    public static App instance() {
        return instance;
    }

    public AtlasRepository repository() {
        return repository;
    }

    public Flags flags() {
        return flags;
    }

    // --------------------------------------------------------------------------------------------
    // ERROR HANDLING

    /**
     * Dump uncaught exception to application logger and generates crash report, is user preferences allows.
     */
    @Override
    public void uncaughtException(Thread thread, final Throwable throwable) {
        log.dump("Uncaught exception on: ", throwable);
        dumpStackTrace(throwable);

        // if in UI thread just launch error activity; otherwise uses a handler
        // source: http://stackoverflow.com/questions/19897628/need-to-handle-uncaught-exception-and-send-log-file
        // not very sure is necessary since error activity is configured to run in separated process

        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            ErrorActivity.start(getApplicationContext(), R.string.error_message, throwable);
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    ErrorActivity.start(getApplicationContext(), R.string.error_message, throwable);
                }
            });
        }

        // do not use default handler in order to avoid system dialog about app crash
        System.exit(0);
    }

    private void dumpStackTrace(@NonNull Throwable throwable) {
        StringWriter stackTrace = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stackTrace));
    }

    // ------------------------------------------------------
    // APPLICATION ACTIVE DETECTION LOGIC

    // for application foreground / background detection, activities life cycle is critical
    // in particular, if A starts B, B.start() should occurs BEFORE a.stop()
    // this condition seems to be guaranteed to APIDOC, see:
    // http://developer.android.com/guide/components/activities.html#Coordinating activities

    /**
     * Open activities index used by application active detection logic. This index is incremented at every activity start
     * and decremented on stop.
     */
    private int startIndex;

    /**
     * Increment open activities index, triggering open application event if index was zero.
     */
    @Override
    public void onActivityStarted(Activity activity) {
        log.trace("onActivityStarted(Activity) - %s %d", activity.getClass().getName(), startIndex);
        if (startIndex++ == 0) {
            log.debug("Start application.");
        }
    }

    /**
     * Decrement open activities index, triggering close application event if index become zero.
     */
    @Override
    public void onActivityStopped(Activity activity) {
        --startIndex;
        log.trace("onActivityStopped(Activity) - %s %d", activity.getClass().getName(), startIndex);
        if (startIndex == 0) {
            log.debug("Stop application.");
        }
    }

    /**
     * Unused.
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    /**
     * Unused.
     */
    @Override
    public void onActivityResumed(Activity activity) {
    }

    /**
     * Unused.
     */
    @Override
    public void onActivityPaused(Activity activity) {
    }

    /**
     * Unused.
     */
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    /**
     * Unused.
     */
    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    // ---------------------------------------------------------------------------------------------
    // ESPRESSO TEST

    private AtomicBoolean isRunningTest;

    public synchronized boolean isRunningTest() {
        if (null == isRunningTest) {
            try {
                Class.forName("android.support.test.espresso.Espresso");
                isRunningTest = new AtomicBoolean(true);
            } catch (ClassNotFoundException e) {
                isRunningTest = new AtomicBoolean(true);
            }
        }

        return isRunningTest.get();
    }
}
