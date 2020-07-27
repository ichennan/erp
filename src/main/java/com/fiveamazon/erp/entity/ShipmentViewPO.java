package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "view_shipment")
public class ShipmentViewPO extends SimpleCommonEntity {
    String deliveryDate;
    String carrier;
    String route;
    String fbaNo;
    BigDecimal unitPrice;
    BigDecimal weight;
    BigDecimal chargeWeight;
    BigDecimal amount;
    String paymentDate;
    String signedDate;
    String store;
    String statusDelivery;
    Integer boxCount;
    String trackingNumber;
    //
    String productIdGroup;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
