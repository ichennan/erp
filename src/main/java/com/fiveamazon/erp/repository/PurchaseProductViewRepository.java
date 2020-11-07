package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.PurchaseDetailPO;
import com.fiveamazon.erp.entity.PurchaseProductViewPO;
import com.fiveamazon.erp.entity.PurchaseViewPO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface PurchaseProductViewRepository extends JpaRepository<PurchaseProductViewPO, Integer> {
    Page<PurchaseProductViewPO> findAll(Specification<PurchaseProductViewPO> spc, Pageable pageable);
}
