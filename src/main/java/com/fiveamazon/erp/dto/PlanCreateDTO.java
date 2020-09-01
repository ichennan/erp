package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;
import java.util.List;

@Data
public class PlanCreateDTO extends SimpleCommonDTO {
    String planDate;
    String deliveryDate;
    String carrier;
    Integer boxCount;
    Integer storeId;
    List<PlanDetailCreateDTO> array;
}
