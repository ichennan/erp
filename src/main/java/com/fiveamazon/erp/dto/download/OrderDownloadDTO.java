package com.fiveamazon.erp.dto.download;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ContentRowHeight(30)
public class OrderDownloadDTO extends BaseRowModel{

    @ExcelProperty(value = "transactionTime", index = 0)
    @ColumnWidth(30)
    Date transactionTime;

    @ExcelProperty(value = "orderId", index = 1)
    @ColumnWidth(30)
    String orderId;

    @ExcelProperty(value = "sku", index = 2)
    @ColumnWidth(30)
    String sku;

    @ExcelProperty(value = "quantity", index = 3)
    @ColumnWidth(15)
    Integer quantity;

    @ExcelProperty(value = "productSales", index = 4)
    @ColumnWidth(15)
    BigDecimal productSales;

    @ExcelProperty(value = "productSalesTax", index = 5)
    @ColumnWidth(15)
    BigDecimal productSalesTax;

    @ExcelProperty(value = "shippingCredits", index = 6)
    @ColumnWidth(15)
    BigDecimal shippingCredits;

    @ExcelProperty(value = "shippingCreditsTax", index = 7)
    @ColumnWidth(15)
    BigDecimal shippingCreditsTax;

    @ExcelProperty(value = "giftWrapCredits", index = 8)
    @ColumnWidth(15)
    BigDecimal giftWrapCredits;

    @ExcelProperty(value = "giftWrapCreditsTax", index = 9)
    @ColumnWidth(15)
    BigDecimal giftWrapCreditsTax;

    @ExcelProperty(value = "promotionalRebates", index = 10)
    @ColumnWidth(15)
    BigDecimal promotionalRebates;

    @ExcelProperty(value = "promotionalRebatesTax", index = 11)
    @ColumnWidth(15)
    BigDecimal promotionalRebatesTax;

    @ExcelProperty(value = "marketplaceWithheldTax", index = 12)
    @ColumnWidth(15)
    BigDecimal marketplaceWithheldTax;

    @ExcelProperty(value = "sellingFees", index = 13)
    @ColumnWidth(15)
    BigDecimal sellingFees;

    @ExcelProperty(value = "fbaFees", index = 14)
    @ColumnWidth(15)
    BigDecimal fbaFees;

    @ExcelProperty(value = "otherTransactionFees", index = 15)
    @ColumnWidth(15)
    BigDecimal otherTransactionFees;

    @ExcelProperty(value = "other", index = 16)
    @ColumnWidth(15)
    BigDecimal other;

    @ExcelProperty(value = "total", index = 17)
    @ColumnWidth(15)
    BigDecimal total;

}