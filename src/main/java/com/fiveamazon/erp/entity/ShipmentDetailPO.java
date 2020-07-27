package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_shipment_detail")
public class ShipmentDetailPO extends SimpleCommonEntity {
    Integer shipmentId;
    String box;
    Integer productId;
    Integer quantity;
    BigDecimal weight;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
