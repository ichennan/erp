package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverseaDTO extends SimpleCommonDTO {
    String deliveryDate;
    String carrier;
    String route;
    String deliveryNo;
    BigDecimal unitPrice;
    BigDecimal weight;
    BigDecimal chargeWeight;
    BigDecimal amount;
    String paymentDate;
    String signedDate;
    String statusDelivery;
    Integer boxCount;
    String trackingNumber;
    String weightRemark;
}
