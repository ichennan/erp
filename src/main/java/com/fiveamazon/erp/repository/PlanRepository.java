package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.PlanDetailPO;
import com.fiveamazon.erp.entity.PlanPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface PlanRepository extends JpaRepository<PlanPO, Integer> {
    PlanPO getById(Integer id);
}
