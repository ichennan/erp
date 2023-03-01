package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.FbaTrackingBatchCreateDTO;
import com.fiveamazon.erp.dto.FbaTrackingBatchUpdateDTO;
import com.fiveamazon.erp.entity.FbaTrackingPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface FbaTrackingService {
    FbaTrackingPO getById(Integer id);
    List<FbaTrackingPO> findAll();
    void batchCreate(FbaTrackingBatchCreateDTO dto);
    void batchUpdate(FbaTrackingBatchUpdateDTO dto);
}
