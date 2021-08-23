package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_month")
public class MonthPO extends SimpleCommonEntity {
    String month;
    String dateFrom;
    String dateTo;
    Integer storeId;
    BigDecimal purchaseAmount;
    Integer purchaseProductQuantity;
    Integer purchaseCount;
    BigDecimal fbaShipmentAmount;
    BigDecimal fbaProductAmount;
    Integer fbaProductQuantity;
    Integer fbaCount;
    BigDecimal overseaWarehouseAmount;
    BigDecimal overseaShipmentAmount;
    BigDecimal overseaProductAmount;
    Integer overseaProductQuantity;
    Integer overseaCount;
    //
    BigDecimal amazonAdjustmentAmount;
    Integer amazonAdjustmentQuantity;
    BigDecimal amazonFbaCustomerReturnFeeAmount;
    Integer amazonFbaCustomerReturnFeeQuantity;
    BigDecimal amazonFbaInventoryFeeAmount;
    Integer amazonFbaInventoryFeeQuantity;
    BigDecimal amazonFeeAdjustmentAmount;
    Integer amazonFeeAdjustmentQuantity;
    BigDecimal amazonOrderAmount;
    Integer amazonOrderQuantity;
    BigDecimal amazonOthersAmount;
    Integer amazonOthersQuantity;
    BigDecimal amazonRefundAmount;
    Integer amazonRefundQuantity;
    BigDecimal amazonRefundRetrochargeAmount;
    Integer amazonRefundRetrochargeQuantity;
    BigDecimal amazonServiceFeeAmount;
    Integer amazonServiceFeeQuantity;
    BigDecimal amazonAmount;
    Integer amazonQuantity;
    BigDecimal amazonTransferAmount;
    Integer amazonTransferQuantity;
    //
    BigDecimal amazonOrderProductAmount;
    BigDecimal amazonRefundProductAmount;
    BigDecimal rate;
    BigDecimal maoli;
    BigDecimal liushui;
    BigDecimal amazonAmountCNY;
    BigDecimal amazonTransferAmountCNY;

    @Override
    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
