package com.sunil.myretail.price.service;

import com.sunil.myretail.convertor.ConvertorUtil;
import com.sunil.myretail.model.Price;
import com.sunil.myretail.price.dao.PriceDao;

import java.util.Date;

public class PriceService {

    private PriceDao priceDao;
    public PriceService(PriceDao priceDao) {
        this.priceDao = priceDao;
    }

    public void insertPrice(Price price, String productId) {
        com.sunil.myretail.price.domain.Price priceDomain = ConvertorUtil.convertPriceModelToPriceDomain(price, productId);
        Date date = new Date();
        priceDomain.setCreatedDatetime(date);
        priceDomain.setUpdatedDatetime(date);
        priceDao.insertPrice(priceDomain);
    }

    public void updatePrice(Price price, String productId) {
        com.sunil.myretail.price.domain.Price priceDomain = ConvertorUtil.convertPriceModelToPriceDomain(price, productId);
        priceDao.updatePrice(priceDomain);
    }

    public com.sunil.myretail.price.domain.Price getPrice(String productId) {
        return priceDao.getPrice(productId);
    }
}
