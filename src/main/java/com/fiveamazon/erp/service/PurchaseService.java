package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.*;
import com.fiveamazon.erp.dto.download.PurchaseDownloadDTO;
import com.fiveamazon.erp.entity.PurchaseDetailPO;
import com.fiveamazon.erp.entity.PurchasePO;
import com.fiveamazon.erp.entity.PurchaseProductViewPO;
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

    List<PurchaseViewPO> findAll(PurchaseSearchDTO purchaseSearchDTO);

    PurchasePO save(PurchasePO purchasePO);

    PurchaseDetailPO saveDetail(PurchaseDetailPO purchaseDetailPO);

    List<PurchaseDetailPO> findAllDetail(Integer purchaseId);

    PurchasePO save(PurchaseDTO purchaseDTO);

    PurchaseDetailPO saveDetail(PurchaseDetailDTO purchaseDetailDTO);

    void createByExcel(UploadSupplierDeliveryDTO uploadSupplierDeliveryDTO);

    List<PurchaseProductViewPO> findAllProducts(PurchaseProductSearchDTO purchaseProductSearchDTO);

    List<String> findSupplierList();

    List<PurchaseDownloadDTO> download(PurchaseSearchDTO searchDTO);
}
