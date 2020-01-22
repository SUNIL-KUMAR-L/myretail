package com.sunil.myretail.price.service;

import com.sunil.myretail.price.dao.PriceDao;
import com.sunil.myretail.price.exception.PriceUpdateException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;


import com.sunil.myretail.model.Price;

import java.util.Date;

import static org.junit.Assert.*;

public class PriceServiceTest {

    PriceService classUnderTest;
    @Mock
    PriceDao priceDao;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new PriceService(priceDao);
    }

    @Test
    public void insertPrice() {

        Price price = new Price();
        price.setCurrencyCode("USD");
        price.setPriceValue("1.1");
        String productId = "1234";

        classUnderTest.insertPrice(price, productId);

        ArgumentCaptor<com.sunil.myretail.price.domain.Price> priceArgumentCaptor = ArgumentCaptor.forClass(com.sunil.myretail.price.domain.Price.class);

        verify(priceDao).insertPrice(priceArgumentCaptor.capture());

        assertNotNull(priceArgumentCaptor.getValue().getCreatedDatetime());
        assertNotNull(priceArgumentCaptor.getValue().getUpdatedDatetime());
        assertEquals("1234",priceArgumentCaptor.getValue().getProductId());
        assertEquals("USD",priceArgumentCaptor.getValue().getCurrencyCode());
        assertEquals("1.1",priceArgumentCaptor.getValue().getProductPrice());

        assertTrue(priceArgumentCaptor.getValue().getCreatedDatetime().equals(priceArgumentCaptor.getValue().getUpdatedDatetime()));

    }

    @Test
    public void updatePrice() {
        Price price = new Price();
        price.setCurrencyCode("USD");
        price.setPriceValue("1.1");
        String productId = "1234";

        doNothing().when(priceDao).updatePrice(any(com.sunil.myretail.price.domain.Price.class));

        classUnderTest.updatePrice(price, productId);
    }

    @Test(expected = PriceUpdateException.class)
    public void updatePriceThrowsException() {
        Price price = new Price();
        price.setCurrencyCode("USD");
        price.setPriceValue("1.1");
        String productId = "1234";

        doThrow(new PriceUpdateException("1234" , new RuntimeException(""))).when(priceDao).updatePrice(any(com.sunil.myretail.price.domain.Price.class));

        classUnderTest.updatePrice(price, productId);
    }

    @Test
    public void getPrice() {

        com.sunil.myretail.price.domain.Price priceDomain = new com.sunil.myretail.price.domain.Price();
        priceDomain.setProductId("1234");
        priceDomain.setCurrencyCode("USD");
        priceDomain.setProductPrice("1.1");
        Date date = new Date();
        priceDomain.setCreatedDatetime(date);
        priceDomain.setUpdatedDatetime(date);

        String productId = "1234";
        when(priceDao.getPrice("1234")).thenReturn(priceDomain);

        com.sunil.myretail.price.domain.Price priceValue = classUnderTest.getPrice(productId);
        assertNotNull(priceValue);


    }
}