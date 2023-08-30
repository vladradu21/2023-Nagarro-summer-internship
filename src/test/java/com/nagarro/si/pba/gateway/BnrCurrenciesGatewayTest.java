package com.nagarro.si.pba.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.nagarro.si.pba.exceptions.RemoteApiException;
import com.nagarro.si.pba.utils.TestUtils;

@ExtendWith(MockitoExtension.class)
public class BnrCurrenciesGatewayTest {
    private String mockUrl = "https://http.cat/418";
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private BnrCurrenciesGateway bnrCurrenciesGateway;

    @BeforeEach
    public void setup() {
        bnrCurrenciesGateway.setBnrUrl(mockUrl);
    }

    @Test
    public void testStatusCode200() {
        when(restTemplate.getForEntity(mockUrl, String.class))
            .thenReturn(ResponseEntity.ok(TestUtils.readFromFile("xml/expectedCurrencies.xml")));

        assertNotNull(bnrCurrenciesGateway.getXmlBodyFromUrl());
    }

    @Test
    public void testStatusCodeNot200() {
        when(restTemplate.getForEntity(mockUrl, String.class)).thenReturn(ResponseEntity.badRequest().build());

        assertThrows(RemoteApiException.class, () -> bnrCurrenciesGateway.getXmlBodyFromUrl());
    }

    @Test
    public void testCurrenciesNull() {
        when(restTemplate.getForEntity(mockUrl, String.class)).thenReturn(ResponseEntity.ok(""));

        assertTrue(bnrCurrenciesGateway.getXmlBodyFromUrl().isEmpty());
    }
}
