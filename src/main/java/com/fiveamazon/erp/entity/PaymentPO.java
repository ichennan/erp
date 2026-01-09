package com.fiveamazon.erp.entity;

import cn.hutool.json.JSONObject;
import com.fiveamazon.erp.common.SimpleCommonEntity;
import com.fiveamazon.erp.common.SimpleConstant;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "tbl_payment")
public class PaymentPO extends SimpleCommonEntity {
    String status;
    String inex;
    String category1st;
    String category2nd;
    String category3rd;
    String transactionDate;
    String transactionStore;
    BigDecimal transactionAmount = SimpleConstant.BIG_DECIMAL_0;
    BigDecimal pendingAmount = SimpleConstant.BIG_DECIMAL_0;
    String transactionPlatform;
    String transactionAccount;
    String transactionSubject;


    @Override
    public JSONObject toJson() {
        JSONObject toJson = new JSONObject(this);
        try {
            toJson.put("seq", StringUtils.hasText(transactionDate) ? Integer.valueOf(transactionDate.replaceAll("-", "")) : 0);
        } catch (Exception e) {
            toJson.put("seq", 0);
        }
        return toJson;
    }

    @Override
    public String toString() {
        return toJson().toString();
    }
}
