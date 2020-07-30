package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ExcelSupplierDeliveryPO;
import com.fiveamazon.erp.entity.ProductPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelSupplierDeliveryRepository extends JpaRepository<ExcelSupplierDeliveryPO, Integer> {
}
