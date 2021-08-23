package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.MonthPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface MonthRepository extends JpaRepository<MonthPO, Integer> {
    MonthPO getById(Integer id);

    MonthPO getByMonthAndStoreId(String month, Integer storeId);
}
