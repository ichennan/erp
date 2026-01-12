package com.fiveamazon.erp.dto.search;

import com.fiveamazon.erp.entity.PaymentPO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentSearchDTO extends PaymentPO {
    Integer offset = 0;
    Integer limit = 10000;
    String order = "desc";
    String search = "";
    String sort = "id";
    BigDecimal transactionAmountFrom;
    BigDecimal transactionAmountTo;
    String transactionDateFrom;
    String transactionDateTo;
    String remark;
}
