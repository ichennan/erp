package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.epo.ExcelSupplierDeliveryOrderEO;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "excel_supplier_delivery_order")
public class ExcelSupplierDeliveryOrderPO extends ExcelSupplierDeliveryOrderEO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    Integer excelId;
    String receivedDate;
    //
    String dingdanhao;
    String dianpuming;
    String dingdanzhuangtai;
    String jiesuanfangshi;
    String yingshouheji;
    String huopinzongshu;
    String shijijiesuan;
    String chulishijian;
    String fahuoshijian;
    String wangming;
    String shoujianren;
    String dianhua;
    String diqu;
    String huopinzhaiyao;
    String hebingbeizhu;
    String wuliufangshi;
    String huoyundanhao;
    String huoyundanpici;
    String dingdanleixing;
    String laiyuan;
    String kehubeizhu;
    String yuanshidanhao;

    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
