package com.fiveamazon.erp.repository;


import com.fiveamazon.erp.entity.StocktakingDetailPO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
@Repository
public interface StocktakingDetailRepository extends JpaRepository<StocktakingDetailPO, Integer> {
    StocktakingDetailPO getById(Integer id);
    List<StocktakingDetailPO> findByStocktakingId(Integer id);
    void deleteByStocktakingId(Integer id);
}
