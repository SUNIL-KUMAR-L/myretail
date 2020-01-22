package com.sunil.myretail.price.exception;

public class PriceUpdateException extends  RuntimeException {
    public PriceUpdateException(String productId, Throwable cause) {
        super("Unable to update Price for productId :" + productId, cause);
    }
}
