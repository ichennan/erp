package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.OverseaViewPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface OverseaViewRepository extends JpaRepository<OverseaViewPO, Integer> {
    @Query("select p from OverseaViewPO p order by p.deliveryDate desc")
    List<OverseaViewPO> findAllByDeliveryDateDesc();
}
