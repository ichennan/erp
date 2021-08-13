package com.fiveamazon.erp.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import com.fiveamazon.erp.common.SimpleConstant;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "tbl_transaction")
public class TransactionPO extends SimpleCommonEntity {
    Integer storeId;
    Integer excelId;
    Date transactionTime;
    String settlementId;
    String type;
    String orderId;
    String sku;
    String description;
    Integer quantity;
    String marketplace;
    String accountType;
    String fulfillment;
    String orderCity;
    String orderState;
    String orderPostal;
    String taxCollectionModel;
    BigDecimal productSales;
    BigDecimal productSalesTax;
    BigDecimal shippingCredits;
    BigDecimal shippingCreditsTax;
    BigDecimal giftWrapCredits;
    BigDecimal giftWrapCreditsTax;
    BigDecimal promotionalRebates;
    BigDecimal promotionalRebatesTax;
    BigDecimal marketplaceWithheldTax;
    BigDecimal sellingFees;
    BigDecimal fbaFees;
    BigDecimal otherTransactionFees;
    BigDecimal other;
    BigDecimal total;

    @Override
    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        toJson.put("transactionTime", null == transactionTime ? null : DateUtil.format(transactionTime, SimpleConstant.DATE_17));
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
