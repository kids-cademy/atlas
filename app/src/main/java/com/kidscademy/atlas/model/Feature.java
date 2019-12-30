package com.kidscademy.atlas.model;

public class Feature {
    /**
     * Feature name is for internal use only; it is not meant to be displayed on
     * user interface.
     */
    private String name;
    /**
     * Mandatory numeric value. If optional {@link #maximum} value is provided this
     * value should be interpreted as minimum. This may be the case if this feature
     * represent a range.
     */
    private double value;
    /**
     * Optional maximum value used when feature represent a range. If this maximum
     * is provided {@link #value} should be interpreted as minimum.
     */
    private Double maximum;
    /**
     * Quantity is used to determine measurement unit. Features always use basic SI
     * units.
     */
    private Quantity quantity;

    private String nameDisplay;
    private String valueDisplay;

    public Feature() {

    }

    public Feature(String name, double value, Quantity quantity) {
        this.name = name;
        this.value = value;
        this.quantity = quantity;
    }

    public Feature(String name, double value, double maximum, Quantity quantity) {
        this.name = name;
        this.value = value;
        this.maximum = maximum;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public boolean hasMaximum() {
        return maximum != null;
    }

    public double getMaximum() {
        return maximum;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public String getNameDisplay() {
        return nameDisplay;
    }

    public String getValueDisplay() {
        return valueDisplay;
    }

    public enum Quantity {
        MASS, TIME, LENGTH, SPEED
    }
}
