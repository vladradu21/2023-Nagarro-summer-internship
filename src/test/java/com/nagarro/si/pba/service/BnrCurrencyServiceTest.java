package com.nagarro.si.pba.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nagarro.si.pba.exceptions.RemoteApiException;
import com.nagarro.si.pba.exceptions.WrongFormatException;
import com.nagarro.si.pba.gateway.BnrCurrenciesGateway;
import com.nagarro.si.pba.model.Currency;
import com.nagarro.si.pba.model.CurrencyRate;
import com.nagarro.si.pba.service.impl.BnrCurrencyServiceImpl;
import com.nagarro.si.pba.utils.TestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class BnrCurrencyServiceTest {
    @Mock
    private BnrCurrenciesGateway bnrCurrenciesGateway;
    @InjectMocks
    private BnrCurrencyServiceImpl currencyService;

    @ParameterizedTest
    @MethodSource("currencyValueProvider")
    void testCorrectValuesForCurrencies(String currencyCode, double expectedValue) {
      when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/expectedCurrencies.xml"));
  
      List<CurrencyRate> currencies = currencyService.getCurrencyRates();
  
      assertTrue(currencies.stream()
          .anyMatch(currency -> currency.getCode().equals(currencyCode) && currency.getRate() == expectedValue));
    }

    @Test
    void testMissingCurrenciesInBody(){
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/missingCurrenciesInBody.xml"));

        List<CurrencyRate> currencies = currencyService.getCurrencyRates();

        assertEquals(4, currencies.size());
        assertEquals("AED", currencies.get(0).getCode());
        assertEquals("AUD", currencies.get(1).getCode());
        assertEquals("BRL", currencies.get(2).getCode());
        assertEquals("CNY", currencies.get(3).getCode());
    }

    @Test
    void testNoRatesInBody() {
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/noRatesInBody.xml"));

        List<CurrencyRate> currencies = currencyService.getCurrencyRates();

        assertTrue(currencies.isEmpty());
    }

    @Test
    void testCurrenciesWrongXmlFormat() {
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/wrongXmlFormat.xml"));

        assertThrows(WrongFormatException.class, () -> currencyService.getCurrencyRates());
    }

    @Test
    void testWrongRateVariableTypeOrEmpty() {
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/wrongRateVariableTypeOrEmpty.xml"));

        List<CurrencyRate> currencies = currencyService.getCurrencyRates();

        assertEquals(3, currencies.size());
        assertEquals("AED", currencies.get(0).getCode());
        assertEquals("BRL", currencies.get(1).getCode());
        assertEquals("CAD", currencies.get(2).getCode());
    }

    @Test
    void testRemoteApiException() {
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenThrow(RemoteApiException.class);

        assertThrows(RemoteApiException.class, () -> currencyService.getCurrencyRates());
    }

    @Test
    void testConvertEURtoRON(){
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/expectedCurrencies.xml"));

        assertEquals(591.507554, currencyService.convert(120.23, Currency.EUR, Currency.RON));
    }

    @Test
    void testConvertRONtoGBP(){
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/expectedCurrencies.xml"));

        assertEquals(2.136482, currencyService.convert(12.21, Currency.RON, Currency.GBP), 0.000001);
    }

    @Test
    void testConvertUSDtoJPY(){
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/expectedCurrencies.xml"));

        assertEquals(1.6968, currencyService.convert(120.0, Currency.USD, Currency.JPY), 0.0001);
    }

    @Test
    void testConvertToSameCurrency(){
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/expectedCurrencies.xml"));

        assertEquals(5.43, currencyService.convert(5.43, Currency.AED, Currency.AED));
    }

    @Test
    void testConvertToInexistentCurrency(){
        when(bnrCurrenciesGateway.getXmlBodyFromUrl()).thenReturn(TestUtils.readFromFile("xml/noRatesInBody.xml"));

        assertThrows(NoSuchElementException.class, () -> currencyService.convert(1.0, Currency.RON, Currency.ZAR));
    }

    private static Stream<Arguments> currencyValueProvider() {
        return Arrays.stream(new Arguments[] {
                Arguments.of("AED", 1.2126),
                Arguments.of("AUD", 3.0134),
                Arguments.of("BGN", 2.5154),
                Arguments.of("BRL", 0.9422),
                Arguments.of("CAD", 3.3805),
                Arguments.of("CHF", 5.1245),
                Arguments.of("CNY", 0.6234),
                Arguments.of("CZK", 0.2043),
                Arguments.of("DKK", 0.6602),
                Arguments.of("EGP", 0.1439),
                Arguments.of("EUR", 4.9198),
                Arguments.of("GBP", 5.7150),
                Arguments.of("HUF", 130.0100),
                Arguments.of("INR", 0.0544),
                Arguments.of("JPY", 314.9800),
                Arguments.of("KRW", 34.8900),
                Arguments.of("MDL", 0.2533),
                Arguments.of("MXN", 0.2646),
                Arguments.of("NOK", 0.4421),
                Arguments.of("NZD", 2.7635),
                Arguments.of("PLN", 1.1088),
                Arguments.of("RSD", 0.0420),
                Arguments.of("RUB", 0.0495),
                Arguments.of("SEK", 0.4283),
                Arguments.of("THB", 0.1289),
                Arguments.of("TRY", 0.1652),
                Arguments.of("UAH", 0.1206),
                Arguments.of("USD", 4.4539),
                Arguments.of("XAU", 280.9445),
                Arguments.of("XDR", 5.9850),
                Arguments.of("ZAR", 0.2518)
        });
    }
}
