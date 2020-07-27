package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ShipmentDTO extends SimpleCommonDTO {
    String deliveryDate;
    String carrier;
    String route;
    String fbaNo;
    BigDecimal unitPrice;
    BigDecimal weight;
    BigDecimal chargeWeight;
    BigDecimal amount;
    String paymentDate;
    String signedDate;
    String store;
    String statusDelivery;
    Integer boxCount;
    String trackingNumber;
    List<ShipmentDetailDTO> shipmentDetailList;
}
