package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryPO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderDetailEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;
import com.fiveamazon.erp.epo.TestEpo2;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ExcelService {
    void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList);
    void insertExcelSupplierDeliveryOrderDetail(Integer excelId, List<ExcelSupplierDeliveryOrderDetailEO> excelSupplierDeliveryOrderDetailEOList);
    Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO);
    List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId);
    List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId);
}
