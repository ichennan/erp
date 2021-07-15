package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.OverseaDTO;
import com.fiveamazon.erp.dto.OverseaDetailDTO;
import com.fiveamazon.erp.entity.OverseaDetailPO;
import com.fiveamazon.erp.entity.OverseaPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface OverseaService {
    Long countAll();

    OverseaPO getById(Integer id);

    OverseaDetailPO getDetailById(Integer id);

    List<OverseaPO> findAll();

    OverseaPO save(OverseaDTO dto);

    OverseaDetailPO saveDetail(OverseaDetailDTO dto);

    List<OverseaDetailPO> findAllDetail(Integer overseaId);
//
//    ShipmentPO save(ShipmentDTO shipmentDTO);
//
//    ShipmentDetailPO saveDetail(ShipmentDetailDTO shipmentDetailDTO);
//
//    void createByExcel(UploadFbaDTO uploadFbaDTO);
//
//    List<ShipmentProductViewPO> findAllProducts(ShipmentProductSearchDTO searchDTO);
//
//    Long countBySkuId(Integer skuId);
}
