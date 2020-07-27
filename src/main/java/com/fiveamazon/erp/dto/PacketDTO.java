package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.util.List;

@Data
public class PacketDTO extends SimpleCommonDTO {
    String deliveryDate;
    List<PacketDetailDTO> packetDetailDTOList;
}
