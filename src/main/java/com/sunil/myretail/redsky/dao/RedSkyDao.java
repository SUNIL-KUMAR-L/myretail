package com.sunil.myretail.redsky.dao;

import com.sunil.myretail.redsky.domain.RedSky;

import com.sunil.myretail.redsky.exception.RedSkyIntegrationClientErrorException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationException;
import com.sunil.myretail.redsky.exception.RedSkyIntegrationServerErrorException;

import com.sunil.myretail.redsky.exception.RedSkyIntegrationProductNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class RedSkyDao {

    final RestTemplate restTemplate;
    final String url;

    public RedSkyDao(RestTemplate restTemplate, String url) {
        this.restTemplate = restTemplate;
        this.url = url;
    }

    public RedSky getProductDetails(String productId) {
        String redSkyUrl =  url.replaceFirst("productId", productId);
        log.debug("redsky URL="+redSkyUrl);
        RedSky redSky = null;
        try {
            redSky = restTemplate.getForObject(redSkyUrl, RedSky.class);
        } catch (RestClientResponseException restClientResponseException) {
            int httpStatusCode = restClientResponseException.getRawStatusCode();
            HttpStatus.Series series = HttpStatus.Series.valueOf(httpStatusCode);
            switch(series) {
                case CLIENT_ERROR :
                    if(HttpStatus.NOT_FOUND.value() == httpStatusCode) {
                        throw new RedSkyIntegrationProductNotFoundException(productId, restClientResponseException);
                    }
                    throw new RedSkyIntegrationClientErrorException(productId, restClientResponseException);
                case SERVER_ERROR:
                    throw new RedSkyIntegrationServerErrorException(productId, restClientResponseException);
            }
            throw new RedSkyIntegrationException(productId, restClientResponseException);
        }

        return redSky;
    }

}
