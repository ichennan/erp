package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.ExcelFbaPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryPO;
import com.fiveamazon.erp.epo.ExcelFbaRowEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderDetailEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ExcelService {
    void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList);
    void insertExcelSupplierDeliveryOrderDetail(Integer excelId, List<ExcelSupplierDeliveryOrderDetailEO> excelSupplierDeliveryOrderDetailEOList);
    void insertFbaPackList(Integer excelId, List<ExcelFbaRowEO> excelFbaRow);
    Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO);
    Integer saveExcelFba(ExcelFbaPO excelFbaPO);
    List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId);
    List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId);
}
