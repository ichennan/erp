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
public class OverseaDetailDownloadDTO extends BaseRowModel{

    @ExcelProperty(value = "Oversea ID", index = 0)
    @ColumnWidth(15)
    private Integer overseaId;

    @ExcelProperty(value = "第几箱", index = 1)
    @ColumnWidth(50)
    String box;

    @ExcelProperty(value = "产品", index = 2)
    @ColumnWidth(50)
    Integer productId;

    @ExcelProperty(value = "数量", index = 3)
    @ColumnWidth(50)
    Integer quantity;

    @ExcelProperty(value = "重量", index = 4)
    @ColumnWidth(50)
    BigDecimal weight;

    @ExcelProperty(value = "Sku Id", index = 5)
    @ColumnWidth(50)
    Integer skuId;

    @ExcelProperty(value = "SKU", index = 6)
    @ColumnWidth(50)
    String sku;

    @ExcelProperty(value = "产品描述", index = 7)
    @ColumnWidth(50)
    String productDescription;

    @ExcelProperty(value = "箱子描述", index = 8)
    @ColumnWidth(50)
    String boxDescription;

    @ExcelProperty(value = "FBA No.", index = 9)
    @ColumnWidth(50)
    String fbaNo;

    @ExcelProperty(value = "FBA 第几箱", index = 10)
    @ColumnWidth(50)
    String fbaBox;

    @ExcelProperty(value = "FBA 日期", index = 11)
    @ColumnWidth(50)
    String fbaDate;

}