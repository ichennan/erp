package com.fiveamazon.erp.service;


import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.epo.SupplierDeliveryOrderEpo;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ExcelService {
//    void save(ProductDTO productDTO);
//    JSONObject getByProductId(Integer productId);
    void insert(List<SupplierDeliveryOrderEpo> supplierDeliveryOrderEpoList);
    void test2(List<TestEpo2> testEpoList);
}
