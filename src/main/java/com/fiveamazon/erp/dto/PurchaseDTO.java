package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseDTO extends SimpleCommonDTO {
    String bookDate;
    String deliveryDate;
    String receivedDate;
    BigDecimal freight;
    String supplier;
}
