package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentDetailDTO extends SimpleCommonDTO {
    Integer shipmentId;
    String box;
    Integer productId;
    Integer quantity;
    BigDecimal weight;
}
