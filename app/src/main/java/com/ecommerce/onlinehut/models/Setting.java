package com.ecommerce.onlinehut.models;

public class Setting implements Comparable{
    private String label, value;

    public Setting(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        return getLabel().compareTo(((Setting)o).getLabel());
    }
}
