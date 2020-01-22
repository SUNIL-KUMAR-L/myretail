package com.sunil.myretail.convertor;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sunil.myretail.model.Product;
import com.sunil.myretail.redsky.domain.RedSky;
import com.sunil.myretail.redsky.domain.RedSkyTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConvertorUtilTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private RedSky redSky;
    com.sunil.myretail.model.Price priceModel;
    com.sunil.myretail.price.domain.Price priceDomain;
    String productId;

    @Before
    public void setUp() throws Exception {

        productId = "1234";

        priceModel = new com.sunil.myretail.model.Price();
        priceModel.setCurrencyCode("USD");
        priceModel.setPriceValue("10.99");

        priceDomain = new com.sunil.myretail.price.domain.Price();
        priceDomain.setProductId(productId);
        priceDomain.setProductPrice("10.99");
        priceDomain.setCurrencyCode("USD");

        try {
            redSky = objectMapper.readValue(RedSkyTest.class.
                            getClassLoader().
                            getResourceAsStream("redsky.json"),
                    RedSky.class);
        } catch (Exception exp){

        }
        if(null == redSky) {
            fail("redSky creation failed");
        }
    }

    @Test
    public void convertPriceModelToPriceDomain() {
        com.sunil.myretail.price.domain.Price priceDomainValue = ConvertorUtil.convertPriceModelToPriceDomain(priceModel, "1234");
        assertEquals("10.99", priceDomainValue.getProductPrice());
        assertEquals("USD", priceDomainValue.getCurrencyCode());
        assertEquals("1234", priceDomainValue.getProductId());
    }

    @Test
    public void convertPriceDomainToModel() {
        com.sunil.myretail.model.Price priceModelValue =  ConvertorUtil.convertPriceDomainToModel(priceDomain);
        assertEquals("10.99", priceModelValue.getPriceValue());
        assertEquals("USD", priceModelValue.getCurrencyCode());
    }

    @Test
    public void buildProduct() {
        Product productValue = ConvertorUtil.buildProduct(redSky, priceDomain);
        assertEquals("13860428" , productValue.getProductId());
        assertEquals("The Big Lebowski (Blu-ray)", productValue.getProductDescription());
        assertEquals("10.99", productValue.getPrice().getPriceValue());
        assertEquals("USD", productValue.getPrice().getCurrencyCode());
    }
}
