package com.sunil.myretail.exception;

import com.sunil.myretail.price.exception.PriceUpdateException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyRetailGlobalExceptionHandlerTest {

    MyRetailGlobalExceptionHandler classUnderTest;

    @BeforeEach
    public void setUp() throws Exception {
        classUnderTest = new MyRetailGlobalExceptionHandler();
    }

    @Test
    public void handleProductNotFoundException() {
        final ResponseEntity<MyRetailApiError> myRetailApiErrorResponseEntity =
        classUnderTest.handleProductNotFoundException(
                new RedSkyIntegrationProductNotFoundException("1234", new RuntimeException("")),
                new MockHttpServletRequest("GET", "/products/1234"));
        assertEquals("Unable to fetch data for product : 1234" , myRetailApiErrorResponseEntity.getBody().getError());
        assertEquals(404 , myRetailApiErrorResponseEntity.getBody().getStatus());
    }

    @Test
    public void handleBadProductException() {
        final ResponseEntity<MyRetailApiError> myRetailApiErrorResponseEntity =
        classUnderTest.handleBadProductException(
                new ProductIdValidationException("ABCD"),
                new MockHttpServletRequest("GET", "/products/ABCD"));
        assertEquals("ABCD" , myRetailApiErrorResponseEntity.getBody().getError());
        assertEquals(400 , myRetailApiErrorResponseEntity.getBody().getStatus());
    }

    @Test
    public void handleAppIntegrationException() {
        final ResponseEntity<MyRetailApiError> myRetailApiErrorResponseEntity =
        classUnderTest.handleAppIntegrationException(
                new PriceUpdateException("1234", new RuntimeException("")),
                new MockHttpServletRequest("PUT", "/products/1234"));
        assertEquals("Unable to update Price for productId :1234" , myRetailApiErrorResponseEntity.getBody().getError());
        assertEquals(500 , myRetailApiErrorResponseEntity.getBody().getStatus());
    }

    @Test
    public void defaultErrorHandler() {
        final ResponseEntity<MyRetailApiError> myRetailApiErrorResponseEntity =
                classUnderTest.defaultErrorHandler(
                new NullPointerException("somewhere in the code"),
                new MockHttpServletRequest("PUT", "/products/1234"));
        assertEquals("Something went wrong..." , myRetailApiErrorResponseEntity.getBody().getError());
        assertEquals(500 , myRetailApiErrorResponseEntity.getBody().getStatus());
    }
}