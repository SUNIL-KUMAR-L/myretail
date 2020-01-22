package com.sunil.myretail.controller;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;

import com.sunil.myretail.cassandra.CassandraConfig;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static  org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class HealthControllerTest {

    HealthController classUnderTest;

    @Mock
    CassandraConfig cassandraConfig;

    @Mock
    Session session;

    @Mock
    ResultSet resultSet;

    @Mock
    Row row;


    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        when(cassandraConfig.session()).thenReturn(session);
        when(session.execute(anyString())).thenReturn(resultSet);
        when(resultSet.one()).thenReturn(row);

        classUnderTest = new HealthController();
        classUnderTest.config = cassandraConfig;
    }

    @Test
    public void getHealth() {
        ResponseEntity responseEntity =  classUnderTest.getHealth();
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void dbHealthSuccess() {
        ResponseEntity responseEntity = classUnderTest.getDBHealth();
        assertEquals(200, responseEntity.getStatusCodeValue());
    }

    @Test
    public void dbHealthNoHostException() {
        Map<InetSocketAddress, Throwable> errors = new HashMap<>();
        errors.put(new InetSocketAddress("localhost", 9042), new RuntimeException(""));
        when(cassandraConfig.session()).thenThrow(new NoHostAvailableException(errors));
        ResponseEntity responseEntity = classUnderTest.getDBHealth();
        assertEquals(500, responseEntity.getStatusCodeValue());
    }

    @Test
    public void dbHealthOtherException() {
        when(resultSet.one()).thenThrow(new RuntimeException(""));
        ResponseEntity responseEntity = classUnderTest.getDBHealth();
        assertEquals(500, responseEntity.getStatusCodeValue());
    }

}