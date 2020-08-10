package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonException;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.*;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.repository.ShipmentDetailRepository;
import com.fiveamazon.erp.repository.ShipmentRepository;
import com.fiveamazon.erp.repository.ShipmentViewRepository;
import com.fiveamazon.erp.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class ShipmentServiceImpl implements ShipmentService {
    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private ShipmentViewRepository shipmentViewRepository;
    @Autowired
    private ShipmentDetailRepository shipmentDetailRepository;

    @Override
    public Long countAll() {
        return shipmentRepository.count();
    }

    @Override
    public ShipmentPO getById(Integer id) {
        return shipmentRepository.getOne(id);
    }

    @Override
    public ShipmentDetailPO getDetailById(Integer id) {
        return shipmentDetailRepository.getOne(id);
    }

    @Override
    public List<ShipmentViewPO> findAll() {
        return shipmentViewRepository.findAll();
    }

    @Override
    public ShipmentPO save(ShipmentPO shipmentPO) {
        return shipmentRepository.save(shipmentPO);
    }

    @Override
    public ShipmentDetailPO saveDetail(ShipmentDetailPO shipmentDetailPO) {
        return shipmentDetailRepository.save(shipmentDetailPO);
    }

    @Override
    public ShipmentPO save(ShipmentDTO shipmentDTO) {
        Integer shipmentId = shipmentDTO.getId();
        ShipmentPO shipmentPO;
        if(shipmentId == null || shipmentId == 0){
            shipmentPO = new ShipmentPO();
            shipmentPO.setCreateDate(new Date());
            shipmentPO.setCreateUser(shipmentDTO.getUsername());
        }else{
            shipmentPO = getById(shipmentId);
            shipmentPO.setUpdateDate(new Date());
            shipmentPO.setUpdateUser(shipmentDTO.getUsername());
            shipmentDetailRepository.deleteByShipmentIdEqualsAndBoxNotLike(shipmentId, "Plan");
            for(ShipmentDetailDTO shipmentDetailDTO: shipmentDTO.getShipmentDetailList()){
                shipmentDetailDTO.setUsername(shipmentDTO.getUsername());
                saveDetail(shipmentDetailDTO);
            }
        }
        BeanUtils.copyProperties(shipmentDTO, shipmentPO, "id");
        return save(shipmentPO);
    }

    @Override
    public ShipmentDetailPO saveDetail(ShipmentDetailDTO shipmentDetailDTO) {
        if(SimpleConstant.ACTION_DELETE.equalsIgnoreCase(shipmentDetailDTO.getAction())){
            shipmentDetailRepository.deleteById(shipmentDetailDTO.getId());
            return null;
        }
        Integer shipmentDetailId = shipmentDetailDTO.getId();
        ShipmentDetailPO shipmentDetailPO;
        if(shipmentDetailId == null || shipmentDetailId == 0){
            shipmentDetailPO = new ShipmentDetailPO();
            shipmentDetailPO.setCreateDate(new Date());
            shipmentDetailPO.setCreateUser(shipmentDetailDTO.getUsername());
        }else{
            shipmentDetailPO = getDetailById(shipmentDetailId);
            shipmentDetailPO.setUpdateDate(new Date());
            shipmentDetailPO.setUpdateUser(shipmentDetailDTO.getUsername());
        }
        BeanUtils.copyProperties(shipmentDetailDTO, shipmentDetailPO, "id");
        return saveDetail(shipmentDetailPO);
    }

    @Override
    public List<ShipmentDetailPO> findAllDetail(Integer shipmentId) {
        return shipmentDetailRepository.findAllByShipmentIdOrderByBox(shipmentId);
    }

    @Override
    public List<ShipmentDetailViewDTO> findByProductId(Integer productId) {
        return shipmentDetailRepository.findByProductId(productId);
    }

    @Override
    public void createByExcel(UploadFbaDTO uploadFbaDTO) {
        Date today = new Date();
        Integer boxCount = uploadFbaDTO.getBoxCount();
        List<ExcelFbaPackListPO> array = uploadFbaDTO.getArray();
        String fbaNo = uploadFbaDTO.getShipmentId();
        if(shipmentRepository.countByFbaNo(fbaNo) > 0){
            throw new SimpleCommonException("Duplicate FBA Found !");
        }
        ShipmentPO shipmentPO = new ShipmentPO();
        shipmentPO.setBoxCount(uploadFbaDTO.getBoxCount());
        shipmentPO.setFbaNo(fbaNo);
        shipmentPO.setCreateDate(today);
        shipmentPO.setDeliveryDate(uploadFbaDTO.getDeliveryDate());
        shipmentPO.setCarrier(uploadFbaDTO.getCarrier());
        shipmentPO.setRoute(uploadFbaDTO.getRoute());
        shipmentPO.setStoreId(uploadFbaDTO.getStoreId());
        shipmentPO.setUnitPrice(uploadFbaDTO.getUnitPrice());
        save(shipmentPO);
        Integer shipmentId = shipmentPO.getId();
        //
        JSONObject allJson = new JSONObject();
        for(Integer i = 1; i <= boxCount; i++){
            String iString = StringUtils.leftPad(i.toString(), 2, "0");
            allJson.put("box" + iString, new JSONArray());
        }

        for(ExcelFbaPackListPO excelFbaPackListPO : array){
            JSONObject excelFbaPackListJson = new JSONObject(excelFbaPackListPO);
            Integer productId = excelFbaPackListJson.getInt("productId");

            String sku = excelFbaPackListPO.getMerchantSku();
            for(Integer i = 1; i <= boxCount; i++){
                String iString = StringUtils.leftPad(i.toString(), 2, "0");
                if(StringUtils.isNotBlank(excelFbaPackListJson.getStr("box" + iString + "Qty"))){
                    String boxString = "box" + iString;
                    JSONArray boxArray = allJson.getJSONArray(boxString);
                    JSONObject productJson = new JSONObject();
                    productJson.put("productId", productId);
                    productJson.put("quantity", excelFbaPackListJson.getStr(boxString + "Qty"));
                    boxArray.add(productJson);
                }
            }
        }

        log.warn("allJson");
        log.warn(allJson.toString());

        for(String boxString : allJson.keySet()){
            log.warn("boxString: " + boxString);
            String boxNumber = boxString.substring(3, 5);
            log.warn("boxNumber: " + boxString);
            JSONArray boxArray = allJson.getJSONArray(boxString);
            log.warn(boxString + " size: " + boxArray.size());
            for(JSONObject boxJson : boxArray.jsonIter()){
                log.warn("boxJson: " + boxJson.toString());
                ShipmentDetailPO shipmentDetailPO = new ShipmentDetailPO();
                shipmentDetailPO.setBox(boxNumber);
                shipmentDetailPO.setQuantity(boxJson.getInt("quantity"));
                shipmentDetailPO.setProductId(boxJson.getInt("productId"));
                shipmentDetailPO.setShipmentId(shipmentId);
                shipmentDetailPO.setWeight(new BigDecimal(0));
                saveDetail(shipmentDetailPO);
            }
        }

    }
}
