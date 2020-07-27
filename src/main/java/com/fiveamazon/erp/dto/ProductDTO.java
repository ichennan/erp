package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO extends SimpleCommonDTO {
    String name;
    String sn;
    String color;
    String size;
    Integer enabledPacketSeq;
    BigDecimal purchasePrice;
    String store;
    String skuStoreList;
    String skuNameList;
}
