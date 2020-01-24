package com.sunil.myretail.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PriceTest {

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void testPriceJsonString() throws Exception{

        Price price = new Price();
        price.setCurrencyCode("USD");
        price.setPriceValue("13.49");


        String priceJsonString =  objectMapper.writeValueAsString(price);

        assertTrue(priceJsonString.contains("USD"));

        assertTrue(priceJsonString.contains("currency_code"));

        assertTrue(priceJsonString.contains("13.49"));

        assertTrue(priceJsonString.contains("value"));

        Price newprice = new Price();
        newprice.setCurrencyCode("USD");
        newprice.setPriceValue("13.49");

        assertEquals(newprice, price);

    }



}