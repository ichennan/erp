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
    BigDecimal rate = new BigDecimal(0);
//    BigDecimal purchaseAmount;
//    Integer purchaseProductQuantity;
//    Integer purchaseCount;
//    BigDecimal fbaShipmentAmount;
//    BigDecimal fbaProductAmount;
//    Integer fbaProductQuantity;
//    Integer fbaCount;
//    BigDecimal overseaWarehouseAmount;
//    BigDecimal overseaShipmentAmount;
//    BigDecimal overseaProductAmount;
//    Integer overseaProductQuantity;
//    Integer overseaCount;
//    //
//    BigDecimal amazonAdjustmentAmount;
//    Integer amazonAdjustmentQuantity;
//    BigDecimal amazonFbaCustomerReturnFeeAmount;
//    Integer amazonFbaCustomerReturnFeeQuantity;
//    BigDecimal amazonFbaInventoryFeeAmount;
//    Integer amazonFbaInventoryFeeQuantity;
//    BigDecimal amazonFeeAdjustmentAmount;
//    Integer amazonFeeAdjustmentQuantity;
//    BigDecimal amazonOrderAmount;
//    Integer amazonOrderQuantity;
//    BigDecimal amazonOthersAmount;
//    Integer amazonOthersQuantity;
//    BigDecimal amazonRefundAmount;
//    Integer amazonRefundQuantity;
//    BigDecimal amazonRefundRetrochargeAmount;
//    Integer amazonRefundRetrochargeQuantity;
//    BigDecimal amazonServiceFeeAmount;
//    Integer amazonServiceFeeQuantity;
//    BigDecimal amazonAmount;
//    Integer amazonQuantity;
//    BigDecimal amazonTransferAmount;
//    Integer amazonTransferQuantity;
//    //
//    BigDecimal amazonOrderProductAmount;
//    BigDecimal amazonRefundProductAmount;
//    BigDecimal rate;
//    BigDecimal maoli;
//    BigDecimal liushui;
//    BigDecimal amazonAmountCNY;
//    BigDecimal amazonTransferAmountCNY;

    BigDecimal purchaseAmount = new BigDecimal(0);
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
    BigDecimal amazonProductSalesAmountCNY = new BigDecimal(0);
    BigDecimal amazonOrderAmountCNY = new BigDecimal(0);
    BigDecimal amazonAmountCNY = new BigDecimal(0);
    BigDecimal amazonTransferAmountCNY = new BigDecimal(0);
    BigDecimal amazonServiceFeeAmountCNY = new BigDecimal(0);
    BigDecimal maoli = new BigDecimal(0);
    BigDecimal liushui = new BigDecimal(0);
    //
    Integer overseaProductQuantity = 0;
    Integer purchaseProductQuantity = 0;
    Integer purchaseCount = 0;
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

    @Override
    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
