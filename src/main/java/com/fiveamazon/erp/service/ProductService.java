package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.entity.ProductVO;
import com.fiveamazon.erp.entity.StorePO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ProductService {
    Long countAll();

    ProductPO getById(Integer id);

    ProductPO getByName(String name);

    List<ProductPO> findAll(String sort);

    List<ProductVO> findAllByView();

    List<ProductPO> findEnablePacket();

    ProductPO save(ProductPO productPO);

    ProductPO save(ProductDTO productDTO);

    ProductPO updatePurchasePrice(ProductDTO productDTO);

    List<StorePO> findAllStore();

    StorePO getStoreById(Integer storeId);

    void updatePurchasePrice(Integer productId, BigDecimal purchasePrice);
}
