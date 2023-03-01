package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FbaTrackingDTO extends SimpleCommonDTO {
    String fbaBoxLabel;
    String dateSent;
    String shipper;
    String route;
    String trackingNo;
    BigDecimal unitPrice;
    Integer isMainTrackingNo;
    Integer storeId;
}
