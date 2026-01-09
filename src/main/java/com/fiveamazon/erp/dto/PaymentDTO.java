package com.fiveamazon.erp.dto;

import com.fiveamazon.erp.common.SimpleCommonDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentDTO extends SimpleCommonDTO {
    String status;
    String inex;
    String category1st;
    String category2nd;
    String category3rd;
    String transactionDate;
    String transactionStore;
    BigDecimal transactionAmount;
    BigDecimal pendingAmount;
    String transactionPlatform;
    String transactionAccount;
    String transactionSubject;
}
