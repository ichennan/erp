package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntityOld;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "view_purchase_product")
public class PurchaseProductViewPO extends SimpleCommonEntityOld {
    Integer purchaseId;
    Integer productId;
    Integer bookQuantity;
    Integer receivedQuantity;
    BigDecimal unitPrice;
    //
    String excelDate;
    String bookDate;
    String deliveryDate;
    String receivedDate;
    String supplier;

    @Override
    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
