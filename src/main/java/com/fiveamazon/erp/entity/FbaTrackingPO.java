package com.fiveamazon.erp.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_fba_tracking")
public class FbaTrackingPO extends SimpleCommonEntity {
    String fbaBoxLabel;
    String dateSent;
    String shipper;
    String route;
    String trackingNo;
    BigDecimal unitPrice = new BigDecimal(0);
    Integer isMainTrackingNo = 0;
    Integer storeId;


    @Override
    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        toJson.put("dateCreate", DateUtil.format(getCreateDate(), "yyyyMMdd"));
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
