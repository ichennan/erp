package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseSearchDTO extends SimpleCommonDTO {
    String dateType;
    String dateFrom;
    String dateTo;
    String supplier;
}
