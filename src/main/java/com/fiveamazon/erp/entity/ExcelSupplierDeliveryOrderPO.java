package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import com.fiveamazon.erp.epo.SupplierDeliveryOrderEpo;
import lombok.Data;

import javax.persistence.*;
import java.util.function.Supplier;

@Data
@Entity
@Table(name = "excel_supplier_delivery_order")
public class ExcelSupplierDeliveryOrderPO extends SupplierDeliveryOrderEpo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    public JSONObject toJson(){
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString(){
        return toJson().toString();
    }
}
