package com.sunil.myretail.http.client;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


public class RestTemplateFactoryOkHttp implements RestTemplateFactory {

    private final OkHttpClient okHttpClient;

    public RestTemplateFactoryOkHttp(OkHttpClient okHttpClient) {

        this.okHttpClient = okHttpClient.newBuilder()
                .cookieJar(CookieJar.NO_COOKIES)
                .build();
    }

    @Override
    public RestTemplate create(int connectTimeoutMs, int readTimeoutMs, int writeTimeoutMs) {
        validate(connectTimeoutMs > 0, "connectTimeoutMs must be > 0");
        validate(readTimeoutMs > 0, "readTimeoutMs must be > 0");
        validate(writeTimeoutMs > 0, "writeTimeoutMs must be > 0");

        final OkHttp3ClientHttpRequestFactory factory = new OkHttp3ClientHttpRequestFactory(okHttpClient);
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);
        factory.setWriteTimeout(writeTimeoutMs);


        final RestTemplate restTemplate = new RestTemplate(factory);

        return restTemplate;
    }

    private static void validate(boolean condition, String validationMessage) {
        if(!condition){
            throw new IllegalArgumentException(validationMessage);
        }
    }
}
