package com.sunil.myretail.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.sunil.myretail.cassandra.CassandraConfig;
import com.sunil.myretail.controller.MyRetailController;
import com.sunil.myretail.model.Product;
import com.sunil.myretail.price.dao.PriceDao;
import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.price.service.PriceService;
import com.sunil.myretail.redsky.dao.RedSkyDao;
import com.sunil.myretail.service.MyRetailService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@Slf4j
public class MyRetailApplicationRunner {

    ObjectMapper mapper = new ObjectMapper();

    @Bean
    @ConditionalOnProperty(name="run.MyRetailApplicationRunner", havingValue="true")
    ApplicationRunner getApplicationRunner(RedSkyDao redSkyDao,
                                           CassandraConfig cassandraConfig,
                                           PriceDao priceDao,
                                           PriceService priceService,
                                           MyRetailService myRetailService,
                                           MyRetailController myRetailController) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {

                try {
                    log.info("run ApplicationRunner");

                    if (null != cassandraConfig.session()) {
                        log.info("Cassandra Connection Success...");
                    }

                    log.info(redSkyDao.getProductDetails("13860428").toString());

                    log.info(mapper.writeValueAsString(redSkyDao.getProductDetails("13860428")));

                    Price price = new Price();
                    price.setProductId("13860428");
                    price.setProductPrice("13.49");
                    price.setCurrencyCode("USD");

                    Date date = new Date(System.currentTimeMillis());
                    price.setCreatedDatetime(date);
                    price.setUpdatedDatetime(date);

                    priceDao.insertPrice(price);

                    Thread.sleep(200);

                    Price priceFromDB = priceDao.getPrice("13860428");

                    log.info(priceFromDB.toString());

                    log.info(priceService.getPrice("13860428").toString());
                    log.info("========  myRetailService.getProduct START ==========");
                    Product product = myRetailService.getProduct("13860428");
                    log.info(mapper.writeValueAsString(product));
                    log.info("========  myRetailService.getProduct END ==========");

                    com.sunil.myretail.model.Price priceModel = new com.sunil.myretail.model.Price();
                    priceModel.setCurrencyCode("USD");
                    priceModel.setPriceValue("14.99");
                    priceService.updatePrice(priceModel, "13860428");

                    log.info("========  myRetailService.getProduct START ==========");
                    Product product123 = myRetailService.getProduct("13860428");
                    log.info(mapper.writeValueAsString(product123));
                    log.info("========  myRetailService.getProduct END ==========");


                    //product available : 13860428, 54643166, 53741618, 53536794 ,76544201
                    //priceDao.insertPrice(buildPrice("13860428", "14.99", "USD"));
                    priceDao.insertPrice(buildPrice("54643166", "101.99", "USD"));
                    priceDao.insertPrice(buildPrice("53741618", "222.99", "USD"));
                    priceDao.insertPrice(buildPrice("53536794", "199.90", "USD"));
                    priceDao.insertPrice(buildPrice("76544201", "57.99", "USD"));

                    log.info(redSkyDao.getProductDetails("54643166").toString());
                    log.info(redSkyDao.getProductDetails("53741618").toString());
                    log.info(redSkyDao.getProductDetails("53536794").toString());
                    log.info(redSkyDao.getProductDetails("76544201").toString());

//                log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("13860428")));
//                log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("54643166")));
//                log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("53741618")));
//                log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("53536794")));
//                log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("76544201")));

                    Product productBluRay = new Product();
                    productBluRay.setProductId("13860428");
                    com.sunil.myretail.model.Price priceModelBluRay = new com.sunil.myretail.model.Price();
                    priceModelBluRay.setCurrencyCode("USD");
                    priceModelBluRay.setPriceValue("13.99");
                    productBluRay.setPrice(priceModelBluRay);

                    log.info(mapper.writeValueAsString(myRetailController.updateProductPrice("13860428", productBluRay)));

                    log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("13860428")));
                    log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("54643166")));
                    log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("53741618")));
                    log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("53536794")));
                    log.info(mapper.writeValueAsString(myRetailController.getProductPriceDetails("76544201")));


                    //Product not found : 15117729, 16483589, 16696652, 16752456, 15643793
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        };
    }

    private Price buildPrice(String productId, String priceValue, String currenyCode) {
        Price price = new Price();
        Date date = new Date();
        price.setProductId(productId);
        price.setProductPrice(priceValue);
        price.setCurrencyCode(currenyCode);
        price.setCreatedDatetime(date);
        price.setUpdatedDatetime(date);
        return price;
    }
}
