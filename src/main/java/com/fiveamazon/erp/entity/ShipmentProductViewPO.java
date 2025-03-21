package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntityOld;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "view_shipment_product")
public class ShipmentProductViewPO extends SimpleCommonEntityOld {
    Integer shipmentId;
    String box;
    Integer productId;
    Integer quantity;
    BigDecimal weight;
    Integer skuId;
    //
    String deliveryDate;
    String carrier;
    String route;
    String fbaNo;
    String paymentDate;
    String signedDate;
    String store;
    String statusDelivery;
    String trackingNumber;
    Integer storeId;
    String excelDate;
    Integer excelId;
    //
    String sku;
    String fnsku;
    String asin;

    @Override
    public JSONObject toJson() {
        return new JSONObject(this);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
