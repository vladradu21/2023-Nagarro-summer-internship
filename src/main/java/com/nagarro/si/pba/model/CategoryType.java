package com.nagarro.si.pba.model;

public enum CategoryType {
    INCOME("INCOME"),
    EXPENSE("EXPENSE");
    private final String value;

    CategoryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
