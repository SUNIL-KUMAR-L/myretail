package com.sunil.myretail.service;

import com.sunil.myretail.convertor.ConvertorUtil;
import com.sunil.myretail.model.Product;
import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.price.service.PriceService;
import com.sunil.myretail.redsky.domain.RedSky;
import com.sunil.myretail.redsky.service.RedSkyService;

import lombok.extern.slf4j.Slf4j;



@Slf4j
public class MyRetailService {

    private RedSkyService redSkyService;
    private PriceService priceService;

    public MyRetailService(RedSkyService redSkyService, PriceService priceService) {
        this.redSkyService = redSkyService;
        this.priceService = priceService;
    }

    public Product getProduct(String productId) {
        RedSky redSky = redSkyService.getProductDetails(productId);
        Price price = new Price();
        try {
            price = priceService.getPrice(productId);
        } catch (Exception exp) {
            log.error(String.format("Unable to get Price for productId :%s", productId));
        }
        return ConvertorUtil.buildProduct(redSky, price);
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
