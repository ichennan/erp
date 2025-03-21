package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.entity.excel.*;
import com.fiveamazon.erp.epo.*;
import com.fiveamazon.erp.repository.*;
import com.fiveamazon.erp.repository.excel.*;
import com.fiveamazon.erp.service.ExcelService;
import com.fiveamazon.erp.service.OverseaService;
import com.fiveamazon.erp.service.ShipmentService;
import com.fiveamazon.erp.service.SkuService;
import com.fiveamazon.erp.util.CommonUtils;
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
    private ExcelAzwsRepository excelAzwsRepository;
    @Autowired
    private ExcelAzpfRepository excelAzpfRepository;
    @Autowired
    private ExcelCarrierBillRepository excelCarrierBillRepository;
    @Autowired
    private ExcelCarrierBillDetailRepository excelCarrierBillDetailRepository;
    @Autowired
    private SkuService skuService;
    @Autowired
    private ShipmentService shipmentService;
    @Autowired
    private OverseaService overseaService;


    //---------------------------------------------
    // fileCategory = azpf
    //---------------------------------------------

    @Override
    public void insertAzpfRow(List<ExcelAzpfRowEO> excelAzpfRowEOList) {
        log.info("ExcelServiceImpl.insertAzpfRow [{}]");
        Integer storeId = null;
        for (ExcelAzpfRowEO row : excelAzpfRowEOList) {
            String sku = row.getSku();
            if (null == storeId) {
                log.info("storeId is null 1st");
                storeId = getStoreId(sku);
                if (null == storeId) {
                    continue;
                }
            }
            excelAzpfRepository.disableBySku(sku);
            ExcelAzpfPO item = new ExcelAzpfPO();
            BeanUtils.copyProperties(row, item);
            item.setDateStart(CommonUtils.dateFormatChange(10, 8, row.getDateStart()));
            item.setDateEnd(CommonUtils.dateFormatChange(10, 8, row.getDateEnd()));
            item.setStoreId(storeId);
            excelAzpfRepository.save(item);
        }
    }

    //---------------------------------------------
    // fileCategory = azws
    //---------------------------------------------

    @Override
    public void insertAzwsRow(List<ExcelAzwsRowEO> excelAzwsRowEOList) {
        log.info("ExcelServiceImpl.insertAzwsRow [{}]");
        Date today = new Date();
        String dateAzws = DateUtil.format(today, SimpleConstant.DATE_8);
        Integer storeId = null;
        for (ExcelAzwsRowEO item : excelAzwsRowEOList) {
            log.info("item: [{}]", item.toString());
            if (null == storeId) {
                log.info("storeId is null 1st");
                String sku = item.getSku();
                storeId = getStoreId(sku);
                log.info("getStoreId [{}]", storeId);
                if (null != storeId) {
                    log.info("storeId is null 2nd");
                    excelAzwsRepository.deleteByStoreId(storeId);
                } else {
                    log.info("storeId is not null 2nd");
                    continue;
                }
            }
            log.info("storeId is not null 1st [{}]", storeId);
            ExcelAzwsPO excelAzwsPO = new ExcelAzwsPO();
            BeanUtils.copyProperties(item, excelAzwsPO);
            excelAzwsPO.setStoreId(storeId);
            excelAzwsPO.setDateAzws(dateAzws);
            excelAzwsRepository.save(excelAzwsPO);
        }
    }

    //---------------------------------------------
    // fileCategory = transaction
    //---------------------------------------------

    @Override
    public void insertTransactionRow(Integer excelId, List<ExcelTransactionRowEO> excelTransactionRowEOList) {
        log.info("ExcelServiceImpl.insertExcelTransactionRow");
        ExcelTransactionPO excelTransactionPO = excelTransactionRepository.getById(excelId);
        Date dateFrom = null == excelTransactionPO.getDateFrom() ? DateUtil.parse("20990101", "yyyyMMdd") : excelTransactionPO.getDateFrom();
        Date dateTo = null == excelTransactionPO.getDateTo() ? DateUtil.parse("19990101", "yyyyMMdd") : excelTransactionPO.getDateTo();
        Integer storeId = excelTransactionPO.getStoreId();
        for (ExcelTransactionRowEO item : excelTransactionRowEOList) {
            Date transactionTime;
            try {
                transactionTime = DateUtil.parse(item.getTransactionTimeStr(), SimpleConstant.TRANSACTION_TIME_DATE_FORMAT);
            } catch (Exception e) {
                log.error("transactionTimeStr DateTime Format Error: " + item.getTransactionTimeStr());
                continue;
            }
            if (transactionTime.before(dateFrom)) {
                dateFrom = transactionTime;
            }
            if (transactionTime.after(dateTo)) {
                dateTo = transactionTime;
            }
            if (null == storeId) {
                String sku = item.getSku();
                String marketplace = item.getMarketplace();
                storeId = getStoreId(sku, marketplace);
            }
            ExcelTransactionDetailPO excelTransactionDetailPO = new ExcelTransactionDetailPO();
            BeanUtils.copyProperties(item, excelTransactionDetailPO);
            excelTransactionDetailPO.setExcelId(excelId);
            excelTransactionDetailPO.setTransactionTime(transactionTime);
            if (null == excelTransactionDetailPO.getQuantity()) {
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
    public Integer saveExcelTransaction(ExcelTransactionPO excelTransactionPO) {
        log.info("ExcelServiceImpl.saveExcelTransaction: " + excelTransactionPO);
        excelTransactionPO.setCreateDate(new Date());
        excelTransactionRepository.save(excelTransactionPO);
        return excelTransactionPO.getId();
    }

    @Override
    public ExcelTransactionPO getTransactionByExcelId(Integer excelId) {
        return excelTransactionRepository.getById(excelId);
    }

    @Override
    public List<ExcelTransactionDetailPO> findTransactionDetailByExcelId(Integer excelId) {
        return excelTransactionDetailRepository.findByExcelId(excelId);
    }


    //---------------------------------------------
    // fileCategory = carrierBillCainiao
    //---------------------------------------------

    @Override
    public void insertCarrierBillRow(Integer excelId, List<ExcelCarrierBillCainiaoRowEO> rowEOList) {
        log.info("ExcelServiceImpl.insertCarrierBillRow");
        ExcelCarrierBillPO excelCarrierBillPO = excelCarrierBillRepository.getById(excelId);
        Date dateFrom = null == excelCarrierBillPO.getDateFrom() ? DateUtil.parse("20990101", "yyyyMMdd") : excelCarrierBillPO.getDateFrom();
        Date dateTo = null == excelCarrierBillPO.getDateTo() ? DateUtil.parse("19990101", "yyyyMMdd") : excelCarrierBillPO.getDateTo();
        for (ExcelCarrierBillCainiaoRowEO item : rowEOList) {
            Integer seqNo = 0;
            try {
                seqNo = Integer.valueOf(item.getSeqNo());
            } catch (Exception e) {
                log.info("seqNo[{}], so break" + item.getSeqNo());
            }
            if (null == seqNo || seqNo == 0) {
                break;
            }
            Date billCreateDate;
            try {
                billCreateDate = DateUtil.parse(item.getBillCreateDate(), SimpleConstant.DATE_10);
            } catch (Exception e) {
                log.error("billCreateDate DateTime Format Error: " + item.getBillCreateDate());
                continue;
            }
            if (billCreateDate.before(dateFrom)) {
                dateFrom = billCreateDate;
            }
            if (billCreateDate.after(dateTo)) {
                dateTo = billCreateDate;
            }
            ExcelCarrierBillDetailPO detailPO = new ExcelCarrierBillDetailPO();
            BeanUtils.copyProperties(item, detailPO);
            detailPO.setExcelId(excelId);
            detailPO.setBillNo(detailPO.getTrackingNumber());
            setCarrierBillRelatedId(detailPO);
            excelCarrierBillDetailRepository.save(detailPO);
        }
        excelCarrierBillPO.setDateFrom(dateFrom);
        excelCarrierBillPO.setDateTo(dateTo);
        excelCarrierBillRepository.save(excelCarrierBillPO);
    }

    private void setCarrierBillRelatedId(ExcelCarrierBillDetailPO detailPO) {
        String fbaNo = detailPO.getFbaNo();
        String billNo = detailPO.getBillNo();
        ShipmentPO shipmentPO = shipmentService.getByFbaNo(fbaNo);
        if (null != shipmentPO) {
            detailPO.setRelatedShipmentId(shipmentPO.getId());
            return;
        }
        if (StringUtils.isBlank(billNo)) {
            return;
        }
        OverseaPO overseaPO = overseaService.getByDeliveryNo(billNo);
        if (null != overseaPO) {
            detailPO.setRelatedOverseaId(overseaPO.getId());
        }
    }

    @Override
    public Integer saveExcelCarrierBill(ExcelCarrierBillPO excelCarrierBillPO) {
        log.info("ExcelServiceImpl.saveExcelCarrierBill: " + excelCarrierBillPO);
        excelCarrierBillPO.setCreateDate(new Date());
        excelCarrierBillRepository.save(excelCarrierBillPO);
        return excelCarrierBillPO.getId();
    }

    @Override
    public List<ExcelCarrierBillDetailPO> findCarrierBillDetailByExcelId(Integer excelId) {
        return excelCarrierBillDetailRepository.findByExcelId(excelId);
    }

    @Override
    public ExcelCarrierBillPO getCarrierBillByExcelId(Integer excelId) {
        return excelCarrierBillRepository.getById(excelId);
    }

    //---------------------------------------------
    // fileCategory = supplierDelivery
    //---------------------------------------------

    @Override
    public void insertExcelSupplierDeliveryOrder(Integer excelId, List<ExcelSupplierDeliveryOrderEO> excelSupplierDeliveryOrderEOList) {
        log.warn("ExcelServiceImpl.insertExcelSupplierDeliveryOrder");
        for (ExcelSupplierDeliveryOrderEO excelSupplierDeliveryOrderEO : excelSupplierDeliveryOrderEOList) {
            if (StringUtils.isBlank(excelSupplierDeliveryOrderEO.getDingdanhao())) {
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
        for (ExcelSupplierDeliveryOrderDetailEO excelSupplierDeliveryOrderDetailEO : excelSupplierDeliveryOrderDetailEOList) {
            if (StringUtils.isBlank(excelSupplierDeliveryOrderDetailEO.getDingdanhao())) {
                continue;
            }
            ExcelSupplierDeliveryOrderDetailPO excelSupplierDeliveryOrderDetailPO = new ExcelSupplierDeliveryOrderDetailPO();
            BeanUtils.copyProperties(excelSupplierDeliveryOrderDetailEO, excelSupplierDeliveryOrderDetailPO);
            excelSupplierDeliveryOrderDetailPO.setExcelId(excelId);
            excelSupplierDeliveryOrderDetailRepository.save(excelSupplierDeliveryOrderDetailPO);
        }
    }

    @Override
    public Integer saveExcelSupplierDelivery(ExcelSupplierDeliveryPO excelSupplierDeliveryPO) {
        log.info("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelSupplierDeliveryPO.setCreateDate(new Date());
        excelSupplierDeliveryRepository.save(excelSupplierDeliveryPO);
        return excelSupplierDeliveryPO.getId();
    }

    @Override
    public List<ExcelSupplierDeliveryOrderDetailPO> findOrderDetailByExcelId(Integer excelId) {
        return excelSupplierDeliveryOrderDetailRepository.findByExcelId(excelId);
    }

    @Override
    public List<ExcelSupplierDeliveryOrderPO> findOrderByExcelId(Integer excelId) {
        return excelSupplierDeliveryOrderRepository.findByExcelId(excelId);
    }

    @Override
    public ExcelSupplierDeliveryOrderPO getExcelSupplierDeliveryOrderByExcelIdAndDingdanhao(Integer excelId, String dingdanghao) {
        if (excelId == null || excelId == 0 || StringUtils.isBlank(dingdanghao)) {
            return null;
        }
        return excelSupplierDeliveryOrderRepository.getByExcelIdAndDingdanhao(excelId, dingdanghao);
    }

    //---------------------------------------------
    // fileCategory = fba || fbaTsv || FBA-CSV
    //---------------------------------------------

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
        for (ExcelFbaRowEO excelFbaRowEO : excelFbaRow) {
            log.warn("row " + row + " : " + new JSONObject(excelFbaRowEO).toString());
            String column00 = excelFbaRowEO.getColumn00();
            if (StringUtils.isBlank(column00)) {
                continue;
            }
            log.warn("column00: " + column00 + " isDetail: " + isDetail);
            if (isDetail) {
                if (column00.contains("Plan ID:")) {
                    log.warn("Plan ID: " + column00.replace("Plan ID: ", ""));
                    excelFbaPO.setPlanId(column00.replace("Plan ID: ", ""));
                    isDetail = false;
                } else {
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
            } else {
                if (column00.contains("Shipment ID")) {
                    excelFbaPO.setShipmentId(excelFbaRowEO.getColumn01());
                } else if (column00.contains("Name:")) {
                    log.warn("Name: " + column00.replace("Name: ", ""));
                    excelFbaPO.setFbaName(column00.replace("Name: ", ""));
                } else if (column00.contains("Merchant SKU")) {
                    log.warn("Merchant SKU");
                    JSONObject boxCountJson = new JSONObject(excelFbaRowEO);
                    for (int i = 11; i <= 40; i++) {
                        String cellString = boxCountJson.getStr("column" + i);
                        if (StringUtils.isBlank(cellString)) {
                            break;
                        }
                        boxCount++;
                    }
                    isDetail = true;
                } else if (column00.contains("Ship To:")) {
                    log.warn("Ship To: " + column00.replace("Ship To: ", ""));
                    log.warn("box01 Weight: " + excelFbaRowEO.getColumn11());
                    excelFbaPO.setShipTo(column00.replace("Ship To: ", ""));
                    //
                    JSONObject boxCountJson = new JSONObject(excelFbaRowEO);
                    for (int i = 11; i <= 40; i++) {
                        String cellString = boxCountJson.getStr("column" + i);
                        if (StringUtils.isNotBlank(cellString)) {
                            weightRemark = weightRemark + "+" + cellString;
                            sumWeight = sumWeight.add(new BigDecimal(cellString));
                        }
                    }
                    weightRemark = weightRemark.replaceFirst("\\+", "") + " = " + sumWeight;
                } else {
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
        for (ExcelFbaPackListPO excelFbaPackListPO : excelFbaPackListPOList) {
            log.info("merchantSKU: " + excelFbaPackListPO.getMerchantSku());
            String sku = excelFbaPackListPO.getMerchantSku();
            excelFbaPackListPO.setExcelId(excelId);
            List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
            if (skuInfoPOList == null || skuInfoPOList.size() == 0) {
                log.info("merchantSKU not found");
                excelFbaPackListRepository.save(excelFbaPackListPO);
                continue;
            }
            for (SkuInfoPO skuInfoPO : skuInfoPOList) {
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
        for (ExcelFbatsvRowEO excelFbatsvRowEO : excelFbatsvRow) {
            log.warn("row " + row + " : " + new JSONObject(excelFbatsvRowEO).toString());
            String column00 = excelFbatsvRowEO.getColumn00();
            String column01 = excelFbatsvRowEO.getColumn01();
            if (row - 0 == 0) {
                excelFbaPO.setShipmentId(column01);
            } else if (row - 1 == 0) {
                excelFbaPO.setFbaName(column01);
            } else if (row - 2 == 0) {
                excelFbaPO.setPlanId(column01);
            } else if (row - 3 == 0) {
                excelFbaPO.setShipTo(column01);
            } else if (row >= 7) {
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
        for (ExcelFbaPackListPO excelFbaPackListPO : excelFbaPackListPOList) {
            log.info("merchantSKU: " + excelFbaPackListPO.getMerchantSku());
            String sku = excelFbaPackListPO.getMerchantSku();
            excelFbaPackListPO.setExcelId(excelId);
            List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
            if (skuInfoPOList == null || skuInfoPOList.size() == 0) {
                log.info("merchantSKU not found");
                excelFbaPackListRepository.save(excelFbaPackListPO);
                continue;
            }
            for (SkuInfoPO skuInfoPO : skuInfoPOList) {
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
    public void insertFbaCsvPackList(Integer excelId, List<ExcelFbaCsvRowEO> excelFbaCsvRow) {
        log.warn("ExcelServiceImpl.insertFbaPackList");
        int row = 0;
        int boxCount = 0;
        int skuCount = 0;
        ExcelFbaPO excelFbaPO = excelFbaRepository.getOne(excelId);
        List<ExcelFbaPackListPO> excelFbaPackListPOList = new ArrayList<ExcelFbaPackListPO>();
        Boolean isDetail = false;
        String weightRemark = "";
        BigDecimal sumWeight = new BigDecimal(0);
        for (ExcelFbaCsvRowEO rowEO : excelFbaCsvRow) {
            log.warn("row " + row + " : " + new JSONObject(rowEO).toString());
            String column00 = rowEO.getColumn00();
            String column01 = rowEO.getColumn01();
            if (row == 1) {
                log.warn("-----------------------1 [{}]", column01);
                excelFbaPO.setShipmentId(column01);
            } else if (row == 2) {
                log.info("2 [{}]", column01);
                excelFbaPO.setFbaName(column01);
            } else if (row == 3) {
                log.warn("-----------------------3 [{}]", column01);
                excelFbaPO.setShipTo(column01);
            } else if (row == 4) {
                log.warn("-----------------------4 [{}]", column01);
                boxCount = CommonUtils.str2Int(column01, true);
                excelFbaPO.setBoxCount(boxCount);
            } else if (row == 5) {
                log.warn("-----------------------5 [{}]", column01);
                skuCount = CommonUtils.str2Int(column01, true);
            } else if (row >= 9) {
                log.warn(">=9 [{}] [{}]", row, column01);
                if (row < (skuCount + 9)) {
                    log.warn("-----------------------sku [{}]", column01);

                    ExcelFbaPackListPO excelFbaPackListPO = new ExcelFbaPackListPO();
                    excelFbaPackListPO.setMerchantSku(rowEO.getColumn00());
                    excelFbaPackListPO.setAsin(rowEO.getColumn02());
                    excelFbaPackListPO.setFnsku(rowEO.getColumn03());
                    excelFbaPackListPO.setBoxedQty(rowEO.getColumn08());

                    excelFbaPackListPO.setBox01Qty(rowEO.getColumn09());
                    excelFbaPackListPO.setBox02Qty(rowEO.getColumn10());
                    excelFbaPackListPO.setBox03Qty(rowEO.getColumn11());
                    excelFbaPackListPO.setBox04Qty(rowEO.getColumn12());
                    excelFbaPackListPO.setBox05Qty(rowEO.getColumn13());
                    excelFbaPackListPO.setBox06Qty(rowEO.getColumn14());
                    excelFbaPackListPO.setBox07Qty(rowEO.getColumn15());
                    excelFbaPackListPO.setBox08Qty(rowEO.getColumn16());
                    excelFbaPackListPO.setBox09Qty(rowEO.getColumn17());
                    excelFbaPackListPO.setBox10Qty(rowEO.getColumn18());
                    excelFbaPackListPO.setBox11Qty(rowEO.getColumn19());
                    excelFbaPackListPO.setBox12Qty(rowEO.getColumn20());
                    excelFbaPackListPO.setBox13Qty(rowEO.getColumn21());
                    excelFbaPackListPO.setBox14Qty(rowEO.getColumn22());
                    excelFbaPackListPO.setBox15Qty(rowEO.getColumn23());
                    excelFbaPackListPO.setBox16Qty(rowEO.getColumn24());
                    excelFbaPackListPO.setBox17Qty(rowEO.getColumn25());
                    excelFbaPackListPO.setBox18Qty(rowEO.getColumn26());
                    excelFbaPackListPO.setBox19Qty(rowEO.getColumn27());
                    excelFbaPackListPO.setBox20Qty(rowEO.getColumn28());
                    excelFbaPackListPO.setBox21Qty(rowEO.getColumn29());
                    excelFbaPackListPO.setBox22Qty(rowEO.getColumn30());
                    excelFbaPackListPO.setBox23Qty(rowEO.getColumn31());
                    excelFbaPackListPO.setBox24Qty(rowEO.getColumn32());
                    excelFbaPackListPO.setBox25Qty(rowEO.getColumn33());
                    excelFbaPackListPO.setBox26Qty(rowEO.getColumn34());
                    excelFbaPackListPO.setBox27Qty(rowEO.getColumn35());
                    excelFbaPackListPO.setBox28Qty(rowEO.getColumn36());
                    excelFbaPackListPO.setBox29Qty(rowEO.getColumn37());
                    excelFbaPackListPO.setBox30Qty(rowEO.getColumn38());

                    excelFbaPackListPOList.add(excelFbaPackListPO);
                } else if (row >= (skuCount + 9 + 2) && row < (skuCount + 9 + 2 + 4)) {
                    log.warn("-----------------------value [{}]", rowEO.getColumn09());
                }
            } else {
//                log.info("else [{}] [{}]", row, column01);
            }
            row++;
        }
        excelFbaRepository.save(excelFbaPO);
        Integer storeId = null;
        for (ExcelFbaPackListPO excelFbaPackListPO : excelFbaPackListPOList) {
            log.info("merchantSKU: " + excelFbaPackListPO.getMerchantSku());
            String sku = excelFbaPackListPO.getMerchantSku();
            excelFbaPackListPO.setExcelId(excelId);
            List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
            if (skuInfoPOList == null || skuInfoPOList.size() == 0) {
                log.info("merchantSKU not found");
                excelFbaPackListRepository.save(excelFbaPackListPO);
                continue;
            }
            for (SkuInfoPO skuInfoPO : skuInfoPOList) {
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
    public Integer saveExcelFba(ExcelFbaPO excelFbaPO) {
        log.info("ExcelServiceImpl.saveExcelSupplierDelivery");
        excelFbaPO.setCreateDate(new Date());
        excelFbaRepository.save(excelFbaPO);
        return excelFbaPO.getId();
    }

    @Override
    public ExcelFbaPO getFbaByExcelId(Integer excelId) {
        return excelFbaRepository.getOne(excelId);
    }

    @Override
    public List<ExcelFbaPackListPO> findFbaPackListByExcelId(Integer excelId) {
        return excelFbaPackListRepository.findByExcelId(excelId);
    }

    //---------------------------------------------
    // not public function
    //---------------------------------------------

    Integer getStoreId(String sku, String marketplace) {
        if (StringUtils.isBlank(sku) || StringUtils.isBlank(marketplace)) {
            return null;
        }
        Integer storeId = null;
        List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
        for (SkuInfoPO skuInfoPO : skuInfoPOList) {
            storeId = skuInfoPO.getStoreId();
        }
        return storeId;
    }

    Integer getStoreId(String sku) {
        if (StringUtils.isBlank(sku)) {
            return null;
        }
        Integer storeId = null;
        List<SkuInfoPO> skuInfoPOList = skuService.findBySku(sku);
        for (SkuInfoPO skuInfoPO : skuInfoPOList) {
            storeId = skuInfoPO.getStoreId();
        }
        return storeId;
    }
}
