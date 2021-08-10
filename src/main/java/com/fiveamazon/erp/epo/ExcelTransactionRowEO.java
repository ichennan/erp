package com.fiveamazon.erp.epo;

import cn.hutool.json.JSONObject;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcelTransactionRowEO {
    @ExcelProperty("date/time")
    String transactionTimeStr;
    @ExcelProperty("settlement id")
    String settlementId;
    @ExcelProperty("type")
    String type;
    @ExcelProperty("order id")
    String orderId;
    @ExcelProperty("sku")
    String sku;
    @ExcelProperty("description")
    String description;
    @ExcelProperty("quantity")
    Integer quantity;
    @ExcelProperty("marketplace")
    String marketplace;
    @ExcelProperty("account type")
    String accountType;
    @ExcelProperty("fulfillment")
    String fulfillment;
    @ExcelProperty("order city")
    String orderCity;
    @ExcelProperty("order state")
    String orderState;
    @ExcelProperty("order postal")
    String orderPostal;
    @ExcelProperty("tax collection model")
    String taxCollectionModel;
    @ExcelProperty("product sales")
    BigDecimal productSales;
    @ExcelProperty("product sales tax")
    BigDecimal productSalesTax;
    @ExcelProperty("shipping credits")
    BigDecimal shippingCredits;
    @ExcelProperty("shipping credits tax")
    BigDecimal shippingCreditsTax;
    @ExcelProperty("gift wrap credits")
    BigDecimal giftWrapCredits;
    @ExcelProperty("giftwrap credits tax")
    BigDecimal giftWrapCreditsTax;
    @ExcelProperty("promotional rebates")
    BigDecimal promotionalRebates;
    @ExcelProperty("promotional rebates tax")
    BigDecimal promotionalRebatesTax;
    @ExcelProperty("marketplace withheld tax")
    BigDecimal marketplaceWithheldTax;
    @ExcelProperty("selling fees")
    BigDecimal sellingFees;
    @ExcelProperty("fba fees")
    BigDecimal fbaFees;
    @ExcelProperty("other transaction fees")
    BigDecimal otherTransactionFees;
    @ExcelProperty("other")
    BigDecimal other;
    @ExcelProperty("total")
    BigDecimal total;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }

}
