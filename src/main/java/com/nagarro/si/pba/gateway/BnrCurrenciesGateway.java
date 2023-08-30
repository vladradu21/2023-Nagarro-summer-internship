package com.nagarro.si.pba.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.nagarro.si.pba.exceptions.RemoteApiException;

@Component
public class BnrCurrenciesGateway {
    @Value("${bnr.url}")
    private String bnrUrl;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(BnrCurrenciesGateway.class);


    public BnrCurrenciesGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getXmlBodyFromUrl() {
        logger.info("Fetching XML body from BNR URL: {}", bnrUrl);
        ResponseEntity<String> response = restTemplate.getForEntity(bnrUrl, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || !response.hasBody()) {
            logger.error("Error while calling BNR currency API");
            throw new RemoteApiException("Error while calling BNR currency API");
        }

        return response.getBody();
    }

    public void setBnrUrl(String bnrUrl) {
        this.bnrUrl = bnrUrl;
    }
}
