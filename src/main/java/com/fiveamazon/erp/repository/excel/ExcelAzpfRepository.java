package com.fiveamazon.erp.repository.excel;


import com.fiveamazon.erp.entity.excel.ExcelAzpfPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelAzpfRepository extends JpaRepository<ExcelAzpfPO, Integer> {
    @Modifying
    @Query("update ExcelAzpfPO p set p.status = -1 where p.sku = :sku and p.status  != -1")
    void disableBySku(String sku);

}
