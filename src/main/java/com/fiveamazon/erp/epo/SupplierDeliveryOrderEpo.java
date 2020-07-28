package com.fiveamazon.erp.epo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SupplierDeliveryOrderEpo {
    @ExcelProperty("订单号")
    String dingdanhao;
    @ExcelProperty("店铺名")
    String dianpuming;
    @ExcelProperty("订单状态")
    String dingdanzhuangtai;
    @ExcelProperty("结算方式")
    String jiesuanfangshi;
    @ExcelProperty("应收合计")
    String yingshouheji;
    @ExcelProperty("货品总数")
    String huopinzongshu;
    @ExcelProperty("实际结算")
    String shijijiesuan;
    @ExcelProperty("处理时间")
    String chulishijian;
    @ExcelProperty("发货时间")
    String fahuoshijian;
    @ExcelProperty("网名")
    String wangming;
    @ExcelProperty("收件人")
    String shoujianren;
    @ExcelProperty("电话")
    String dianhua;
    @ExcelProperty("地区")
    String diqu;
    @ExcelProperty("货品摘要")
    String huopinzhaiyao;
    @ExcelProperty("合并备注")
    String hebingbeizhu;
    @ExcelProperty("物流方式")
    String wuliufangshi;
    @ExcelProperty("货运单号")
    String huoyundanhao;
    @ExcelProperty("货运单批次")
    String huoyundanpici;
    @ExcelProperty("订单类型")
    String dingdanleixing;
    @ExcelProperty("来源")
    String laiyuan;
    @ExcelProperty("客户备注")
    String kehubeizhu;
    @ExcelProperty("原始单号")
    String yuanshidanhao;

}
