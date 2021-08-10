package com.fiveamazon.erp.repository.excel;


import com.fiveamazon.erp.entity.excel.ExcelTransactionDetailPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelTransactionDetailRepository extends JpaRepository<ExcelTransactionDetailPO, Integer> {
    List<ExcelTransactionDetailPO> findByExcelId(Integer excelId);

}
