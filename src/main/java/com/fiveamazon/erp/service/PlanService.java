package com.fiveamazon.erp.service;

import com.fiveamazon.erp.dto.PlanCreateDTO;
import com.fiveamazon.erp.entity.PlanDetailPO;
import com.fiveamazon.erp.entity.PlanPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface PlanService {
    Integer create(PlanCreateDTO planCreateDTO);
    List<PlanPO> findAll();
    PlanPO getById(Integer id);
    List<PlanDetailPO> findAllDetail(Integer planId);
}
