package com.fiveamazon.erp.entity.excel;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "excel_carrier_bill")
public class ExcelCarrierBillPO extends SimpleCommonEntity {
    String fileName;
    String carrier;
    Date dateFrom;
    Date dateTo;

    @Override
    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
