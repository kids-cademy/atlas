package com.kidscademy.atlas.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import js.lang.Callback;
import js.log.Log;
import js.log.LogFactory;
import js.net.client.HttpRmiTransaction;
import js.util.Net;
import js.util.Strings;

public class HubService {
    private static final Log log = LogFactory.getLog(HubService.class);

    /**
     * kids (a)cademy sync hosts service remoteLogger logic.
     */
    private static final String SERVER_URL = "http://kids-cademy.com/";

    void recordAuditEvent(@NonNull String packageName, @NonNull String event, @Nullable String parameter1, @Nullable String parameter2) {
        log.trace("recordAuditEvent(String, String, String, String)");
        String macAddress = Net.getActiveMacAddress(App.instance().getApplicationContext());
        if (macAddress != null) {
            invoke("recordAuditEvent", packageName, macAddress, event, parameter1, parameter2);
        }
    }

    void dumpStackTrace(@NonNull String packageName, @NonNull String stackTrace) {
        log.trace("dumpStackTrace(String, String)");
        String macAddress = Net.getActiveMacAddress(App.instance().getApplicationContext());
        if (macAddress != null) {
            invoke("dumpStackTrace", packageName, macAddress, stackTrace);
        }
    }

    public void votePlus(@NonNull String packageName, @NonNull String projectName) {
        log.trace("votePlus(String, String)");
        String macAddress = Net.getActiveMacAddress(App.instance().getApplicationContext());
        if (macAddress != null) {
            invoke("voteProject", packageName, macAddress, projectName, 1);
        }
    }

    public void voteMinus(@NonNull String packageName, @NonNull String projectName) {
        log.trace("voteMinus(String, String)");
        String macAddress = Net.getActiveMacAddress(App.instance().getApplicationContext());
        if (macAddress != null) {
            invoke("voteProject", packageName, macAddress, projectName, -1);
        }
    }

    private static void invoke(@NonNull String methodName, Object... args) {
        log.debug("Invoke |%s(%s)|.", methodName, Strings.join(args, ", "));
        if (App.instance().isRunningTest()) {
            return;
        }

        try {
            HttpRmiTransaction rmi = HttpRmiTransaction.getInstance(SERVER_URL);
            // remote class has the same name as this stub
            rmi.setMethod(HubService.class.getSimpleName(), methodName);
            rmi.setArguments(args);
            rmi.exec(new Callback<Void>() {
                @Override
                public void handle(Void unused) {
                }
            });
        } catch (Exception e) {
            log.error(e);
        }
    }
}
