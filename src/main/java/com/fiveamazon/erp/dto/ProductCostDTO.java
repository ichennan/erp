package com.fiveamazon.erp.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCostDTO{
    BigDecimal productPurchaseAmount = new BigDecimal(0);
    BigDecimal productFreightAmount = new BigDecimal(0);
    BigDecimal productCostAmount = new BigDecimal(0);

    public void ProductCostDTO(){
        this.productPurchaseAmount = new BigDecimal(0);
        this.productFreightAmount = new BigDecimal(0);
        this.productCostAmount = new BigDecimal(0);
    }
}
