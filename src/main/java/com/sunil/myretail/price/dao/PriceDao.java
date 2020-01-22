package com.sunil.myretail.price.dao;

import com.sunil.myretail.price.domain.Price;

public interface PriceDao {

    void insertPrice(Price priceDomain);

    void updatePrice(Price priceDomain);

    Price getPrice(String productId);
}
