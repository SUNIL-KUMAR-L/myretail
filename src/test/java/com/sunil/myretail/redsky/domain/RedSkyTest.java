package com.sunil.myretail.redsky.domain;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedSkyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RedSky redSky;

    @BeforeEach
    public void setup() {
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
    }

    @Test
    public void testRedSkyObjectJsonString() throws Exception{

        assertEquals("13860428", redSky.getProduct().getItem().getTcin());

        assertEquals("The Big Lebowski (Blu-ray)", redSky.getProduct().getItem().getProductDescription().getTitle());

       String redSkyJson = objectMapper.writeValueAsString(redSky);

       assertTrue(redSkyJson.contains("\"tcin\""));
       assertTrue(redSkyJson.contains("\"product_description\""));

       assertTrue(redSkyJson.contains("13860428"));
       assertTrue(redSkyJson.contains("\"The Big Lebowski (Blu-ray)\""));

       assertTrue(redSkyJson.contains("\"title\""));
       assertTrue(redSkyJson.contains("\"item\""));
       assertTrue(redSkyJson.contains("\"product\""));

    }

    @Test
    public void testRedSkyEquals() throws Exception{

        ProductDescription productDescription = new ProductDescription();
        productDescription.setTitle("The Big Lebowski (Blu-ray)");

        Item item = new Item();
        item.setProductDescription(productDescription);
        item.setTcin("13860428");

        Product product = new Product();
        product.setItem(item);

        RedSky newRedSky = new RedSky();
        newRedSky.setProduct(product);

        assertEquals(newRedSky, redSky);

    }



}