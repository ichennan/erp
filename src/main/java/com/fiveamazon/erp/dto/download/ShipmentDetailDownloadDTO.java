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
public class ShipmentDetailDownloadDTO extends BaseRowModel {

    @ExcelProperty(value = "FBA ID", index = 0)
    @ColumnWidth(15)
    private Integer shipmentId;

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

}