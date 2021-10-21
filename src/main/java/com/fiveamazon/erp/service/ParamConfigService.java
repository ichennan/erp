package com.fiveamazon.erp.service;


import com.fiveamazon.erp.dto.ParamConfigDTO;
import com.fiveamazon.erp.entity.ParamConfigPO;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface ParamConfigService {
    Long countAll();

    ParamConfigPO getById(Integer id);

    List<ParamConfigPO> findAll();

    ParamConfigPO save(ParamConfigDTO dto);

    List<String> findListByCategory(String paramCategory);

    List<String> findParamCategoryList();

}
