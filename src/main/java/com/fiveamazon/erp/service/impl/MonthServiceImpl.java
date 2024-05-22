package com.fiveamazon.erp.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.fiveamazon.erp.common.SimpleConstant;
import com.fiveamazon.erp.dto.ProductCostDTO;
import com.fiveamazon.erp.dto.download.*;
import com.fiveamazon.erp.entity.*;
import com.fiveamazon.erp.repository.MonthRepository;
import com.fiveamazon.erp.service.*;
import com.fiveamazon.erp.util.FreezeAndFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@Slf4j
public class MonthServiceImpl implements MonthService {
    @Autowired
    private MonthRepository theRepository;
    @Autowired
    PurchaseService purchaseService;
    @Autowired
    ShipmentService shipmentService;
    @Autowired
    OverseaService overseaService;
    @Autowired
    ProductService productService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    SmartService smartService;
    @Autowired
    SkuService skuService;
    @Value("${simple.folder.file.report}")
    private String reportFileFolder;

    @Override
    public MonthPO getById(Integer id) {
        return theRepository.getById(id);
    }

    @Override
    public MonthPO save(MonthPO item) {
        return theRepository.save(item);
    }

    @Override
    public List<MonthPO> findAll(Sort sort) {
        return theRepository.findAll(sort);
    }

    @Override
    public void generate(Integer id) {
        log.info("MonthServiceImpl.generate id: " + id);
        //
        String filename = reportFileFolder + "test.xlsx";
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        WriteFont headWriteFont = new WriteFont();
        headWriteFont.setFontHeightInPoints((short) 15);
        headWriteFont.setBold(false);
        headWriteCellStyle.setWriteFont(headWriteFont);
        // excel内容策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        WriteFont contentWriteFont = new WriteFont();
        contentWriteFont.setFontHeightInPoints((short) 15);
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 设置handler
        HorizontalCellStyleStrategy styleStrategy =
                new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
        //
//        ExcelWriter excelWriter = EasyExcel.write(response.getOutputStream()).registerWriteHandler(new FreezeAndFilter()).registerWriteHandler(styleStrategy).build();
        ExcelWriter excelWriter = EasyExcel.write(filename).registerWriteHandler(new FreezeAndFilter()).registerWriteHandler(styleStrategy).build();
        //
        MonthPO monthPO = getById(id);
        String month = monthPO.getMonth();
        BigDecimal rate = smartService.getRate(month);
        String dateFrom = month + "01";
        Date dateFromDate = DateUtil.parse(dateFrom, SimpleConstant.DATE_8);
        Date dateToDate = DateUtil.endOfMonth(dateFromDate);
        String dateTo = DateUtil.format(dateToDate, SimpleConstant.DATE_8);
        monthPO.setDateFrom(dateFrom);
        monthPO.setDateTo(dateTo);
//        DateUtil.endOfMonth()
        //
        JSONObject productAllJson = new JSONObject();
        List<ProductPO> productPOS = productService.findAll("name");
        for (ProductPO productPO : productPOS) {
            productAllJson.put("id" + productPO.getId(), productPO.toJson());
        }
        purchaseFee(monthPO, excelWriter, productAllJson);
        fbaFee(monthPO, excelWriter, productAllJson);
        overseaFee(monthPO, excelWriter, productAllJson);
        amazonFee(monthPO, excelWriter, productAllJson);
        sumFee(monthPO, rate, excelWriter, productAllJson);
        log.error("monthPO: " + monthPO.toString());
        save(monthPO);
        updateParentStore(monthPO);
        excelWriter.finish();
    }

