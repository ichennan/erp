package com.fiveamazon.erp.repository.excel;


import com.fiveamazon.erp.entity.excel.ExcelTransactionPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelTransactionRepository extends JpaRepository<ExcelTransactionPO, Integer> {
    ExcelTransactionPO getById(Integer id);
}
