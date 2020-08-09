package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.dto.SkuInfoDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuViewPO;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;
import com.fiveamazon.erp.repository.SkuInfoRepository;
import com.fiveamazon.erp.repository.SkuViewRepository;
import com.fiveamazon.erp.service.SkuInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoRepository skuInfoRepository;

    @Autowired
    private SkuViewRepository skuViewRepository;

    @Override
    public void save(SkuInfoPO skuInfoPO) {
        //for unique key sku
        if(StringUtils.isEmpty(skuInfoPO.getSku())){
            skuInfoPO.setSku(null);
        }
        skuInfoPO.setCombineId(1);
        skuInfoRepository.save(skuInfoPO);
    }

    @Override
    public void save(ProductDTO productDTO) {
        log.warn("SkuInfoServiceImpl.save");
        log.warn(new JSONObject(productDTO).toString());
        Integer productId = productDTO.getId();
        skuInfoRepository.deleteAllByProductId(productId);
        List<SkuInfoDTO> skuInfoDTOList = productDTO.getSkuArray();
        for(SkuInfoDTO skuInfoDTO : skuInfoDTOList){
            SkuInfoPO skuInfoPO = new SkuInfoPO();
            BeanUtils.copyProperties(skuInfoDTO, skuInfoPO, "id");
            skuInfoPO.setCreateDate(new Date());
            skuInfoPO.setProductId(productId);
            save(skuInfoPO);
        }
    }

    @Override
    public List<SkuInfoPO> findByProductId(Integer productId) {
        return skuInfoRepository.findByProductId(productId);
    }

    @Override
    public JSONObject getByProductId(Integer productId) {
        JSONObject rs = new JSONObject();
        String skuNameList = "";
        String skuStoreList = "";
        List<SkuInfoPO> skuInfoPOList = skuInfoRepository.findByProductId(productId);
        for(SkuInfoPO skuInfoPO : skuInfoPOList){
            skuNameList += skuInfoPO.getSku() + "; ";
            skuStoreList += skuInfoPO.getStore() + "; ";
        }
        rs.put("skuNameList", skuNameList);
        rs.put("skuStoreList", skuStoreList);
        return rs;
    }

    @Override
    public void test1(List<TestEpo> testEpoList) {
        log.warn("SkuInfoServiceImpl.test1");
        for(TestEpo testEpo : testEpoList){
            log.warn("h1yr");
            log.warn(new JSONObject(testEpo).toString());
        }
    }

    @Override
    public void test2(List<TestEpo2> testEpoList) {
        log.warn("SkuInfoServiceImpl.test2");
        for(TestEpo2 testEpo2 : testEpoList){
            log.warn("h2yr");
            log.warn(new JSONObject(testEpo2).toString());
        }
    }

    @Override
    public List<SkuInfoPO> findBySku(String sku) {
        return skuInfoRepository.findBySku(sku);
    }

    @Override
    public List<SkuViewPO> findAll() {
        return skuViewRepository.findAll();
    }
}
