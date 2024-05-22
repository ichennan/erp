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
    //小啰啰 10对
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
    //order 8
    Integer amazonOrderQuantity = 0;
    BigDecimal amazonOrderAmount = new BigDecimal(0);
    BigDecimal erpOrderProductAmount = new BigDecimal(0);
    BigDecimal erpOrderProductAmountUSD = new BigDecimal(0);
    BigDecimal erpOrderProductPurchaseAmount = new BigDecimal(0);
    BigDecimal erpOrderProductPurchaseAmountUSD = new BigDecimal(0);
    BigDecimal erpOrderProductFreightAmount = new BigDecimal(0);
    BigDecimal erpOrderProductFreightAmountUSD = new BigDecimal(0);
    //refund 8
    Integer amazonRefundQuantity = 0;
    BigDecimal amazonRefundAmount = new BigDecimal(0);
    BigDecimal erpRefundProductAmount = new BigDecimal(0);
    BigDecimal erpRefundProductAmountUSD = new BigDecimal(0);
    BigDecimal erpRefundProductPurchaseAmount = new BigDecimal(0);
    BigDecimal erpRefundProductPurchaseAmountUSD = new BigDecimal(0);
    BigDecimal erpRefundProductFreightAmount = new BigDecimal(0);
    BigDecimal erpRefundProductFreightAmountUSD = new BigDecimal(0);
    //zong 9
    Integer amazonProductSalesQuantity = 0;
    BigDecimal amazonProductSalesAmount = new BigDecimal(0);
    Integer amazonProductPaymentQuantity = 0;
    BigDecimal amazonProductPaymentAmount = new BigDecimal(0);
    BigDecimal chengben = new BigDecimal(0);
    BigDecimal chengbenUSD = new BigDecimal(0);
    Integer amazonQuantity = 0;
    BigDecimal amazonAmount = new BigDecimal(0);
    BigDecimal lirun = new BigDecimal(0);

    @Override
    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
