package com.sunil.myretail.price.exception;

public class PriceCreateException extends  RuntimeException {
    public PriceCreateException(String productId, Throwable cause) {
        super("Unable to insert Price for productId :" + productId, cause);
    }
}
