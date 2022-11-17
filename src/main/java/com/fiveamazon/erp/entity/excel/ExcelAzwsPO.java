package com.fiveamazon.erp.entity.excel;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "excel_azws")
public class ExcelAzwsPO extends SimpleCommonEntity {
    String dateAzws;
    Integer storeId;
    String sku;
    String fnsku;
    String asin;
    Integer afnWarehouseQuantity;
    Integer afnFulfillableQuantity;
    Integer afnUnsellableQuantity;
    Integer afnReservedQuantity;
    Integer afnTotalQuantity;
    BigDecimal perUnitVolume;
    Integer afnInboundWorkingQuantity;
    Integer afnInboundShippedQuantity;
    Integer afnInboundReceivingQuantity;
    Integer afnResearchingQuantity;
    Integer afnReservedFutureSupply;
    Integer afnFutureSupplyBuyable;

    @Override
    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
