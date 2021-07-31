package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OverseaDetailDTO extends SimpleCommonDTO {
    Integer overseaId;
    Integer box;
    Integer productId;
    Integer quantity;
    BigDecimal weight;
    Integer storeId;
    Integer skuId;
    String sku;
    String productDescription;
    String boxDescription;
    String action;
    String fbaNo;
    String fbaBox;
    String fbaDate;
}
