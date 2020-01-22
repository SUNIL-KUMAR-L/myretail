package com.sunil.myretail.price.dao;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.mapping.Mapper;
import com.sunil.myretail.cassandra.CassandraConfig;
import com.sunil.myretail.price.domain.Price;
import com.sunil.myretail.price.exception.GetPriceException;
import com.sunil.myretail.price.exception.PriceCreateException;
import com.sunil.myretail.price.exception.PriceUpdateException;

import java.util.Date;

public class PriceDaoCassandra implements PriceDao {


    private String TABLE_NAME =  "product_price";
    private String UPDATE_PRICE = "update "+ TABLE_NAME +" set currency_code=?, product_price=?, updated_datetime=? where product_id=?";
    private PreparedStatement updatePriceStatement;
    private Mapper<Price> priceMapper;
    private CassandraConfig cassandraConfig;

    public PriceDaoCassandra(CassandraConfig cassandraConfig) {
        this.cassandraConfig = cassandraConfig;
        priceMapper = cassandraConfig.mappingManager().mapper(Price.class);
        updatePriceStatement = cassandraConfig.session().prepare(UPDATE_PRICE);
    }

    public void insertPrice(Price price) {
        try {
            priceMapper.save(price);
        } catch (Exception exp) {
            throw  new PriceCreateException(price.getProductId(), exp);
        }
    }

    public void updatePrice(Price priceDomain) {
        try {
            Date date = new Date();
            priceDomain.setUpdatedDatetime(date);

            BoundStatement bs = updatePriceStatement.bind(  priceDomain.getCurrencyCode(),
                                                            priceDomain.getProductPrice(),
                                                            priceDomain.getUpdatedDatetime(),
                                                            priceDomain.getProductId());

            cassandraConfig.session().execute(bs);
        } catch (Exception exp){
            throw new PriceUpdateException(priceDomain.getProductId(), exp);
        }
    }

    public Price getPrice(String productId) {
        try {
            return priceMapper.get(productId);
        } catch (Exception exp) {
            throw new GetPriceException(productId, exp);
        }
    }

}
