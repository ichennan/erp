package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

@Data
public class FbaTrackingBatchCreateDTO extends SimpleCommonDTO {
    String fbaNo;
    Integer boxCount;
    Integer storeId;
}
