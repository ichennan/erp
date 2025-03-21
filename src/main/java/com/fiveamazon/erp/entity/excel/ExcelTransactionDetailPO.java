package com.fiveamazon.erp.entity.excel;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "excel_transaction_detail")
public class ExcelTransactionDetailPO extends SimpleCommonEntity {
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

    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
