package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.entity.ProductVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface ProductViewRepository extends JpaRepository<ProductVO, Integer> {
}
