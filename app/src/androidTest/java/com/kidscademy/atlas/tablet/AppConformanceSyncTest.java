package com.kidscademy.atlas.tablet;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AppConformanceSyncTest extends AppConformanceTest {
    @Before
    public void beforeTest() {
        SyncServiceLoader.load();
        super.beforeTest();
    }
}
