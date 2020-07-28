package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.entity.ProductPO;
import com.fiveamazon.erp.entity.SkuPO;
import com.fiveamazon.erp.epo.TestEpo;
import com.fiveamazon.erp.epo.TestEpo2;
import com.fiveamazon.erp.repository.ProductRepository;
import com.fiveamazon.erp.repository.SkuRepository;
import com.fiveamazon.erp.service.ProductService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuRepository skuRepository;

    @Override
    public void save(ProductDTO productDTO) {
        log.warn("SkuServiceImpl.save");
        log.warn(new JSONObject(productDTO).toString());
        Integer productId = productDTO.getId();
        skuRepository.deleteAllByProductId(productId);
        List<String> skuNameList = Arrays.asList(productDTO.getSkuNameList().split(";"));
        List<String> skuStoreList = Arrays.asList(productDTO.getSkuStoreList().split(";"));
        log.warn("size: " + skuNameList.size() + " : " + skuStoreList.size());
        int i = 0;
        for(String skuName : skuNameList){
            log.warn("skuName: " + skuName);
            skuName = skuName.replaceAll("\\s*", "");
            if(StringUtils.isBlank(skuName)){
                continue;
            }
            SkuPO skuPO = new SkuPO();
            skuPO.setCreateDate(new Date());
            skuPO.setCreateUser(productDTO.getUsername());
            skuPO.setProductId(productId);
            skuPO.setName(skuName);
            String skuStore = "NoStore";
            if(skuStoreList.size() > i){
                if(StringUtils.isNotBlank(skuStoreList.get(i))){
                    skuStore = skuStoreList.get(i).replaceAll("\\s*", "");
                }
            }
            skuPO.setStore(skuStore);
            i += 1;
            skuRepository.save(skuPO);
        }
    }

    @Override
    public JSONObject getByProductId(Integer productId) {
        JSONObject rs = new JSONObject();
        String skuNameList = "";
        String skuStoreList = "";
        List<SkuPO> skuPOList = skuRepository.findByProductId(productId);
        for(SkuPO skuPO : skuPOList){
            skuNameList += skuPO.getName() + "; ";
            skuStoreList += skuPO.getStore() + "; ";
        }
        rs.put("skuNameList", skuNameList);
        rs.put("skuStoreList", skuStoreList);
        return rs;
    }

    @Override
    public void test1(List<TestEpo> testEpoList) {
        log.warn("SkuServiceImpl.test1");
        for(TestEpo testEpo : testEpoList){
            log.warn("h1yr");
            log.warn(new JSONObject(testEpo).toString());
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
