package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.MonthPO;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface MonthRepository extends JpaRepository<MonthPO, Integer> {
    MonthPO getById(Integer id);

    MonthPO getByMonthAndStoreId(String month, Integer storeId);

    List<MonthPO> findByMonth(String month);

    List<MonthPO> findAll(Sort sort);
}
