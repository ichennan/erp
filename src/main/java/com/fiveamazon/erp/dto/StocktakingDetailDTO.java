package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

@Data
public class StocktakingDetailDTO extends SimpleCommonDTO {
    Integer stocktakingId;
    Integer productId;
    Integer calculatedQuantity;
    Integer stocktakingQuantity;
    Integer adjustmentQuantity;
}
