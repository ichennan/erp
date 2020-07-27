package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PacketDetailDTO extends SimpleCommonDTO {
    Integer packetId;
    Integer productId;
    Integer quantity;
}
