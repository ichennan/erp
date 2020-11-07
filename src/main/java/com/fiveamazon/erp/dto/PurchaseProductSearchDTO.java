package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseProductSearchDTO extends SimpleCommonDTO {
    String dateType;
    String dateFrom;
    String dateTo;
    String productId;
}
