package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.StocktakingDTO;
import com.fiveamazon.erp.dto.StocktakingDetailDTO;
import com.fiveamazon.erp.entity.StocktakingDetailPO;
import com.fiveamazon.erp.entity.StocktakingPO;
import com.fiveamazon.erp.entity.StocktakingViewPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface StocktakingService {
    Long countAll();

    StocktakingPO getById(Integer id);

    StocktakingDetailPO getDetailById(Integer id);

    List<StocktakingViewPO> findAll();

    StocktakingPO save(StocktakingDTO dto);

    StocktakingDetailPO saveDetail(StocktakingDetailDTO dto);

    List<StocktakingDetailPO> findAllDetail(Integer id);
}
