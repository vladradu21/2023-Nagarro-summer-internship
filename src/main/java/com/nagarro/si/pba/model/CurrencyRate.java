package com.nagarro.si.pba.model;

public class CurrencyRate {
    private String code;
    private double rate;

    public CurrencyRate(String currencyCode, double rate) {
        this.code = currencyCode;
        this.rate = rate;
    }

    public CurrencyRate() {
    }

    public String getCode() {
        return code;
    }

    public double getRate() {
        return rate;
    }

    public void setCode(String currencyCode) {
        this.code = currencyCode;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
