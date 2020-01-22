package com.sunil.myretail.exception;

import com.sunil.myretail.price.exception.PriceUpdateException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.*;

public class MyRetailGlobalExceptionHandlerTest {

    MyRetailGlobalExceptionHandler classUnderTest;

    @Before
    public void setUp() throws Exception {
        classUnderTest = new MyRetailGlobalExceptionHandler();
    }

    @Test
    public void handleProductNotFoundException() {
        classUnderTest.handleProductNotFoundException(
                new RedSkyIntegrationProductNotFoundException("1234", new RuntimeException("")),
                new MockHttpServletRequest("GET", "/products/1234"));
    }

    @Test
    public void handleBadProductException() {

        classUnderTest.handleBadProductException(
                new ProductIdValidationException("ABCD"),
                new MockHttpServletRequest("GET", "/products/ABCD"));
    }

    @Test
    public void handleAppIntegrationException() {
        classUnderTest.handleAppIntegrationException(
                new PriceUpdateException("1234", new RuntimeException("")),
                new MockHttpServletRequest("PUT", "/products/1234"));
    }

    @Test
    public void defaultErrorHandler() {
        classUnderTest.defaultErrorHandler(
                new NullPointerException("somewhere in the code"),
                new MockHttpServletRequest("PUT", "/products/1234"));
    }
}