package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.*;
import com.fiveamazon.erp.entity.ShipmentDetailPO;
import com.fiveamazon.erp.entity.ShipmentPO;
import com.fiveamazon.erp.entity.ShipmentViewPO;

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

    void createByExcel(UploadFbaDTO uploadFbaDTO);
}
