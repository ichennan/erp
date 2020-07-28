package com.fiveamazon.erp.epo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TestEpo2 {
    @ExcelProperty(index = 0)
    String name;
    @ExcelProperty(index = 1)
    String age;
    @ExcelProperty(index = 2)
    String school;

}
