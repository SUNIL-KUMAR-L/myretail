package com.sunil.myretail.config;

import com.sunil.myretail.http.client.RestTemplateFactory;
import com.sunil.myretail.http.client.RestTemplateFactoryOkHttp;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {

    @Bean
    RestTemplateFactory restTemplateFactory(OkHttpClient okHttpClient) {
        return new RestTemplateFactoryOkHttp(okHttpClient);
    }

    @Bean
    public OkHttpClient defaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .cookieJar(CookieJar.NO_COOKIES)
                .connectTimeout(1000, TimeUnit.MILLISECONDS)
                .readTimeout(1000, TimeUnit.MILLISECONDS)
                .writeTimeout(1000, TimeUnit.MILLISECONDS)
                .build();
    }
}
