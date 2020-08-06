package com.fiveamazon.erp.service;


import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SkuInfoService {
    void save(SkuInfoPO skuInfoPO);
    void save(ProductDTO productDTO);
    JSONObject getByProductId(Integer productId);
    List<SkuInfoPO> findByProductId(Integer productId);
    void test1(List<TestEpo> testEpoList);
    void test2(List<TestEpo2> testEpoList);
    List<SkuInfoPO> findBySku(String sku);
}
