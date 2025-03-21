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
public class PurchaseDownloadDTO extends BaseRowModel {

    @ExcelProperty(value = "发货ID", index = 0)
    @ColumnWidth(15)
    private Integer id;

    @ExcelProperty(value = "发货日期", index = 1)
    @ColumnWidth(15)
    private String deliveryDate;

    @ExcelProperty(value = "供应商", index = 2)
    @ColumnWidth(20)
    private String supplier;

    @ExcelProperty(value = "总费用", index = 3)
    @ColumnWidth(15)
    private BigDecimal amount;

}