package com.sunil.myretail.price.exception;

public class GetPriceException extends  RuntimeException {
        public GetPriceException(String productId, Throwable cause) {
            super("Unable to get Price for productId :" + productId, cause);
        }
}