package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.repository.RateRepository;
import com.fiveamazon.erp.service.SmartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Service
@Transactional
public class SmartServiceImpl implements SmartService {
    @Autowired
    private RateRepository rateRepository;

    @Override
    public BigDecimal getRate(String monthOrDate) {
        return getRate(monthOrDate, null);
    }

    @Override
    public BigDecimal getRate(String monthOrDate, String currency) {
        Date date = new Date();
        if (StringUtils.isBlank(currency)) {
            currency = "USD";
        }
        if (!StringUtils.isBlank(monthOrDate)) {
            if (monthOrDate.length() == 6) {
                date = DateUtil.parse(monthOrDate + "01000001", SimpleConstant.DATE_14);
            } else if (monthOrDate.length() == 8) {
                date = DateUtil.parse(monthOrDate + "000001", SimpleConstant.DATE_14);
            }
        }
        return rateRepository.getTopByCurrencyAndEffectiveTimeLessThanEqualOrderByEffectiveTimeDesc(currency, date).getRate();
    }
}
