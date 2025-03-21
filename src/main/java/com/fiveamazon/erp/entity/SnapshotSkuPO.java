package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "snapshot_sku")
public class SnapshotSkuPO {
    @Id
    Integer id;
    String snapshotDate;
    Integer productId;
    Integer skuId;
    //
    Integer sumProductPurchaseQuantity;
    Integer sumProductShipmentQuantity;
    Integer sumProductShipmentOnthewayQuantity;
    Integer sumProductPacketQuantity;
    Integer sumProductShipmentArrivedQuantity;
    Integer productInventoryQuantity;
    Integer sumSkuShipmentQuantity;
    Integer sumSkuShipmentOnthewayQuantity;
    Integer sumSkuShipmentArrivedQuantity;

    public JSONObject toJson() {
        return new JSONObject(this);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
