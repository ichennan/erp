package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "view_oversea")
public class OverseaViewPO extends SimpleCommonEntity {
    String warehouseName;
    String deliveryDate;
    String carrier;
    String route;
    String deliveryNo;
    BigDecimal unitPrice;
    BigDecimal weight;
    BigDecimal chargeWeight;
    BigDecimal amount;
    String paymentDate;
    String signedDate;
    String status;
    Integer boxCount;
    String trackingNumber;
    String weightRemark;
    String productIdGroup;
    Integer storeId;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
