package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuInfoDTO extends SimpleCommonDTO {
    Integer productId;
    String sku;
    String fnsku;
    Integer storeId;
    String asin;
    Integer priority;
}
