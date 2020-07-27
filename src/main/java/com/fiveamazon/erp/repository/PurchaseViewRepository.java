package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.PurchaseViewPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface PurchaseViewRepository extends JpaRepository<PurchaseViewPO, Integer> {
}
