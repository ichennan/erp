package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "tbl_stocktaking_detail")
public class StocktakingDetailPO extends SimpleCommonEntity {
    Integer stocktakingId;
    Integer productId;
    Integer calculatedQuantity;
    Integer stocktakingQuantity;
    Integer adjustmentQuantity;

    public JSONObject toJson() {
        return new JSONObject(this);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
