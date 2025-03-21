package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.entity.excel.ExcelCarrierBillDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelCarrierBillPO;
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
    Integer saveExcelTransaction(ExcelTransactionPO excelTransactionPO);
    ExcelTransactionPO getTransactionByExcelId(Integer excelId);
    List<ExcelTransactionDetailPO> findTransactionDetailByExcelId(Integer excelId);
    //
    void insertCarrierBillRow(Integer excelId, List<ExcelCarrierBillCainiaoRowEO> excelCarrierBillCainiaoRowEOList);
    Integer saveExcelCarrierBill(ExcelCarrierBillPO excelCarrierBillPO);
    ExcelCarrierBillPO getCarrierBillByExcelId(Integer excelId);
    List<ExcelCarrierBillDetailPO> findCarrierBillDetailByExcelId(Integer excelId);
    //
    void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList);
    void insertExcelSupplierDeliveryOrderDetail(Integer excelId, List<ExcelSupplierDeliveryOrderDetailEO> excelSupplierDeliveryOrderDetailEOList);
    Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO);
    List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId);
    List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId);
    ExcelSupplierDeliveryOrderPO getExcelSupplierDeliveryOrderByExcelIdAndDingdanhao(Integer excelId, String dingdanghao);
    //
    void insertFbaPackList(Integer excelId, List<ExcelFbaRowEO> excelFbaRow);
    void insertFbatsvPackList(Integer excelId, List<ExcelFbatsvRowEO> excelFbatsvRow);
    void insertFbaCsvPackList(Integer excelId, List<ExcelFbaCsvRowEO> excelFbaCsvRow);
    Integer saveExcelFba(ExcelFbaPO excelFbaPO);
    ExcelFbaPO getFbaByExcelId(Integer excelId);
    List<ExcelFbaPackListPO> findFbaPackListByExcelId(Integer excelId);
    //
    void insertAzwsRow(List<ExcelAzwsRowEO> excelAzwsRowEOList);
    void insertAzpfRow(List<ExcelAzpfRowEO> excelAzpfRowEOList);
}
