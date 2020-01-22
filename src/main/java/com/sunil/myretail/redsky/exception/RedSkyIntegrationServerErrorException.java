package com.sunil.myretail.redsky.exception;

import org.springframework.web.client.RestClientResponseException;

public class RedSkyIntegrationServerErrorException extends RuntimeException {

    public RedSkyIntegrationServerErrorException(String productId, RestClientResponseException exp) {
        super("Unable to fetch data for product : "+productId, exp);
    }
}
