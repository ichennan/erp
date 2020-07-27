package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_purchase_detail")
public class PurchaseDetailPO extends SimpleCommonEntity {
    Integer purchaseId;
    Integer productId;
    Integer bookQuantity;
    Integer receivedQuantity;
    BigDecimal unitPrice;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
