package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import com.fiveamazon.erp.common.SimpleCommonView;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "view_sku_info")
public class SkuInfoVO extends SimpleCommonEntity {
    //
    Integer productId;
    String sku;
    String store;
    Integer storeId;
    String fnsku;
    String asin;
    Integer combineId;
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
    Integer productInventoryQuantity;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
