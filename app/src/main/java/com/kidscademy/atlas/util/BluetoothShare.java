package com.kidscademy.atlas.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import java.io.File;

public final class BluetoothShare
{
  private static final String PACKAGE = "com.android.bluetooth";

  public static boolean isBluetoothPackage(String packageName)
  {
    return PACKAGE.equals(packageName);
  }

  private Activity activity;

  public BluetoothShare(Activity activity)
  {
    this.activity = activity;
  }

  public void send()
  {
    ApplicationInfo applicationInfo;
    try {
      applicationInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), 0);
    }
    catch(NameNotFoundException unused) {
      // PackageManager.getApplicationInfo cannot throw exception since package is known
      return;
    }

    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_SEND);
    intent.setType("text/plain");

    // set explicit package name to limit intent resolution to Bluetooth only
    // since intent will resolve to a component only chooser will not be displayed
    intent.setPackage(PACKAGE);

    // ApplicationInfo.sourceDir API: Full path to the base APK for this application.
    File apk = new File(applicationInfo.sourceDir);
    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(apk));

    activity.startActivity(intent);
  }
}
