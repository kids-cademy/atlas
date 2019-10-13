package com.kidscademy.atlas.unit;

import com.kidscademy.atlas.model.HDate;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class HDateTest {
    @Test
    public void hdate() {
        HDate hdate = new HDate(1770, HDate.Format.YEAR);
        assertThat(hdate.toString(), equalTo("1770 CE"));
        hdate.roundToCenturies();
        assertThat(hdate.toString(), equalTo("XVIII-th Century, CE"));
    }
}
