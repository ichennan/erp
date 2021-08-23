package com.fiveamazon.erp.entity.excel;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "excel_carrier_bill_detail")
public class ExcelCarrierBillDetailPO extends SimpleCommonEntity {
    Integer excelId;
    String billNo;
    String billCreateDate;
    String destination;
    String chargeBoxCount;
    String chargeWeight;
    String route;
    String trackingNumber;
    String transferTrackingNumber1;
    String transferTrackingNumber2;
    String transferTrackingNumber3;
    String unitPrice;
    String description;
    String fbaNo;
    String amount;
    Integer relatedShipmentId;
    Integer relatedOverseaId;

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
