package com.kidscademy.atlas.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import js.log.Log;
import js.log.LogFactory;

public final class TwitterShare {
    private static final Log log = LogFactory.getLog(TwitterShare.class);

    private static final List<String> PACKAGES = new ArrayList<>();

    static {
        PACKAGES.add("com.twitter.android");
    }

    public static boolean isTwitterPackage(String packageName) {
        return PACKAGES.contains(packageName);
    }

    private Activity activity;

    public TwitterShare(Activity activity) {
        this.activity = activity;
    }

    public void post(String text, String url) {
        // there are two use cases: with Twitter application installed on device and without
        // for both cases uses the same URI with parameters 'text' and 'url'
        // if Twitter app is installed it declares filter for ACTION_VIEW and twitter host and knows to handle parameters
        // if is not installed platform will found the browser, that send request to twitter server via HTTPS

        // do not use chooser and relies on user selected default

        Uri.Builder builder = Uri.parse("https://twitter.com/intent/tweet").buildUpon();
        builder.appendQueryParameter("text", text);
        builder.appendQueryParameter("url", url);

        Intent tweetIntent = new Intent(Intent.ACTION_VIEW);
        tweetIntent.setData(builder.build());

        PackageManager packageManager = activity.getPackageManager();
        if (tweetIntent.resolveActivity(packageManager) != null) {
            activity.startActivity(tweetIntent);
        } else {
            final String message = String.format("There is no application able to send tweet |%s|.", tweetIntent.getData());
            log.warn(message);
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
        }
    }
}
