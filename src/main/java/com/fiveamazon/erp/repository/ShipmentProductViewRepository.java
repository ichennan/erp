package com.fiveamazon.erp.repository;

import com.fiveamazon.erp.entity.ShipmentProductViewPO;
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
public interface ShipmentProductViewRepository extends JpaRepository<ShipmentProductViewPO, Integer> {
    Page<ShipmentProductViewPO> findAll(Specification<ShipmentProductViewPO> spc, Pageable pageable);
}
