package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "excel_fba_pack_list")
public class ExcelFbaPackListPO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer excelId;
    Integer productId;
    Integer storeId;
    Integer skuId;
    //
    String merchantSku;
    String asin;
    String title;
    String fnsku;
    String externalId;
    String whoWillPrep;
    String prepType;
    String whoWillLabel;
    String expectedQty;
    String boxedQty;
    String box01Qty;
    String box02Qty;
    String box03Qty;
    String box04Qty;
    String box05Qty;
    String box06Qty;
    String box07Qty;
    String box08Qty;
    String box09Qty;
    String box10Qty;
    String box11Qty;
    String box12Qty;
    String box13Qty;
    String box14Qty;
    String box15Qty;
    String box16Qty;
    String box17Qty;
    String box18Qty;
    String box19Qty;
    String box20Qty;
    String box21Qty;
    String box22Qty;
    String box23Qty;
    String box24Qty;
    String box25Qty;
    String box26Qty;
    String box27Qty;
    String box28Qty;
    String box29Qty;
    String box30Qty;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
