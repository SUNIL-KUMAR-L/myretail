package com.sunil.myretail.price.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.sunil.myretail.cassandra.CassandraConfig;

import static org.mockito.Mockito.*;

import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.price.exception.GetPriceException;
import com.sunil.myretail.price.exception.PriceCreateException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PriceDaoCassandraTest {

    @Mock
    CassandraConfig config;
    @Mock
    MappingManager mappingManager;
    @Mock
    Session session;
    @Mock
    Mapper<Price> priceMapper;
    @Mock
    PreparedStatement updatePriceStatement;

    PriceDaoCassandra classUnderTest;

    Price price;


    @Mock
    Cluster cluster;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        price = new Price();
        price.setProductPrice("123.44");
        price.setCurrencyCode("USD");
        Date date = new Date();
        price.setCreatedDatetime(date);
        price.setUpdatedDatetime(date);
        price.setProductId("1234");

        when(config.mappingManager()).thenReturn(mappingManager);

        when(mappingManager.mapper(Price.class)).thenReturn(priceMapper);

        when(config.session()).thenReturn(session);

        when(session.prepare(anyString())).thenReturn(updatePriceStatement);


        classUnderTest = new PriceDaoCassandra(config);

    }

    @Test
    public void insertPrice() {
        classUnderTest.insertPrice(price);
        verify(priceMapper, times(1)).save(any(Price.class));
    }

    @Test
    public void insertPriceFailed() {
        doThrow(new PriceCreateException("1234", new RuntimeException())).when(priceMapper).save(any(Price.class));
        try{
            classUnderTest.insertPrice(price);
        } catch (Exception exp){
            assertEquals("Unable to insert Price for productId :1234", exp.getMessage());
        }
    }

    @Test
    public void updatePrice() {
        classUnderTest.updatePrice(price);
    }

    @Test
    public void updatePriceFailed() {
        try{
            classUnderTest.updatePrice(null);
        } catch (Exception exp){
               assertNotNull(exp);
        }
    }

    @Test
    public void getPrice() {
        when(priceMapper.get("1234")).thenReturn(price);
        classUnderTest.getPrice("1234");
        verify(priceMapper, times(1)).get("1234");
    }

    @Test
    public void getPriceFailed() {
        doThrow(new GetPriceException("3333", new RuntimeException())).when(priceMapper).get(eq("3333"));
        try{
            classUnderTest.getPrice("3333");
        } catch (Exception exp){
            assertEquals("Unable to get Price for productId :3333", exp.getMessage());
        }
    }
}