package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.MonthPO;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface MonthService {
    MonthPO getById(Integer id);

    MonthPO save(MonthPO item);

    List<MonthPO> findAll(Sort sort);

    void generate(Integer id);

    void generate(Integer year, Integer monthStart, Integer monthEnd, List<Integer> storeIdList);

}
