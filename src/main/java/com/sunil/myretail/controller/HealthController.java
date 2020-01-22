package com.sunil.myretail.controller;

import com.datastax.driver.core.exceptions.NoHostAvailableException;

import com.sunil.myretail.cassandra.CassandraConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/myretail/v1/")
public class HealthController {

    @Autowired
    CassandraConfig config;

    private final String priceQuery = "select product_id from product_price where product_id='1'";

    @GetMapping("/health")
    public ResponseEntity getHealth() {
       return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/dbhealth")
    public ResponseEntity getDBHealth() {
        boolean dbHealthFlag = true;
        try{
            config.session().execute(priceQuery).one();
        } catch (NoHostAvailableException exp) {
            dbHealthFlag = false;
        } catch (Exception exp) {
            dbHealthFlag = false;
        }
        if(dbHealthFlag) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
