package com.sunil.myretail.redsky.exception;

import org.springframework.web.client.RestClientResponseException;

public class RedSkyIntegrationException extends RuntimeException {

    public RedSkyIntegrationException(String productId, RestClientResponseException exp) {
        super("Unable to fetch data for product : "+productId, exp);
    }

}
