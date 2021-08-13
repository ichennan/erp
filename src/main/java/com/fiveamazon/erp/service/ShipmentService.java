package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.*;
import com.fiveamazon.erp.entity.*;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ShipmentService {
    Long countAll();

    ShipmentPO getById(Integer id);

    ShipmentDetailPO getDetailById(Integer id);

    List<ShipmentViewPO> findAll();

    ShipmentPO save(ShipmentPO shipmentPO);

    ShipmentDetailPO saveDetail(ShipmentDetailPO shipmentDetailPO);

    List<ShipmentDetailPO> findAllDetail(Integer shipmentId);

    ShipmentPO save(ShipmentDTO shipmentDTO);

    ShipmentDetailPO saveDetail(ShipmentDetailDTO shipmentDetailDTO);

    ShipmentPO saveByOverseaDetail(OverseaDetailPO overseaDetailPO, OverseaDetailDTO overseaDetailDTO);

    void createByExcel(UploadFbaDTO uploadFbaDTO);

    List<ShipmentProductViewPO> findAllProducts(ShipmentProductSearchDTO searchDTO);

    Long countBySkuId(Integer skuId);

    List<ShipmentPO> findByDate(String dateFrom, String dateTo, Integer storeId);
}
