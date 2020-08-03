package com.fiveamazon.erp.repository;

import com.fiveamazon.erp.entity.StorePO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface StoreRepository extends JpaRepository<StorePO, Integer> {
	StorePO getById(Integer id);
//	List<StorePO> findAll();
}
