package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.dto.ProductDTO;
import com.fiveamazon.erp.dto.SkuInfoDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuInfoVO;
import com.fiveamazon.erp.entity.SnapshotSkuPO;
import com.fiveamazon.erp.repository.SkuInfoRepository;
import com.fiveamazon.erp.repository.SkuInfoViewRepository;
import com.fiveamazon.erp.repository.SnapshotSkuRepository;
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
    private SkuInfoViewRepository skuInfoViewRepository;

    @Autowired
    private SnapshotSkuRepository snapshotSkuRepository;

    @Override
    public SkuInfoPO getById(Integer id) {
        return skuInfoRepository.getOne(id);
    }

    @Override
    public SkuInfoVO getViewById(Integer id) {
        return skuInfoViewRepository.getOne(id);
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
    public List<SkuInfoVO> findAll() {
        return skuInfoViewRepository.findBySkuIsNotNull();
    }

    private JSONArray findSkuShipmentArray(Integer skuId) {
        JSONArray rs = new JSONArray();
        JSONArray array = new JSONArray(skuInfoViewRepository.findSkuShipment(skuId));
        for(Object object : array){
            JSONArray objectArray = new JSONArray(object);
            JSONObject json = new JSONObject();
            json.put("skuId", Integer.valueOf(objectArray.get(0).toString()));
            json.put("deliveryDate", objectArray.get(1).toString());
            json.put("shipmentQuantity", -1 * Integer.valueOf(objectArray.get(2).toString()));
            rs.put(json);
        }
        return rs;
    }

    private JSONArray findSkuElseShipmentArray(Integer productId, Integer skuId) {
        JSONArray rs = new JSONArray();
        JSONArray array = new JSONArray(skuInfoViewRepository.findSkuElseShipment(productId, skuId));
        for(Object object : array){
            JSONArray objectArray = new JSONArray(object);
            JSONObject json = new JSONObject();
            json.put("skuId", Integer.valueOf(objectArray.get(0).toString()));
            json.put("deliveryDate", objectArray.get(1).toString());
            json.put("shipmentQuantity", -1 * Integer.valueOf(objectArray.get(2).toString()));
            rs.put(json);
        }
        return rs;
    }

    private JSONArray findProductPurchaseArray(Integer productId) {
        log.warn("SkuServiceImpl.findProductPurchaseArray");
        JSONArray rs = new JSONArray();
        JSONArray array = new JSONArray(skuInfoViewRepository.findProductPurchase(productId));
        log.warn("array: " + array.toString());
        for(Object object : array){
            JSONArray objectArray = new JSONArray(object);
            JSONObject json = new JSONObject();
            json.put("productId", Integer.valueOf(objectArray.get(0).toString()));
            json.put("receivedDate", objectArray.get(1).toString());
            json.put("purchaseQuantity", Integer.valueOf(objectArray.get(2).toString()));
            rs.put(json);
        }
        return rs;
    }

    @Override
    public JSONObject getSkuShipmentObject(Integer skuId) {
        JSONObject rs = new JSONObject();
        JSONArray array = findSkuShipmentArray(skuId);
        for(JSONObject json : array.jsonIter()){
            rs.put(json.getStr("deliveryDate"), json.getInt("shipmentQuantity"));
        }
        return rs;
    }

    @Override
    public JSONObject getSkuElseShipmentObject(Integer productId, Integer skuId) {
        JSONObject rs = new JSONObject();
        JSONArray array = findSkuElseShipmentArray(productId, skuId);
        for(JSONObject json : array.jsonIter()){
            rs.put(json.getStr("deliveryDate"), json.getInt("shipmentQuantity"));
        }
        return rs;
    }

    @Override
    public JSONObject getProductPurchaseObject(Integer productId) {
        JSONObject rs = new JSONObject();
        JSONArray array = findProductPurchaseArray(productId);
        for(JSONObject json : array.jsonIter()){
            rs.put(json.getStr("receivedDate"), json.getInt("purchaseQuantity"));
        }
        return rs;
    }

    @Override
    public JSONObject getProductInventoryObjectBySkuId(Integer skuId) {
        JSONObject rs = new JSONObject();
        List<SnapshotSkuPO> snapshotSkuPOList = snapshotSkuRepository.findBySkuId(skuId);
        for(SnapshotSkuPO snapshotSkuPO : snapshotSkuPOList){
            rs.put(snapshotSkuPO.getSnapshotDate(), snapshotSkuPO.getProductInventoryQuantity());
        }
        return rs;
    }
}
