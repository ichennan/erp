package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.PurchaseDTO;
import com.fiveamazon.erp.dto.PurchaseDetailDTO;
import com.fiveamazon.erp.dto.PurchaseProductSearchDTO;
import com.fiveamazon.erp.dto.UploadSupplierDeliveryDTO;
import com.fiveamazon.erp.entity.PurchaseDetailPO;
import com.fiveamazon.erp.entity.PurchasePO;
import com.fiveamazon.erp.entity.PurchaseViewPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface PurchaseService {
    Long countAll();

    PurchasePO getById(Integer id);

    PurchaseDetailPO getDetailById(Integer id);

    List<PurchaseViewPO> findAll();

    PurchasePO save(PurchasePO purchasePO);

    PurchaseDetailPO saveDetail(PurchaseDetailPO purchaseDetailPO);

    List<PurchaseDetailPO> findAllDetail(Integer purchaseId);

    PurchasePO save(PurchaseDTO purchaseDTO);

    PurchaseDetailPO saveDetail(PurchaseDetailDTO purchaseDetailDTO);

    void createByExcel(UploadSupplierDeliveryDTO uploadSupplierDeliveryDTO);

    List<PurchaseDetailPO> findAllProducts(PurchaseProductSearchDTO purchaseProductSearchDTO);
}
