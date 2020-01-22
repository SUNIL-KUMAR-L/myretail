package com.sunil.myretail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunil.myretail.model.Product;
import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.price.exception.PriceUpdateException;
import com.sunil.myretail.price.service.PriceService;
import com.sunil.myretail.redsky.domain.RedSky;
import com.sunil.myretail.redsky.domain.RedSkyTest;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import com.sunil.myretail.redsky.service.RedSkyService;
import static  org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import java.util.Date;

public class MyRetailServiceTest {

    @Mock
    RedSkyService redSkyService;

    @Mock
    PriceService priceService;

    MyRetailService classUnderTest;

    ObjectMapper objectMapper = new ObjectMapper();

    RedSky redSky;

    com.sunil.myretail.price.domain.Price priceDomain;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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

        priceDomain = new Price();
        priceDomain.setProductId("13860428");
        Date date = new Date();
        priceDomain.setCreatedDatetime(date);
        priceDomain.setUpdatedDatetime(date);
        priceDomain.setCurrencyCode("USD");
        priceDomain.setProductPrice("33.45");

        classUnderTest = new MyRetailService(redSkyService, priceService);
    }

    @Test
    public void getProduct() {
        when(redSkyService.getProductDetails("13860428")).thenReturn(redSky);

        when(priceService.getPrice("13860428")).thenReturn(priceDomain);

        Product product =  classUnderTest.getProduct("13860428");

        assertEquals("13860428" , product.getProductId());
        assertEquals("The Big Lebowski (Blu-ray)", product.getProductDescription());

        assertEquals("33.45",product.getPrice().getPriceValue());
        assertEquals("USD", product.getPrice().getCurrencyCode());
    }

    @Test
    public void getProductFailedToGetPrice() {
        when(redSkyService.getProductDetails("13860428")).thenReturn(redSky);

        when(priceService.getPrice("13860428")).thenThrow(new PriceUpdateException("13860428", new RuntimeException("")));

        Product product =  classUnderTest.getProduct("13860428");

        verify(redSkyService, times(1)).getProductDetails("13860428");
        verify(priceService, times(1)).getPrice("13860428");

        assertEquals("13860428" , product.getProductId());
        assertEquals("The Big Lebowski (Blu-ray)", product.getProductDescription());

        assertNull(product.getPrice().getPriceValue());
        assertNull(product.getPrice().getCurrencyCode());
    }

    @Test
    public void getProductThrowsException() {
        when(redSkyService.getProductDetails("13860428"))
                .thenThrow(new RedSkyIntegrationProductNotFoundException("13860428", new RuntimeException("")));
        when(priceService.getPrice("13860428")).thenReturn(priceDomain);

        try {
            classUnderTest.getProduct("13860428");
        } catch (RedSkyIntegrationProductNotFoundException exp) {
            verify(redSkyService, times(1)).getProductDetails("13860428");
        }
        verify(redSkyService, times(1)).getProductDetails("13860428");
        verify(priceService, times(0)).getPrice("13860428");
    }

    @Test
    public void updatePrice() {
        com.sunil.myretail.model.Price priceModel = new com.sunil.myretail.model.Price();
        priceModel.setPriceValue("123.45");
        priceModel.setCurrencyCode("USD");

        String productId = "13860428";

        doNothing().when(priceService).updatePrice(priceModel, productId);

        classUnderTest.updatePrice(priceModel, productId);
    }

    @Test (expected = PriceUpdateException.class)
    public void updatePriceFailed() {
        com.sunil.myretail.model.Price priceModel = new com.sunil.myretail.model.Price();
        priceModel.setPriceValue("123.45");
        priceModel.setCurrencyCode("USD");

        String productId = "13860428";

        doThrow(new PriceUpdateException(productId, new RuntimeException(""))).when(priceService).updatePrice(priceModel, productId);

        classUnderTest.updatePrice(priceModel, productId);
    }
}