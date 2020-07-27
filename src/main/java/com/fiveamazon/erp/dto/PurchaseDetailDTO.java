package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseDetailDTO extends SimpleCommonDTO {
    Integer purchaseId;
    Integer productId;
    Integer bookQuantity;
    Integer receivedQuantity;
    BigDecimal unitPrice;
}
