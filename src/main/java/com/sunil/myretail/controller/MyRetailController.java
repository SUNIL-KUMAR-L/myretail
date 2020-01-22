package com.sunil.myretail.controller;


import com.sunil.myretail.exception.PriceValidationException;
import com.sunil.myretail.exception.ProductIdValidationException;
import com.sunil.myretail.exception.ValidationException;
import com.sunil.myretail.model.Price;
import com.sunil.myretail.model.Product;
import com.sunil.myretail.service.MyRetailService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/myretail/v1/")
@Slf4j
public class MyRetailController {

    private static final String REGEX_PATTERN_NUMBER = "[0-9]+";

    private final NumberFormat formatter = new DecimalFormat("#0.00");

    private MyRetailService retailService;
    public MyRetailController(MyRetailService retailService){
        this.retailService = retailService;
    }

    @GetMapping(path = "/products/{productId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Product> getProductPriceDetails(@PathVariable(name = "productId") String productId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        validate(productId);
        Product product = retailService.getProduct(productId);
        stopWatch.stop();
        log.info("app_event=contoller_exit http_method={} api_name=get_product_price total_time_in_ms={} http_status={} product_id={}",
                HttpMethod.GET,
                stopWatch.getTotalTimeMillis(),
                HttpStatus.OK.value(),
                productId);
        return  new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping(path = "/products/{productId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> updateProductPrice(@PathVariable(name = "productId") String productId,
                                                           @RequestBody Product product) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        validateProductPrice(productId, product);
        Price priceModel = new Price();
        priceModel.setPriceValue(formatPrice(product.getPrice().getPriceValue()));
        priceModel.setCurrencyCode(product.getPrice().getCurrencyCode());
        retailService.updatePrice(priceModel, ""+product.getProductId());
        product.setPrice(priceModel);
        stopWatch.stop();
        log.info("app_event=contoller_exit http_method={} api_name=update_product_price total_time_in_ms={} http_status={} product_id={}",
                HttpMethod.PUT,
                stopWatch.getTotalTimeMillis(),
                HttpStatus.OK.value(),
                productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    private void validate(String productId) {
        if(!Pattern.matches(REGEX_PATTERN_NUMBER, productId)){
            throw new ProductIdValidationException("Invalid productId : " + productId);
        }
    }

    private void validateProduct(Product product){
        if(null == product || null == product.getPrice()){
            throw new ValidationException("Invalid input");
        }
    }

    private void validatePrice(Product product) {
        if(Float.parseFloat(product.getPrice().getPriceValue()) <= 0f) {
            throw new PriceValidationException("Invalid price: "
                    + product.getPrice().getPriceValue() +" for productId:"
                    + product.getProductId());
        }

        if(!("USD".equals(product.getPrice().getCurrencyCode()))) {
            throw new PriceValidationException("Invalid Currency Code:"
                    + product.getPrice().getCurrencyCode()
                    + " for productId:"+ product.getProductId());
        }
    }

    private void validateProductPrice(String productId, Product product) {
        validate(productId);
        validateProduct(product);
        validatePrice(product);
        if(!(productId.equals(product.getProductId()))){
            throw new ValidationException("productId input : " + productId + " is not matching with id :"+ product.getProductId());
        }
    }

    private String formatPrice(String price) {
        return formatter.format(new Float(price));
    }
}

