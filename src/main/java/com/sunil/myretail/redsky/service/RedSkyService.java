package com.sunil.myretail.redsky.service;

import com.sunil.myretail.redsky.dao.RedSkyDao;
import com.sunil.myretail.redsky.domain.RedSky;

public class RedSkyService {

    private RedSkyDao redSkyDao;
    public RedSkyService(RedSkyDao redSkyDao) {
       this.redSkyDao = redSkyDao;
    }

    public RedSky getProductDetails(String productId) {
        return redSkyDao.getProductDetails(productId);
    }
}
