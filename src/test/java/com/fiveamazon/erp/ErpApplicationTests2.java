package com.fiveamazon.erp;

import cn.hutool.core.date.DateUtil;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.repository.RateRepository;
import com.fiveamazon.erp.service.SmartService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class ErpApplicationTests2 {
    @Autowired
    SmartService smartService;
    @Autowired
    RateRepository rateRepository;

    @Test
    public void contextLoads() {
        test();
    }

    @Transactional
    public void test(){
        log.info("=========================================");
        log.info("Date [{}]", DateUtil.parse("Aug 22, 2018 3:44:38 PM PDT", SimpleConstant.TRANSACTION_TIME_DATE_FORMAT));
        log.info("=========================================");
        Date abc = DateUtil.parse("20200101", SimpleConstant.DATE_8);
        log.info(abc.toString());
        log.info(DateUtil.format(abc, "yyyy-MM-dd HH:mm:ss SSS Z"));
        log.info(DateUtil.format(abc, "yyyy-MM-dd HH:mm:ss SSS z"));
        Date def = rateRepository.getById(7).getEffectiveTime();
        log.info(def.toString());
        log.info(DateUtil.format(def, "yyyy-MM-dd HH:mm:ss SSS Z"));
        log.info(DateUtil.format(def, "yyyy-MM-dd HH:mm:ss SSS z"));
        Date ccc = DateUtil.parse("2020-01-01 00:00:00 000 +0400", "yyyy-MM-dd HH:mm:ss SSS z");
        log.info(ccc.toString());
        log.info(DateUtil.format(ccc, "yyyy-MM-dd HH:mm:ss SSS Z"));
        log.info(DateUtil.format(ccc, "yyyy-MM-dd HH:mm:ss SSS z"));

        log.info("getRate 20200708: [{}]", smartService.getRate("20200708"));
        log.info("getRate 20200808: [{}]", smartService.getRate("20200808"));
        log.info("getRate 20200908: [{}]", smartService.getRate("20200908"));
        log.info("getRate 20201008: [{}]", smartService.getRate("20201008"));
        log.info("getRate 20201108: [{}]", smartService.getRate("20201108"));
        log.info("getRate 20201208: [{}]", smartService.getRate("20201208"));
        log.info("getRate 20210108: [{}]", smartService.getRate("20210108"));
        log.info("getRate 20210208: [{}]", smartService.getRate("20210208"));
        log.info("getRate 20210308: [{}]", smartService.getRate("20210308"));
        log.info("getRate 20210101: [{}]", smartService.getRate("20210101"));
        log.info("getRate 20210102: [{}]", smartService.getRate("20210102"));
        log.info("getRate 20210103: [{}]", smartService.getRate("20210103"));
        log.info("getRate 20210104: [{}]", smartService.getRate("20210104"));
        //
        log.info("getRate 202007: [{}]", smartService.getRate("202007"));
        log.info("getRate 202008: [{}]", smartService.getRate("202008"));
        log.info("getRate 202009: [{}]", smartService.getRate("202009"));
        log.info("getRate 202010: [{}]", smartService.getRate("202010"));
        log.info("getRate 202011: [{}]", smartService.getRate("202011"));
        log.info("getRate 202012: [{}]", smartService.getRate("202012"));
        log.info("getRate 202101: [{}]", smartService.getRate("202101"));
        log.info("getRate 202102: [{}]", smartService.getRate("202102"));
        log.info("getRate 202103: [{}]", smartService.getRate("202103"));
        log.info("getRate 202104: [{}]", smartService.getRate("202104"));
        log.info("getRate 202104: [{}]", smartService.getRate("202104"));
    }

}
