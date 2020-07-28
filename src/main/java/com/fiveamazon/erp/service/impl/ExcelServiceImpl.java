package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ExcelSupplierDeliveryOrderPO;
import com.fiveamazon.erp.entity.SkuPO;
import com.fiveamazon.erp.epo.SupplierDeliveryOrderEpo;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;
import com.fiveamazon.erp.repository.ExcelSupplierDeliveryOrderRepository;
import com.fiveamazon.erp.repository.SkuRepository;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private ExcelSupplierDeliveryOrderRepository excelSupplierDeliveryOrderRepository;

    @Override
    public void insert(List<SupplierDeliveryOrderEpo> supplierDeliveryOrderEpoList) {
        log.warn("ExcelServiceImpl.insert");
        for(SupplierDeliveryOrderEpo supplierDeliveryOrderEpo : supplierDeliveryOrderEpoList){
            log.warn(new JSONObject(supplierDeliveryOrderEpo).toString());
            ExcelSupplierDeliveryOrderPO excelSupplierDeliveryOrderPO = new ExcelSupplierDeliveryOrderPO();
            BeanUtils.copyProperties(supplierDeliveryOrderEpo, excelSupplierDeliveryOrderPO);
            log.warn(new JSONObject(excelSupplierDeliveryOrderPO).toString());
            excelSupplierDeliveryOrderPO.setDianhua(supplierDeliveryOrderEpo.getDianhua());
            log.warn(new JSONObject(excelSupplierDeliveryOrderPO).toString());
            excelSupplierDeliveryOrderRepository.save(excelSupplierDeliveryOrderPO);
        }
    }

    @Override
    public void test2(List<TestEpo2> testEpoList) {
        log.warn("SkuServiceImpl.test2");
        for(TestEpo2 testEpo2 : testEpoList){
            log.warn("h2yr");
            log.warn(new JSONObject(testEpo2).toString());
        }
    }
}
