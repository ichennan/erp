package com.fiveamazon.erp.service;


import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.dto.SkuInfoDTO;
import com.fiveamazon.erp.entity.*;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SkuService {
    SkuInfoPO getById(Integer id);
    SkuInfoVO getViewById(Integer id);
    SkuInfoPO save(SkuInfoPO skuInfoPO);
    SkuInfoPO save(SkuInfoDTO skuInfoDTO);
    List<SkuInfoPO> findByProductId(Integer productId);
    List<SkuInfoPO> findBySku(String sku);
    List<SkuInfoVO> findAll();
    //
    JSONObject getSkuShipmentObject(Integer skuId);
    JSONObject getSkuElseShipmentObject(Integer productId, Integer skuId);
    JSONObject getProductPurchaseObject(Integer productId);
    JSONObject getProductInventoryObjectBySkuId(Integer skuId);
}
