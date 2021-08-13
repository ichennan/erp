package com.fiveamazon.erp.service;


import com.fiveamazon.erp.entity.MonthPO;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface MonthService {
    MonthPO getById(Integer id);

    MonthPO save(MonthPO item);

}
