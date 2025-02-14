package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntityOld;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "view_sku_info")
public class SkuInfoVO extends SimpleCommonEntityOld {
    //
    Integer productId;
    String sku;
    String store;
    Integer storeId;
    String fnsku;
    String asin;
    Integer combineId;
    Integer priority;
    String skuDesc;
    //
    Integer sumProductPurchaseQuantity;
    Integer sumProductPacketQuantity;
    Integer sumProductShipmentQuantity;
    Integer sumProductShipmentOnthewayQuantity;
    Integer sumProductShipmentArrivedQuantity;
    Integer sumSkuShipmentQuantity;
    Integer sumSkuShipmentOnthewayQuantity;
    Integer sumSkuShipmentArrivedQuantity;
    Integer sumSkuOverseaQuantity;
    Integer productInventoryQuantity;

    @Override
    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
