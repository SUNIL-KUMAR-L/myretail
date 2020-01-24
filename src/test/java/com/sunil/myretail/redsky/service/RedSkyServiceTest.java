package com.sunil.myretail.redsky.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunil.myretail.redsky.dao.RedSkyDao;
import com.sunil.myretail.redsky.domain.RedSky;
import com.sunil.myretail.redsky.domain.RedSkyTest;

import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedSkyServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RedSky redSky;

    RedSkyService classUnderTest;

    @Mock
    RedSkyDao redSkyDao;

    @BeforeEach
    public void setup() {
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

        classUnderTest = new RedSkyService(redSkyDao);
    }

    @Test
    public void getProductDetails() {
        when(redSkyDao.getProductDetails("13860428")).thenReturn(redSky);
        RedSky redskyResponse =  classUnderTest.getProductDetails("13860428");
        assertNotNull(redskyResponse);
        assertEquals(redSky.getProduct().getItem().getTcin(), redskyResponse.getProduct().getItem().getTcin());
    }
}