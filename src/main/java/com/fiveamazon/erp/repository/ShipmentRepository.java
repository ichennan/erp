package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ShipmentPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ShipmentRepository extends JpaRepository<ShipmentPO, Integer> {
    Long countByFbaNo(String fbaNo);
    ShipmentPO getByFbaNo(String fbaNo);

}