    void purchaseFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson) {
        log.info("-------------------------------------");
        log.info("PurchaseFee");
        log.info("-------------------------------------");
        List<PurchaseDownloadDTO> purchaseDownloadDTOList = new ArrayList<>();
        List<PurchaseDetailDownloadDTO> detailDownloadDTOList = new ArrayList<>();
        //
        BigDecimal purchaseAmount = new BigDecimal(0);
        Integer purchaseCount = 0;
        Integer purchaseProductQuantity = 0;
        List<PurchasePO> purchasePOList = purchaseService.findByDate(monthPO.getDateFrom(), monthPO.getDateTo());
        for (PurchasePO item : purchasePOList) {
            log.info("PurchasePO: " + item.toString());
            PurchaseDownloadDTO purchaseDownloadDTO = new PurchaseDownloadDTO();
            BeanUtils.copyProperties(item, purchaseDownloadDTO);
            purchaseDownloadDTOList.add(purchaseDownloadDTO);
            //
            purchaseCount++;
            purchaseAmount = purchaseAmount.add(item.getAmount());
            Integer id = item.getId();
            List<PurchaseDetailPO> detailList = purchaseService.findAllDetail(id);
            for (PurchaseDetailPO detailItem : detailList) {
                log.info("PurchaseDetailPO: " + detailItem.toString());
                PurchaseDetailDownloadDTO purchaseDetailDownloadDTO = new PurchaseDetailDownloadDTO();
                BeanUtils.copyProperties(detailItem, purchaseDetailDownloadDTO);
                detailDownloadDTOList.add(purchaseDetailDownloadDTO);
                //
                purchaseProductQuantity += detailItem.getReceivedQuantity();
            }
        }
        // monthPO.setPurchaseAmount(purchaseAmount);
        // monthPO.setPurchaseCount(purchaseCount);
        // monthPO.setPurchaseProductQuantity(purchaseProductQuantity);
        log.info("-------------------------------------");
        log.info("Purchase Amount: " + purchaseAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("采购批次").head(PurchaseDownloadDTO.class).build();
        WriteSheet writeSheet2 = EasyExcel.writerSheet("采购详情").head(PurchaseDetailDownloadDTO.class).build();
        excelWriter.write(purchaseDownloadDTOList, writeSheet1);
        excelWriter.write(detailDownloadDTOList, writeSheet2);

    }

    void fbaFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson) {
        log.info("-------------------------------------");
        log.info("FbaFee");
        log.info("-------------------------------------");
        List<ShipmentDownloadDTO> downloadDTOList = new ArrayList<>();
        List<ShipmentDetailDownloadDTO> detailDownloadDTOList = new ArrayList<>();
        //
        Integer storeId = monthPO.getStoreId();
        BigDecimal shipmentAmount = new BigDecimal(0);
        BigDecimal productAmount = new BigDecimal(0);
        Integer itemCount = 0;
        Integer productQuantity = 0;
        List<ShipmentPO> shipmentPOList = shipmentService.findByDate(monthPO.getDateFrom(), monthPO.getDateTo(), storeId);
        for (ShipmentPO item : shipmentPOList) {
            log.info("ShipmentPO: " + item.toString());
            ShipmentDownloadDTO downloadDTO = new ShipmentDownloadDTO();
            BeanUtils.copyProperties(item, downloadDTO);
            downloadDTOList.add(downloadDTO);
            //
            itemCount++;
            shipmentAmount = shipmentAmount.add(item.getAmount());
            Integer id = item.getId();
            List<ShipmentDetailPO> detailList = shipmentService.findAllDetail(id);
            String route = item.getRoute();
            for (ShipmentDetailPO detailItem : detailList) {
                if (SimpleConstant.PLAN.equalsIgnoreCase(detailItem.getBox())) {
                    continue;
                }
                ShipmentDetailDownloadDTO detailDownloadDTO = new ShipmentDetailDownloadDTO();
                BeanUtils.copyProperties(detailItem, detailDownloadDTO);
                detailDownloadDTOList.add(detailDownloadDTO);
                //
                productQuantity += detailItem.getQuantity();
                Integer productId = detailItem.getProductId();
                if (!SimpleConstant.FBA.equalsIgnoreCase(route)) {
                    ProductPO productPO = productService.getById(productId);
                    if (null == productPO) {
                        log.error("Error Product Id: " + productId);
                        continue;
                    }
                    BigDecimal pa = productPO.getPurchasePrice().multiply(
                            new BigDecimal(detailItem.getQuantity()));
                    productAmount = productAmount.add(pa);
                }
            }
        }
        // monthPO.setFbaProductAmount(productAmount);
        // monthPO.setFbaCount(itemCount);
        // monthPO.setFbaShipmentAmount(shipmentAmount);
        // monthPO.setFbaProductQuantity(productQuantity);
        log.info("-------------------------------------");
        log.info("FBA Shipment Amount: " + shipmentAmount);
        log.info("FBA Product Amount: " + productAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("FBA批次").head(ShipmentDownloadDTO.class).build();
        WriteSheet writeSheet2 = EasyExcel.writerSheet("FBA详情").head(ShipmentDetailDownloadDTO.class).build();
        excelWriter.write(downloadDTOList, writeSheet1);
        excelWriter.write(detailDownloadDTOList, writeSheet2);
    }

    void overseaFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson) {
        log.info("-------------------------------------");
        log.info("OverseaFee");
        log.info("-------------------------------------");
        List<OverseaDownloadDTO> downloadDTOList = new ArrayList<>();
        List<OverseaDetailDownloadDTO> detailDownloadDTOList = new ArrayList<>();
        Integer storeId = monthPO.getStoreId();
        BigDecimal shipmentAmount = new BigDecimal(0);
        BigDecimal productAmount = new BigDecimal(0);
        BigDecimal warehouseAmount = new BigDecimal(0);
        Integer itemCount = 0;
        Integer productQuantity = 0;
        List<OverseaPO> list = overseaService.findByDate(monthPO.getDateFrom(), monthPO.getDateTo(), storeId);
        for (OverseaPO item : list) {
            log.info("OverseaPO: " + item.toString());
            OverseaDownloadDTO downloadDTO = new OverseaDownloadDTO();
            BeanUtils.copyProperties(item, downloadDTO);
            downloadDTOList.add(downloadDTO);
            //
            itemCount++;
            shipmentAmount = shipmentAmount.add(item.getAmount());
            warehouseAmount = warehouseAmount.add(item.getWarehouseAmount());
            Integer id = item.getId();
            List<OverseaDetailPO> detailList = overseaService.findAllDetail(id);
            for (OverseaDetailPO detailItem : detailList) {
                OverseaDetailDownloadDTO detailDownloadDTO = new OverseaDetailDownloadDTO();
                BeanUtils.copyProperties(detailItem, detailDownloadDTO);
                detailDownloadDTOList.add(detailDownloadDTO);
                //
                productQuantity += detailItem.getQuantity();
                Integer productId = detailItem.getProductId();
                ProductPO productPO = productService.getById(productId);
                if (null == productPO) {
                    log.error("Error Product Id: " + productId);
                    continue;
                }
                BigDecimal pa = productPO.getPurchasePrice().multiply(
                        new BigDecimal(detailItem.getQuantity()));
                productAmount = productAmount.add(pa);
            }
        }
        // monthPO.setOverseaWarehouseAmount(warehouseAmount);
        // monthPO.setOverseaProductAmount(productAmount);
        // monthPO.setOverseaCount(itemCount);
        // monthPO.setOverseaShipmentAmount(shipmentAmount);
        // monthPO.setOverseaProductQuantity(productQuantity);
        log.info("-------------------------------------");
        log.info("Oversea Warehouse Amount: " + warehouseAmount);
        log.info("Oversea Shipment Amount: " + shipmentAmount);
        log.info("Oversea Product Amount: " + productAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("海外仓批次").head(OverseaDownloadDTO.class).build();
        WriteSheet writeSheet2 = EasyExcel.writerSheet("海外仓详情").head(OverseaDetailDownloadDTO.class).build();
        excelWriter.write(downloadDTOList, writeSheet1);
        excelWriter.write(detailDownloadDTOList, writeSheet2);
    }

    void amazonFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson) {
        log.info("-------------------------------------");
        log.info("AmazonFee");
        log.info("-------------------------------------");
        List<OrderDownloadDTO> downloadDTOList = new ArrayList<>();
        Date dateFrom = DateUtil.parse(monthPO.getDateFrom(), SimpleConstant.DATE_8);
        Date dateTo = DateUtil.endOfDay(DateUtil.parse(monthPO.getDateTo(), SimpleConstant.DATE_8));
        Integer storeId = monthPO.getStoreId();
        JSONObject skuAllJson = skuService.findAllByStoreId(storeId);

        BigDecimal amazonAdjustmentAmount = new BigDecimal(0);
        Integer amazonAdjustmentQuantity = 0;
        BigDecimal amazonFbaCustomerReturnFeeAmount = new BigDecimal(0);
        Integer amazonFbaCustomerReturnFeeQuantity = 0;
        BigDecimal amazonFbaInventoryFeeAmount = new BigDecimal(0);
        Integer amazonFbaInventoryFeeQuantity = 0;
        BigDecimal amazonFeeAdjustmentAmount = new BigDecimal(0);
        Integer amazonFeeAdjustmentQuantity = 0;
        BigDecimal amazonOrderAmount = new BigDecimal(0);
        Integer amazonOrderQuantity = 0;
        BigDecimal amazonOthersAmount = new BigDecimal(0);
        Integer amazonOthersQuantity = 0;
        BigDecimal amazonRefundAmount = new BigDecimal(0);
        Integer amazonRefundQuantity = 0;
        BigDecimal amazonRefundRetrochargeAmount = new BigDecimal(0);
        Integer amazonRefundRetrochargeQuantity = 0;
        BigDecimal amazonServiceFeeAmount = new BigDecimal(0);
        Integer amazonServiceFeeQuantity = 0;
        BigDecimal amazonAmount = new BigDecimal(0);
        Integer amazonQuantity = 0;
        BigDecimal amazonTransferAmount = new BigDecimal(0);
        Integer amazonTransferQuantity = 0;
        BigDecimal amazonDealFeeAmount = new BigDecimal(0);
        Integer amazonDealFeeQuantity = 0;
        BigDecimal amazonCouponFeeAmount = new BigDecimal(0);
        Integer amazonCouponFeeQuantity = 0;
        BigDecimal amazonProductSalesAmount = new BigDecimal(0);
        Integer amazonProductSalesQuantity = 0;
        //
        BigDecimal erpOrderProductPurchaseAmount = new BigDecimal(0);
        BigDecimal erpRefundProductPurchaseAmount = new BigDecimal(0);
        BigDecimal erpOrderProductFreightAmount = new BigDecimal(0);
        BigDecimal erpRefundProductFreightAmount = new BigDecimal(0);
        //
        List<TransactionPO> list = transactionService.findByDate(dateFrom, dateTo, storeId);
        for (TransactionPO item : list) {
            String type = item.getType() == null ? "" : item.getType();
            BigDecimal total = item.getTotal();
            BigDecimal productSales = item.getProductSales();
            Integer quantity = item.getQuantity() == 0 ? 1 : item.getQuantity();
            String sku = item.getSku();
            switch (type) {
                // 2023-06-01之后优惠券类型由空变为ServiceFee。
                case SimpleConstant.AMAZON_TYPE_Coupon_Fee_TERRY:
                    // amazonCouponFeeAmount = amazonCouponFeeAmount.add(total);
                    // amazonCouponFeeQuantity += quantity;
                    // break;
                case SimpleConstant.AMAZON_TYPE_Service_Fee:
                    amazonServiceFeeAmount = amazonServiceFeeAmount.add(total);
                    amazonServiceFeeQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_Adjustment:
                    amazonAdjustmentAmount = amazonAdjustmentAmount.add(total);
                    amazonAdjustmentQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_FBA_Customer_Return_Fee:
                    amazonFbaCustomerReturnFeeAmount = amazonFbaCustomerReturnFeeAmount.add(total);
                    amazonFbaCustomerReturnFeeQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_FBA_Inventory_Fee:
                    amazonFbaInventoryFeeAmount = amazonFbaInventoryFeeAmount.add(total);
                    amazonFbaInventoryFeeQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_Fee_Adjustment:
                    amazonFeeAdjustmentAmount = amazonFeeAdjustmentAmount.add(total);
                    amazonFeeAdjustmentQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_ORDER:
                    OrderDownloadDTO downloadDTO = new OrderDownloadDTO();
                    BeanUtils.copyProperties(item, downloadDTO);
                    downloadDTOList.add(downloadDTO);
                    //
                    amazonProductSalesAmount = amazonProductSalesAmount.add(productSales);
                    amazonOrderAmount = amazonOrderAmount.add(total);
                    amazonOrderQuantity += quantity;
                    amazonProductSalesQuantity = amazonProductSalesQuantity + quantity;
                    ProductCostDTO orderProductCostDTO = calculateAmazonProductAmount(storeId, sku, quantity, productAllJson, skuAllJson);
                    erpOrderProductPurchaseAmount = erpOrderProductPurchaseAmount.add(orderProductCostDTO.getProductPurchaseAmount());
                    erpOrderProductFreightAmount = erpOrderProductFreightAmount.add(orderProductCostDTO.getProductFreightAmount());
                    break;
                case SimpleConstant.AMAZON_TYPE_Refund:
                    amazonProductSalesAmount = amazonProductSalesAmount.add(productSales);
                    amazonRefundAmount = amazonRefundAmount.add(total);
                    amazonRefundQuantity += quantity;
                    amazonProductSalesQuantity = amazonProductSalesQuantity - quantity;
                    ProductCostDTO refundProductCostDTO = calculateAmazonProductAmount(storeId, sku, quantity, productAllJson, skuAllJson);
                    erpRefundProductPurchaseAmount = erpRefundProductPurchaseAmount.add(refundProductCostDTO.getProductPurchaseAmount());
                    erpRefundProductFreightAmount = erpRefundProductFreightAmount.add(refundProductCostDTO.getProductFreightAmount());
                    break;
                case SimpleConstant.AMAZON_TYPE_Refund_Retrocharge:
                    amazonRefundRetrochargeAmount = amazonRefundRetrochargeAmount.add(total);
                    amazonRefundRetrochargeQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_Transfer:
                    amazonTransferAmount = amazonTransferAmount.add(total);
                    amazonTransferQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_DEAL_FEE:
                    amazonDealFeeAmount = amazonDealFeeAmount.add(total);
                    amazonDealFeeQuantity += quantity;
                    break;
                default:
                    log.info("===============type=================");
                    log.info("type [{}] [{}]", type, total);
                    log.info("===============type=================");
                    amazonOthersAmount = amazonOthersAmount.add(total);
                    amazonOthersQuantity += quantity;
                    break;
            }
        }
        amazonAmount = amazonAdjustmentAmount
                .add(amazonDealFeeAmount)
                .add(amazonCouponFeeAmount)
                .add(amazonFbaCustomerReturnFeeAmount)
                .add(amazonFbaInventoryFeeAmount)
                .add(amazonFeeAdjustmentAmount)
                .add(amazonOrderAmount)
                .add(amazonOthersAmount)
                .add(amazonRefundAmount)
                .add(amazonRefundRetrochargeAmount)
                .add(amazonServiceFeeAmount);
        amazonQuantity = amazonAdjustmentQuantity
                + (amazonDealFeeQuantity)
                + (amazonCouponFeeQuantity)
                + (amazonFbaCustomerReturnFeeQuantity)
                + (amazonFbaInventoryFeeQuantity)
                + (amazonFeeAdjustmentQuantity)
                + (amazonOrderQuantity)
                + (amazonOthersQuantity)
                + (amazonRefundQuantity)
                + (amazonRefundRetrochargeQuantity)
                + (amazonServiceFeeQuantity);
        monthPO.setAmazonAdjustmentAmount(amazonAdjustmentAmount);
        monthPO.setAmazonAdjustmentQuantity(amazonAdjustmentQuantity);
        monthPO.setAmazonFbaCustomerReturnFeeAmount(amazonFbaCustomerReturnFeeAmount);
        monthPO.setAmazonFbaCustomerReturnFeeQuantity(amazonFbaCustomerReturnFeeQuantity);
        monthPO.setAmazonFbaInventoryFeeAmount(amazonFbaInventoryFeeAmount);
        monthPO.setAmazonFbaInventoryFeeQuantity(amazonFbaInventoryFeeQuantity);
        monthPO.setAmazonFeeAdjustmentAmount(amazonFeeAdjustmentAmount);
        monthPO.setAmazonFeeAdjustmentQuantity(amazonFeeAdjustmentQuantity);
        monthPO.setAmazonOthersAmount(amazonOthersAmount);
        monthPO.setAmazonOthersQuantity(amazonOthersQuantity);
        monthPO.setAmazonRefundRetrochargeAmount(amazonRefundRetrochargeAmount);
        monthPO.setAmazonRefundRetrochargeQuantity(amazonRefundRetrochargeQuantity);
        monthPO.setAmazonServiceFeeAmount(amazonServiceFeeAmount);
        monthPO.setAmazonServiceFeeQuantity(amazonServiceFeeQuantity);
        monthPO.setAmazonAmount(amazonAmount);
        monthPO.setAmazonQuantity(amazonQuantity);
        monthPO.setAmazonTransferAmount(amazonTransferAmount);
        monthPO.setAmazonTransferQuantity(amazonTransferQuantity);
        // 订单销售额 = orderProductSales + refundProductSales
        monthPO.setAmazonProductSalesAmount(amazonProductSalesAmount);
        monthPO.setAmazonProductSalesQuantity(amazonProductSalesQuantity);
        // 订单回款金额
        monthPO.setAmazonOrderAmount(amazonOrderAmount);
        monthPO.setAmazonOrderQuantity(amazonOrderQuantity);
        monthPO.setAmazonRefundAmount(amazonRefundAmount);
        monthPO.setAmazonRefundQuantity(amazonRefundQuantity);
        monthPO.setAmazonProductPaymentQuantity(amazonOrderQuantity + amazonRefundQuantity);
        monthPO.setAmazonProductPaymentAmount(amazonOrderAmount.add(amazonRefundAmount));
        // 订单采购及运费
        monthPO.setErpOrderProductPurchaseAmount(erpOrderProductPurchaseAmount);
        monthPO.setErpOrderProductFreightAmount(erpOrderProductFreightAmount);
        monthPO.setErpOrderProductAmount(erpOrderProductPurchaseAmount.add(erpOrderProductFreightAmount));
        monthPO.setErpRefundProductPurchaseAmount(erpRefundProductPurchaseAmount);
        monthPO.setErpRefundProductFreightAmount(erpRefundProductFreightAmount);
        monthPO.setErpRefundProductAmount(erpRefundProductPurchaseAmount.add(erpRefundProductFreightAmount));
        //
        monthPO.setAmazonCouponFeeAmount(amazonCouponFeeAmount);
        monthPO.setAmazonCouponFeeQuantity(amazonCouponFeeQuantity);
        monthPO.setAmazonDealFeeAmount(amazonDealFeeAmount);
        monthPO.setAmazonDealFeeQuantity(amazonDealFeeQuantity);
        log.info("-------------------------------------");
        log.info("Amazon Amount [{}] [{}]", amazonAmount, amazonQuantity);
        log.info("Transfer Amount [{}] [{}]", amazonTransferQuantity, amazonTransferAmount);
        log.info("Order Product Purchase Amount [{}] [{}]", amazonOrderQuantity, erpOrderProductPurchaseAmount);
        log.info("Order Product Freight Amount [{}] [{}]", amazonOrderQuantity, erpOrderProductFreightAmount);
        log.info("Refund Product Purchase Amount [{}] [{}]", amazonRefundQuantity, erpRefundProductPurchaseAmount);
        log.info("Refund Product Freight Amount [{}] [{}]", amazonRefundQuantity, erpRefundProductFreightAmount);
        log.info("Product Sales [{}] [{}]", amazonProductSalesQuantity, amazonProductSalesAmount);
        log.info("Coupon Fee Amount [{}] [{}]", amazonCouponFeeQuantity, amazonCouponFeeAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("订单列表").head(OrderDownloadDTO.class).build();
        excelWriter.write(downloadDTOList, writeSheet1);
    }

    void sumFee(MonthPO monthPO, BigDecimal rate, ExcelWriter excelWriter, JSONObject productAllJson) {
        log.info("-------------------------------------");
        log.info("SumFee");
        log.info("-------------------------------------");
        monthPO.setRate(rate);
        BigDecimal amazonAmount = monthPO.getAmazonAmount();
        //
        BigDecimal erpOrderProductAmount = monthPO.getErpOrderProductAmount();
        BigDecimal erpRefundProductAmount = monthPO.getErpRefundProductAmount();
        BigDecimal erpOrderProductAmountUSD = cny2usd(rate, erpOrderProductAmount);
        BigDecimal erpRefundProductAmountUSD = cny2usd(rate, erpRefundProductAmount);
        monthPO.setErpOrderProductAmountUSD(erpOrderProductAmountUSD);
        monthPO.setErpRefundProductAmountUSD(erpRefundProductAmountUSD);
        // 成本 = 卖出产品采购及运费 -1/2退货产品采购及运费
        BigDecimal chengben =
                erpOrderProductAmount
                        .subtract(erpRefundProductAmount.divide(new BigDecimal(2), 2, BigDecimal.ROUND_HALF_UP));
        BigDecimal chengbenUSD = cny2usd(rate, chengben);
        monthPO.setChengben(chengben);
        monthPO.setChengbenUSD(chengbenUSD);
        //利润 = amazonAmount - chengbenUSD
        BigDecimal lirun = amazonAmount.add(chengbenUSD);
        monthPO.setLirun(lirun);
        log.info("-------------------------------------");
        log.info("Amazon Amount: " + amazonAmount);
        log.info("Chengben: " + chengben);
        log.info("ChengbenUSD: " + chengbenUSD);
        log.info("Lirun: " + lirun);
        log.info("-------------------------------------");
    }

    private ProductCostDTO calculateAmazonProductAmount(Integer storeId, String sku, Integer quantity, JSONObject productAllJson, JSONObject skuAllJson) {
        ProductCostDTO dto = new ProductCostDTO();
        BigDecimal productPurchaseAmount = new BigDecimal(0);
        BigDecimal productFreightAmount = new BigDecimal(0);
        if ((StringUtils.isBlank(sku)) || (null == quantity) || (quantity == 0)) {
            return dto;
        }
        JSONArray skuArray = skuAllJson.getJSONArray(sku);
        if (null == skuArray || skuArray.size() < 1) {
            log.error("SKU Not Found: [{}]", sku);
            return dto;
        }
        for (JSONObject skuJson : skuArray.jsonIter()) {
            Integer productId = skuJson.getInt("productId");
            JSONObject productJson = productAllJson.getJSONObject("id" + productId);
            if (null == productJson) {
                log.error("Product Not Found: [{}]", productId);
                continue;
            }
            BigDecimal purchasePrice = mm1(productJson.getBigDecimal("purchasePrice"));
            BigDecimal freightFee = mm1(new BigDecimal(15).multiply(productJson.getBigDecimal("weight")).setScale(2));
            if (purchasePrice.add(freightFee).compareTo(new BigDecimal(-99999)) < 0) {
                log.error("productId purchase price or weight error [{}]", productId);
            }
            productPurchaseAmount = productPurchaseAmount.add(purchasePrice.multiply(new BigDecimal(quantity)));
            productFreightAmount = productFreightAmount.add(freightFee.multiply(new BigDecimal(quantity)));
        }
//        log.info("amazonProductAmount: " + amazonProductAmount);
        dto.setProductPurchaseAmount(productPurchaseAmount);
        dto.setProductFreightAmount(productFreightAmount);
        return dto;
    }

    void updateParentStore(MonthPO monthPO) {
        log.info("updateParentStore");
        String month = monthPO.getMonth();
        List<MonthPO> list = theRepository.findByMonth(month);
        MonthPO parentMonthPO = theRepository.getByMonthAndStoreId(month, SimpleConstant.parentStoreId);
        if (null == parentMonthPO) {
            parentMonthPO = new MonthPO();
            parentMonthPO.setMonth(month);
            parentMonthPO.setStoreId(SimpleConstant.parentStoreId);
        }
        parentMonthPO.setRate(monthPO.getRate());
        // 小啰啰 10对
        Integer amazonAdjustmentQuantity = 0;
        BigDecimal amazonAdjustmentAmount = new BigDecimal(0);
        Integer amazonFbaCustomerReturnFeeQuantity = 0;
        BigDecimal amazonFbaCustomerReturnFeeAmount = new BigDecimal(0);
        Integer amazonFbaInventoryFeeQuantity = 0;
        BigDecimal amazonFbaInventoryFeeAmount = new BigDecimal(0);
        Integer amazonFeeAdjustmentQuantity = 0;
        BigDecimal amazonFeeAdjustmentAmount = new BigDecimal(0);
        Integer amazonOthersQuantity = 0;
        BigDecimal amazonOthersAmount = new BigDecimal(0);
        Integer amazonRefundRetrochargeQuantity = 0;
        BigDecimal amazonRefundRetrochargeAmount = new BigDecimal(0);
        Integer amazonServiceFeeQuantity = 0;
        BigDecimal amazonServiceFeeAmount = new BigDecimal(0);
        Integer amazonTransferQuantity = 0;
        BigDecimal amazonTransferAmount = new BigDecimal(0);
        Integer amazonDealFeeQuantity = 0;
        BigDecimal amazonDealFeeAmount = new BigDecimal(0);
        Integer amazonCouponFeeQuantity = 0;
        BigDecimal amazonCouponFeeAmount = new BigDecimal(0);
        // order 8
        Integer amazonOrderQuantity = 0;
        BigDecimal amazonOrderAmount = new BigDecimal(0);
        BigDecimal erpOrderProductAmount = new BigDecimal(0);
        BigDecimal erpOrderProductAmountUSD = new BigDecimal(0);
        BigDecimal erpOrderProductPurchaseAmount = new BigDecimal(0);
        BigDecimal erpOrderProductPurchaseAmountUSD = new BigDecimal(0);
        BigDecimal erpOrderProductFreightAmount = new BigDecimal(0);
        BigDecimal erpOrderProductFreightAmountUSD = new BigDecimal(0);
        // refund 8
        Integer amazonRefundQuantity = 0;
        BigDecimal amazonRefundAmount = new BigDecimal(0);
        BigDecimal erpRefundProductAmount = new BigDecimal(0);
        BigDecimal erpRefundProductAmountUSD = new BigDecimal(0);
        BigDecimal erpRefundProductPurchaseAmount = new BigDecimal(0);
        BigDecimal erpRefundProductPurchaseAmountUSD = new BigDecimal(0);
        BigDecimal erpRefundProductFreightAmount = new BigDecimal(0);
        BigDecimal erpRefundProductFreightAmountUSD = new BigDecimal(0);
        // zong 9
        Integer amazonProductSalesQuantity = 0;
        BigDecimal amazonProductSalesAmount = new BigDecimal(0);
        Integer amazonProductPaymentQuantity = 0;
        BigDecimal amazonProductPaymentAmount = new BigDecimal(0);
        BigDecimal chengben = new BigDecimal(0);
        BigDecimal chengbenUSD = new BigDecimal(0);
        Integer amazonQuantity = 0;
        BigDecimal amazonAmount = new BigDecimal(0);
        BigDecimal lirun = new BigDecimal(0);
        //

        for (MonthPO item : list) {
            Integer storeId = item.getStoreId();
            if (SimpleConstant.parentStoreId.equals(storeId)) {
                continue;
            }

            // 小啰啰 10对
            amazonAdjustmentQuantity = add(amazonAdjustmentQuantity, item.getAmazonAdjustmentQuantity());
            amazonAdjustmentAmount = add(amazonAdjustmentAmount, item.getAmazonAdjustmentAmount());
            amazonFbaCustomerReturnFeeQuantity = add(amazonFbaCustomerReturnFeeQuantity, item.getAmazonFbaCustomerReturnFeeQuantity());
            amazonFbaCustomerReturnFeeAmount = add(amazonFbaCustomerReturnFeeAmount, item.getAmazonFbaCustomerReturnFeeAmount());
            amazonFbaInventoryFeeQuantity = add(amazonFbaInventoryFeeQuantity, item.getAmazonFbaInventoryFeeQuantity());
            amazonFbaInventoryFeeAmount = add(amazonFbaInventoryFeeAmount, item.getAmazonFbaInventoryFeeAmount());
            amazonFeeAdjustmentQuantity = add(amazonFeeAdjustmentQuantity, item.getAmazonFeeAdjustmentQuantity());
            amazonFeeAdjustmentAmount = add(amazonFeeAdjustmentAmount, item.getAmazonFeeAdjustmentAmount());
            amazonOthersQuantity = add(amazonOthersQuantity, item.getAmazonOthersQuantity());
            amazonOthersAmount = add(amazonOthersAmount, item.getAmazonOthersAmount());
            amazonRefundRetrochargeQuantity = add(amazonRefundRetrochargeQuantity, item.getAmazonRefundRetrochargeQuantity());
            amazonRefundRetrochargeAmount = add(amazonRefundRetrochargeAmount, item.getAmazonRefundRetrochargeAmount());
            amazonServiceFeeQuantity = add(amazonServiceFeeQuantity, item.getAmazonServiceFeeQuantity());
            amazonServiceFeeAmount = add(amazonServiceFeeAmount, item.getAmazonServiceFeeAmount());
            amazonTransferQuantity = add(amazonTransferQuantity, item.getAmazonTransferQuantity());
            amazonTransferAmount = add(amazonTransferAmount, item.getAmazonTransferAmount());
            amazonDealFeeQuantity = add(amazonDealFeeQuantity, item.getAmazonDealFeeQuantity());
            amazonDealFeeAmount = add(amazonDealFeeAmount, item.getAmazonDealFeeAmount());
            amazonCouponFeeQuantity = add(amazonCouponFeeQuantity, item.getAmazonCouponFeeQuantity());
            amazonCouponFeeAmount = add(amazonCouponFeeAmount, item.getAmazonCouponFeeAmount());
            // order 8
            amazonOrderQuantity = add(amazonOrderQuantity, item.getAmazonOrderQuantity());
            amazonOrderAmount = add(amazonOrderAmount, item.getAmazonOrderAmount());
            erpOrderProductAmount = add(erpOrderProductAmount, item.getErpOrderProductAmount());
            erpOrderProductAmountUSD = add(erpOrderProductAmountUSD, item.getErpOrderProductAmountUSD());
            erpOrderProductPurchaseAmount = add(erpOrderProductPurchaseAmount, item.getErpOrderProductPurchaseAmount());
            erpOrderProductPurchaseAmountUSD = add(erpOrderProductPurchaseAmountUSD, item.getErpOrderProductPurchaseAmountUSD());
            erpOrderProductFreightAmount = add(erpOrderProductFreightAmount, item.getErpOrderProductFreightAmount());
            erpOrderProductFreightAmountUSD = add(erpOrderProductFreightAmountUSD, item.getErpOrderProductFreightAmountUSD());
            // refund 8
            amazonRefundQuantity = add(amazonRefundQuantity, item.getAmazonRefundQuantity());
            amazonRefundAmount = add(amazonRefundAmount, item.getAmazonRefundAmount());
            erpRefundProductAmount = add(erpRefundProductAmount, item.getErpRefundProductAmount());
            erpRefundProductAmountUSD = add(erpRefundProductAmountUSD, item.getErpRefundProductAmountUSD());
            erpRefundProductPurchaseAmount = add(erpRefundProductPurchaseAmount, item.getErpRefundProductPurchaseAmount());
            erpRefundProductPurchaseAmountUSD = add(erpRefundProductPurchaseAmountUSD, item.getErpRefundProductPurchaseAmountUSD());
            erpRefundProductFreightAmount = add(erpRefundProductFreightAmount, item.getErpRefundProductFreightAmount());
            erpRefundProductFreightAmountUSD = add(erpRefundProductFreightAmountUSD, item.getErpRefundProductFreightAmountUSD());
            // zong 9
            amazonProductSalesQuantity = add(amazonProductSalesQuantity, item.getAmazonProductSalesQuantity());
            amazonProductSalesAmount = add(amazonProductSalesAmount, item.getAmazonProductSalesAmount());
            amazonProductPaymentQuantity = add(amazonProductPaymentQuantity, item.getAmazonProductPaymentQuantity());
            amazonProductPaymentAmount = add(amazonProductPaymentAmount, item.getAmazonProductPaymentAmount());
            chengben = add(chengben, item.getChengben());
            chengbenUSD = add(chengbenUSD, item.getChengbenUSD());
            amazonQuantity = add(amazonQuantity, item.getAmazonQuantity());
            amazonAmount = add(amazonAmount, item.getAmazonAmount());
            lirun = add(lirun, item.getLirun());
        }
        // 小啰啰10对
        parentMonthPO.setAmazonAdjustmentQuantity(amazonAdjustmentQuantity);
        parentMonthPO.setAmazonAdjustmentAmount(amazonAdjustmentAmount);
        parentMonthPO.setAmazonFbaCustomerReturnFeeQuantity(amazonFbaCustomerReturnFeeQuantity);
        parentMonthPO.setAmazonFbaCustomerReturnFeeAmount(amazonFbaCustomerReturnFeeAmount);
        parentMonthPO.setAmazonFbaInventoryFeeQuantity(amazonFbaInventoryFeeQuantity);
        parentMonthPO.setAmazonFbaInventoryFeeAmount(amazonFbaInventoryFeeAmount);
        parentMonthPO.setAmazonFeeAdjustmentQuantity(amazonFeeAdjustmentQuantity);
        parentMonthPO.setAmazonFeeAdjustmentAmount(amazonFeeAdjustmentAmount);
        parentMonthPO.setAmazonOthersQuantity(amazonOthersQuantity);
        parentMonthPO.setAmazonOthersAmount(amazonOthersAmount);
        parentMonthPO.setAmazonRefundRetrochargeQuantity(amazonRefundRetrochargeQuantity);
        parentMonthPO.setAmazonRefundRetrochargeAmount(amazonRefundRetrochargeAmount);
        parentMonthPO.setAmazonServiceFeeQuantity(amazonServiceFeeQuantity);
        parentMonthPO.setAmazonServiceFeeAmount(amazonServiceFeeAmount);
        parentMonthPO.setAmazonTransferQuantity(amazonTransferQuantity);
        parentMonthPO.setAmazonTransferAmount(amazonTransferAmount);
        parentMonthPO.setAmazonDealFeeQuantity(amazonDealFeeQuantity);
        parentMonthPO.setAmazonDealFeeAmount(amazonDealFeeAmount);
        parentMonthPO.setAmazonCouponFeeQuantity(amazonCouponFeeQuantity);
        parentMonthPO.setAmazonCouponFeeAmount(amazonCouponFeeAmount);
        // order 8
        parentMonthPO.setAmazonOrderQuantity(amazonOrderQuantity);
        parentMonthPO.setAmazonOrderAmount(amazonOrderAmount);
        parentMonthPO.setErpOrderProductAmount(erpOrderProductAmount);
        parentMonthPO.setErpOrderProductAmountUSD(erpOrderProductAmountUSD);
        parentMonthPO.setErpOrderProductPurchaseAmount(erpOrderProductPurchaseAmount);
        parentMonthPO.setErpOrderProductPurchaseAmountUSD(erpOrderProductPurchaseAmountUSD);
        parentMonthPO.setErpOrderProductFreightAmount(erpOrderProductFreightAmount);
        parentMonthPO.setErpOrderProductFreightAmountUSD(erpOrderProductFreightAmountUSD);
        // refund 8
        parentMonthPO.setAmazonRefundQuantity(amazonRefundQuantity);
        parentMonthPO.setAmazonRefundAmount(amazonRefundAmount);
        parentMonthPO.setErpRefundProductAmount(erpRefundProductAmount);
        parentMonthPO.setErpRefundProductAmountUSD(erpRefundProductAmountUSD);
        parentMonthPO.setErpRefundProductPurchaseAmount(erpRefundProductPurchaseAmount);
        parentMonthPO.setErpRefundProductPurchaseAmountUSD(erpRefundProductPurchaseAmountUSD);
        parentMonthPO.setErpRefundProductFreightAmount(erpRefundProductFreightAmount);
        parentMonthPO.setErpRefundProductFreightAmountUSD(erpRefundProductFreightAmountUSD);
        // zong 9
        parentMonthPO.setAmazonProductSalesQuantity(amazonProductSalesQuantity);
        parentMonthPO.setAmazonProductSalesAmount(amazonProductSalesAmount);
        parentMonthPO.setAmazonProductPaymentQuantity(amazonProductPaymentQuantity);
        parentMonthPO.setAmazonProductPaymentAmount(amazonProductPaymentAmount);
        parentMonthPO.setChengben(chengben);
        parentMonthPO.setChengbenUSD(chengbenUSD);
        parentMonthPO.setAmazonQuantity(amazonQuantity);
        parentMonthPO.setAmazonAmount(amazonAmount);
        parentMonthPO.setLirun(lirun);
        //
        save(parentMonthPO);
    }

    public void autoCreate() {
        Date today = new Date();
        Integer year = DateUtil.year(today);
        Integer month = DateUtil.month(today);
        Integer monthStart = year * 100 + 1;
        Integer monthEnd = year * 100 + month;

        List<StorePO> storePOList = productService.findAllStore();
        for (int theMonth = monthStart; theMonth <= monthEnd; theMonth++) {
            for (StorePO storePO : storePOList) {
                MonthPO monthPO = theRepository.getByMonthAndStoreId(String.valueOf(theMonth), storePO.getId());
                if (null == monthPO) {
                    monthPO = new MonthPO();
                    monthPO.setMonth(String.valueOf(theMonth));
                    monthPO.setStoreId(storePO.getId());
                    save(monthPO);
                }
            }
        }
    }

    @Override
    public void generate(Integer year, Integer monthStart, Integer monthEnd, List<Integer> storeIdList) {
        log.info("generate [{}] [{}] [{}] [{}]", year, monthStart, monthEnd, storeIdList);
        Integer generateMonthStart = year * 100 + monthStart;
        Integer generateMonthEnd = year * 100 + monthEnd;
        for (int theMonth = generateMonthStart; theMonth <= generateMonthEnd; theMonth++) {
            for (Integer storeId : storeIdList) {
                log.info("generate [{}] [{}]", theMonth, storeId);
                StorePO storePO = productService.getStoreById(storeId);
                MonthPO monthPO = theRepository.getByMonthAndStoreId(String.valueOf(theMonth), storePO.getId());
                if (null == monthPO) {
                    monthPO = new MonthPO();
                    monthPO.setMonth(String.valueOf(theMonth));
                    monthPO.setStoreId(storePO.getId());
                    save(monthPO);
                }
                generate(monthPO.getId());
            }
        }
    }

    private BigDecimal cny2usd(BigDecimal rate, BigDecimal c) {
        return c.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal mm1(BigDecimal b) {
        return b.multiply(new BigDecimal(-1));
    }

    private BigDecimal add(BigDecimal a, BigDecimal b) {
        return (null == a ? new BigDecimal(0) : a)
                .add((null == b ? new BigDecimal(0) : b));
    }

    private Integer add(Integer a, Integer b) {
        return (null == a ? 0 : a) + (null == b ? 0 : b);
    }
}
