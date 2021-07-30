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
public class PurchaseDownloadDTO extends BaseRowModel{

    @ExcelProperty(value = "发货ID", index = 0)
    @ColumnWidth(10)
    private Integer purchaseId;

    @ExcelProperty(value = "发货日期", index = 1)
    @ColumnWidth(15)
    private String deliveryDate;

    @ExcelProperty(value = "供应商", index = 2)
    @ColumnWidth(15)
    private String supplier;

    @ExcelProperty(value = "产品", index = 3)
    @ColumnWidth(30)
    private String snname;

    @ExcelProperty(value = "数量", index = 4)
    @ColumnWidth(15)
    private Integer receivedQuantity;

    @ExcelProperty(value = "单价", index = 5)
    @ColumnWidth(15)
    private BigDecimal unitPrice;

    @ExcelProperty(value = "数量*单价", index = 6)
    @ColumnWidth(15)
    private BigDecimal totalPrice;

}