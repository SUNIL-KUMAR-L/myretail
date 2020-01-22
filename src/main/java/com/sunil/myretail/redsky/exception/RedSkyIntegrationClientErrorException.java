package com.sunil.myretail.redsky.exception;

import org.springframework.web.client.RestClientResponseException;

public class RedSkyIntegrationClientErrorException extends RuntimeException {

    public RedSkyIntegrationClientErrorException(String productId, RestClientResponseException exp) {
        super("Unable to fetch data for product : "+productId, exp);
    }

}
