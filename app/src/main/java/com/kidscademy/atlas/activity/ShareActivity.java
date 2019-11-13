package com.kidscademy.atlas.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kidscademy.atlas.R;
import com.kidscademy.atlas.adapter.SharingAdapter;
import com.kidscademy.atlas.model.SharingApp;
import com.kidscademy.atlas.view.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import js.log.Log;
import js.log.LogFactory;
import js.util.BluetoothShare;
import js.util.TwitterShare;

/**
 * Share application using device installed senders.
 *
 * @author Iulian Rotaru
 */
public class ShareActivity extends AppActivity implements SharingAdapter.Listener, View.OnClickListener {
    private static final Log log = LogFactory.getLog(ShareActivity.class);

    public static void start(Activity activity) {
        log.trace("start(Activity)");
        Intent intent = new Intent(activity, ShareActivity.class);
        activity.startActivity(intent);
    }

    private TwitterShare twitterShare;

    public ShareActivity() {
        super(R.layout.activity_share);
        log.trace("ShareActivity()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.trace("onCreate(Bundle)");
        super.onCreate(savedInstanceState);

        RecyclerView listView = findViewById(R.id.share_list);
        LinearLayoutManager layoutManager;
        if (isTablet()) {
            int rows = getResources().getInteger(R.integer.share_acitivty_rows);
            layoutManager = new GridLayoutManager(this, rows, LinearLayoutManager.HORIZONTAL, false);

            int cardSpace = getResources().getDimensionPixelSize(R.dimen.card_space);
            listView.addItemDecoration(new SpacesItemDecoration(cardSpace));
        } else {
            layoutManager = new LinearLayoutManager(this);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        }
        listView.setLayoutManager(layoutManager);

        // discover device application able to send, aka share
        SortedSet<SharingApp> apps = new TreeSet<>();

        // uses SENDTO action and email message mime type to discover email clients installed on device
        // also target activity should be able to handle 'mailto' schema in order to qualify as email client
        Intent actionSendto = new Intent(Intent.ACTION_SENDTO);
        actionSendto.setDataAndType(Uri.fromParts("mailto", "", null), "message/rfc822");
        for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(actionSendto, 0)) {
            apps.add(new SharingApp(this, resolveInfo, SharingApp.Type.EMAIL));
        }

        // discover installed applications able to SEND plain text
        // usually send means share but there are exceptions; for example Wikipedia uses text to conduct an internal search
        // also bluetooth system application has filter for SEND plain text and is listed here

        Intent actionSend = new Intent(Intent.ACTION_SEND);
        actionSend.setType("text/plain");
        for (ResolveInfo resolveInfo : getPackageManager().queryIntentActivities(actionSend, 0)) {
            assert resolveInfo.activityInfo.packageName != null;
            final String packageName = resolveInfo.activityInfo.packageName;
            SharingApp.Type sharingType = SharingApp.Type.TEXT;

            if (TwitterShare.isTwitterPackage(packageName)) {
                sharingType = SharingApp.Type.TWITTER;
            } else if (BluetoothShare.isBluetoothPackage(packageName)) {
                sharingType = SharingApp.Type.BLUETOOTH;
            }

            apps.add(new SharingApp(this, resolveInfo, sharingType));
        }

        log.debug("Apps discovered as beeing able to share. See next debug records.");
        for (SharingApp app : apps) {
            log.debug("%s: %s", app.getType(), app.getPackageName());
        }

        SharingAdapter sharingAdapter = new SharingAdapter(this, new ArrayList<>(apps));
        listView.setAdapter(sharingAdapter);
        sharingAdapter.notifyDataSetChanged();

        twitterShare = new TwitterShare(this);

        setClickListener(R.id.action_back);
    }

    @Override
    public void onSharingAppSelected(SharingApp sharingApp) {
        log.trace("onSharingAppSelected(SharingApp) - %s", sharingApp.getAppName());

        switch (sharingApp.getType()) {
            case TWITTER:
                onTwitterShare();
                return;

            case EMAIL:
                onEmailShare();
                return;

            case BLUETOOTH:
                BluetoothShare bluetoothShare = new BluetoothShare(this);
                bluetoothShare.send();
                return;

            case TEXT:
                break;

            default:
                throw new IllegalStateException();
        }

        // at this point user selected a generic text sending application

        // I do not have a reliable logic to determine EXTRA parameters supported by application able to send plain text
        // also it can happen that SEND action to not mean SHARE on target application activity
        // the only solution I have is educated guess: use SUBJECT and TEXT in the hope they are supported
        // SUBJECT is for completeness but TEXT is highly probably to be supported
        // if none supported sharing will silently fail

        final String url = storeURL();
        final String subject = getString(R.string.app_name);
        final String text = String.format("%s\r\n\r\n%s", getString(R.string.app_description), url);

        Intent share = new Intent(Intent.ACTION_SEND);
        // share intent is explicit since I know application package and activity class name
        share.setComponent(new ComponentName(sharingApp.getPackageName(), sharingApp.getActivityName()));

        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, subject);
        share.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(share);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log.trace("onActivityResult(int, int, Intent)");
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onTwitterShare() {
        log.trace("onTwitterShare()");
        final String text = String.format("%s. %s.", getString(R.string.app_name), getString(R.string.app_logotype));
        final String url = storeURL();

        twitterShare.post(text, url);
    }

    private void onEmailShare() {
        log.trace("onEmailShare()");
        // see FableSharedFragment#onEmailShare for comment regarding Email client (com.adroid.email) bug

        final String url = storeURL();
        final String subject = getString(R.string.app_name);
        final String body = String.format("%s.\r\n\r\n%s\r\n\r\n%s", getString(R.string.app_logotype), getString(R.string.app_description), url);

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(emailIntent);
    }

    private String storeURL() {
        return "https://play.google.com/store/apps/details?id=" + getPackageName();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_back) {
            onBackPressed();
        }
    }
}
