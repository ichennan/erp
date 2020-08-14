package com.fiveamazon.erp.service.impl;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.epo.ExcelFbaRowEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderDetailEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;
import com.fiveamazon.erp.repository.*;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    private ExcelSupplierDeliveryRepository excelSupplierDeliveryRepository;
    @Autowired
    private ExcelSupplierDeliveryOrderRepository excelSupplierDeliveryOrderRepository;
    @Autowired
    private ExcelSupplierDeliveryOrderDetailRepository excelSupplierDeliveryOrderDetailRepository;
    @Autowired
    private ExcelFbaRepository excelFbaRepository;
    @Autowired
    private ExcelFbaPackListRepository excelFbaPackListRepository;
    @Autowired
    private SkuService skuService;

    @Override
    public void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList) {
        log.warn("ExcelServiceImpl.insertExcelSupplierDeliveryOrder");
        for(ExcelSupplierDeliveryOrderEO excelSupplierDeliveryOrderEO : excelSupplierDeliveryOrderEOList){
            if(StringUtils.isBlank(excelSupplierDeliveryOrderEO.getDingdanhao())){
                continue;
            }
            ExcelSupplierDeliveryOrderPO excelSupplierDeliveryOrderPO = new ExcelSupplierDeliveryOrderPO();
            BeanUtils.copyProperties(excelSupplierDeliveryOrderEO, excelSupplierDeliveryOrderPO);
            excelSupplierDeliveryOrderPO.setExcelId(excelId);
            excelSupplierDeliveryOrderRepository.save(excelSupplierDeliveryOrderPO);
        }
    }

    @Override
    public void insertExcelSupplierDeliveryOrderDetail(Integer excelId, List<ExcelSupplierDeliveryOrderDetailEO> excelSupplierDeliveryOrderDetailEOList) {
        log.warn("ExcelServiceImpl.insertExcelSupplierDeliveryOrderDetail");
        for(ExcelSupplierDeliveryOrderDetailEO excelSupplierDeliveryOrderDetailEO : excelSupplierDeliveryOrderDetailEOList){
            if(StringUtils.isBlank(excelSupplierDeliveryOrderDetailEO.getDingdanhao())){
                continue;
            }
            ExcelSupplierDeliveryOrderDetailPO excelSupplierDeliveryOrderDetailPO = new ExcelSupplierDeliveryOrderDetailPO();
            BeanUtils.copyProperties(excelSupplierDeliveryOrderDetailEO, excelSupplierDeliveryOrderDetailPO);
            excelSupplierDeliveryOrderDetailPO.setExcelId(excelId);
            excelSupplierDeliveryOrderDetailRepository.save(excelSupplierDeliveryOrderDetailPO);
        }
    }

    @Override
    public void insertFbaPackList(Integer excelId, List<ExcelFbaRowEO> excelFbaRow) {
        log.warn("ExcelServiceImpl.insertFbaPackList");
        int row = 0;
        int boxCount = 0;
        ExcelFbaPO excelFbaPO = excelFbaRepository.getOne(excelId);
        List<ExcelFbaPackListPO> excelFbaPackListPOList = new ArrayList<ExcelFbaPackListPO>();
        Boolean isDetail = false;
        for(ExcelFbaRowEO excelFbaRowEO : excelFbaRow){
            log.warn("row " + row + " : " + new JSONObject(excelFbaRowEO).toString());
            String column00 = excelFbaRowEO.getColumn00();
            if(StringUtils.isBlank(column00)){
                continue;
            }
            log.warn("column00: " + column00 + " isDetail: " + isDetail);
            if(isDetail){
                if(column00.contains("Plan ID:")){
                    log.warn("Plan ID: " + column00.replace("Plan ID: ", ""));
                    excelFbaPO.setPlanId(column00.replace("Plan ID: ", ""));
                    isDetail = false;
                }else{
                    ExcelFbaPackListPO excelFbaPackListPO = new ExcelFbaPackListPO();
                    excelFbaPackListPO.setMerchantSku(excelFbaRowEO.getColumn00());
                    excelFbaPackListPO.setAsin(excelFbaRowEO.getColumn01());
                    excelFbaPackListPO.setTitle(excelFbaRowEO.getColumn02());
                    excelFbaPackListPO.setFnsku(excelFbaRowEO.getColumn03());
                    excelFbaPackListPO.setExternalId(excelFbaRowEO.getColumn04());
                    excelFbaPackListPO.setWhoWillPrep(excelFbaRowEO.getColumn05());
                    excelFbaPackListPO.setPrepType(excelFbaRowEO.getColumn06());
                    excelFbaPackListPO.setWhoWillLabel(excelFbaRowEO.getColumn07());
                    excelFbaPackListPO.setExpectedQty(excelFbaRowEO.getColumn08());
                    excelFbaPackListPO.setBoxedQty(excelFbaRowEO.getColumn09());
                    //
                    excelFbaPackListPO.setBox01Qty(excelFbaRowEO.getColumn11());
                    excelFbaPackListPO.setBox02Qty(excelFbaRowEO.getColumn12());
                    excelFbaPackListPO.setBox03Qty(excelFbaRowEO.getColumn13());
                    excelFbaPackListPO.setBox04Qty(excelFbaRowEO.getColumn14());
                    excelFbaPackListPO.setBox05Qty(excelFbaRowEO.getColumn15());
                    excelFbaPackListPO.setBox06Qty(excelFbaRowEO.getColumn16());
                    excelFbaPackListPO.setBox07Qty(excelFbaRowEO.getColumn17());
                    excelFbaPackListPO.setBox08Qty(excelFbaRowEO.getColumn18());
                    excelFbaPackListPO.setBox09Qty(excelFbaRowEO.getColumn19());
                    excelFbaPackListPO.setBox10Qty(excelFbaRowEO.getColumn20());
                    excelFbaPackListPO.setBox11Qty(excelFbaRowEO.getColumn21());
                    excelFbaPackListPO.setBox12Qty(excelFbaRowEO.getColumn22());
                    excelFbaPackListPO.setBox13Qty(excelFbaRowEO.getColumn23());
                    excelFbaPackListPO.setBox14Qty(excelFbaRowEO.getColumn24());
                    excelFbaPackListPO.setBox15Qty(excelFbaRowEO.getColumn25());
                    excelFbaPackListPO.setBox16Qty(excelFbaRowEO.getColumn26());
                    excelFbaPackListPO.setBox17Qty(excelFbaRowEO.getColumn27());
                    excelFbaPackListPO.setBox18Qty(excelFbaRowEO.getColumn28());
                    excelFbaPackListPO.setBox19Qty(excelFbaRowEO.getColumn29());
                    excelFbaPackListPO.setBox20Qty(excelFbaRowEO.getColumn30());
                    excelFbaPackListPO.setBox21Qty(excelFbaRowEO.getColumn31());
                    excelFbaPackListPO.setBox22Qty(excelFbaRowEO.getColumn32());
                    excelFbaPackListPO.setBox23Qty(excelFbaRowEO.getColumn33());
                    excelFbaPackListPO.setBox24Qty(excelFbaRowEO.getColumn34());
                    excelFbaPackListPO.setBox25Qty(excelFbaRowEO.getColumn35());
                    excelFbaPackListPO.setBox26Qty(excelFbaRowEO.getColumn36());
                    excelFbaPackListPO.setBox27Qty(excelFbaRowEO.getColumn37());
                    excelFbaPackListPO.setBox28Qty(excelFbaRowEO.getColumn38());
                    excelFbaPackListPO.setBox29Qty(excelFbaRowEO.getColumn39());
                    excelFbaPackListPO.setBox30Qty(excelFbaRowEO.getColumn40());
                    excelFbaPackListPOList.add(excelFbaPackListPO);
                }
            }else{
                if(column00.contains("Shipment ID")){
                    excelFbaPO.setShipmentId(excelFbaRowEO.getColumn01());
                }else if(column00.contains("Name:")){
                    log.warn("Name: " + column00.replace("Name: ", ""));
                    excelFbaPO.setFbaName(column00.replace("Name: ", ""));
                }else if(column00.contains("Merchant SKU")){
                    log.warn("Merchant SKU");
                    JSONObject boxCountJson = new JSONObject(excelFbaRowEO);
                    for(int i=11; i <= 40; i++){
                        String cellString = boxCountJson.getStr("column" + i);
                        if(StringUtils.isBlank(cellString)){
                            break;
                        }
                        boxCount ++;
                    }
                    isDetail = true;
                }else if(column00.contains("Ship To:")){
                    log.warn("Ship To: " + column00.replace("Ship To: ", ""));
                    excelFbaPO.setShipTo(column00.replace("Ship To: ", ""));
                }else{
                    log.warn(column00);
                }
            }

            row++;
        }

        excelFbaPO.setBoxCount(boxCount);
        excelFbaRepository.save(excelFbaPO);
        Integer storeId = null;
        for(ExcelFbaPackListPO excelFbaPackListPO : excelFbaPackListPOList){
            String sku = excelFbaPackListPO.getMerchantSku();
            excelFbaPackListPO.setExcelId(excelId);
            List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
            if(skuInfoPOList == null || skuInfoPOList.size() == 0){
                excelFbaPackListRepository.save(excelFbaPackListPO);
                continue;
            }
            for(SkuInfoPO skuInfoPO : skuInfoPOList){
                ExcelFbaPackListPO skuExcelFbaPackListPO = new ExcelFbaPackListPO();
                BeanUtils.copyProperties(excelFbaPackListPO, skuExcelFbaPackListPO);
                skuExcelFbaPackListPO.setProductId(skuInfoPO.getProductId());
                skuExcelFbaPackListPO.setStoreId(skuInfoPO.getStoreId());
                skuExcelFbaPackListPO.setSkuId(skuInfoPO.getId());
                storeId = skuInfoPO.getStoreId();
                excelFbaPackListRepository.save(skuExcelFbaPackListPO);
            }
        }
        excelFbaPO.setStoreId(storeId);
    }

    @Override
    public Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO) {
        log.warn("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelSupplierDeliveryPO.setCreateDate(new Date());
        excelSupplierDeliveryRepository.save(excelSupplierDeliveryPO);
        return excelSupplierDeliveryPO.getId();
    }

    @Override
    public Integer saveExcelFba(ExcelFbaPO excelFbaPO) {
        log.warn("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelFbaPO.setCreateDate(new Date());
        excelFbaRepository.save(excelFbaPO);
        return excelFbaPO.getId();
    }

    @Override
    public List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId) {
        return excelSupplierDeliveryOrderRepository.findByExcelId(excelId);
    }

    @Override
    public List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId) {
        return excelSupplierDeliveryOrderDetailRepository.findByExcelId(excelId);
    }

    @Override
    public ExcelFbaPO getFbaByExcelId(Integer excelId) {
        return excelFbaRepository.getOne(excelId);
    }

    @Override
    public List<ExcelFbaPackListPO> findFbaPackListByExcelId(Integer excelId) {
        return excelFbaPackListRepository.findByExcelId(excelId);
    }

    @Override
    public ExcelSupplierDeliveryOrderPO getExcelSupplierDeliveryOrderByExcelIdAndDingdanhao(Integer excelId, String dingdanghao) {
        if(excelId == null || excelId == 0 || StringUtils.isBlank(dingdanghao)){
            return null;
        }
        return excelSupplierDeliveryOrderRepository.getByExcelIdAndDingdanhao(excelId, dingdanghao);
    }
}
