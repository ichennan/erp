// package com.fiveamazon.erp;
//
// import cn.hutool.core.date.DateUtil;
// import com.fiveamazon.erp.common.SimpleConstant;
// import com.fiveamazon.erp.entity.*;
// import com.fiveamazon.erp.service.*;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.commons.lang.StringUtils;
// import org.junit.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.test.context.junit4.SpringRunner;
//
// import javax.transaction.Transactional;
// import java.math.BigDecimal;
// import java.util.ArrayList;
// import java.util.Date;
// import java.util.List;
//
// @RunWith(SpringRunner.class)
// @SpringBootTest
// @Slf4j
// public class ErpApplicationTests {
//     @Autowired
//     PurchaseService purchaseService;
//     @Autowired
//     ShipmentService shipmentService;
//     @Autowired
//     OverseaService overseaService;
//     @Autowired
//     ProductService productService;
//     @Autowired
//     TransactionService transactionService;
//     @Autowired
//     SkuService skuService;
//     @Autowired
//     MonthService monthService;
//
//     @Test
//     public void contextLoads() {
//         log.info("15818699587");
//         log.info(new BCryptPasswordEncoder().encode("15818699587"));
//         test();
//     }
//
//     @Transactional
//     public void test(){
//         List<MonthPO> monthPOList = new ArrayList<>();
//         String month = "000000";
//         MonthPO monthPO = new MonthPO();
//         monthPO.setMonth(month);
//         String dateFrom = "20210701";
//         String dateTo = "20210730";
//         monthPO.setDateFrom(dateFrom);
//         monthPO.setDateTo(dateTo);
//         monthPO.setStoreId(211);
//         purchaseFee(monthPO);
//         fbaFee(monthPO);
//         overseaFee(monthPO);
//         amazonFee(monthPO);
//         log.error("monthPO: " + monthPO.toString());
//         monthService.save(monthPO);
//     }
//
//     void purchaseFee(MonthPO monthPO){
//         log.info("-------------------------------------");
//         BigDecimal purchaseAmount = new BigDecimal(0);
//         Integer purchaseCount = 0;
//         Integer purchaseProductQuantity = 0;
//         List<PurchasePO> purchasePOList = purchaseService.findByDate(monthPO.getDateFrom(), monthPO.getDateTo());
//         for(PurchasePO item : purchasePOList){
//             log.info("PurchasePO: " + item.toString());
//             purchaseCount++;
//             purchaseAmount = purchaseAmount.add(item.getAmount());
//             Integer id = item.getId();
//             List<PurchaseDetailPO> detailList = purchaseService.findAllDetail(id);
//             for(PurchaseDetailPO detailItem : detailList){
//                 purchaseProductQuantity += detailItem.getReceivedQuantity();
//                 log.info("PurchaseDetailPO: " + detailItem.toString());
//             }
//         }
//         // monthPO.setPurchaseAmount(purchaseAmount);
//         // monthPO.setPurchaseCount(purchaseCount);
//         // monthPO.setPurchaseProductQuantity(purchaseProductQuantity);
//         log.info("-------------------------------------");
//         log.info("Purchase Amount: " + purchaseAmount);
//         log.info("-------------------------------------");
//     }
//
//     void fbaFee(MonthPO monthPO){
//         String month = monthPO.getMonth();
//         Integer storeId = monthPO.getStoreId();
//         BigDecimal shipmentAmount = new BigDecimal(0);
//         BigDecimal productAmount = new BigDecimal(0);
//         Integer itemCount = 0;
//         Integer productQuantity = 0;
//         List<ShipmentPO> shipmentPOList = shipmentService.findByDate(monthPO.getDateFrom(), monthPO.getDateTo(), storeId);
//         for(ShipmentPO item : shipmentPOList){
//             log.info("ShipmentPO: " + item.toString());
//             itemCount++;
//             shipmentAmount = shipmentAmount.add(item.getAmount());
//             Integer id = item.getId();
//             List<ShipmentDetailPO> detailList = shipmentService.findAllDetail(id);
//             for(ShipmentDetailPO detailItem : detailList){
//                 productQuantity += detailItem.getQuantity();
//                 Integer productId = detailItem.getProductId();
//                 ProductPO productPO = productService.getById(productId);
//                 if(null == productPO){
//                     log.error("Error Product Id: " + productId);
//                     continue;
//                 }
//                 BigDecimal pa = productPO.getPurchasePrice().multiply(
//                         new BigDecimal(detailItem.getQuantity()));
//                 productAmount = productAmount.add(pa);
//             }
//         }
//         monthPO.setFbaProductAmount(productAmount);
//         monthPO.setFbaCount(itemCount);
//         monthPO.setFbaShipmentAmount(shipmentAmount);
//         monthPO.setFbaProductQuantity(productQuantity);
//         log.info("-------------------------------------");
//         log.info("FBA Shipment Amount: " + shipmentAmount);
//         log.info("FBA Product Amount: " + productAmount);
//         log.info("-------------------------------------");
//     }
//
//     void overseaFee(MonthPO monthPO){
//         String month = monthPO.getMonth();
//         Integer storeId = monthPO.getStoreId();
//         BigDecimal shipmentAmount = new BigDecimal(0);
//         BigDecimal productAmount = new BigDecimal(0);
//         BigDecimal warehouseAmount = new BigDecimal(0);
//         Integer itemCount = 0;
//         Integer productQuantity = 0;
//         List<OverseaPO> list = overseaService.findByDate(monthPO.getDateFrom(), monthPO.getDateTo(), storeId);
//         for(OverseaPO item : list){
//             itemCount++;
//             shipmentAmount = shipmentAmount.add(item.getAmount());
//             warehouseAmount = warehouseAmount.add(item.getWarehouseAmount());
//             Integer id = item.getId();
//             List<OverseaDetailPO> detailList = overseaService.findAllDetail(id);
//             for(OverseaDetailPO detailItem : detailList){
//                 productQuantity += detailItem.getQuantity();
//                 Integer productId = detailItem.getProductId();
//                 ProductPO productPO = productService.getById(productId);
//                 if(null == productPO){
//                     log.error("Error Product Id: " + productId);
//                     continue;
//                 }
//                 BigDecimal pa = productPO.getPurchasePrice().multiply(
//                         new BigDecimal(detailItem.getQuantity()));
//                 productAmount = productAmount.add(pa);
//             }
//         }
//         monthPO.setOverseaWarehouseAmount(warehouseAmount);
//         monthPO.setOverseaProductAmount(productAmount);
//         monthPO.setOverseaCount(itemCount);
//         monthPO.setOverseaShipmentAmount(shipmentAmount);
//         monthPO.setOverseaProductQuantity(productQuantity);
//         log.info("-------------------------------------");
//         log.info("Oversea Warehouse Amount: " + warehouseAmount);
//         log.info("Oversea Shipment Amount: " + shipmentAmount);
//         log.info("Oversea Product Amount: " + productAmount);
//         log.info("-------------------------------------");
//     }
//
//     void amazonFee(MonthPO monthPO){
//         log.error("amazonFee");
//         String month = monthPO.getMonth();
//         Date dateFrom = DateUtil.parse(monthPO.getDateFrom(), SimpleConstant.DATE_8);
//         Date dateTo = DateUtil.parse(monthPO.getDateTo(), SimpleConstant.DATE_8);
//         Integer storeId = monthPO.getStoreId();
//
//         BigDecimal amazonAdjustmentAmount = new BigDecimal(0);
//         Integer amazonAdjustmentQuantity = 0;
//         BigDecimal amazonFbaCustomerReturnFeeAmount = new BigDecimal(0);
//         Integer amazonFbaCustomerReturnFeeQuantity = 0;
//         BigDecimal amazonFbaInventoryFeeAmount = new BigDecimal(0);
//         Integer amazonFbaInventoryFeeQuantity = 0;
//         BigDecimal amazonFeeAdjustmentAmount = new BigDecimal(0);
//         Integer amazonFeeAdjustmentQuantity = 0;
//         BigDecimal amazonOrderAmount = new BigDecimal(0);
//         Integer amazonOrderQuantity = 0;
//         BigDecimal amazonOthersAmount = new BigDecimal(0);
//         Integer amazonOthersQuantity = 0;
//         BigDecimal amazonRefundAmount = new BigDecimal(0);
//         Integer amazonRefundQuantity = 0;
//         BigDecimal amazonRefundRetrochargeAmount = new BigDecimal(0);
//         Integer amazonRefundRetrochargeQuantity = 0;
//         BigDecimal amazonServiceFeeAmount = new BigDecimal(0);
//         Integer amazonServiceFeeQuantity = 0;
//         BigDecimal amazonAmount = new BigDecimal(0);
//         Integer amazonQuantity = 0;
//         BigDecimal amazonTransferAmount = new BigDecimal(0);
//         Integer amazonTransferQuantity = 0;
//         //
//         BigDecimal amazonOrderProductAmount = new BigDecimal(0);
//         BigDecimal amazonRefundProductAmount = new BigDecimal(0);
//         //
//         List<TransactionPO> list = transactionService.findByDate(dateFrom, dateTo, storeId);
//         for(TransactionPO item : list){
//             String type = item.getType() == null ? "" : item.getType();
//             BigDecimal total = item.getTotal();
//             Integer quantity = item.getQuantity() == 0 ? 1 : item.getQuantity();
//             String sku = item.getSku();
//             switch (type){
//                 case SimpleConstant.AMAZON_TYPE_Adjustment:
//                     amazonAdjustmentAmount = amazonAdjustmentAmount.add(total);
//                     amazonAdjustmentQuantity += quantity;
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_FBA_Customer_Return_Fee:
//                     amazonFbaCustomerReturnFeeAmount = amazonFbaCustomerReturnFeeAmount.add(total);
//                     amazonFbaCustomerReturnFeeQuantity += quantity;
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_FBA_Inventory_Fee:
//                     amazonFbaInventoryFeeAmount = amazonFbaInventoryFeeAmount.add(total);
//                     amazonFbaInventoryFeeQuantity += quantity;
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_Fee_Adjustment:
//                     amazonFeeAdjustmentAmount = amazonFeeAdjustmentAmount.add(total);
//                     amazonFeeAdjustmentQuantity += quantity;
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_ORDER:
//                     amazonOrderAmount = amazonOrderAmount.add(total);
//                     amazonOrderQuantity += quantity;
//                     amazonOrderProductAmount = amazonOrderProductAmount.add(calculateAmazonProductAmount(storeId, sku, quantity));
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_Refund:
//                     amazonRefundAmount = amazonRefundAmount.add(total);
//                     amazonRefundQuantity += quantity;
//                     amazonRefundProductAmount = amazonRefundProductAmount.add(calculateAmazonProductAmount(storeId, sku, quantity));
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_Refund_Retrocharge:
//                     amazonRefundRetrochargeAmount = amazonRefundRetrochargeAmount.add(total);
//                     amazonRefundRetrochargeQuantity += quantity;
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_Service_Fee:
//                     amazonServiceFeeAmount = amazonServiceFeeAmount.add(total);
//                     amazonServiceFeeQuantity += quantity;
//                     break;
//                 case SimpleConstant.AMAZON_TYPE_Transfer:
//                     amazonTransferAmount = amazonTransferAmount.add(total);
//                     amazonTransferQuantity += quantity;
//                     break;
//                 default:
//                     amazonOthersAmount = amazonOthersAmount.add(total);
//                     amazonOthersQuantity += quantity;
//                     break;
//             }
//         }
//         amazonAmount = amazonAdjustmentAmount
//                 .add(amazonFbaCustomerReturnFeeAmount)
//                 .add(amazonFbaInventoryFeeAmount)
//                 .add(amazonFeeAdjustmentAmount)
//                 .add(amazonOrderAmount)
//                 .add(amazonOthersAmount)
//                 .add(amazonRefundAmount)
//                 .add(amazonRefundRetrochargeAmount)
//                 .add(amazonServiceFeeAmount);
//         amazonQuantity = amazonAdjustmentQuantity
//                 + (amazonFbaCustomerReturnFeeQuantity)
//                 + (amazonFbaInventoryFeeQuantity)
//                 + (amazonFeeAdjustmentQuantity)
//                 + (amazonOrderQuantity)
//                 + (amazonOthersQuantity)
//                 + (amazonRefundQuantity)
//                 + (amazonRefundRetrochargeQuantity)
//                 + (amazonServiceFeeQuantity);
//         monthPO.setAmazonAdjustmentAmount(amazonAdjustmentAmount);
//         monthPO.setAmazonAdjustmentQuantity(amazonAdjustmentQuantity);
//         monthPO.setAmazonFbaCustomerReturnFeeAmount(amazonFbaCustomerReturnFeeAmount);
//         monthPO.setAmazonFbaCustomerReturnFeeQuantity(amazonFbaCustomerReturnFeeQuantity);
//         monthPO.setAmazonFbaInventoryFeeAmount(amazonFbaInventoryFeeAmount);
//         monthPO.setAmazonFbaInventoryFeeQuantity(amazonFbaInventoryFeeQuantity);
//         monthPO.setAmazonFeeAdjustmentAmount(amazonFeeAdjustmentAmount);
//         monthPO.setAmazonFeeAdjustmentQuantity(amazonFeeAdjustmentQuantity);
//         monthPO.setAmazonOrderAmount(amazonOrderAmount);
//         monthPO.setAmazonOrderQuantity(amazonOrderQuantity);
//         monthPO.setAmazonOthersAmount(amazonOthersAmount);
//         monthPO.setAmazonOthersQuantity(amazonOthersQuantity);
//         monthPO.setAmazonRefundAmount(amazonRefundAmount);
//         monthPO.setAmazonRefundQuantity(amazonRefundQuantity);
//         monthPO.setAmazonRefundRetrochargeAmount(amazonRefundRetrochargeAmount);
//         monthPO.setAmazonRefundRetrochargeQuantity(amazonRefundRetrochargeQuantity);
//         monthPO.setAmazonServiceFeeAmount(amazonServiceFeeAmount);
//         monthPO.setAmazonServiceFeeQuantity(amazonServiceFeeQuantity);
//         monthPO.setAmazonAmount(amazonAmount);
//         monthPO.setAmazonQuantity(amazonQuantity);
//         monthPO.setAmazonTransferAmount(amazonTransferAmount);
//         monthPO.setAmazonTransferQuantity(amazonTransferQuantity);
//         monthPO.setAmazonOrderProductAmount(amazonOrderProductAmount);
//         monthPO.setAmazonRefundProductAmount(amazonRefundProductAmount);
//         log.info("-------------------------------------");
//         log.info("Amazon Amount: " + amazonAmount);
//         log.info("Amazon Quantity: " + amazonQuantity);
//         log.info("Transfer Amount: " + amazonTransferAmount);
//         log.info("Transfer Quantity: " + amazonTransferQuantity);
//         log.info("Order Product Amount: " + amazonOrderProductAmount);
//         log.info("Refund Product Amount: " + amazonRefundProductAmount);
//         log.info("-------------------------------------");
//     }
//
//     private BigDecimal calculateAmazonProductAmount(Integer storeId, String sku, Integer quantity){
//         BigDecimal amazonProductAmount = new BigDecimal(0);
//         if((StringUtils.isBlank(sku)) || (null == quantity) || (quantity == 0)){
//             return amazonProductAmount;
//         }
//         List<SkuInfoPO> list = skuService.findBySkuAndStoreId(sku, storeId);
//         for(SkuInfoPO item : list){
//             ProductPO productPO = productService.getById(item.getProductId());
//             if(null != productPO){
//                 BigDecimal purchaseAmount = productPO.getPurchasePrice();
//                 if(purchaseAmount.compareTo(new BigDecimal(0)) == 0){
//                     log.error("Purchase Price is 0: [{}]", item.getProductId());
//                 }else{
//                     amazonProductAmount = amazonProductAmount.add(productPO.getPurchasePrice().multiply(new BigDecimal(quantity)));
//                 }
//             }
//         }
//         log.info("amazonProductAmount: " + amazonProductAmount);
//         return amazonProductAmount;
//     }
//
// }
