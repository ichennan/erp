package com.fiveamazon.erp.service;


import java.math.BigDecimal;

/**
 * @author chennan
 * @date 2018/8/7 15:51
 */
public interface SmartService {
    BigDecimal getRate(String monthOrDate);
    BigDecimal getRate(String monthOrDate, String currency);
}
