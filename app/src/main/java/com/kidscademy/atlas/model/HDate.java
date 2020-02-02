package com.kidscademy.atlas.model;

import android.support.annotation.NonNull;

import com.kidscademy.atlas.util.Strings;

import java.util.Calendar;
import java.util.Date;
import java.util.TreeMap;

public class HDate implements Comparable<HDate> {
    private Double value;
    private int mask;

    public HDate() {
    }

    public HDate(java.util.Date date) {
        this(date.getTime());
    }

    public HDate(java.sql.Date date) {
        this(date.getTime());
    }

    public HDate(java.sql.Time time) {
        this(time.getTime());
    }

    public HDate(long value) {
        this(value, Format.DATE, Period.FULL, Era.CE);
    }

    // 1822 CE : new HDate(1822, HDate.Unit.YEAR)
    // XIV-th Century CE : new HDate(14, HDate.Unit.CENTURY)
    public HDate(long value, Format format) {
        this(value, format, Period.FULL, Era.CE);
    }

    // 1000 BCE : new HDate(1000, HDate.Unit.YEAR, HDate.Era.BCE)
    public HDate(long value, Format format, Era era) {
        this(value, format, Period.FULL, era);
    }

    // End of XVII-th Century CE : new HDate(17, HDate.Unit.CENTURY, HDate.Period.END)
    // Middle of XIX-th Century CE : new HDate(19, HDate.Unit.CENTURY, HDate.Period.MIDDLE)
    public HDate(long value, Format format, Period period) {
        this(value, format, period, Era.CE);
    }

    public HDate(long value, Format format, Period period, Era era) {
        this.value = new Double(value);
        this.mask = format.ordinal() + (period.ordinal() << 8) + (era.ordinal() << 16);
    }

    public double getValue() {
        return value;
    }

    public Format getFormat() {
        return Format.values()[mask & 0x000000FF];
    }

    public Period getPeriod() {
        return Period.values()[(mask & 0x0000FF00) >> 8];
    }

    public Era getEra() {
        return Era.values()[(mask & 0x00FF0000) >> 16];
    }

    public HDate roundToCenturies() {
        switch (getFormat()) {
            case CENTURY:
                break;

            case DECADE:
                value = value / 10 + 1;
                break;

            case YEAR:
                value = value / 100 + 1;
                break;

            case DATE:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(value.longValue()));
                value = calendar.get(Calendar.YEAR) / 100.0;
                break;

            default:
                break;
        }

        format(Format.CENTURY);
        period(Period.FULL);
        return this;
    }

    private void format(Format format) {
        mask |= (format.ordinal() & 0x000000FF);
    }

    private void period(Period period) {
        mask |= (period.ordinal() & 0x0000FF00);
    }

    @Override
    @NonNull
    public String toString() {
        switch (getFormat()) {
            case YEAR:
                return Strings.format("%d %s", value.intValue(), getEra());

            case DECADE:
                return Strings.format("%d0s %s", value.intValue(), getEra());

            case CENTURY:
                switch (getPeriod()) {
                    case BEGINNING:
                        return Strings.format("Beginning of %d%s Century, %s", value.intValue(), suffix(), getEra());

                    case MIDDLE:
                        return Strings.format("Middle of %d%s Century, %s", value.intValue(), suffix(), getEra());

                    case END:
                        return Strings.format("End of %d%s Century, %s", value.intValue(), suffix(), getEra());

                    default:
                        return Strings.format("%d%s Century, %s", value.intValue(), suffix(), getEra());
                }

            case KYA:
                return Strings.format("%.2f kilo years ago", value);

            case MYA:
                return Strings.format("%.2f million years ago", value);

            case BYA:
                return Strings.format("%.2f billion years ago", value);

            default:
                break;
        }

        return "";
    }

    private String suffix() {
        switch (value.intValue()) {
            case 1:
                return "st";

            case 2:
                return "nd";

            case 3:
                return "rd";

            default:
                return "th";
        }
    }

    @Override
    public int compareTo(HDate other) {
        switch (getEra()) {
            case CE:
                if (other.getEra() == Era.CE) {
                    return ((Double) this.value).compareTo(other.value);
                }
                return 1;

            case BCE:
                if (other.getEra() == Era.BCE) {
                    return ((Double) other.value).compareTo(this.value);
                }
                return -1;
        }
        return 0;
    }

    private final static TreeMap<Integer, String> map = new TreeMap<Integer, String>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    private static String toRoman(double number) {
        int key = (int) number;
        int l = map.floorKey(key);
        if (number == l) {
            return map.get(key);
        }
        return map.get(l) + toRoman(key - l);
    }

    public enum Format {
        DATE, YEAR, DECADE, CENTURY, MILLENNIA, KYA, MYA, BYA
    }

    public enum Period {
        FULL, BEGINNING, MIDDLE, END
    }

    public enum Era {
        CE, BCE
    }
}
