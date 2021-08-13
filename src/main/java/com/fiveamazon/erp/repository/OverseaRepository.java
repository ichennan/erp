package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.OverseaPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface OverseaRepository extends JpaRepository<OverseaPO, Integer> {
    OverseaPO getById(Integer id);

    List<OverseaPO> findByDeliveryDateBetweenAndStoreIdOrderByStoreIdAscDeliveryDateAsc(String dateFrom, String dateTo, Integer storeId);

}
