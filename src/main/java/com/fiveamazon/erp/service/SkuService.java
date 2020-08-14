package com.fiveamazon.erp.service;


import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.InventorySnapshotPO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuViewPO;
import com.fiveamazon.erp.entity.SnapshotSkuInventoryPO;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SkuService {
    SkuInfoPO getById(Integer id);
    void save(SkuInfoPO skuInfoPO);
    void save(ProductDTO productDTO);
    List<SkuInfoPO> findByProductId(Integer productId);
    List<SkuInfoPO> findBySku(String sku);
    List<SkuViewPO> findAll();
    List<SnapshotSkuInventoryPO> findSnapshotBySkuId(Integer skuId);
}
