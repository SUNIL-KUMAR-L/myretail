package com.sunil.myretail.service;

import com.sunil.myretail.convertor.ConvertorUtil;
import com.sunil.myretail.model.Product;
import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.price.exception.GetPriceException;
import com.sunil.myretail.price.service.PriceService;
import com.sunil.myretail.redsky.domain.RedSky;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationClientErrorException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationServerErrorException;
import com.sunil.myretail.redsky.service.RedSkyService;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;


@Slf4j
public class MyRetailService {

    private RedSkyService redSkyService;
    private PriceService priceService;

    public MyRetailService(RedSkyService redSkyService, PriceService priceService) {
        this.redSkyService = redSkyService;
        this.priceService = priceService;
    }

    public Product getProduct(String productId) {

        final CompletableFuture<RedSky> redSkyCompletableFuture = CompletableFuture.supplyAsync(() -> redSkyService.getProductDetails(productId));
        final CompletableFuture<Price> priceCompletableFuture = CompletableFuture.supplyAsync(() -> priceService.getPrice(productId));

        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(redSkyCompletableFuture, priceCompletableFuture);

        final CompletableFuture<Product> productCompletableFuture = voidCompletableFuture
                .thenApply(voidInput ->
                                        {
                                            return  ConvertorUtil.buildProduct(redSkyCompletableFuture.join(), priceCompletableFuture.join());
                                        })
                .exceptionally( (exception)  ->
                    {
                        // return dummy product or throw exception
                        log.error("exception --- " + exception.getMessage(), exception);
                        Throwable cause = exception.getCause();
                        if(cause != null) {
                            log.error("exception --- cause " + cause.getMessage(), cause);
                            if (cause instanceof GetPriceException) {
                                return ConvertorUtil.buildProduct(redSkyCompletableFuture.join(), new Price());
                            } else if (cause instanceof RedSkyIntegrationProductNotFoundException) {
                                throw (RedSkyIntegrationProductNotFoundException) cause;
                            } else if (cause instanceof RedSkyIntegrationClientErrorException) {
                                throw (RedSkyIntegrationClientErrorException) cause;
                            } else if (cause instanceof RedSkyIntegrationServerErrorException) {
                                throw (RedSkyIntegrationServerErrorException) cause;
                            } else if (cause instanceof RedSkyIntegrationException) {
                                throw (RedSkyIntegrationException) cause;
                            }
                        }
                        return new Product();
                });

        return productCompletableFuture.join();
    }

    public void updatePrice(com.sunil.myretail.model.Price priceModel, String productId) {
        try {
            priceService.updatePrice(priceModel, productId);
        } catch (Exception exp) {
            log.error(String.format("Unable to update Price for productId :%s", productId));
            throw exp;
        }
    }
}
