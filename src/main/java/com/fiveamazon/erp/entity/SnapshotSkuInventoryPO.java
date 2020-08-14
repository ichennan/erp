package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "snapshot_sku_inventory")
public class SnapshotSkuInventoryPO extends SimpleCommonEntity {
    @Id
    Integer id;
    String snapshotDate;
    //
    Integer productId;
    String skuDesc;
    Integer skuId;
    Integer combineId;
    Integer combineCount;
    Integer storeId;
    String sku;
    String fnsku;
    String asin;
    //
    Integer allPurchaseQuantity;
    Integer allPacketQuantity;
    Integer allStocktakingQuantity;
    Integer allShipmentQuantity;
    Integer inventoryQuantity;
    Integer onthewayShipmentQuantity;
    Integer onthewayPurchaseQuantity;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
