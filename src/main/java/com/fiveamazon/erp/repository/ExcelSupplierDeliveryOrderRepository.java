package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import com.fiveamazon.erp.entity.PurchasePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelSupplierDeliveryOrderRepository extends JpaRepository<ExcelSupplierDeliveryOrderPO, Integer> {
    List<ExcelSupplierDeliveryOrderPO> findByExcelId(Integer excelId);
    ExcelSupplierDeliveryOrderPO getByExcelIdAndDingdanhao(Integer excelId, String dingdanhao);
}
