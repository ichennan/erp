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
    //
    BigDecimal amazonOrderProductAmount = new BigDecimal(0);
    BigDecimal amazonRefundProductAmount = new BigDecimal(0);

    public MonthPO(String month){
        this.month = month;
    }

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
