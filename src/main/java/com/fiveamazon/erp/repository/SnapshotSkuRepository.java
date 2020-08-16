package com.fiveamazon.erp.repository;

import com.fiveamazon.erp.entity.SnapshotSkuPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SnapshotSkuRepository extends JpaRepository<SnapshotSkuPO, Integer> {
    List<SnapshotSkuPO> findBySkuId(Integer skuId);
}
