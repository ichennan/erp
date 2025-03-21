package com.fiveamazon.erp.epo;

import cn.hutool.json.JSONObject;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelCarrierBillCainiaoRowEO {
    @ExcelProperty(index = 0)
    String seqNo;
    String billCreateDate;
    String destination;
    String chargeBoxCount;
    String chargeWeight;
    String route;
    String trackingNumber;
    String transferTrackingNumber1;
    String unitPrice;
    String description;
    String fbaNo;
    String amount;

    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }

}
