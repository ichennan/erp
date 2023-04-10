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
        contentWriteFont.setFontHeightInPoints((short)15);
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
        for(ProductPO productPO: productPOS){
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

    void purchaseFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson){
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
        for(PurchasePO item : purchasePOList){
            log.info("PurchasePO: " + item.toString());
            PurchaseDownloadDTO purchaseDownloadDTO = new PurchaseDownloadDTO();
            BeanUtils.copyProperties(item, purchaseDownloadDTO);
            purchaseDownloadDTOList.add(purchaseDownloadDTO);
            //
            purchaseCount++;
            purchaseAmount = purchaseAmount.add(item.getAmount());
            Integer id = item.getId();
            List<PurchaseDetailPO> detailList = purchaseService.findAllDetail(id);
            for(PurchaseDetailPO detailItem : detailList){
                log.info("PurchaseDetailPO: " + detailItem.toString());
                PurchaseDetailDownloadDTO purchaseDetailDownloadDTO = new PurchaseDetailDownloadDTO();
                BeanUtils.copyProperties(detailItem, purchaseDetailDownloadDTO);
                detailDownloadDTOList.add(purchaseDetailDownloadDTO);
                //
                purchaseProductQuantity += detailItem.getReceivedQuantity();
            }
        }
        monthPO.setPurchaseAmount(purchaseAmount);
        monthPO.setPurchaseCount(purchaseCount);
        monthPO.setPurchaseProductQuantity(purchaseProductQuantity);
        log.info("-------------------------------------");
        log.info("Purchase Amount: " + purchaseAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("采购批次").head(PurchaseDownloadDTO.class).build();
        WriteSheet writeSheet2 = EasyExcel.writerSheet("采购详情").head(PurchaseDetailDownloadDTO.class).build();
        excelWriter.write(purchaseDownloadDTOList, writeSheet1);
        excelWriter.write(detailDownloadDTOList, writeSheet2);

    }

    void fbaFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson){
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
        for(ShipmentPO item : shipmentPOList){
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
            for(ShipmentDetailPO detailItem : detailList){
                if(SimpleConstant.PLAN.equalsIgnoreCase(detailItem.getBox())){
                    continue;
                }
                ShipmentDetailDownloadDTO detailDownloadDTO = new ShipmentDetailDownloadDTO();
                BeanUtils.copyProperties(detailItem, detailDownloadDTO);
                detailDownloadDTOList.add(detailDownloadDTO);
                //
                productQuantity += detailItem.getQuantity();
                Integer productId = detailItem.getProductId();
                if(!SimpleConstant.FBA.equalsIgnoreCase(route)){
                    ProductPO productPO = productService.getById(productId);
                    if(null == productPO){
                        log.error("Error Product Id: " + productId);
                        continue;
                    }
                    BigDecimal pa = productPO.getPurchasePrice().multiply(
                            new BigDecimal(detailItem.getQuantity()));
                    productAmount = productAmount.add(pa);
                }
            }
        }
        monthPO.setFbaProductAmount(productAmount);
        monthPO.setFbaCount(itemCount);
        monthPO.setFbaShipmentAmount(shipmentAmount);
        monthPO.setFbaProductQuantity(productQuantity);
        log.info("-------------------------------------");
        log.info("FBA Shipment Amount: " + shipmentAmount);
        log.info("FBA Product Amount: " + productAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("FBA批次").head(ShipmentDownloadDTO.class).build();
        WriteSheet writeSheet2 = EasyExcel.writerSheet("FBA详情").head(ShipmentDetailDownloadDTO.class).build();
        excelWriter.write(downloadDTOList, writeSheet1);
        excelWriter.write(detailDownloadDTOList, writeSheet2);
    }

    void overseaFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson){
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
        for(OverseaPO item : list){
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
            for(OverseaDetailPO detailItem : detailList){
                OverseaDetailDownloadDTO detailDownloadDTO = new OverseaDetailDownloadDTO();
                BeanUtils.copyProperties(detailItem, detailDownloadDTO);
                detailDownloadDTOList.add(detailDownloadDTO);
                //
                productQuantity += detailItem.getQuantity();
                Integer productId = detailItem.getProductId();
                ProductPO productPO = productService.getById(productId);
                if(null == productPO){
                    log.error("Error Product Id: " + productId);
                    continue;
                }
                BigDecimal pa = productPO.getPurchasePrice().multiply(
                        new BigDecimal(detailItem.getQuantity()));
                productAmount = productAmount.add(pa);
            }
        }
        monthPO.setOverseaWarehouseAmount(warehouseAmount);
        monthPO.setOverseaProductAmount(productAmount);
        monthPO.setOverseaCount(itemCount);
        monthPO.setOverseaShipmentAmount(shipmentAmount);
        monthPO.setOverseaProductQuantity(productQuantity);
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

    void amazonFee(MonthPO monthPO, ExcelWriter excelWriter, JSONObject productAllJson){
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
        BigDecimal amazonOrderProductPurchaseAmount = new BigDecimal(0);
        BigDecimal amazonRefundProductPurchaseAmount = new BigDecimal(0);
        BigDecimal amazonOrderProductFreightAmount = new BigDecimal(0);
        BigDecimal amazonRefundProductFreightAmount = new BigDecimal(0);
        //
        List<TransactionPO> list = transactionService.findByDate(dateFrom, dateTo, storeId);
        for(TransactionPO item : list){
            String type = item.getType() == null ? "" : item.getType();
            BigDecimal total = item.getTotal();
            BigDecimal productSales = item.getProductSales();
            Integer quantity = item.getQuantity() == 0 ? 1 : item.getQuantity();
            String sku = item.getSku();
            switch (type){
                case SimpleConstant.AMAZON_TYPE_Coupon_Fee_TERRY:
                    amazonCouponFeeAmount = amazonCouponFeeAmount.add(total);
                    amazonCouponFeeQuantity += quantity;
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
                    amazonOrderProductPurchaseAmount = amazonOrderProductPurchaseAmount.add(orderProductCostDTO.getProductPurchaseAmount());
                    amazonOrderProductFreightAmount = amazonOrderProductFreightAmount.add(orderProductCostDTO.getProductFreightAmount());
                    break;
                case SimpleConstant.AMAZON_TYPE_Refund:
                    amazonProductSalesAmount = amazonProductSalesAmount.add(productSales);
                    amazonRefundAmount = amazonRefundAmount.add(total);
                    amazonRefundQuantity += quantity;
                    amazonProductSalesQuantity = amazonProductSalesQuantity - quantity;
                    ProductCostDTO refundProductCostDTO = calculateAmazonProductAmount(storeId, sku, quantity, productAllJson, skuAllJson);
                    amazonRefundProductPurchaseAmount = amazonRefundProductPurchaseAmount.add(refundProductCostDTO.getProductPurchaseAmount());
                    amazonRefundProductFreightAmount = amazonRefundProductFreightAmount.add(refundProductCostDTO.getProductFreightAmount());
                    break;
                case SimpleConstant.AMAZON_TYPE_Refund_Retrocharge:
                    amazonRefundRetrochargeAmount = amazonRefundRetrochargeAmount.add(total);
                    amazonRefundRetrochargeQuantity += quantity;
                    break;
                case SimpleConstant.AMAZON_TYPE_Service_Fee:
                    amazonServiceFeeAmount = amazonServiceFeeAmount.add(total);
                    amazonServiceFeeQuantity += quantity;
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
        monthPO.setAmazonOrderAmount(amazonOrderAmount);
        monthPO.setAmazonOrderQuantity(amazonOrderQuantity);
        monthPO.setAmazonOthersAmount(amazonOthersAmount);
        monthPO.setAmazonOthersQuantity(amazonOthersQuantity);
        monthPO.setAmazonRefundAmount(amazonRefundAmount);
        monthPO.setAmazonRefundQuantity(amazonRefundQuantity);
        monthPO.setAmazonRefundRetrochargeAmount(amazonRefundRetrochargeAmount);
        monthPO.setAmazonRefundRetrochargeQuantity(amazonRefundRetrochargeQuantity);
        monthPO.setAmazonServiceFeeAmount(amazonServiceFeeAmount);
        monthPO.setAmazonServiceFeeQuantity(amazonServiceFeeQuantity);
        monthPO.setAmazonAmount(amazonAmount);
        monthPO.setAmazonQuantity(amazonQuantity);
        monthPO.setAmazonTransferAmount(amazonTransferAmount);
        monthPO.setAmazonTransferQuantity(amazonTransferQuantity);
        monthPO.setAmazonOrderProductPurchaseAmount(amazonOrderProductPurchaseAmount);
        monthPO.setAmazonOrderProductFreightAmount(amazonOrderProductFreightAmount);
        monthPO.setAmazonOrderProductAmount(amazonOrderProductPurchaseAmount.add(amazonOrderProductFreightAmount));
        monthPO.setAmazonRefundProductPurchaseAmount(amazonRefundProductPurchaseAmount);
        monthPO.setAmazonRefundProductFreightAmount(amazonRefundProductFreightAmount);
        monthPO.setAmazonRefundProductAmount(amazonRefundProductPurchaseAmount.add(amazonRefundProductFreightAmount));
        monthPO.setAmazonProductSalesAmount(amazonProductSalesAmount);
        monthPO.setAmazonProductSalesQuantity(amazonProductSalesQuantity);
        monthPO.setAmazonCouponFeeAmount(amazonCouponFeeAmount);
        monthPO.setAmazonCouponFeeQuantity(amazonCouponFeeQuantity);
        monthPO.setAmazonDealFeeAmount(amazonDealFeeAmount);
        monthPO.setAmazonDealFeeQuantity(amazonDealFeeQuantity);
        log.info("-------------------------------------");
        log.info("Amazon Amount [{}] [{}]", amazonAmount, amazonQuantity);
        log.info("Transfer Amount [{}] [{}]",  amazonTransferQuantity, amazonTransferAmount);
        log.info("Order Product Purchase Amount [{}] [{}]", amazonOrderQuantity, amazonOrderProductPurchaseAmount);
        log.info("Order Product Freight Amount [{}] [{}]", amazonOrderQuantity, amazonOrderProductFreightAmount);
        log.info("Refund Product Purchase Amount [{}] [{}]", amazonRefundQuantity, amazonRefundProductPurchaseAmount);
        log.info("Refund Product Freight Amount [{}] [{}]", amazonRefundQuantity, amazonRefundProductFreightAmount);
        log.info("Product Sales [{}] [{}]", amazonProductSalesQuantity, amazonProductSalesAmount);
        log.info("Coupon Fee Amount [{}] [{}]", amazonCouponFeeQuantity, amazonCouponFeeAmount);
        log.info("-------------------------------------");

        WriteSheet writeSheet1 = EasyExcel.writerSheet("订单列表").head(OrderDownloadDTO.class).build();
        excelWriter.write(downloadDTOList, writeSheet1);
    }

    void sumFee(MonthPO monthPO, BigDecimal rate, ExcelWriter excelWriter, JSONObject productAllJson){
        log.info("-------------------------------------");
        log.info("SumFee");
        log.info("-------------------------------------");
        // BigDecimal fbaShipmentAmount = monthPO.getFbaShipmentAmount();
        // BigDecimal fbaProductAmount = monthPO.getFbaProductAmount();
        // BigDecimal overseaShipmentAmount = monthPO.getOverseaShipmentAmount();
        // BigDecimal overseaProductAmount = monthPO.getOverseaProductAmount();
        // BigDecimal overseaWarehouseAmount = monthPO.getOverseaWarehouseAmount();
        BigDecimal amazonOrderAmount = monthPO.getAmazonOrderAmount();
        BigDecimal amazonRefundAmount = monthPO.getAmazonRefundAmount();
        BigDecimal amazonProductSalesAmount = monthPO.getAmazonProductSalesAmount();
        BigDecimal amazonAmount = monthPO.getAmazonAmount();
        BigDecimal amazonTransferAmount = monthPO.getAmazonTransferAmount();
        BigDecimal amazonOrderProductAmount = monthPO.getAmazonOrderProductAmount();
        BigDecimal amazonRefundProductAmount = monthPO.getAmazonRefundProductAmount();
        BigDecimal amazonServiceFeeAmount = monthPO.getAmazonServiceFeeAmount();
        //
        BigDecimal amazonAmountCNY =  amazonAmount.multiply(rate);
        BigDecimal amazonOrderAmountCNY =  amazonOrderAmount.multiply(rate);
        BigDecimal amazonAmountTransferCNY =  amazonTransferAmount.multiply(rate);
        BigDecimal amazonProductSalesAmountCNY =  amazonProductSalesAmount.multiply(rate);
        BigDecimal amazonServiceFeeAmountCNY =  amazonServiceFeeAmount.multiply(rate);
        BigDecimal amazonOrderProductAmountUSD = amazonOrderProductAmount.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal amazonRefundProductAmountUSD = amazonRefundProductAmount.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal maoli = amazonAmount
                .subtract(amazonOrderProductAmountUSD)
                .add(amazonRefundProductAmountUSD.divide(new BigDecimal(2), 2, BigDecimal.ROUND_HALF_UP))
                ;
        BigDecimal liushui = amazonAmount;
        monthPO.setRate(rate);
        monthPO.setAmazonOrderAmountCNY(amazonOrderAmountCNY);
        monthPO.setAmazonAmountCNY(amazonAmountCNY);
        monthPO.setAmazonTransferAmountCNY(amazonAmountTransferCNY);
        monthPO.setAmazonProductSalesAmountCNY(amazonProductSalesAmountCNY);
        monthPO.setAmazonServiceFeeAmountCNY(amazonServiceFeeAmountCNY);
        monthPO.setMaoli(maoli);
        monthPO.setLiushui(liushui);
        log.info("-------------------------------------");
        log.info("Amazon Amount: " + amazonAmount);
        log.info("Maoli: " + maoli);
        log.info("Liushui: " + liushui);
        log.info("-------------------------------------");
    }

    private ProductCostDTO calculateAmazonProductAmount(Integer storeId, String sku, Integer quantity, JSONObject productAllJson, JSONObject skuAllJson){
        ProductCostDTO dto = new ProductCostDTO();
        BigDecimal productPurchaseAmount =  new BigDecimal(0);
        BigDecimal productFreightAmount =  new BigDecimal(0);
        if((StringUtils.isBlank(sku)) || (null == quantity) || (quantity == 0)){
            return dto;
        }
        JSONArray skuArray = skuAllJson.getJSONArray(sku);
        if(null == skuArray || skuArray.size() < 1){
            log.error("SKU Not Found: [{}]", sku);
            return dto;
        }
        for(JSONObject skuJson : skuArray.jsonIter()){
            Integer productId = skuJson.getInt("productId");
            JSONObject productJson = productAllJson.getJSONObject("id" + productId);
            if(null == productJson){
                log.error("Product Not Found: [{}]", productId);
                continue;
            }
            BigDecimal purchasePrice = productJson.getBigDecimal("purchasePrice");
            BigDecimal freightFee = new BigDecimal(15).multiply(productJson.getBigDecimal("weight")).setScale(2);
            if(purchasePrice.add(freightFee).compareTo(new BigDecimal(99999)) > 0){
                log.error("productId purchase price or weight error [{}]", productId);
            }
            productPurchaseAmount =  productPurchaseAmount.add(purchasePrice.multiply(new BigDecimal(quantity)));
            productFreightAmount =  productFreightAmount.add(freightFee.multiply(new BigDecimal(quantity)));
        }
//        log.info("amazonProductAmount: " + amazonProductAmount);
        dto.setProductPurchaseAmount(productPurchaseAmount);
        dto.setProductFreightAmount(productFreightAmount);
        return dto;
    }

    void updateParentStore(MonthPO monthPO){
        log.info("updateParentStore");
        String month = monthPO.getMonth();
        List<MonthPO> list = theRepository.findByMonth(month);
        MonthPO parentMonthPO = theRepository.getByMonthAndStoreId(month, SimpleConstant.parentStoreId);
        if(null == parentMonthPO){
            parentMonthPO = new MonthPO();
            parentMonthPO.setMonth(month);
            parentMonthPO.setStoreId(SimpleConstant.parentStoreId);
        }
        parentMonthPO.setRate(monthPO.getRate());
        BigDecimal purchaseAmount = monthPO.getPurchaseAmount();
        Integer purchaseProductQuantity = monthPO.getPurchaseProductQuantity();
        Integer purchaseCount = monthPO.getPurchaseCount();
        //
        // BigDecimal purchaseAmount = new BigDecimal(0);
        BigDecimal fbaShipmentAmount = new BigDecimal(0);
        BigDecimal fbaProductAmount = new BigDecimal(0);
        BigDecimal overseaWarehouseAmount = new BigDecimal(0);
        BigDecimal overseaShipmentAmount = new BigDecimal(0);
        BigDecimal overseaProductAmount = new BigDecimal(0);
        BigDecimal amazonAdjustmentAmount = new BigDecimal(0);
        BigDecimal amazonFbaCustomerReturnFeeAmount = new BigDecimal(0);
        BigDecimal amazonFbaInventoryFeeAmount = new BigDecimal(0);
        BigDecimal amazonFeeAdjustmentAmount = new BigDecimal(0);
        BigDecimal amazonOrderAmount = new BigDecimal(0);
        BigDecimal amazonOthersAmount = new BigDecimal(0);
        BigDecimal amazonRefundRetrochargeAmount = new BigDecimal(0);
        BigDecimal amazonRefundAmount = new BigDecimal(0);
        BigDecimal amazonServiceFeeAmount = new BigDecimal(0);
        BigDecimal amazonAmount = new BigDecimal(0);
        BigDecimal amazonTransferAmount = new BigDecimal(0);
        BigDecimal amazonOrderProductAmount = new BigDecimal(0);
        BigDecimal amazonRefundProductAmount = new BigDecimal(0);
        BigDecimal amazonProductSalesAmount = new BigDecimal(0);
        BigDecimal maoli = new BigDecimal(0);
        BigDecimal liushui = new BigDecimal(0);
        BigDecimal amazonProductSalesAmountCNY = new BigDecimal(0);
        BigDecimal amazonOrderAmountCNY = new BigDecimal(0);
        BigDecimal amazonAmountCNY = new BigDecimal(0);
        BigDecimal amazonTransferAmountCNY = new BigDecimal(0);
        BigDecimal amazonServiceFeeAmountCNY = new BigDecimal(0);
        //
        Integer overseaProductQuantity = 0;
        // Integer purchaseProductQuantity = 0;
        // Integer purchaseCount = 0;
        Integer fbaProductQuantity = 0;
        Integer fbaCount = 0;
        Integer overseaCount = 0;
        Integer amazonAdjustmentQuantity = 0;
        Integer amazonFbaCustomerReturnFeeQuantity = 0;
        Integer amazonFbaInventoryFeeQuantity = 0;
        Integer amazonFeeAdjustmentQuantity = 0;
        Integer amazonOrderQuantity = 0;
        Integer amazonOthersQuantity = 0;
        Integer amazonRefundQuantity = 0;
        Integer amazonRefundRetrochargeQuantity = 0;
        Integer amazonServiceFeeQuantity = 0;
        Integer amazonQuantity = 0;
        Integer amazonTransferQuantity = 0;

        for(MonthPO item : list){
            Integer storeId = item.getStoreId();
            if(SimpleConstant.parentStoreId.equals(storeId)){
                continue;
            }
            // purchaseAmount = item.getPurchaseAmount().add(purchaseAmount);
            fbaShipmentAmount = item.getFbaShipmentAmount().add(fbaShipmentAmount);
            fbaProductAmount = item.getFbaProductAmount().add(fbaProductAmount);
            overseaWarehouseAmount = item.getOverseaWarehouseAmount().add(overseaWarehouseAmount);
            overseaShipmentAmount = item.getOverseaShipmentAmount().add(overseaShipmentAmount);
            overseaProductAmount = item.getOverseaProductAmount().add(overseaProductAmount);
            amazonAdjustmentAmount = item.getAmazonAdjustmentAmount().add(amazonAdjustmentAmount);
            amazonFbaCustomerReturnFeeAmount = item.getAmazonFbaCustomerReturnFeeAmount().add(amazonFbaCustomerReturnFeeAmount);
            amazonFbaInventoryFeeAmount = item.getAmazonFbaInventoryFeeAmount().add(amazonFbaInventoryFeeAmount);
            amazonFeeAdjustmentAmount = item.getAmazonFeeAdjustmentAmount().add(amazonFeeAdjustmentAmount);
            amazonOrderAmount = item.getAmazonOrderAmount().add(amazonOrderAmount);
            amazonOthersAmount = item.getAmazonOthersAmount().add(amazonOthersAmount);
            amazonRefundRetrochargeAmount = item.getAmazonRefundRetrochargeAmount().add(amazonRefundRetrochargeAmount);
            amazonRefundAmount = item.getAmazonRefundAmount().add(amazonRefundAmount);
            amazonServiceFeeAmount = item.getAmazonServiceFeeAmount().add(amazonServiceFeeAmount);
            amazonAmount = item.getAmazonAmount().add(amazonAmount);
            amazonTransferAmount = item.getAmazonTransferAmount().add(amazonTransferAmount);
            amazonOrderProductAmount = item.getAmazonOrderProductAmount().add(amazonOrderProductAmount);
            amazonRefundProductAmount = item.getAmazonRefundProductAmount().add(amazonRefundProductAmount);
            amazonProductSalesAmount = item.getAmazonProductSalesAmount().add(amazonProductSalesAmount);
            amazonProductSalesAmountCNY = item.getAmazonProductSalesAmountCNY().add(amazonProductSalesAmountCNY);
            amazonOrderAmountCNY = item.getAmazonOrderAmountCNY().add(amazonOrderAmountCNY);
            amazonAmountCNY = item.getAmazonAmountCNY().add(amazonAmountCNY);
            amazonTransferAmountCNY = item.getAmazonTransferAmountCNY().add(amazonTransferAmountCNY);
            amazonServiceFeeAmountCNY = item.getAmazonServiceFeeAmountCNY().add(amazonServiceFeeAmountCNY);
            maoli = item.getMaoli().add(maoli);
            liushui = item.getLiushui().add(liushui);
            //
            overseaProductQuantity += item.getOverseaProductQuantity();
            // purchaseProductQuantity += item.getPurchaseProductQuantity();
            // purchaseCount += item.getPurchaseCount();
            fbaProductQuantity += item.getFbaProductQuantity();
            fbaCount += item.getFbaCount();
            overseaCount += item.getOverseaCount();
            amazonAdjustmentQuantity += item.getAmazonAdjustmentQuantity();
            amazonFbaCustomerReturnFeeQuantity += item.getAmazonFbaCustomerReturnFeeQuantity();
            amazonFbaInventoryFeeQuantity += item.getAmazonFbaInventoryFeeQuantity();
            amazonFeeAdjustmentQuantity += item.getAmazonFeeAdjustmentQuantity();
            amazonOrderQuantity += item.getAmazonOrderQuantity();
            amazonOthersQuantity += item.getAmazonOthersQuantity();
            amazonRefundQuantity += item.getAmazonRefundQuantity();
            amazonRefundRetrochargeQuantity += item.getAmazonRefundRetrochargeQuantity();
            amazonServiceFeeQuantity += item.getAmazonServiceFeeQuantity();
            amazonQuantity += item.getAmazonQuantity();
            amazonTransferQuantity += item.getAmazonTransferQuantity();
        }
        parentMonthPO.setPurchaseAmount(purchaseAmount);
        parentMonthPO.setFbaShipmentAmount(fbaShipmentAmount);
        parentMonthPO.setFbaProductAmount(fbaProductAmount);
        parentMonthPO.setOverseaWarehouseAmount(overseaWarehouseAmount);
        parentMonthPO.setOverseaShipmentAmount(overseaShipmentAmount);
        parentMonthPO.setOverseaProductAmount(overseaProductAmount);
        parentMonthPO.setAmazonAdjustmentAmount(amazonAdjustmentAmount);
        parentMonthPO.setAmazonFbaCustomerReturnFeeAmount(amazonFbaCustomerReturnFeeAmount);
        parentMonthPO.setAmazonFbaInventoryFeeAmount(amazonFbaInventoryFeeAmount);
        parentMonthPO.setAmazonFeeAdjustmentAmount(amazonFeeAdjustmentAmount);
        parentMonthPO.setAmazonOrderAmount(amazonOrderAmount);
        parentMonthPO.setAmazonOthersAmount(amazonOthersAmount);
        parentMonthPO.setAmazonRefundRetrochargeAmount(amazonRefundRetrochargeAmount);
        parentMonthPO.setAmazonRefundAmount(amazonRefundAmount);
        parentMonthPO.setAmazonServiceFeeAmount(amazonServiceFeeAmount);
        parentMonthPO.setAmazonAmount(amazonAmount);
        parentMonthPO.setAmazonTransferAmount(amazonTransferAmount);
        parentMonthPO.setAmazonOrderProductAmount(amazonOrderProductAmount);
        parentMonthPO.setAmazonRefundProductAmount(amazonRefundProductAmount);
        parentMonthPO.setAmazonProductSalesAmount(amazonProductSalesAmount);
        parentMonthPO.setAmazonProductSalesAmountCNY(amazonProductSalesAmountCNY);
        parentMonthPO.setAmazonOrderAmountCNY(amazonOrderAmountCNY);
        parentMonthPO.setAmazonAmountCNY(amazonAmountCNY);
        parentMonthPO.setAmazonTransferAmountCNY(amazonTransferAmountCNY);
        parentMonthPO.setAmazonServiceFeeAmountCNY(amazonServiceFeeAmountCNY);
        parentMonthPO.setMaoli(maoli);
        // liushui = amazonAmount;
        parentMonthPO.setLiushui(liushui);
        //
        parentMonthPO.setOverseaProductQuantity(overseaProductQuantity);
        parentMonthPO.setPurchaseProductQuantity(purchaseProductQuantity);
        parentMonthPO.setPurchaseCount(purchaseCount);
        parentMonthPO.setFbaProductQuantity(fbaProductQuantity);
        parentMonthPO.setFbaCount(fbaCount);
        parentMonthPO.setOverseaCount(overseaCount);
        parentMonthPO.setAmazonAdjustmentQuantity(amazonAdjustmentQuantity);
        parentMonthPO.setAmazonFbaCustomerReturnFeeQuantity(amazonFbaCustomerReturnFeeQuantity);
        parentMonthPO.setAmazonFbaInventoryFeeQuantity(amazonFbaInventoryFeeQuantity);
        parentMonthPO.setAmazonFeeAdjustmentQuantity(amazonFeeAdjustmentQuantity);
        parentMonthPO.setAmazonOrderQuantity(amazonOrderQuantity);
        parentMonthPO.setAmazonOthersQuantity(amazonOthersQuantity);
        parentMonthPO.setAmazonRefundQuantity(amazonRefundQuantity);
        parentMonthPO.setAmazonRefundRetrochargeQuantity(amazonRefundRetrochargeQuantity);
        parentMonthPO.setAmazonServiceFeeQuantity(amazonServiceFeeQuantity);
        parentMonthPO.setAmazonQuantity(amazonQuantity);
        parentMonthPO.setAmazonTransferQuantity(amazonTransferQuantity);
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
        for(int theMonth = monthStart; theMonth <= monthEnd; theMonth++){
            for(StorePO storePO : storePOList){
                MonthPO monthPO = theRepository.getByMonthAndStoreId(String.valueOf(theMonth), storePO.getId());
                if(null == monthPO){
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
        for(int theMonth = generateMonthStart; theMonth <= generateMonthEnd; theMonth++){
            for(Integer storeId : storeIdList){
                log.info("generate [{}] [{}]", theMonth, storeId);
                StorePO storePO = productService.getStoreById(storeId);
                MonthPO monthPO = theRepository.getByMonthAndStoreId(String.valueOf(theMonth), storePO.getId());
                if(null == monthPO){
                    monthPO = new MonthPO();
                    monthPO.setMonth(String.valueOf(theMonth));
                    monthPO.setStoreId(storePO.getId());
                    save(monthPO);
                }
                generate(monthPO.getId());
            }
        }
    }
}
