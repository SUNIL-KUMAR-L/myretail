package com.sunil.myretail.convertor;

import com.sunil.myretail.model.Product;
import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.redsky.domain.Item;
import com.sunil.myretail.redsky.domain.RedSky;

public class ConvertorUtil {

    public static Price convertPriceModelToPriceDomain
            (com.sunil.myretail.model.Price priceModel, String productId) {
        Price priceDomain  = new Price();
        priceDomain.setProductId(productId);
        priceDomain.setProductPrice(priceModel.getPriceValue());
        priceDomain.setCurrencyCode(priceModel.getCurrencyCode());
        return priceDomain;
    }

    public static com.sunil.myretail.model.Price convertPriceDomainToModel
            (com.sunil.myretail.price.domain.Price priceDomain) {
        com.sunil.myretail.model.Price priceModel  = new com.sunil.myretail.model.Price();
        if(null == priceDomain){
            return priceModel;
        }
        priceModel.setPriceValue(priceDomain.getProductPrice());
        priceModel.setCurrencyCode(priceDomain.getCurrencyCode());
        return priceModel;
    }

    public static Product buildProduct(RedSky redSky, Price price) {

        Product product = new Product();

        Item redskyItem = redSky.getProduct().getItem();

        product.setProductId(redskyItem.getTcin());

        if(null != redskyItem.getProductDescription()) {
            String title = redskyItem.getProductDescription().getTitle();
            if (null != title) {
                product.setProductDescription(title);
            }
        }

        product.setPrice(convertPriceDomainToModel(price));

        return product;
    }

}
