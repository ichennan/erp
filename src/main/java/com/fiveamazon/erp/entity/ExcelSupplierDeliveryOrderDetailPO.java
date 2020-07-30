package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderDetailEO;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "excel_supplier_delivery_order_detail")
public class ExcelSupplierDeliveryOrderDetailPO extends ExcelSupplierDeliveryOrderDetailEO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer excelId;
    Integer productId;
    //
    String dingdanhao;
    String yuanshidanhao;
    String xu;
    String bianhao;
    String pinming;
    String guige;
    String danwei;
    String shuliang;
    String danjia;
    String jine;
    String beizhu;
    String zengpin;
    String zuhezhuang;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
