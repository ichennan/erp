package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.SkuInfoPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SkuInfoRepository extends JpaRepository<SkuInfoPO, Integer> {
	SkuInfoPO getById(Integer id);
	List<SkuInfoPO> findBySku(String sku);
	List<SkuInfoPO> findByProductId(Integer productId);
	void deleteAllByProductId(Integer productId);
}
