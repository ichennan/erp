package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

@Data
public class ShipmentProductSearchDTO extends SimpleCommonDTO {
    String dateType;
    String dateFrom;
    String dateTo;
    String productId;
    String skuId;
}
