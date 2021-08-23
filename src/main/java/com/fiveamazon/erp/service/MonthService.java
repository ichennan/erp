package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.MonthPO;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface MonthService {
    MonthPO getById(Integer id);

    MonthPO save(MonthPO item);

    List<MonthPO> findAll();

    void generate(Integer id, BigDecimal rate);

    void autoCreate();

}
