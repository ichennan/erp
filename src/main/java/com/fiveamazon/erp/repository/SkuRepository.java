package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.SkuPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface SkuRepository extends JpaRepository<SkuPO, Integer> {
	SkuPO getByName(String name);
	List<SkuPO> findByProductId(Integer productId);
	void deleteAllByProductId(Integer productId);
}
