package com.kidscademy.atlas.unit;

import android.view.LayoutInflater;

import com.kidscademy.atlas.adapter.MobileReaderAdapter;
import com.kidscademy.atlas.model.AtlasRepository;
import com.kidscademy.atlas.view.ReaderPage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import js.util.Classes;

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MobileReaderAdapterTest {
    @Mock
    private LayoutInflater inflater;

    @Mock
    private AtlasRepository repository;

    private MobileReaderAdapter adapter;

    @Before
    public void beforeTest() {
        adapter = new MobileReaderAdapter(inflater, repository);
    }

    @Test
    public void constructor() {
        ReaderPage[] pages = Classes.getFieldValue(adapter, "pages");
        assertNotNull(pages);
        assertThat(pages, arrayWithSize(3));
    }

    @Test
    public void isViewFromObject() {
//        MobileReaderAdapter adapter = mock(MobileReaderAdapter.class);
//        assertTrue(this.adapter.isViewFromObject(this.adapter, adapter));
//        assertFalse(this.adapter.isViewFromObject(this.adapter, new Object()));
    }

    @Test
    public void getCount() {
        when(repository.getObjectsCount()).thenReturn(100);
        assertThat(adapter.getCount(), is(100));
    }
}
