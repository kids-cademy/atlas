package com.kidscademy.atlas.sync;

import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;

import com.kidscademy.atlas.http.HttpServer;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import js.lang.AsyncTask;
import js.lang.Event;
import js.log.Log;
import js.log.LogFactory;

public class SyncService extends Service {
    private static final Log log = LogFactory.getLog(SyncService.class);

    private static final int HTTP_PORT = 8080;

    public class LocalBinder extends Binder {
        public SyncService getService() {
            return SyncService.this;
        }
    }

    public static void start(Context context) {
        log.trace("start(Context)");
        context.startService(new Intent(context, SyncService.class));
    }

    public static void stop(Context context) {
        log.trace("stop(Context)");
        context.stopService(new Intent(context, SyncService.class));
    }

    private final IBinder binder = new LocalBinder();

    private HttpServer server;

    public SyncService() {
        log.trace("SyncService()");
        // service context is not stable on constructor and we cannot create assets storage here
        // therefore we cannot create HTTP server and cannot use final field
    }

    @Override
    public IBinder onBind(Intent intent) {
        log.trace("onBind(Intent)");
        return binder;
    }

    @Override
    public void onCreate() {
        log.trace("onCreate()");
        super.onCreate();
        server = new HttpServer(new AssetsStorage(this), HTTP_PORT);
        server.start();
    }

    @Override
    public void onDestroy() {
        log.trace("onDestroy()");
        AsyncTask<Void> task = new AsyncTask<Void>() {
            @Override
            protected Void execute() {
                server.pushEvent(new DisconnectEvent());
                server.stop();
                return null;
            }
        };
        task.start();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log.trace("onStartCommand(Intent,int,int)");
        return START_STICKY;
    }

    public void pushEvent(Event event) {
        server.pushEvent(event);
    }

    private String appURL;

    public String getAppURL() throws UnknownHostException {
        if (appURL == null) {
            appURL = createAppURL(this, HTTP_PORT);
        }
        return appURL;
    }

    public String createAppURL(ContextWrapper context, int port) throws UnknownHostException {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        byte[] address = BigInteger.valueOf(wifi.getConnectionInfo().getIpAddress()).toByteArray();

        // InetAddress.getByAddress access and array in network order, that is, big endian
        // BigEndian.valueOf require big endian
        // BigInteger.toByteArray return also big endian

        // so far so good but resulting IP address is reversed; probably WifiInfo.getIpAddress returns little endian
        // hack is to reverse array order

        byte b = address[0];
        address[0] = address[3];
        address[3] = b;
        b = address[1];
        address[1] = address[2];
        address[2] = b;

        StringBuilder builder = new StringBuilder();
        builder.append("http://");
        builder.append(InetAddress.getByAddress(address).getHostAddress());
        if (port != 80) {
            builder.append(':');
            builder.append(port);
        }
        builder.append("/");

        appURL = builder.toString();
        return appURL;
    }
}
