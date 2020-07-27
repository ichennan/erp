package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "view_purchase")
public class PurchaseViewPO extends SimpleCommonEntity {
    String bookDate;
    String deliveryDate;
    String receivedDate;
    BigDecimal freight;
    String supplier;
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
