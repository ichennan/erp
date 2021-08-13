package com.fiveamazon.erp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.SkuInfoDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import com.fiveamazon.erp.entity.SkuInfoVO;
import com.fiveamazon.erp.entity.SnapshotSkuPO;
import com.fiveamazon.erp.repository.SkuInfoRepository;
import com.fiveamazon.erp.repository.SkuInfoViewRepository;
import com.fiveamazon.erp.repository.SnapshotSkuRepository;
import com.fiveamazon.erp.service.ShipmentService;
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

    @Autowired
    ShipmentService shipmentService;

    @Override
    public SkuInfoPO getById(Integer id) {
        return skuInfoRepository.getById(id);
    }

    @Override
    public SkuInfoVO getViewById(Integer id) {
        return skuInfoViewRepository.getById(id);
    }

    @Override
    public SkuInfoPO save(SkuInfoPO skuInfoPO) {
        //for unique key sku
        if(StringUtils.isEmpty(skuInfoPO.getSku())){
            skuInfoPO.setSku(null);
        }
        skuInfoPO.setCombineId(1);
        return skuInfoRepository.save(skuInfoPO);
    }

    @Override
    public SkuInfoPO save(SkuInfoDTO skuInfoDTO) {
        log.warn("SkuServiceImpl.save" + new JSONObject(skuInfoDTO).toString());
        Integer skuId = skuInfoDTO.getId();
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(skuInfoDTO.getAction())){
            if(shipmentService.countBySkuId(skuId) > 0){
                throw new SimpleCommonException("该SKU 已存在于FBA表中，无法删除，请联系管理员");
            }
            log.error("SkuServiceImpl.deleteSku" + new JSONObject(skuInfoDTO).toString());
            skuInfoRepository.deleteById(skuId);
            return null;
        }
        SkuInfoPO skuInfoPO;
        if(skuId == null || skuId == 0){
            skuInfoPO = new SkuInfoPO();
            BeanUtil.copyProperties(skuInfoDTO, skuInfoPO,  "id");
            skuInfoPO.setCreateDate(new Date());
            skuInfoPO.setCreateUser(skuInfoDTO.getUsername());
            skuInfoPO = save(skuInfoPO);
        }else{
            skuInfoPO = getById(skuId);
            if(!skuInfoPO.getSku().equals(skuInfoDTO.getSku())){
                if(shipmentService.countBySkuId(skuId) > 0){
                    throw new SimpleCommonException("该SKU 已存在于FBA表中，无法更改，请联系管理员");
                }
                log.error("SkuServiceImpl.updateSku old" + new JSONObject(skuInfoPO).toString());
                log.error("SkuServiceImpl.updateSku new" + new JSONObject(skuInfoDTO).toString());
            }
            BeanUtils.copyProperties(skuInfoDTO, skuInfoPO, "id");
            skuInfoPO.setUpdateDate(new Date());
            skuInfoPO.setUpdateUser(skuInfoDTO.getUsername());
            skuInfoPO = save(skuInfoPO);
        }
        return skuInfoPO;
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
    public List<SkuInfoPO> findBySkuAndStoreId(String sku, Integer storeId) {
        return skuInfoRepository.findBySkuAndStoreId(sku, storeId);
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
            json.put("deliveryDate", objectArray.get(1).toString());
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
        log.info("getProductPurchaseObject: " + productId);
        JSONObject rs = new JSONObject();
        JSONArray array = findProductPurchaseArray(productId);
        for(JSONObject json : array.jsonIter()){
            rs.put(json.getStr("deliveryDate"), json.getInt("purchaseQuantity"));
        }
        log.info(rs.toString());
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
