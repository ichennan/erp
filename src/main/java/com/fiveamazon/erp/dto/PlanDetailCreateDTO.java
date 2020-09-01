package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

@Data
public class PlanDetailCreateDTO extends SimpleCommonDTO {
    Integer skuId;
    Integer productId;
    Integer planQuantity;
}
