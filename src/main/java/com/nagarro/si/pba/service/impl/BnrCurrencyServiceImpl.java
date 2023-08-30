package com.nagarro.si.pba.service.impl;

import com.nagarro.si.pba.gateway.BnrCurrenciesGateway;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.CurrencyRate;
import com.nagarro.si.pba.service.BnrCurrencyService;
import com.nagarro.si.pba.utils.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Service
public class BnrCurrencyServiceImpl implements BnrCurrencyService {
    private static final String CURRENCY = "currency";
    private static final String RATE = "Rate";
    private static final String MULTIPLIER = "multiplier";
    private final BnrCurrenciesGateway bnrCurrenciesGateway;
    private static final Logger LOGGER = LoggerFactory.getLogger(BnrCurrencyServiceImpl.class);

    public BnrCurrencyServiceImpl(BnrCurrenciesGateway bnrCurrenciesGateway) {
        this.bnrCurrenciesGateway = bnrCurrenciesGateway;
    }

    @Override
    public List<CurrencyRate> getCurrencyRates() {
        LOGGER.info("Fetching currency rates from BNR Currencies Gateway.");
        String xmlString = bnrCurrenciesGateway.getXmlBodyFromUrl();
        Document xml = XmlUtils.parseXmlIntoDocument(xmlString);
        NodeList elementsOfTypeRate = xml.getElementsByTagName(RATE);

        LOGGER.info("Parsing currency rates from XML response.");
        return IntStream.range(0, elementsOfTypeRate.getLength())
                .mapToObj(elementsOfTypeRate::item)
                .map(this::getCurrencyAndRateFromNode)
                .filter(Objects::nonNull)
                .toList();
    }

    public double convert(double amount, Currency fromCurrency, Currency toCurrency) {
        List<CurrencyRate> currencyRates = getCurrencyRates();

        double fromCurrencyRate = 1.0;
        if (!fromCurrency.equals(Currency.RON)) {
            fromCurrencyRate = currencyRates.stream()
                    .filter(currencyRate -> currencyRate.getCode().equals(fromCurrency.toString()))
                    .findFirst()
                    .orElseThrow()
                    .getRate();
        }

        double toCurrencyRate = 1.0;
        if (!toCurrency.equals(Currency.RON)) {
            toCurrencyRate = currencyRates.stream()
                    .filter(currencyRate -> currencyRate.getCode().equals(toCurrency.toString()))
                    .findFirst()
                    .orElseThrow()
                    .getRate();
        }

        return amount * fromCurrencyRate / toCurrencyRate;
    }

    private CurrencyRate getCurrencyAndRateFromNode(Node node) {
        CurrencyRate currency = new CurrencyRate();

        try {
            currency.setCode(getCodeFromNode(node));
            currency.setRate(getRateFromNode(node));

        } catch (NumberFormatException e) {
            return null;
        }

        return currency;
    }

    private String getCodeFromNode(Node node) {
        Node currencyCode = node.getAttributes().getNamedItem(CURRENCY);

        if (currencyCode != null) {
            return currencyCode.getNodeValue();
        } else {
            throw new NumberFormatException();
        }
    }

    private double getRateFromNode(Node node) {
        double rateValue = Double.parseDouble(node.getTextContent());
        Node multiplier = node.getAttributes().getNamedItem(MULTIPLIER);

        if (multiplier != null) {
            rateValue *= Integer.parseInt(multiplier.getNodeValue());
        }

        return rateValue;
    }
}
