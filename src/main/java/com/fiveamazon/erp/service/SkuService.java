package com.fiveamazon.erp.service;


import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SkuService {
    void save(ProductDTO productDTO);
    JSONObject getByProductId(Integer productId);
}
