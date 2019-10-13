package com.kidscademy.atlas.sync;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.kidscademy.atlas.activity.SyncActivity;

import js.log.Log;
import js.log.LogFactory;

/**
 * Controller is invoked from browser script via HTTP-RMI.
 *
 * @author Iulian Rotaru
 */
@SuppressWarnings("unused")
public class Controller {
    private static final Log log = LogFactory.getLog(Controller.class);

    public Release onPageLoaded(boolean browserSupported) {
        log.trace("onPageLoaded(boolean)");
        log.debug("Browser supported: |%s|", browserSupported);

        Handler handler = SyncActivity.getBrowserHandler();
        Message message = handler.obtainMessage();

        Bundle bundle = new Bundle();
        bundle.putBoolean("BROWSER_SUPPORTED", browserSupported);
        message.setData(bundle);

        handler.sendMessage(message);

        return new Release();
    }
}
