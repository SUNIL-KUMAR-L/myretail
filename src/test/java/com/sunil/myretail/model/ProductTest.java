package com.sunil.myretail.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Price price = null;


    @Before
    public void setup() {
        try {
            price = objectMapper.readValue(ProductTest.class.getClassLoader().getResourceAsStream("price.json"), Price.class);
        } catch (Exception exp){
            price = new Price();
            price.setCurrencyCode("USD");
            price.setPriceValue("13.49");
        }
    }

    @Test
    public void testProductJsonString() throws Exception {
        Product testClassProduct = new Product();
        testClassProduct.setPrice(price);
        testClassProduct.setProductId("13860428");
        testClassProduct.setProductDescription("The Big Lebowski (Blu-ray) (Widescreen)");


        String productJsonString =  objectMapper.writeValueAsString(testClassProduct);

        assertTrue(productJsonString.contains("\"name\""));
        assertTrue(productJsonString.contains("\"id\""));
        assertTrue(productJsonString.contains("\"current_price\""));


        assertTrue(productJsonString.contains("\"The Big Lebowski (Blu-ray) (Widescreen)\""));
        assertTrue(productJsonString.contains("13860428"));
        assertTrue(productJsonString.contains("\"value\""));
        assertTrue(productJsonString.contains("\"currency_code\""));
        assertTrue(productJsonString.contains("\"USD\""));


        Price newPrice = new Price();
        newPrice.setCurrencyCode("USD");
        newPrice.setPriceValue("13.49");

        Product newProduct = new Product();
        newProduct.setPrice(newPrice);
        newProduct.setProductId("13860428");
        newProduct.setProductDescription("The Big Lebowski (Blu-ray) (Widescreen)");

        assertEquals(newProduct, testClassProduct);

    }

}