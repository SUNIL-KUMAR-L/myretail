package com.sunil.myretail.redsky.exception;

public class RedSkyIntegrationProductNotFoundException extends RuntimeException {

    public RedSkyIntegrationProductNotFoundException(String productId, Throwable cause) {
        super("Unable to fetch data for product : "+ productId, cause);
    }
}
