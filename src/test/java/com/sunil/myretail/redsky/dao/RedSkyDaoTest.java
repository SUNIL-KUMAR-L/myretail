package com.sunil.myretail.redsky.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunil.myretail.redsky.domain.RedSky;
import com.sunil.myretail.redsky.domain.RedSkyTest;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationClientErrorException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationServerErrorException;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedSkyDaoTest {

    String url = "https://redsky.target.com/v2/pdp/tcin/productId?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics";

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RedSky redSky;

    @Mock
    RestTemplate  restTemplate;

    RedSkyDao classUnderTest;

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
        classUnderTest = new RedSkyDao(restTemplate, url);
    }

    @Test
    public void getProductDetails() {

        when(restTemplate.getForObject(any(String.class), eq(RedSky.class))).thenReturn(redSky);

        RedSky redSkyResponse = classUnderTest.getProductDetails("13860428");

        ArgumentCaptor<String> redskyUrlCaptor  = ArgumentCaptor.forClass(String.class);

        verify(restTemplate).getForObject(redskyUrlCaptor.capture(), any());

        assertEquals("https://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics",
                redskyUrlCaptor.getValue());


        assertEquals("13860428", redSkyResponse.getProduct().getItem().getTcin());
        assertEquals("The Big Lebowski (Blu-ray)", redSkyResponse.getProduct().getItem().getProductDescription().getTitle());
    }

    @Test
    public void getProductNotFoundException() {

        when(restTemplate.getForObject(any(String.class), eq(RedSky.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertThrows(RedSkyIntegrationProductNotFoundException.class ,() -> classUnderTest.getProductDetails("13860428"));

        ArgumentCaptor<String> redskyUrlCaptor  = ArgumentCaptor.forClass(String.class);

        verify(restTemplate).getForObject(redskyUrlCaptor.capture(), any());

        assertEquals("https://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics",
                redskyUrlCaptor.getValue());

    }

    @Test
    public void getProductIntegrationBadInputException() {

        when(restTemplate.getForObject(any(String.class), eq(RedSky.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(RedSkyIntegrationClientErrorException.class ,() -> classUnderTest.getProductDetails("13860428"));

        ArgumentCaptor<String> redskyUrlCaptor  = ArgumentCaptor.forClass(String.class);

        verify(restTemplate).getForObject(redskyUrlCaptor.capture(), any());

        assertEquals("https://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics",
                redskyUrlCaptor.getValue());

    }

    @Test
    public void getProductIntegrationException() {

        when(restTemplate.getForObject(any(String.class), eq(RedSky.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(RedSkyIntegrationServerErrorException.class ,() -> classUnderTest.getProductDetails("13860428"));

        ArgumentCaptor<String> redskyUrlCaptor  = ArgumentCaptor.forClass(String.class);

        verify(restTemplate).getForObject(redskyUrlCaptor.capture(), any());

        assertEquals("https://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics",
                redskyUrlCaptor.getValue());

    }

    @Test
    public void getProductIntegrationUnknownException() {

        when(restTemplate.getForObject(any(String.class), eq(RedSky.class))).thenThrow(new UnknownHttpStatusCodeException(111,"Unknown", null, null, null));

        assertThrows(RedSkyIntegrationException.class ,() -> classUnderTest.getProductDetails("13860428"));

        ArgumentCaptor<String> redskyUrlCaptor  = ArgumentCaptor.forClass(String.class);

        verify(restTemplate).getForObject(redskyUrlCaptor.capture(), any());

        assertEquals("https://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics",
                redskyUrlCaptor.getValue());

    }
}