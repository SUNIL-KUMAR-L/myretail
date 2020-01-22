package com.sunil.myretail.price.domain;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Table(name = "product_price")
public class Price implements Serializable {

    @PartitionKey
    @JsonProperty("productId")
    @Column(name="product_id")
    private String productId;

    @JsonProperty("productPrice")
    @Column(name="product_price")
    private String productPrice;

    @JsonProperty("currencyCode")
    @Column(name="currency_code")
    private String currencyCode;

    @JsonProperty("createdDatetime")
    @Column(name = "created_datetime")
    private Date createdDatetime;

    @JsonProperty("updatedDatetime")
    @Column(name = "updated_datetime")
    private Date updatedDatetime;
}
