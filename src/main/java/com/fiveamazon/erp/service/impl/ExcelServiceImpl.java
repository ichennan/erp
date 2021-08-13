package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.entity.excel.ExcelTransactionDetailPO;
import com.fiveamazon.erp.entity.excel.ExcelTransactionPO;
import com.fiveamazon.erp.epo.*;
import com.fiveamazon.erp.repository.*;
import com.fiveamazon.erp.repository.excel.ExcelTransactionDetailRepository;
import com.fiveamazon.erp.repository.excel.ExcelTransactionRepository;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.SkuService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private ExcelTransactionRepository excelTransactionRepository;
    @Autowired
    private ExcelTransactionDetailRepository excelTransactionDetailRepository;
    @Autowired
    private SkuService skuService;

    @Override
    public void insertTransactionRow(Integer excelId, List<ExcelTransactionRowEO> excelTransactionRowEOList) {
        log.info("ExcelServiceImpl.insertExcelTransactionRow");
        ExcelTransactionPO  excelTransactionPO = excelTransactionRepository.getById(excelId);
        Date dateFrom = null == excelTransactionPO.getDateFrom() ? DateUtil.parse("20990101", "yyyyMMdd") : excelTransactionPO.getDateFrom();
        Date dateTo = null == excelTransactionPO.getDateTo() ? DateUtil.parse("19990101", "yyyyMMdd") : excelTransactionPO.getDateTo();
        Integer storeId = null;
        for(ExcelTransactionRowEO item : excelTransactionRowEOList){
            Date transactionTime;
            try{
                transactionTime = DateUtil.parse(item.getTransactionTimeStr(), SimpleConstant.TRANSACTION_TIME_DATE_FORMAT);
            }catch(Exception e){
                log.error("transactionTimeStr DateTime Format Error: " + item.getTransactionTimeStr());
                continue;
            }
            if(transactionTime.before(dateFrom)){
                dateFrom = transactionTime;
            }
            if(transactionTime.after(dateTo)){
                dateTo = transactionTime;
            }
            if(null == storeId){
                String sku = item.getSku();
                if(StringUtils.isNotBlank(sku)){
                    List<SkuInfoPO> skuInfoPOList =  skuService.findBySku(sku);
                    for(SkuInfoPO skuInfoPO : skuInfoPOList){
                        storeId = skuInfoPO.getStoreId();
                    }
                }
            }
            ExcelTransactionDetailPO excelTransactionDetailPO = new ExcelTransactionDetailPO();
            BeanUtils.copyProperties(item, excelTransactionDetailPO);
            excelTransactionDetailPO.setExcelId(excelId);
            excelTransactionDetailPO.setTransactionTime(transactionTime);
            if(null == excelTransactionDetailPO.getQuantity()){
                excelTransactionDetailPO.setQuantity(0);
            }
            excelTransactionDetailRepository.save(excelTransactionDetailPO);
        }
        excelTransactionPO.setDateFrom(dateFrom);
        excelTransactionPO.setDateTo(dateTo);
        excelTransactionPO.setStoreId(storeId);
        excelTransactionRepository.save(excelTransactionPO);
    }

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
        String weightRemark = "";
        BigDecimal sumWeight = new BigDecimal(0);
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
                    log.warn("box01 Weight: " + excelFbaRowEO.getColumn11());
                    excelFbaPO.setShipTo(column00.replace("Ship To: ", ""));
                    //
                    JSONObject boxCountJson = new JSONObject(excelFbaRowEO);
                    for(int i=11; i <= 40; i++){
                        String cellString = boxCountJson.getStr("column" + i);
                        if(StringUtils.isNotBlank(cellString)){
                            weightRemark = weightRemark + "+" + cellString;
                            sumWeight = sumWeight.add(new BigDecimal(cellString));
                        }
                    }
                    weightRemark = weightRemark.replaceFirst("\\+", "") + " = " + sumWeight;
                }else{
                    log.warn(column00);
                }
            }

            row++;
        }

        excelFbaPO.setBoxCount(boxCount);
        excelFbaPO.setWeightRemark(weightRemark);
        excelFbaPO.setWeight(sumWeight);
        excelFbaRepository.save(excelFbaPO);
        Integer storeId = null;
        for(ExcelFbaPackListPO excelFbaPackListPO : excelFbaPackListPOList){
            log.info("merchantSKU: " + excelFbaPackListPO.getMerchantSku());
            String sku = excelFbaPackListPO.getMerchantSku();
            excelFbaPackListPO.setExcelId(excelId);
            List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
            if(skuInfoPOList == null || skuInfoPOList.size() == 0){
                log.info("merchantSKU not found");
                excelFbaPackListRepository.save(excelFbaPackListPO);
                continue;
            }
            for(SkuInfoPO skuInfoPO : skuInfoPOList){
                log.info("merchantSKU found skuId: " + skuInfoPO.getId());
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
    public void insertFbatsvPackList(Integer excelId, List<ExcelFbatsvRowEO> excelFbatsvRow) {
        log.warn("ExcelServiceImpl.insertFbaPackList");
        int row = 0;
        int boxCount = 1;
        ExcelFbaPO excelFbaPO = excelFbaRepository.getOne(excelId);
        List<ExcelFbaPackListPO> excelFbaPackListPOList = new ArrayList<ExcelFbaPackListPO>();
        Boolean isDetail = false;
        String weightRemark = "";
        BigDecimal sumWeight = new BigDecimal(0);
        for(ExcelFbatsvRowEO excelFbatsvRowEO : excelFbatsvRow){
            log.warn("row " + row + " : " + new JSONObject(excelFbatsvRowEO).toString());
            String column00 = excelFbatsvRowEO.getColumn00();
            String column01 = excelFbatsvRowEO.getColumn01();
            if(row - 0 == 0){
                excelFbaPO.setShipmentId(column01);
            }else if(row - 1 == 0){
                excelFbaPO.setFbaName(column01);
            }else if(row - 2 == 0){
                excelFbaPO.setPlanId(column01);
            }else if(row - 3 == 0){
                excelFbaPO.setShipTo(column01);
            }else if(row >= 8){
                ExcelFbaPackListPO excelFbaPackListPO = new ExcelFbaPackListPO();
                excelFbaPackListPO.setMerchantSku(excelFbatsvRowEO.getColumn00());
                excelFbaPackListPO.setAsin(excelFbatsvRowEO.getColumn02());
                excelFbaPackListPO.setFnsku(excelFbatsvRowEO.getColumn03());
                excelFbaPackListPO.setExternalId(excelFbatsvRowEO.getColumn04());
                excelFbaPackListPO.setBoxedQty(excelFbatsvRowEO.getColumn09());
                excelFbaPackListPO.setBox01Qty(excelFbatsvRowEO.getColumn09());
                excelFbaPackListPOList.add(excelFbaPackListPO);
            }
            row++;
        }

        excelFbaPO.setBoxCount(1);
        excelFbaPO.setWeightRemark(weightRemark);
        excelFbaPO.setWeight(sumWeight);
        excelFbaRepository.save(excelFbaPO);
        Integer storeId = null;
        for(ExcelFbaPackListPO excelFbaPackListPO : excelFbaPackListPOList){
            log.info("merchantSKU: " + excelFbaPackListPO.getMerchantSku());
            String sku = excelFbaPackListPO.getMerchantSku();
            excelFbaPackListPO.setExcelId(excelId);
            List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
            if(skuInfoPOList == null || skuInfoPOList.size() == 0){
                log.info("merchantSKU not found");
                excelFbaPackListRepository.save(excelFbaPackListPO);
                continue;
            }
            for(SkuInfoPO skuInfoPO : skuInfoPOList){
                log.info("merchantSKU found skuId: " + skuInfoPO.getId());
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
        log.info("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelSupplierDeliveryPO.setCreateDate(new Date());
        excelSupplierDeliveryRepository.save(excelSupplierDeliveryPO);
        return excelSupplierDeliveryPO.getId();
    }

    @Override
    public Integer saveExcelFba(ExcelFbaPO excelFbaPO) {
        log.info("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelFbaPO.setCreateDate(new Date());
        excelFbaRepository.save(excelFbaPO);
        return excelFbaPO.getId();
    }

    @Override
    public Integer saveExcelTransaction(ExcelTransactionPO excelTransactionPO) {
        log.info("ExcelServiceImpl.saveExcelTransaction: " + excelTransactionPO);
        excelTransactionPO.setCreateDate(new Date());
        excelTransactionRepository.save(excelTransactionPO);
        return excelTransactionPO.getId();
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
    public ExcelTransactionPO getTransactionByExcelId(Integer excelId) {
        return excelTransactionRepository.getById(excelId);
    }

    @Override
    public List<ExcelFbaPackListPO> findFbaPackListByExcelId(Integer excelId) {
        return excelFbaPackListRepository.findByExcelId(excelId);
    }

    @Override
    public List<ExcelTransactionDetailPO> findTransactionDetailByExcelId(Integer excelId) {
        return excelTransactionDetailRepository.findByExcelId(excelId);
    }

    @Override
    public ExcelSupplierDeliveryOrderPO getExcelSupplierDeliveryOrderByExcelIdAndDingdanhao(Integer excelId, String dingdanghao) {
        if(excelId == null || excelId == 0 || StringUtils.isBlank(dingdanghao)){
            return null;
        }
        return excelSupplierDeliveryOrderRepository.getByExcelIdAndDingdanhao(excelId, dingdanghao);
    }
}
