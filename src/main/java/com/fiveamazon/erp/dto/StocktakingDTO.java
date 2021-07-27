package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

@Data
public class StocktakingDTO extends SimpleCommonDTO {
    String stocktakingDate;
    String stocktakingDescription;
}
