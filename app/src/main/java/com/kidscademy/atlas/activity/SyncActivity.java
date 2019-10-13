package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.sync.SyncService;
import com.kidscademy.atlas.view.SyncDevicesView;

import java.lang.ref.WeakReference;

import js.lang.AsyncTask;
import js.lang.BugError;
import js.log.Log;
import js.log.LogFactory;

public class SyncActivity extends AppActivity implements ServiceConnection {
    private static final Log log = LogFactory.getLog(SyncActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, SyncActivity.class);
        activity.startActivity(intent);
    }

    private static final BrowserHandler browserHandler = new BrowserHandler();

    public static Handler getBrowserHandler() {
        return browserHandler;
    }

    private static final Object syncActivityMutex = new Object();
    private static WeakReference<SyncActivity> syncActivity;

    private StartFragment startFragment;
    private StopFragment stopFragment;
    private DialogFragment dialogFragment;

    public SyncActivity() {
        super(R.layout.activity_sync);
        log.trace("SyncActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        startFragment = findViewById(R.id.sync_start_fragment);
        stopFragment = findViewById(R.id.sync_stop_fragment);
        dialogFragment = findViewById(R.id.sync_dialog_fragment);

        setClickListener(R.id.sync_start_we_play);
        setClickListener(R.id.sync_stop_we_play);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log.trace("onNewIntent(Intent");
        if ("SYNC_PAGE_LOADED".equals(intent.getAction())) {
            boolean browserSupported = intent.getBooleanExtra("BROWSER_SUPPORTED", false);
            startFragment.onPageLoaded(browserSupported);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        synchronized (syncActivityMutex) {
            syncActivity = new WeakReference<>(this);
        }
        bindService(new Intent(this, SyncService.class), this, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(this);
        synchronized (syncActivityMutex) {
            syncActivity = null;
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.sync_start_we_play || id == R.id.sync_stop_we_play) {
            WePlayActivity.start(this);
        } else if (id == R.id.action_back) {
            if (startFragment.isPending()) {
                startFragment.setVisibility(View.GONE);
                dialogFragment.setVisibility(View.VISIBLE);
                showFAB(View.INVISIBLE);
            } else {
                onBackPressed();
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        final SyncService syncService = ((SyncService.LocalBinder) service).getService();
        if (startFragment.isPending()) {
            AsyncTask<String> task = new AsyncTask<String>() {
                @Override
                protected String execute() throws Throwable {
                    return syncService.getAppURL();
                }

                @Override
                protected void onPostExecute(String appURL) {
                    startFragment.setAppURL(appURL);
                }
            };
            task.start();
        } else {
            startFragment.setVisibility(View.GONE);
            stopFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
//        startFragment.setVisibility(View.VISIBLE);
//        stopFragment.setVisibility(View.GONE);
    }

    private void showFAB(int visibility) {
        View fab = findViewById(R.id.action_back);
        if (fab != null) {
            fab.setVisibility(visibility);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private static class BrowserHandler extends Handler {
        private static final Log log = LogFactory.getLog(BrowserHandler.class);

        @Override
        public void handleMessage(Message message) {
            log.trace("handleMessage(Message)");

            Bundle bundle = message.getData();
            boolean browserSupported = bundle.getBoolean("BROWSER_SUPPORTED");

            synchronized (syncActivityMutex) {
                if (syncActivity != null) {
                    syncActivity.get().startFragment.onPageLoaded(browserSupported);
                }
            }
        }
    }

    public static class StartFragment extends ConstraintLayout implements View.OnClickListener {
        private boolean pending;

        private View messageView;
        private View successView;
        private View errorView;
        private View browserGroup;

        private TextView appUrlText;
        private View wePlayView;
        private SyncDevicesView syncDevicesView;

        public StartFragment(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            inflate(context, R.layout.compo_sync_start, this);
        }

        @Override
        public void onFinishInflate() {
            super.onFinishInflate();

            messageView = findViewById(R.id.sync_start_message);
            successView = findViewById(R.id.sync_start_success);
            errorView = findViewById(R.id.sync_start_error);
            browserGroup = findViewById(R.id.sync_start_browser);

            appUrlText = findViewById(R.id.sync_start_browser_url);
            wePlayView = findViewById(R.id.sync_start_we_play);
            syncDevicesView = findViewById(R.id.sync_start_devices);

            View button = findViewById(R.id.sync_start_button);
            button.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SyncService.start(getContext());
            messageView.setVisibility(View.GONE);
            browserGroup.setVisibility(View.VISIBLE);
            if (wePlayView != null) {
                wePlayView.setVisibility(View.GONE);
            }
            pending = true;
        }

        public void setAppURL(String appURL) {
            appUrlText.setText(appURL);
        }

        public void onPageLoaded(boolean browserSupported) {
            // browser group should remain on layout since SYNC button is aligned relative to it
            browserGroup.setVisibility(View.INVISIBLE);

            if (browserSupported) {
                pending = false;
                syncDevicesView.setInSyncState(true);
                successView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
            } else {
                successView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
            }
        }

        public boolean isPending() {
            return pending;
        }
    }

    public static class StopFragment extends ConstraintLayout implements View.OnClickListener {
        private final Activity activity;
        private SyncDevicesView syncDevicesView;

        public StopFragment(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            inflate(context, R.layout.compo_sync_stop, this);

            if (!(context instanceof Activity)) {
                if (isInEditMode()) {
                    this.activity = null;
                    return;
                }
                throw new BugError("Invalid context for sync dialog. Expected activity but got |%s|.", context.getClass());
            }
            this.activity = (Activity) context;
        }

        @Override
        public void onFinishInflate() {
            super.onFinishInflate();
            findViewById(R.id.sync_stop_button).setOnClickListener(this);
            syncDevicesView = findViewById(R.id.sync_stop_devices);
        }

        @Override
        public void onClick(View v) {
            SyncService.stop(getContext());
            syncDevicesView.setInSyncState(false);

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.onBackPressed();
                }
            }, 1000);
        }
    }

    public static class DialogFragment extends ConstraintLayout implements View.OnClickListener {
        private final SyncActivity activity;

        public DialogFragment(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            inflate(context, R.layout.compo_sync_dialog, this);
            if (!(context instanceof SyncActivity)) {
                if (isInEditMode()) {
                    this.activity = null;
                    return;
                }
                throw new BugError("Invalid context for sync dialog. Expected activity but got |%s|.", context.getClass());
            }
            this.activity = (SyncActivity) context;
        }

        @Override
        public void onFinishInflate() {
            super.onFinishInflate();
            findViewById(R.id.sync_dialog_abort_button).setOnClickListener(this);
            findViewById(R.id.sync_dialog_continue_button).setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.sync_dialog_continue_button) {
                activity.startFragment.setVisibility(View.VISIBLE);
                activity.showFAB(View.VISIBLE);
                activity.dialogFragment.setVisibility(View.GONE);
            } else if (id == R.id.sync_dialog_abort_button) {
                SyncService.stop(activity);
                activity.onBackPressed();
            }
        }
    }
}
