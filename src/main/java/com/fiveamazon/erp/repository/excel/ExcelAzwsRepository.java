package com.fiveamazon.erp.repository.excel;


import com.fiveamazon.erp.entity.excel.ExcelAzwsPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ExcelAzwsRepository extends JpaRepository<ExcelAzwsPO, Integer> {
    @Modifying
    @Query("delete from ExcelAzwsPO p where p.storeId = :storeId")
    void deleteByStoreId(Integer storeId);

}
