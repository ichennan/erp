package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "excel_fba")
public class ExcelFbaPO extends SimpleCommonEntity {
    String fileName;
    String status;
    String fbaName;
    String shipmentId;
    String planId;
    String shipTo;
    Integer boxCount;
    Integer storeId;
    String weightRemark;
    BigDecimal weight;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
