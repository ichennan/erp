package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntityOld;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "view_stocktaking")
public class StocktakingViewPO extends SimpleCommonEntityOld {
    String stocktakingDate;
    String stocktakingDescription;
    String productIdGroup;

    @Override
    public JSONObject toJson() {
        return new JSONObject(this);
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
