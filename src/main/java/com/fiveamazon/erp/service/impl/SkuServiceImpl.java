package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.dto.SkuInfoDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuViewPO;
import com.fiveamazon.erp.entity.SnapshotSkuInventoryPO;
import com.fiveamazon.erp.repository.SkuInfoRepository;
import com.fiveamazon.erp.repository.SkuViewRepository;
import com.fiveamazon.erp.repository.SnapshotSkuInventoryRepository;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class SkuServiceImpl implements SkuService {

    @Autowired
    private SkuInfoRepository skuInfoRepository;

    @Autowired
    private SkuViewRepository skuViewRepository;

    @Autowired
    private SnapshotSkuInventoryRepository snapshotSkuInventoryRepository;

    @Override
    public SkuInfoPO getById(Integer id) {
        return skuInfoRepository.getOne(id);
    }

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
        log.warn("SkuServiceImpl.save");
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
    public List<SkuInfoPO> findBySku(String sku) {
        return skuInfoRepository.findBySku(sku);
    }

    @Override
    public List<SkuViewPO> findAll() {
        return skuViewRepository.findBySkuIsNotNull();
    }

    @Override
    public List<SnapshotSkuInventoryPO> findSnapshotBySkuId(Integer skuId) {
        return snapshotSkuInventoryRepository.findBySkuId(skuId);
    }
}
