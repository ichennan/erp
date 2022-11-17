package com.fiveamazon.erp.epo;

import cn.hutool.json.JSONObject;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ExcelAzwsRowEO {
    @ExcelProperty("sku")
    String sku;
    @ExcelProperty("fnsku")
    String fnsku;
    @ExcelProperty("asin")
    String asin;
    @ExcelProperty("afn-warehouse-quantity")
    Integer afnWarehouseQuantity;
    @ExcelProperty("afn-fulfillable-quantity")
    Integer afnFulfillableQuantity;
    @ExcelProperty("afn-unsellable-quantity")
    Integer afnUnsellableQuantity;
    @ExcelProperty("afn-reserved-quantity")
    Integer afnReservedQuantity;
    @ExcelProperty("afn-total-quantity")
    Integer afnTotalQuantity;
    @ExcelProperty("per-unit-volume")
    BigDecimal perUnitVolume;
    @ExcelProperty("afn-inbound-working-quantity")
    Integer afnInboundWorkingQuantity;
    @ExcelProperty("afn-inbound-shipped-quantity")
    Integer afnInboundShippedQuantity;
    @ExcelProperty("afn-inbound-receiving-quantity")
    Integer afnInboundReceivingQuantity;
    @ExcelProperty("afn-researching-quantity")
    Integer afnResearchingQuantity;
    @ExcelProperty("afn-reserved-future-supply")
    Integer afnReservedFutureSupply;
    @ExcelProperty("afn-future-supply-buyable")
    Integer afnFutureSupplyBuyable;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }

}
