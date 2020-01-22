package com.sunil.myretail.config;

import com.sunil.myretail.cassandra.CassandraConfig;
import com.sunil.myretail.http.client.RestTemplateFactory;
import com.sunil.myretail.price.dao.PriceDao;
import com.sunil.myretail.price.dao.PriceDaoCassandra;
import com.sunil.myretail.price.service.PriceService;
import com.sunil.myretail.redsky.dao.RedSkyDao;
import com.sunil.myretail.redsky.service.RedSkyService;
import com.sunil.myretail.service.MyRetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;


@Configuration
public class MyRetailConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateFactory restTemplateFactory, Environment environment) {
        return restTemplateFactory.create(
                environment.getProperty("myretail.redsky.connectTimeoutMs", Integer.class, 1000),
                environment.getProperty("myretail.redsky.readTimeoutMs", Integer.class, 1000),
                environment.getProperty("myretail.redsky.writeTimeoutMs", Integer.class, 1000)
        );
    }

    @Bean
    public RedSkyDao redSkyDao(RestTemplate restTemplate, Environment environment) {
        String redskyUrlTemplate = environment.getProperty("redskyUrlTemplate");
        return new RedSkyDao(restTemplate, redskyUrlTemplate);
    }

    @Bean
    public RedSkyService getRedSkyService(RedSkyDao redSkyDao){
        return new RedSkyService(redSkyDao);
    }

    @Bean
    public CassandraConfig getCassandraConfig(Environment environment) {
        return new CassandraConfig(environment);
    }


    @Bean
    public PriceDaoCassandra getPriceDao(CassandraConfig cassandraConfig){
        return new PriceDaoCassandra(cassandraConfig);
    }

    @Bean
    public PriceService getPriceService(PriceDao priceDao){
        return new PriceService(priceDao);
    }

    @Bean
    public MyRetailService getMyRetailService(RedSkyService redSkyService, PriceService priceService) {
        return new MyRetailService(redSkyService, priceService);
    }
}
