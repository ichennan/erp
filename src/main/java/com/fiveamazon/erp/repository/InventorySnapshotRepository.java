package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.InventorySnapshotPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface InventorySnapshotRepository extends JpaRepository<InventorySnapshotPO, Integer> {
    List<InventorySnapshotPO> findByProductId(Integer productId);
}
