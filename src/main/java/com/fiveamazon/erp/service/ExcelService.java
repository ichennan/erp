package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.entity.excel.ExcelTransactionDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelTransactionPO;
import com.fiveamazon.erp.epo.*;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ExcelService {
    void insertTransactionRow(Integer excelId, List<ExcelTransactionRowEO> excelTransactionRowEOList);
    void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList);
    void insertExcelSupplierDeliveryOrderDetail(Integer excelId, List<ExcelSupplierDeliveryOrderDetailEO> excelSupplierDeliveryOrderDetailEOList);
    void insertFbaPackList(Integer excelId, List<ExcelFbaRowEO> excelFbaRow);
    void insertFbatsvPackList(Integer excelId, List<ExcelFbatsvRowEO> excelFbatsvRow);
    Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO);
    Integer saveExcelFba(ExcelFbaPO excelFbaPO);
    Integer saveExcelTransaction(ExcelTransactionPO excelTransactionPO);
    List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId);
    List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId);
    ExcelFbaPO getFbaByExcelId(Integer excelId);
    ExcelTransactionPO getTransactionByExcelId(Integer excelId);
    List<ExcelFbaPackListPO> findFbaPackListByExcelId(Integer excelId);
    List<ExcelTransactionDetailPO> findTransactionDetailByExcelId(Integer excelId);
    ExcelSupplierDeliveryOrderPO getExcelSupplierDeliveryOrderByExcelIdAndDingdanhao(Integer excelId, String dingdanghao);
}
