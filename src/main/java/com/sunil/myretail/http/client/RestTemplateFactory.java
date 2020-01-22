package com.sunil.myretail.http.client;

import org.springframework.web.client.RestTemplate;

public interface RestTemplateFactory {
    RestTemplate create(int connectTimeoutMs, int readTimeoutMs, int writeTimeoutMs);
}
