package com.nagarro.si.pba.service;

import java.util.List;

import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.CurrencyRate;

public interface BnrCurrencyService {
    /**
     * This method uses a gateway class to access the BNR API and extract all the
     * currencies and their respective rates.
     * 
     * @return a list of CurrencyRate objects
     */
    List<CurrencyRate> getCurrencyRates();

    /**
     * This method converts the amount of an expense to the default currency of the
     * user.
     * 
     * @param amount the amount to be converted
     * @param fromCurrency the currency of the amount to be converted from
     * @param toCurrency the currency of the amount to be converted to
     * @return the converted amount
     */
    double convert(double amount, Currency fromCurrency, Currency toCurrency);
}
