package com.fiveamazon.erp.dto.download;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ContentRowHeight(30)
public class ShipmentDownloadDTO extends BaseRowModel {

    @ExcelProperty(value = "ID", index = 0)
    @ColumnWidth(15)
    private Integer id;

    @ExcelProperty(value = "FBA No", index = 1)
    @ColumnWidth(15)
    private String fbaNo;

    @ExcelProperty(value = "发货日期", index = 2)
    @ColumnWidth(15)
    private String deliveryDate;

    @ExcelProperty(value = "货代", index = 3)
    @ColumnWidth(20)
    private String carrier;

    @ExcelProperty(value = "路线", index = 4)
    @ColumnWidth(20)
    private String route;

    @ExcelProperty(value = "运费单价", index = 5)
    @ColumnWidth(20)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "重量", index = 6)
    @ColumnWidth(20)
    private BigDecimal weight;

    @ExcelProperty(value = "收费重量", index = 7)
    @ColumnWidth(20)
    private BigDecimal chargeWeight;

    @ExcelProperty(value = "总运费", index = 8)
    @ColumnWidth(20)
    private BigDecimal amount;

    @ExcelProperty(value = "付款日期", index = 9)
    @ColumnWidth(20)
    String paymentDate;

    @ExcelProperty(value = "签收日期", index = 10)
    @ColumnWidth(20)
    String signedDate;

    @ExcelProperty(value = "发货状态", index = 11)
    @ColumnWidth(20)
    String statusDelivery;

    @ExcelProperty(value = "箱子数量", index = 12)
    @ColumnWidth(20)
    Integer boxCount;

    @ExcelProperty(value = "货运单号", index = 13)
    @ColumnWidth(20)
    String trackingNumber;

    @ExcelProperty(value = "重量备注", index = 14)
    @ColumnWidth(20)
    String weightRemark;

}