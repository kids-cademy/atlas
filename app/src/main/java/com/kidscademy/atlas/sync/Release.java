package com.kidscademy.atlas.sync;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class Release {
    private final String name = "Musical Instruments Atlas";
    private final String edition = "Community Edition";
    private final String publisher = "kids (a)cademy";
    private final String release = "1.0 beta";
    private final Date date = date(2019, 11, 1);
    private final String license = "Public Domain Knowledge";

    private static Date date(int year, int month, int day) {
	Calendar calendar = Calendar.getInstance();
	calendar.set(Calendar.YEAR, year);
	calendar.set(Calendar.MONTH, month);
	calendar.set(Calendar.DAY_OF_MONTH, day);
	return calendar.getTime();
    }
}
