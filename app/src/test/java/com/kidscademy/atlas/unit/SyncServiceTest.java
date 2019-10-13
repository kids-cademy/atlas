package com.kidscademy.atlas.unit;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.kidscademy.atlas.sync.SyncService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigInteger;
import java.net.UnknownHostException;

import static android.content.Context.WIFI_SERVICE;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SyncServiceTest {
    @Mock
    ContextWrapper contextWrapper;
    @Mock
    Context context;
    @Mock
    WifiManager wifiManager;
    @Mock
    WifiInfo wifiInfo;

    private SyncService service;

    @Before
    public void beforeTest() {
        service = new SyncService();
    }

    @Test
    public void createAppURL() throws UnknownHostException {
        when(contextWrapper.getApplicationContext()).thenReturn(context);
        when(context.getSystemService(WIFI_SERVICE)).thenReturn(wifiManager);
        when(wifiManager.getConnectionInfo()).thenReturn(wifiInfo);

        // it seems WifiInfo.getIpAddress does not return network byte order but instead returns little endian
        int ip = new BigInteger(new byte[]{12, 1, (byte) 168, (byte) 192}).intValue();
        when(wifiInfo.getIpAddress()).thenReturn(ip);

        assertThat(service.createAppURL(contextWrapper, 80), equalTo("http://192.168.1.12/"));
        assertThat(service.createAppURL(contextWrapper, 8080), equalTo("http://192.168.1.12:8080/"));
    }
}
