package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import com.fiveamazon.erp.common.SimpleCommonView;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "view_sku")
public class SkuViewPO extends SimpleCommonView {
    @Id
    Integer id;
    Integer productId;
    String sku;
    Integer storeId;
    String fnsku;
    String asin;
    Integer combineId;
    Integer inventoryQuantity;

    public JSONObject toJson(){
        return new JSONObject(this);
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
