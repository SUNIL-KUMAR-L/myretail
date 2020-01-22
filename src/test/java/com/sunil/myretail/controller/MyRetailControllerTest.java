package com.sunil.myretail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunil.myretail.exception.PriceValidationException;
import com.sunil.myretail.exception.ProductIdValidationException;
import com.sunil.myretail.exception.ValidationException;
import com.sunil.myretail.model.Price;
import com.sunil.myretail.model.Product;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import com.sunil.myretail.service.MyRetailService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static  org.mockito.Mockito.*;

import org.springframework.http.ResponseEntity;

import static org.junit.Assert.*;

public class MyRetailControllerTest {


    MyRetailService myRetailService;

    MyRetailController classUnderTest;

    Product product = null;

    ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup(){

        product = new Product();
        product.setProductId("13860428");
        product.setProductDescription("The Big Lebowski (Blu-ray)");

        Price price = new Price();
        price.setPriceValue("13.49");
        price.setCurrencyCode("USD");

        product.setPrice(price);

        myRetailService = mock(MyRetailService.class);

        classUnderTest = new MyRetailController(myRetailService);
    }


    @Test
    public void getProductPriceDetails() throws  Exception{

        when(myRetailService.getProduct("13860428")).thenReturn(product);

        ResponseEntity<Product> productResponse =  classUnderTest.getProductPriceDetails("13860428");

        String productResponseValue =  mapper.writeValueAsString(productResponse.getBody());

        assertTrue(productResponseValue.contains("\"id\""));
        assertTrue(productResponseValue.contains("13860428"));

        assertTrue(productResponseValue.contains("\"name\""));
        assertTrue(productResponseValue.contains("\"The Big Lebowski (Blu-ray)\""));

        assertTrue(productResponseValue.contains("\"13.49\""));
        assertTrue(productResponseValue.contains("\"USD\""));
        assertTrue(productResponseValue.contains("\"current_price\""));
    }

    @Test(expected = RedSkyIntegrationProductNotFoundException.class)
    public void getProductPriceDetailsNotFound() {

        when(myRetailService.getProduct("1234"))
                .thenThrow(new RedSkyIntegrationProductNotFoundException("1234", new RuntimeException()));
        classUnderTest.getProductPriceDetails("1234");
    }

    @Test
    public void updateProductPrice() {

        //update price value
        Price price = product.getPrice();
        price.setPriceValue("22.441");

        doNothing().when(myRetailService).updatePrice(any(Price.class), eq("13860428"));


        classUnderTest.updateProductPrice("13860428", product);

        ArgumentCaptor<Price>  priceArgumentCaptor = ArgumentCaptor.forClass(Price.class);
        ArgumentCaptor<String>  productIdStringCaptor = ArgumentCaptor.forClass(String.class);

        verify(myRetailService).updatePrice(priceArgumentCaptor.capture(), productIdStringCaptor.capture());

        Price priceModelCapturedValue  = priceArgumentCaptor.getValue();

        String productIdCaptorValue  = productIdStringCaptor.getValue();

        assertTrue("22.44".equals(priceModelCapturedValue.getPriceValue()));
        assertTrue(priceModelCapturedValue.getCurrencyCode().equals("USD"));

        assertEquals("13860428", productIdCaptorValue);
    }

    @Test
    public void testValidationErrorInvalidProductIdWhileGet(){
        when(myRetailService.getProduct("1234A")).thenReturn(product);
        try {
            classUnderTest.getProductPriceDetails("1234A");
        } catch (ProductIdValidationException exp) {
            assertNotNull(exp);
        }
        verify(myRetailService, times(0)).getProduct("1234A");
    }

    @Test
    public void testValidationErrorInvalidProductWhileUpdate() {
        try {
            classUnderTest.updateProductPrice("1234", new Product());
        } catch (ValidationException exp) {
            assertNotNull(exp);
        }
        verify(myRetailService, times(0)).updatePrice(any(), anyString());
    }

    @Test
    public void testValidationErrorProductPriceWhileUpdate() {
        Product product = new Product();
        Price price = new Price();
        price.setPriceValue("-1.00");
        product.setPrice(price);
        try {
            classUnderTest.updateProductPrice("1234", product);
        } catch (PriceValidationException exp) {
            assertNotNull(exp);
        }
        verify(myRetailService, times(0)).updatePrice(any(), anyString());
    }

    @Test
    public void testValidationErrorInvalidPriceCurrencyWhileUpdate() {
        Product product = new Product();
        Price price = new Price();
        price.setPriceValue("1.1");
        price.setCurrencyCode("INR");
        product.setPrice(price);
        try {
            classUnderTest.updateProductPrice("1234", product);
        } catch (PriceValidationException exp) {
            assertNotNull(exp);
        }
        verify(myRetailService, times(0)).updatePrice(any(), anyString());
    }

    @Test
    public void testValidationErrorInvalidProductIdAndPriceIdWhileUpdate() {
        Product product = new Product();
        product.setProductId("123");
        Price price = new Price();
        price.setPriceValue("10.00");
        price.setCurrencyCode("USD");
        product.setPrice(price);
        try {
            classUnderTest.updateProductPrice("1234", product);
        } catch (ValidationException exp) {
            assertNotNull(exp);
        }
        verify(myRetailService, times(0)).updatePrice(any(), anyString());
    }
}