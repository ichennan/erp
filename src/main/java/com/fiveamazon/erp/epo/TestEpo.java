package com.fiveamazon.erp.epo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TestEpo {
    @ExcelProperty(index = 0)
    String name;
    @ExcelProperty(index = 1)
    String age;

}
