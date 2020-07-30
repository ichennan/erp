package com.fiveamazon.erp.epo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelSupplierDeliveryOrderDetailEO {
    @ExcelProperty("订单号")
    String dingdanhao;
    @ExcelProperty("原始单号")
    String yuanshidanhao;
    @ExcelProperty("序")
    String xu;
    @ExcelProperty("编号")
    String bianhao;
    @ExcelProperty("品名")
    String pinming;
    @ExcelProperty("规格")
    String guige;
    @ExcelProperty("单位")
    String danwei;
    @ExcelProperty("数量")
    String shuliang;
    @ExcelProperty("单价")
    String danjia;
    @ExcelProperty("金额")
    String jine;
    @ExcelProperty("备注")
    String beizhu;
    @ExcelProperty("赠品")
    String zengpin;
    @ExcelProperty("组合装")
    String zuhezhuang;

}
