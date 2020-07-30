package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderDetailPO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelSupplierDeliveryOrderDetailRepository extends JpaRepository<ExcelSupplierDeliveryOrderDetailPO, Integer> {
    List<ExcelSupplierDeliveryOrderDetailPO> findByExcelId(Integer excelId);
}
