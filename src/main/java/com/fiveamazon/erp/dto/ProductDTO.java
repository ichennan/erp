package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import com.fiveamazon.erp.entity.SkuInfoPO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDTO extends SimpleCommonDTO {
    String name;
    String sn;
    String color;
    String size;
    Integer enabledPacketSeq;
    BigDecimal purchasePrice;
    String store;
}
