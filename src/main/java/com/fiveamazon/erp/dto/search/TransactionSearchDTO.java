package com.fiveamazon.erp.dto.search;

import com.fiveamazon.erp.entity.TransactionPO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionSearchDTO extends TransactionPO {
    Integer offset = 0;
    Integer limit = 10000;
    String order = "asc";
    String search = "";
    String sort = "id";
    BigDecimal transactionAmountFrom;
    BigDecimal transactionAmountTo;
    String transactionTimeFrom;
    String transactionTimeTo;
    String description;
    String sku;
}
