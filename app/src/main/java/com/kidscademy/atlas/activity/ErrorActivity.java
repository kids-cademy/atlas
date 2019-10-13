package com.kidscademy.atlas.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.kidscademy.atlas.R;

import java.io.Serializable;

import js.log.Log;
import js.log.LogFactory;

/**
 * Error activity displays the error message or exception content.
 * <p>
 * In order to use error activity from uncaught exception handler need to open it in separated process. For this add
 * process and task affinity attributes to activity element from manifest.
 *
 * <pre>
 *  &lt;activity android:name="com.kids_cademy.app.ErrorActivity"
 *      . . .
 *      android:process=":exception_process"
 *      android:taskAffinity="com.kids_cademy.app.ErrorActivity"
 * </pre>
 *
 * @author Iulian Rotaru
 */
public class ErrorActivity extends AppCompatActivity implements OnClickListener {
    private static final Log log = LogFactory.getLog(ErrorActivity.class);

    /**
     * Start error activity with message and optional throwable instance.
     *
     * @param context    execution context,
     * @param messageRef message references.
     */
    public static void start(@NonNull Context context, @StringRes int messageRef, Throwable throwable) {
        log.trace("start(Context, int, Throwable...)");
        Intent intent = new Intent(context, ErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(messageRef));

        Bundle extras = new Bundle();
        extras.putSerializable("throwable", throwable);
        intent.putExtras(extras);

        context.startActivity(intent);
    }

    public ErrorActivity() {
        log.trace("ErrorActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(isTablet() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_error);
        TextView messageText = findViewById(R.id.error_message);
        TextView traceText = findViewById(R.id.error_trace);

        Intent intent = getIntent();
        messageText.setText(intent.getStringExtra(Intent.EXTRA_TEXT));

        Bundle extras = intent.getExtras();
        Throwable throwable = (Throwable) extras.getSerializable("throwable");
        if (throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        StringBuilder builder = new StringBuilder();
        builder.append(throwable.getClass().getCanonicalName());
        builder.append(" : ");
        builder.append(throwable.getMessage());

        StackTraceElement[] trace = throwable.getStackTrace();
        for (int i = 0; i < Math.min(trace.length, 5); ++i) {
            builder.append("\r\n- ");
            builder.append(trace[i].toString());
        }
        traceText.setText(builder);

        findViewById(R.id.error_restart).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.error_restart) {
            MainActivity.start(this);
        }
    }

    public boolean isTablet() {
        return (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
